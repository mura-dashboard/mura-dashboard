import {describe, expect, it, vi} from 'vitest';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import TestStatusFilter from './TestStatusFilter';

describe('TestStatusFilter', () => {
  it('renders the Test Status label', () => {
    render(<TestStatusFilter statuses={['FLAKY']} onChange={vi.fn()} />);
    expect(screen.getByLabelText('Test Status')).toBeInTheDocument();
  });

  it('renders selected status as a chip', () => {
    render(<TestStatusFilter statuses={['FLAKY']} onChange={vi.fn()} />);
    expect(screen.getByText('Flaky')).toBeInTheDocument();
  });

  it('renders multiple selected statuses as chips', () => {
    render(
      <TestStatusFilter statuses={['FLAKY', 'FAILED', 'SUCCESSFUL']} onChange={vi.fn()} />,
    );
    expect(screen.getByText('Flaky')).toBeInTheDocument();
    expect(screen.getByText('Failed')).toBeInTheDocument();
    expect(screen.getByText('Successful')).toBeInTheDocument();
  });

  it('calls onChange when a status is selected', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    render(<TestStatusFilter statuses={['FLAKY']} onChange={onChange} />);

    // Open the select dropdown
    const selectButton = screen.getByRole('combobox');
    await user.click(selectButton);

    // Click on "Failed" option
    const failedOption = await screen.findByText('Failed', { selector: '[role="option"] *' });
    await user.click(failedOption);

    expect(onChange).toHaveBeenCalled();
    // The callback should include both FLAKY and FAILED
    const calledWith = onChange.mock.calls[0][0];
    expect(calledWith).toContain('FLAKY');
    expect(calledWith).toContain('FAILED');
  });

  it('shows all three status options in dropdown', async () => {
    const user = userEvent.setup();
    render(<TestStatusFilter statuses={['FLAKY']} onChange={vi.fn()} />);

    const selectButton = screen.getByRole('combobox');
    await user.click(selectButton);

    const options = await screen.findAllByRole('option');
    expect(options).toHaveLength(3);
  });
});
