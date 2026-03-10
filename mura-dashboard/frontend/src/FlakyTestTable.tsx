import React, {useEffect, useMemo, useState} from 'react';
import {
  Alert,
  Box,
  Checkbox,
  Chip,
  IconButton,
  LinearProgress,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TableSortLabel,
  Toolbar,
  Typography,
} from '@mui/material';
import {FilterList} from '@mui/icons-material';
import {FlakyTestSummary, SortField, SortOrder, TestStatus} from './types';

interface Column {
  id: string;
  label: string;
  align?: 'left' | 'right' | 'center';
  minWidth?: number;
  sortable?: boolean;
  format?: (value: FlakyTestSummary) => React.ReactNode;
}

function severityColor(rate: number): 'error' | 'warning' | 'success' {
  if (rate >= 0.5) return 'error';
  if (rate >= 0.2) return 'warning';
  return 'success';
}

const STATUS_CHIP_CONFIG: Record<TestStatus, { label: string; color: 'warning' | 'error' | 'success' }> = {
  FLAKY: { label: 'Flaky', color: 'warning' },
  FAILED: { label: 'Failed', color: 'error' },
  SUCCESSFUL: { label: 'Successful', color: 'success' },
};

function formatRate(rate: number): string {
  return `${(rate * 100).toFixed(1)}%`;
}

function formatDate(iso: string): string {
  return new Date(iso).toLocaleString();
}

const columns: Column[] = [
  {
    id: 'reportName',
    label: 'Report Name',
    minWidth: 160,
    format: (row) => (
      <Typography variant="body2" sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.8rem', color: '#E8F4F8' }}>
        {row.reportName}
      </Typography>
    ),
  },
  {
    id: 'modulePath',
    label: 'Module Path',
    minWidth: 150,
    format: (row) => (
      <Typography variant="body2" sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.8rem', color: '#E8F4F8' }}>
        {row.modulePath}
      </Typography>
    ),
  },
  {
    id: 'testTaskName',
    label: 'Test Task',
    minWidth: 120,
    format: (row) => (
      <Typography variant="body2" sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.8rem', color: '#E8F4F8' }}>
        {row.testTaskName}
      </Typography>
    ),
  },
  {
    id: 'classname',
    label: 'Test Class',
    minWidth: 250,
    format: (row) => (
      <Typography variant="body2" sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.8rem', color: '#E8F4F8' }}>
        {row.classname}
      </Typography>
    ),
  },
  {
    id: 'name',
    label: 'Test Method',
    minWidth: 180,
    format: (row) => (
      <Typography variant="body2" sx={{ fontFamily: '"JetBrains Mono", monospace', fontSize: '0.8rem', color: '#7EDCE0' }}>
        {row.name}
      </Typography>
    ),
  },
  {
    id: 'testStatus',
    label: 'Status',
    align: 'center',
    minWidth: 110,
    sortable: false,
    format: (row) => {
      const config = STATUS_CHIP_CONFIG[row.testStatus] ?? STATUS_CHIP_CONFIG.FLAKY;
      return (
        <Chip
          label={config.label}
          color={config.color}
          size="small"
          variant="outlined"
        />
      );
    },
  },
  {
    id: 'flakyCount',
    label: 'Flaky Count',
    align: 'right',
    minWidth: 110,
    format: (row) => row.flakyCount,
  },
  {
    id: 'errorCount',
    label: 'Error Count',
    align: 'right',
    minWidth: 110,
    format: (row) => row.errorCount,
  },
  {
    id: 'totalRuns',
    label: 'Total Runs',
    align: 'right',
    minWidth: 100,
    format: (row) => row.totalRuns,
  },
  {
    id: 'flakinessRate',
    label: 'Flakiness Rate',
    align: 'center',
    minWidth: 140,
    format: (row) => (
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1 }}>
        <Chip
          label={formatRate(row.flakinessRate)}
          color={severityColor(row.flakinessRate)}
          size="small"
          variant="filled"
        />
        <Box sx={{ width: 60 }}>
          <LinearProgress
            variant="determinate"
            value={row.flakinessRate * 100}
            color={severityColor(row.flakinessRate)}
            sx={{ height: 6, borderRadius: 3 }}
          />
        </Box>
      </Box>
    ),
  },
  {
    id: 'lastSeen',
    label: 'Last Seen',
    align: 'right',
    minWidth: 170,
    format: (row) => formatDate(row.lastSeen),
  },
];

