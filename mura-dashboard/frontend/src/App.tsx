import {useCallback, useEffect, useState} from 'react';
import {AppBar, Box, Container, createTheme, CssBaseline, ThemeProvider, Toolbar, Typography,} from '@mui/material';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import FlakyTestTable from './FlakyTestTable';
import DateRangeFilter from './DateRangeFilter';
import {fetchFlakyTests} from './api';
import {FlakyTestSummary, SortField, SortOrder} from './types';
import dayjs from 'dayjs';

function useBrowserLocale() {
  const [locale, setLocale] = useState('en');
  useEffect(() => {
    const lang = navigator.language?.split('-')[0]?.toLowerCase() || 'en';
    if (lang === 'en') return;
    // Explicit lazy imports so Vite can statically resolve each chunk
    const localeLoaders: Record<string, () => Promise<unknown>> = {
      de: () => import('dayjs/locale/de'),
      fr: () => import('dayjs/locale/fr'),
      es: () => import('dayjs/locale/es'),
      it: () => import('dayjs/locale/it'),
      pt: () => import('dayjs/locale/pt'),
      nl: () => import('dayjs/locale/nl'),
      ja: () => import('dayjs/locale/ja'),
      ko: () => import('dayjs/locale/ko'),
      zh: () => import('dayjs/locale/zh-cn'),
      ru: () => import('dayjs/locale/ru'),
      pl: () => import('dayjs/locale/pl'),
      sv: () => import('dayjs/locale/sv'),
      da: () => import('dayjs/locale/da'),
      fi: () => import('dayjs/locale/fi'),
      nb: () => import('dayjs/locale/nb'),
      tr: () => import('dayjs/locale/tr'),
      cs: () => import('dayjs/locale/cs'),
      uk: () => import('dayjs/locale/uk'),
      hu: () => import('dayjs/locale/hu'),
      ro: () => import('dayjs/locale/ro'),
    };
    const loader = localeLoaders[lang];
    if (!loader) return;
    loader()
      .then(() => setLocale(lang))
      .catch(() => { /* fallback to en */ });
  }, []);
  return locale;
}

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: { main: '#2EC4B6' },       // teal from logo zigzag
    secondary: { main: '#7EDCE0' },     // light mint from logo terminal
    error: { main: '#E55353' },          // red X from logo
    warning: { main: '#F0A500' },        // warm amber for medium flakiness
    success: { main: '#2EC4B6' },        // teal for low flakiness
    background: {
      default: '#0F1C26',               // deep navy, darker than logo bg
      paper: '#1B2D3A',                 // logo background navy
    },
    text: {
      primary: '#E8F4F8',              // off-white with cool tint
      secondary: '#8BA8B8',            // muted slate
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          backgroundImage: 'radial-gradient(ellipse at 50% 0%, #1B2D3A 0%, #0F1C26 70%)',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
          borderColor: '#2A4A5C',
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        containedPrimary: {
          color: '#0F1C26',
          fontWeight: 600,
          '&:hover': {
            backgroundColor: '#3DD5C7',
          },
        },
        outlinedPrimary: {
          borderColor: '#2A4A5C',
          color: '#7EDCE0',
          '&:hover': {
            borderColor: '#2EC4B6',
            backgroundColor: 'rgba(46,196,182,0.08)',
          },
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderBottomColor: '#1E3A4A',
        },
      },
    },
    MuiLinearProgress: {
      styleOverrides: {
        root: {
          backgroundColor: 'rgba(46,196,182,0.12)',
        },
      },
    },
  },
});

export default function App() {
  const locale = useBrowserLocale();
  const [rows, setRows] = useState<FlakyTestSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [totalElements, setTotalElements] = useState(0);

  const [sortField, setSortField] = useState<SortField>('flakinessRate');
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
    setRowsPerPage(Number.parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleDateChange = (from: dayjs.Dayjs, to: dayjs.Dayjs) => {
    setFromDate(from);
    setToDate(to);
    setPage(0);
  };

  return (
    <ThemeProvider theme={theme}>
      <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale={locale}>
      <CssBaseline />
      <AppBar
        position="static"
        elevation={0}
        sx={{
          background: 'linear-gradient(135deg, #1B2D3A 0%, #0F1C26 100%)',
          borderBottom: '1px solid #2A4A5C',
        }}
      >
        <Toolbar>
          <Box
            component="img"
            src="/logo.png"
            alt="Mura Dashboard Logo"
            sx={{
              height: 40,
              width: 40,
              mr: 1.5,
              borderRadius: '50%',
              boxShadow: '0 0 12px rgba(46,196,182,0.3)',
            }}
          />
          <Typography
            variant="h6"
            component="div"
            sx={{
              flexGrow: 1,
              fontWeight: 700,
              letterSpacing: '-0.02em',
              background: 'linear-gradient(90deg, #2EC4B6, #7EDCE0)',
              backgroundClip: 'text',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}
          >
            Mura Dashboard
          </Typography>
          <Typography
            variant="body2"
            sx={{ color: '#8BA8B8', fontWeight: 500, letterSpacing: '0.05em', textTransform: 'uppercase', fontSize: '0.75rem' }}
          >
            Flaky Tests
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
      </LocalizationProvider>
    </ThemeProvider>
  );
}
