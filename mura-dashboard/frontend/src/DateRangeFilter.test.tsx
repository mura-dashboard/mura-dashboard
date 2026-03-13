import {describe, expect, it, vi} from 'vitest';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
import DateRangeFilter from './DateRangeFilter';

function renderWithLocalization(ui: React.ReactElement) {
  return render(
    <LocalizationProvider dateAdapter={AdapterDayjs}>{ui}</LocalizationProvider>,
  );
}

describe('DateRangeFilter', () => {
  const defaultProps = {
    from: dayjs('2025-01-01'),
    to: dayjs('2025-01-07'),
    onChange: vi.fn(),
  };

  it('renders From and To date pickers', () => {
    renderWithLocalization(<DateRangeFilter {...defaultProps} />);
    // MUI DatePicker renders the label in multiple places, use getAllBy
    expect(screen.getAllByText('From').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('To').length).toBeGreaterThanOrEqual(1);
  });

  it('renders the Apply button', () => {
    renderWithLocalization(<DateRangeFilter {...defaultProps} />);
    expect(screen.getByRole('button', { name: 'Apply' })).toBeInTheDocument();
  });

  it('renders preset buttons', () => {
    renderWithLocalization(<DateRangeFilter {...defaultProps} />);
    expect(screen.getByRole('button', { name: 'Last 7d' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Last 14d' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Last 30d' })).toBeInTheDocument();
  });

  it('calls onChange when Apply is clicked', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    renderWithLocalization(
      <DateRangeFilter from={dayjs('2025-01-01')} to={dayjs('2025-01-07')} onChange={onChange} />,
    );

    await user.click(screen.getByRole('button', { name: 'Apply' }));
    expect(onChange).toHaveBeenCalledOnce();

    const [from, to] = onChange.mock.calls[0];
    expect(from.format('YYYY-MM-DD')).toBe('2025-01-01');
    expect(to.format('YYYY-MM-DD')).toBe('2025-01-07');
  });

  it('calls onChange immediately when preset button is clicked', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    renderWithLocalization(
      <DateRangeFilter from={dayjs('2025-01-01')} to={dayjs('2025-01-07')} onChange={onChange} />,
    );

    await user.click(screen.getByRole('button', { name: 'Last 7d' }));
    expect(onChange).toHaveBeenCalledOnce();

    const [from, to] = onChange.mock.calls[0];
    // The preset creates a "from" date 7 days before "to"
    expect(to.diff(from, 'day')).toBe(7);
  });

  it('calls onChange for Last 30d preset', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    renderWithLocalization(
      <DateRangeFilter from={dayjs('2025-01-01')} to={dayjs('2025-01-07')} onChange={onChange} />,
    );

    await user.click(screen.getByRole('button', { name: 'Last 30d' }));
    expect(onChange).toHaveBeenCalledOnce();

    const [from, to] = onChange.mock.calls[0];
    expect(to.diff(from, 'day')).toBe(30);
  });
});
