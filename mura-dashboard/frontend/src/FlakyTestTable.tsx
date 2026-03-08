import {
  Alert,
  Box,
  Chip,
  LinearProgress,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TableSortLabel,
  Typography,
} from '@mui/material';
import {FlakyTestSummary, SortField, SortOrder} from './types';

interface Column {
  id: SortField;
  label: string;
  align?: 'left' | 'right' | 'center';
  minWidth?: number;
  format?: (value: FlakyTestSummary) => React.ReactNode;
}

function severityColor(rate: number): 'error' | 'warning' | 'success' {
  if (rate >= 0.5) return 'error';
  if (rate >= 0.2) return 'warning';
  return 'success';
}

function formatRate(rate: number): string {
  return `${(rate * 100).toFixed(1)}%`;
}

function formatDate(iso: string): string {
  return new Date(iso).toLocaleString();
}

const columns: Column[] = [
  {
    id: 'classname',
    label: 'Test Class',
    minWidth: 250,
    format: (row) => (
      <Typography variant="body2" sx={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>
        {row.classname}
      </Typography>
    ),
  },
  {
    id: 'name',
    label: 'Test Method',
    minWidth: 180,
    format: (row) => (
      <Typography variant="body2" sx={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>
        {row.name}
      </Typography>
    ),
  },
  {
    id: 'flakyCount',
    label: 'Flaky Count',
    align: 'right',
    minWidth: 110,
    format: (row) => row.flakyCount,
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
  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <Paper variant="outlined">
      {loading && <LinearProgress />}
      <TableContainer sx={{ maxHeight: 'calc(100vh - 260px)' }}>
        <Table stickyHeader size="small">
          <TableHead>
            <TableRow>
              {columns.map((col) => (
                <TableCell
                  key={col.id}
                  align={col.align ?? 'left'}
                  style={{ minWidth: col.minWidth }}
                  sx={{ fontWeight: 'bold', backgroundColor: 'grey.100' }}
                >
                  <TableSortLabel
                    active={sortField === col.id}
                    direction={sortField === col.id ? sortOrder : 'asc'}
                    onClick={() => onSortChange(col.id)}
                  >
                    {col.label}
                  </TableSortLabel>
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {!loading && rows.length === 0 && (
              <TableRow>
                <TableCell colSpan={columns.length} align="center" sx={{ py: 4 }}>
                  <Typography color="text.secondary">
                    No flaky tests found in the selected date range.
                  </Typography>
                </TableCell>
              </TableRow>
            )}
            {rows.map((row, idx) => (
              <TableRow hover key={`${row.classname}-${row.name}-${idx}`}>
                {columns.map((col) => (
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
      />
    </Paper>
  );
}
