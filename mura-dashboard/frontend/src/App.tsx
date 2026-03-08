import {useCallback, useEffect, useState} from 'react';
import {AppBar, Box, Container, createTheme, CssBaseline, ThemeProvider, Toolbar, Typography,} from '@mui/material';
import {BugReport} from '@mui/icons-material';
import FlakyTestTable from './FlakyTestTable';
import DateRangeFilter from './DateRangeFilter';
import {fetchFlakyTests} from './api';
import {FlakyTestSummary, SortField, SortOrder} from './types';
import dayjs from 'dayjs';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#1565c0' },
    secondary: { main: '#c62828' },
  },
});

export default function App() {
  const [rows, setRows] = useState<FlakyTestSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [totalElements, setTotalElements] = useState(0);

  const [sortField, setSortField] = useState<SortField>('flakyCount');
  const [sortOrder, setSortOrder] = useState<SortOrder>('desc');

  const [fromDate, setFromDate] = useState<dayjs.Dayjs>(dayjs().subtract(7, 'day'));
  const [toDate, setToDate] = useState<dayjs.Dayjs>(dayjs());

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchFlakyTests({
        from: fromDate.toISOString(),
        to: toDate.toISOString(),
        page,
        size: rowsPerPage,
        sort: sortField,
        order: sortOrder,
      });
      setRows(data.content);
      setTotalElements(data.totalElements);
    } catch (e: unknown) {
      if (e instanceof Error) {
        setError(e.message);
      } else {
        setError('An unknown error occurred');
      }
    } finally {
      setLoading(false);
    }
  }, [fromDate, toDate, page, rowsPerPage, sortField, sortOrder]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleSortChange = (field: SortField) => {
    if (field === sortField) {
      setSortOrder(prev => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortField(field);
      setSortOrder('desc');
    }
    setPage(0);
  };

  const handlePageChange = (_: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleRowsPerPageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleDateChange = (from: dayjs.Dayjs, to: dayjs.Dayjs) => {
    setFromDate(from);
    setToDate(to);
    setPage(0);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AppBar position="static" elevation={1}>
        <Toolbar>
          <BugReport sx={{ mr: 1 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Mura Dashboard — Flaky Tests
          </Typography>
        </Toolbar>
      </AppBar>
      <Container maxWidth="xl" sx={{ mt: 3, mb: 3 }}>
        <DateRangeFilter
          from={fromDate}
          to={toDate}
          onChange={handleDateChange}
        />
        <Box sx={{ mt: 2 }}>
          <FlakyTestTable
            rows={rows}
            loading={loading}
            error={error}
            page={page}
            rowsPerPage={rowsPerPage}
            totalElements={totalElements}
            sortField={sortField}
            sortOrder={sortOrder}
            onSortChange={handleSortChange}
            onPageChange={handlePageChange}
            onRowsPerPageChange={handleRowsPerPageChange}
          />
        </Box>
      </Container>
    </ThemeProvider>
  );
}