interface FlakyTestTableProps {
  rows: FlakyTestSummary[];
  loading: boolean;
  error: string | null;
  page: number;
  rowsPerPage: number;
  totalElements: number;
  sortField: SortField;
  sortOrder: SortOrder;
  onSortChange: (field: SortField) => void;
  onPageChange: (event: unknown, newPage: number) => void;
  onRowsPerPageChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

export default function FlakyTestTable({
  rows,
  loading,
  error,
  page,
  rowsPerPage,
  totalElements,
  sortField,
  sortOrder,
  onSortChange,
  onPageChange,
  onRowsPerPageChange,
}: FlakyTestTableProps) {
  // column visibility state with localStorage persistence
  const LOCALSTORAGE_KEY = 'mura.flakyTestTable.visibleColumns';
  const DEFAULT_HIDDEN = new Set(['testTaskName', 'lastSeen']);

  const [visible, setVisible] = useState<Record<string, boolean>>(() => {
    if (typeof window === 'undefined') {
      const temp: Record<string, boolean> = {};
      columns.forEach(c => (temp[c.id] = !DEFAULT_HIDDEN.has(c.id)));
      return temp;
    }
    try {
      const raw = localStorage.getItem(LOCALSTORAGE_KEY);
      if (raw) {
        const parsed = JSON.parse(raw);
        const m: Record<string, boolean> = {};
        columns.forEach(c => {
          m[c.id] = typeof parsed[c.id] === 'boolean' ? parsed[c.id] : !DEFAULT_HIDDEN.has(c.id);
        });
        return m;
      }
    } catch {
      /* ignore parse errors */
    }
    const m: Record<string, boolean> = {};
    columns.forEach(c => (m[c.id] = !DEFAULT_HIDDEN.has(c.id)));
    return m;
  });

  useEffect(() => {
    try {
      localStorage.setItem(LOCALSTORAGE_KEY, JSON.stringify(visible));
    } catch {
      // ignore storage errors
    }
  }, [visible]);

  const displayedColumns = useMemo(() => columns.filter(c => visible[c.id] !== false), [visible]);

  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
  const openColumnMenu = (e: React.MouseEvent<HTMLElement>) => setAnchorEl(e.currentTarget);
  const closeColumnMenu = () => setAnchorEl(null);
  const toggleColumn = (id: string) => setVisible(prev => ({ ...prev, [id]: !(prev[id] ?? true) }));

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <Paper
      variant="outlined"
      sx={{
        borderColor: '#2A4A5C',
        borderRadius: 2,
        overflow: 'hidden',
      }}
    >
      {loading && <LinearProgress color="primary" />}
      <Toolbar sx={{ display: 'flex', justifyContent: 'flex-end', px: 2 }}>
        <IconButton size="small" onClick={openColumnMenu} aria-label="Show / hide columns">
          <FilterList />
        </IconButton>
        <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={closeColumnMenu}>
          {columns.map((col) => (
            <MenuItem key={col.id} onClick={() => toggleColumn(col.id)}>
              <ListItemIcon>
                <Checkbox edge="start" checked={visible[col.id] ?? true} tabIndex={-1} disableRipple />
              </ListItemIcon>
              <ListItemText>{col.label}</ListItemText>
            </MenuItem>
          ))}
        </Menu>
      </Toolbar>
      <TableContainer sx={{ maxHeight: 'calc(100vh - 260px)' }}>
        <Table stickyHeader size="small">
          <TableHead>
              <TableRow>
                {displayedColumns.map((col) => (
                  <TableCell
                    key={col.id}
                    align={col.align ?? 'left'}
                    style={{ minWidth: col.minWidth }}
                    sx={{
                      fontWeight: 'bold',
                      backgroundColor: '#152230',
                      color: '#7EDCE0',
                      borderBottomColor: '#2A4A5C',
                      fontSize: '0.8rem',
                      textTransform: 'uppercase',
                      letterSpacing: '0.04em',
                    }}
                  >
                    {col.sortable === false ? (
                      col.label
                    ) : (
                      <TableSortLabel
                        active={sortField === col.id}
                        direction={sortField === col.id ? sortOrder : 'asc'}
                        onClick={() => onSortChange(col.id as SortField)}
                        sx={{
                          '&.Mui-active': { color: '#2EC4B6' },
                          '& .MuiTableSortLabel-icon': { color: '#2EC4B6 !important' },
                        }}
                      >
                        {col.label}
                      </TableSortLabel>
                    )}
                  </TableCell>
                ))}
              </TableRow>
          </TableHead>
          <TableBody>
            {!loading && rows.length === 0 && (
              <TableRow>
                <TableCell colSpan={Math.max(displayedColumns.length, 1)} align="center" sx={{ py: 4 }}>
                  <Typography color="text.secondary">
                    No flaky tests found in the selected date range.
                  </Typography>
                </TableCell>
              </TableRow>
            )}
            {rows.map((row, idx) => (
              <TableRow
                hover
                key={`${row.reportName}-${row.modulePath}-${row.testTaskName}-${row.classname}-${row.name}-${idx}`}
                sx={{
                  '&:hover': { backgroundColor: 'rgba(46,196,182,0.04)' },
                }}
              >
                {displayedColumns.map((col) => (
                  <TableCell key={col.id} align={col.align ?? 'left'}>
                    {col.format ? col.format(row) : String((row as unknown as Record<string, unknown>)[col.id])}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        onPageChange={onPageChange}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={onRowsPerPageChange}
        rowsPerPageOptions={[10, 20, 50, 100]}
        sx={{ borderTop: '1px solid #2A4A5C' }}
      />
    </Paper>
  );
}
