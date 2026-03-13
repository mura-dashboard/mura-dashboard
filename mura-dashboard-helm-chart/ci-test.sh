#!/usr/bin/env bash
#
# Lint, template-render, and schema-validate the mura-dashboard Helm chart.
# Runs the same checks locally that CI executes in GitHub Actions.
#
# Usage:
#   ./ci-test.sh              # from the chart directory
#   ./mura-dashboard-helm-chart/ci-test.sh   # from the repo root
#
# Prerequisites:
#   - helm  (required)
#   - kubeconform (optional – schema validation is skipped if not found)
#
set -euo pipefail

# ── Resolve chart directory ──────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CHART_DIR="$SCRIPT_DIR"

# ── Colour helpers (disabled when stdout is not a terminal) ──────────────────
if [[ -t 1 ]]; then
  GREEN='\033[0;32m'; RED='\033[0;31m'; YELLOW='\033[0;33m'; BOLD='\033[1m'; RESET='\033[0m'
else
  GREEN=''; RED=''; YELLOW=''; BOLD=''; RESET=''
fi

PASSED=0
FAILED=0

pass() { PASSED=$((PASSED + 1)); echo -e "${GREEN}PASS${RESET} $1"; }
fail() { FAILED=$((FAILED + 1)); echo -e "${RED}FAIL${RESET} $1"; }
info() { echo -e "${BOLD}==>${RESET} $1"; }
warn() { echo -e "${YELLOW}WARN${RESET} $1"; }

# ── Pre-flight checks ───────────────────────────────────────────────────────
command -v helm >/dev/null 2>&1 || { echo "Error: helm is not installed." >&2; exit 1; }

HAVE_KUBECONFORM=false
if command -v kubeconform >/dev/null 2>&1; then
  HAVE_KUBECONFORM=true
else
  warn "kubeconform not found – schema validation will be skipped."
  warn "Install: https://github.com/yannh/kubeconform#installation"
fi

# ── 1. Dependency build ─────────────────────────────────────────────────────
info "Building chart dependencies …"
if helm dependency build "$CHART_DIR" --skip-refresh 2>/dev/null || helm dependency build "$CHART_DIR"; then
  pass "helm dependency build"
else
  fail "helm dependency build"
fi

# ── 2. Lint ──────────────────────────────────────────────────────────────────
info "Linting chart (default values) …"
if helm lint "$CHART_DIR" --quiet; then
  pass "helm lint (default values)"
else
  fail "helm lint (default values)"
fi

info "Linting chart (external DB + ingress + HPA) …"
if helm lint "$CHART_DIR" --quiet \
  --set postgresql.enabled=false \
  --set externalDatabase.host=db.example.com \
  --set externalDatabase.password=secret \
  --set ingress.enabled=true \
  --set autoscaling.enabled=true; then
  pass "helm lint (external DB + ingress + HPA)"
else
  fail "helm lint (external DB + ingress + HPA)"
fi

info "Linting chart (Gateway API + HPA) …"
if helm lint "$CHART_DIR" --quiet \
  --set gatewayApi.enabled=true \
  --set gatewayApi.parentRefs[0].name=my-gateway \
  --set autoscaling.enabled=true; then
  pass "helm lint (Gateway API + HPA)"
else
  fail "helm lint (Gateway API + HPA)"
fi

# ── 3. Template rendering ───────────────────────────────────────────────────
TEMPLATE_DEFAULT=$(mktemp)
TEMPLATE_EXTERNAL=$(mktemp)
TEMPLATE_GATEWAY=$(mktemp)
trap 'rm -f "$TEMPLATE_DEFAULT" "$TEMPLATE_EXTERNAL" "$TEMPLATE_GATEWAY"' EXIT

info "Rendering templates (default values) …"
if helm template test-release "$CHART_DIR" > "$TEMPLATE_DEFAULT"; then
  pass "helm template (default values)"
else
  fail "helm template (default values)"
fi

info "Rendering templates (external DB + ingress + HPA) …"
if helm template test-release "$CHART_DIR" \
  --set postgresql.enabled=false \
  --set externalDatabase.host=db.example.com \
  --set externalDatabase.password=secret \
  --set ingress.enabled=true \
  --set autoscaling.enabled=true > "$TEMPLATE_EXTERNAL"; then
  pass "helm template (external DB + ingress + HPA)"
else
  fail "helm template (external DB + ingress + HPA)"
fi

info "Rendering templates (Gateway API + HPA) …"
if helm template test-release "$CHART_DIR" \
  --set gatewayApi.enabled=true \
  --set gatewayApi.parentRefs[0].name=my-gateway \
  --set autoscaling.enabled=true > "$TEMPLATE_GATEWAY"; then
  pass "helm template (Gateway API + HPA)"
else
  fail "helm template (Gateway API + HPA)"
fi

# ── 4. Kubeconform schema validation ────────────────────────────────────────
if $HAVE_KUBECONFORM; then
  KUBECONFORM_ARGS=(
    -strict
    -summary
    -output text
    -kubernetes-version 1.32.0
    -ignore-missing-schemas
  )

  info "Validating schemas (default values) …"
  if kubeconform "${KUBECONFORM_ARGS[@]}" < "$TEMPLATE_DEFAULT"; then
    pass "kubeconform (default values)"
  else
    fail "kubeconform (default values)"
  fi

  info "Validating schemas (external DB + ingress + HPA) …"
  if kubeconform "${KUBECONFORM_ARGS[@]}" < "$TEMPLATE_EXTERNAL"; then
    pass "kubeconform (external DB + ingress + HPA)"
  else
    fail "kubeconform (external DB + ingress + HPA)"
  fi

  info "Validating schemas (Gateway API + HPA) …"
  if kubeconform "${KUBECONFORM_ARGS[@]}" < "$TEMPLATE_GATEWAY"; then
    pass "kubeconform (Gateway API + HPA)"
  else
    fail "kubeconform (Gateway API + HPA)"
  fi
fi

# ── Summary ──────────────────────────────────────────────────────────────────
echo ""
echo -e "${BOLD}────────────────────────────────────${RESET}"
echo -e "${GREEN}Passed: ${PASSED}${RESET}  ${RED}Failed: ${FAILED}${RESET}"
echo -e "${BOLD}────────────────────────────────────${RESET}"

if [[ "$FAILED" -gt 0 ]]; then
  exit 1
fi
