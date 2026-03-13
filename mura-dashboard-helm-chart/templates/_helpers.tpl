{{/*
Expand the name of the chart.
*/}}
{{- define "mura-dashboard.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "mura-dashboard.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "mura-dashboard.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "mura-dashboard.labels" -}}
helm.sh/chart: {{ include "mura-dashboard.chart" . }}
{{ include "mura-dashboard.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "mura-dashboard.selectorLabels" -}}
app.kubernetes.io/name: {{ include "mura-dashboard.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "mura-dashboard.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "mura-dashboard.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Resolve the Secret name to use for credentials.
If existingSecret is set, use that; otherwise fall back to the chart-generated Secret.
*/}}
{{- define "mura-dashboard.secretName" -}}
{{- if .Values.existingSecret }}
{{- .Values.existingSecret }}
{{- else }}
{{- include "mura-dashboard.fullname" . }}
{{- end }}
{{- end }}

{{/*
Compute the JDBC datasource URL.
When the Bitnami PostgreSQL subchart is enabled, construct the URL from the subchart service name.
Otherwise, use the externalDatabase values.
*/}}
{{- define "mura-dashboard.datasourceUrl" -}}
{{- if .Values.postgresql.enabled }}
{{- printf "jdbc:postgresql://%s-postgresql:5432/%s" .Release.Name .Values.postgresql.auth.database }}
{{- else }}
{{- printf "jdbc:postgresql://%s:%v/%s" .Values.externalDatabase.host (.Values.externalDatabase.port | int) .Values.externalDatabase.database }}
{{- end }}
{{- end }}

{{/*
Compute the datasource username.
*/}}
{{- define "mura-dashboard.datasourceUsername" -}}
{{- if .Values.postgresql.enabled }}
{{- .Values.postgresql.auth.username }}
{{- else }}
{{- .Values.externalDatabase.username }}
{{- end }}
{{- end }}

{{/*
Compute the datasource password.
*/}}
{{- define "mura-dashboard.datasourcePassword" -}}
{{- if .Values.postgresql.enabled }}
{{- .Values.postgresql.auth.password }}
{{- else }}
{{- .Values.externalDatabase.password }}
{{- end }}
{{- end }}
