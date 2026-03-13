import {describe, expect, it, vi, beforeEach, afterEach} from 'vitest';
import {render, screen, waitFor} from '@testing-library/react';
import App from './App';
import type {FlakyTestPageResponse} from './types';

const mockResponse: FlakyTestPageResponse = {
  content: [
    {
      reportName: 'ci-report',
      modulePath: ':app',
      testTaskName: 'test',
      classname: 'com.example.FlakyTest',
      name: 'sometimesFails',
      totalRuns: 10,
      flakyCount: 4,
      errorCount: 2,
      flakinessRate: 0.4,
      lastSeen: '2025-06-01T10:00:00Z',
      testStatus: 'FLAKY',
    },
  ],
  page: 0,
  size: 20,
  totalElements: 1,
  totalPages: 1,
};

describe('App', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockResponse),
    } as Response);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders the app header', async () => {
    render(<App />);
    expect(screen.getByText('Mura Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Flaky Tests')).toBeInTheDocument();
  });

  it('renders the logo image', () => {
    render(<App />);
    const logo = screen.getByAltText('Mura Dashboard Logo');
    expect(logo).toBeInTheDocument();
    expect(logo).toHaveAttribute('src', '/logo.png');
  });

  it('renders filter components', () => {
    render(<App />);
    // MUI DatePicker renders the label in multiple places, use getAllBy
    expect(screen.getAllByText('From').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('To').length).toBeGreaterThanOrEqual(1);
    expect(screen.getByLabelText('Test Status')).toBeInTheDocument();
  });

  it('renders preset date buttons', () => {
    render(<App />);
    expect(screen.getByRole('button', { name: 'Last 7d' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Last 14d' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Last 30d' })).toBeInTheDocument();
  });

  it('fetches and displays data on mount', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText('com.example.FlakyTest')).toBeInTheDocument();
    });

    expect(screen.getByText('sometimesFails')).toBeInTheDocument();
    expect(screen.getByText('ci-report')).toBeInTheDocument();
  });

  it('calls fetch with correct endpoint', async () => {
    render(<App />);

    await waitFor(() => {
      expect(globalThis.fetch).toHaveBeenCalled();
    });

    const url = vi.mocked(globalThis.fetch).mock.calls[0][0] as string;
    expect(url).toContain('/rapi/flaky-tests?');
    expect(url).toContain('statuses=FLAKY');
  });

  it('displays error message when API fails', async () => {
    vi.mocked(globalThis.fetch).mockResolvedValue({
      ok: false,
      status: 500,
    } as Response);

    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Failed to fetch flaky tests: 500/)).toBeInTheDocument();
    });
  });

  it('shows loading indicator initially', () => {
    // Make fetch hang forever to keep loading state
    vi.mocked(globalThis.fetch).mockReturnValue(new Promise(() => {}));
    render(<App />);
    expect(screen.getByRole('progressbar')).toBeInTheDocument();
  });
});
