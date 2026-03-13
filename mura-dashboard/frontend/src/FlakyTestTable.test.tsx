import {describe, expect, it, vi, beforeEach} from 'vitest';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import FlakyTestTable from './FlakyTestTable';
import type {FlakyTestSummary, SortField, SortOrder} from './types';

const createRow = (overrides: Partial<FlakyTestSummary> = {}): FlakyTestSummary => ({
  reportName: 'test-report',
  modulePath: 'module/path',
  testTaskName: 'test',
  classname: 'com.example.TestClass',
  name: 'shouldPass',
  totalRuns: 10,
  flakyCount: 3,
  errorCount: 1,
  flakinessRate: 0.3,
  lastSeen: '2025-01-01T12:00:00Z',
  testStatus: 'FLAKY',
  ...overrides,
});

interface DefaultProps {
  rows: FlakyTestSummary[];
  loading: boolean;
  error: string | null;
  page: number;
  rowsPerPage: number;
  totalElements: number;
  sortField: SortField;
  sortOrder: SortOrder;
  onSortChange: ReturnType<typeof vi.fn>;
  onPageChange: ReturnType<typeof vi.fn>;
  onRowsPerPageChange: ReturnType<typeof vi.fn>;
}

const defaultProps: DefaultProps = {
  rows: [],
  loading: false,
  error: null,
  page: 0,
  rowsPerPage: 20,
  totalElements: 0,
  sortField: 'flakinessRate',
  sortOrder: 'desc',
  onSortChange: vi.fn(),
  onPageChange: vi.fn(),
  onRowsPerPageChange: vi.fn(),
};

beforeEach(() => {
  localStorage.clear();
});

describe('FlakyTestTable', () => {
  describe('empty state', () => {
    it('shows empty message when no rows', () => {
      render(<FlakyTestTable {...defaultProps} />);
      expect(screen.getByText('No flaky tests found in the selected date range.')).toBeInTheDocument();
    });
  });

  describe('error state', () => {
    it('displays error alert when error prop is set', () => {
      render(<FlakyTestTable {...defaultProps} error="Something went wrong" />);
      expect(screen.getByText('Something went wrong')).toBeInTheDocument();
      expect(screen.getByRole('alert')).toBeInTheDocument();
    });

    it('does not render the table when error is present', () => {
      render(<FlakyTestTable {...defaultProps} error="Error" />);
      expect(screen.queryByRole('table')).not.toBeInTheDocument();
    });
  });

  describe('loading state', () => {
    it('renders progressbar when loading', () => {
      render(<FlakyTestTable {...defaultProps} loading={true} />);
      expect(screen.getByRole('progressbar')).toBeInTheDocument();
    });

    it('does not render progressbar when not loading', () => {
      render(<FlakyTestTable {...defaultProps} loading={false} />);
      expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
    });
  });

  describe('data rendering', () => {
    it('renders row data correctly', () => {
      const row = createRow({
        reportName: 'my-report',
        classname: 'com.example.MyTest',
        name: 'shouldWork',
        flakyCount: 5,
        errorCount: 2,
        totalRuns: 15,
      });
      render(<FlakyTestTable {...defaultProps} rows={[row]} totalElements={1} />);

      expect(screen.getByText('my-report')).toBeInTheDocument();
      expect(screen.getByText('com.example.MyTest')).toBeInTheDocument();
      expect(screen.getByText('shouldWork')).toBeInTheDocument();
      expect(screen.getByText('5')).toBeInTheDocument();
      expect(screen.getByText('2')).toBeInTheDocument();
      expect(screen.getByText('15')).toBeInTheDocument();
    });

    it('renders flakiness rate as percentage', () => {
      const row = createRow({ flakinessRate: 0.456 });
      render(<FlakyTestTable {...defaultProps} rows={[row]} totalElements={1} />);
      expect(screen.getByText('45.6%')).toBeInTheDocument();
    });

    it('renders status chip for FLAKY test', () => {
      const row = createRow({ testStatus: 'FLAKY' });
      render(<FlakyTestTable {...defaultProps} rows={[row]} totalElements={1} />);
      // The status column should show a "Flaky" chip
      expect(screen.getByText('Flaky')).toBeInTheDocument();
    });

    it('renders status chip for FAILED test', () => {
      const row = createRow({ testStatus: 'FAILED' });
      render(<FlakyTestTable {...defaultProps} rows={[row]} totalElements={1} />);
      expect(screen.getByText('Failed')).toBeInTheDocument();
    });

    it('renders status chip for SUCCESSFUL test', () => {
      const row = createRow({ testStatus: 'SUCCESSFUL' });
      render(<FlakyTestTable {...defaultProps} rows={[row]} totalElements={1} />);
      expect(screen.getByText('Successful')).toBeInTheDocument();
    });

    it('renders multiple rows', () => {
      const rows = [
        createRow({ name: 'test1' }),
        createRow({ name: 'test2' }),
        createRow({ name: 'test3' }),
      ];
      render(<FlakyTestTable {...defaultProps} rows={rows} totalElements={3} />);
      expect(screen.getByText('test1')).toBeInTheDocument();
      expect(screen.getByText('test2')).toBeInTheDocument();
      expect(screen.getByText('test3')).toBeInTheDocument();
    });
  });

  describe('sorting', () => {
    it('renders sort labels for sortable columns', () => {
      render(<FlakyTestTable {...defaultProps} />);
      // "Report Name" should be sortable (has a button)
      const reportNameHeader = screen.getByText('Report Name');
      expect(reportNameHeader.closest('span')).toHaveClass('MuiTableSortLabel-root');
    });

    it('shows active sort on the current sort field', () => {
      render(<FlakyTestTable {...defaultProps} sortField="flakinessRate" sortOrder="desc" />);
      const label = screen.getByText('Flakiness Rate');
      expect(label.closest('.MuiTableSortLabel-root')).toHaveClass('Mui-active');
    });

    it('calls onSortChange when a sortable column header is clicked', async () => {
      const user = userEvent.setup();
      const onSortChange = vi.fn();
      render(<FlakyTestTable {...defaultProps} onSortChange={onSortChange} />);

      await user.click(screen.getByText('Report Name'));
      expect(onSortChange).toHaveBeenCalledWith('reportName');
    });

    it('Status column is not sortable', () => {
      render(<FlakyTestTable {...defaultProps} />);
      const statusHeader = screen.getByText('Status');
      // Status should not be wrapped in a sort label
      expect(statusHeader.closest('.MuiTableSortLabel-root')).toBeNull();
    });
  });

  describe('pagination', () => {
    it('displays pagination controls', () => {
      render(<FlakyTestTable {...defaultProps} totalElements={100} page={0} rowsPerPage={20} />);
      // MUI TablePagination renders "1-20 of 100" or similar
      expect(screen.getByText(/1–20 of 100/)).toBeInTheDocument();
    });

    it('calls onPageChange when navigating', async () => {
      const user = userEvent.setup();
      const onPageChange = vi.fn();
      render(
        <FlakyTestTable
          {...defaultProps}
          totalElements={100}
          page={0}
          rowsPerPage={20}
          onPageChange={onPageChange}
        />,
      );

      const nextButton = screen.getByLabelText('Go to next page');
      await user.click(nextButton);
      expect(onPageChange).toHaveBeenCalled();
    });
  });

  describe('column visibility', () => {
    it('has a column visibility toggle button', () => {
      render(<FlakyTestTable {...defaultProps} />);
      expect(screen.getByLabelText('Show / hide columns')).toBeInTheDocument();
    });

    it('opens column menu on toggle button click', async () => {
      const user = userEvent.setup();
      render(<FlakyTestTable {...defaultProps} />);

      await user.click(screen.getByLabelText('Show / hide columns'));

      // All 11 columns should be listed in the menu
      const menu = screen.getByRole('menu');
      const menuItems = within(menu).getAllByRole('menuitem');
      expect(menuItems).toHaveLength(11);
    });

    it('hides testTaskName and lastSeen columns by default', () => {
      render(<FlakyTestTable {...defaultProps} />);
      // These columns should be hidden by default
      const columnHeaders = screen.getAllByRole('columnheader');
      const headerTexts = columnHeaders.map(h => h.textContent);
      expect(headerTexts.join(' ')).not.toContain('Test Task');
      expect(headerTexts.join(' ')).not.toContain('Last Seen');
    });

    it('shows visible columns by default', () => {
      render(<FlakyTestTable {...defaultProps} />);
      const columnHeaders = screen.getAllByRole('columnheader');
      const headerTexts = columnHeaders.map(h => h.textContent);
      expect(headerTexts.join(' ')).toContain('Report Name');
      expect(headerTexts.join(' ')).toContain('Test Class');
      expect(headerTexts.join(' ')).toContain('Flakiness Rate');
    });

    it('persists column visibility to localStorage', async () => {
      const user = userEvent.setup();
      render(<FlakyTestTable {...defaultProps} />);

      // Open column menu and toggle a column
      await user.click(screen.getByLabelText('Show / hide columns'));
      const menu = screen.getByRole('menu');
      const reportNameItem = within(menu).getByText('Report Name').closest('[role="menuitem"]')!;
      await user.click(reportNameItem);

      // Check localStorage was updated
      const stored = localStorage.getItem('mura.flakyTestTable.visibleColumns');
      expect(stored).not.toBeNull();
      const parsed = JSON.parse(stored!);
      expect(parsed.reportName).toBe(false);
    });

    it('restores column visibility from localStorage', () => {
      localStorage.setItem(
        'mura.flakyTestTable.visibleColumns',
        JSON.stringify({
          reportName: false,
          modulePath: true,
          testTaskName: true,
          classname: true,
          name: true,
          testStatus: true,
          flakyCount: true,
          errorCount: true,
          totalRuns: true,
          flakinessRate: true,
          lastSeen: true,
        }),
      );

      render(<FlakyTestTable {...defaultProps} />);
      const columnHeaders = screen.getAllByRole('columnheader');
      const headerTexts = columnHeaders.map(h => h.textContent);
      // Report Name should be hidden
      expect(headerTexts.join(' ')).not.toContain('Report Name');
      // Test Task and Last Seen should be visible (overridden from default)
      expect(headerTexts.join(' ')).toContain('Test Task');
      expect(headerTexts.join(' ')).toContain('Last Seen');
    });
  });
});
