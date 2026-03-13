import {describe, expect, it, vi, beforeEach, afterEach} from 'vitest';
import {fetchFlakyTests} from './api';
import type {FlakyTestPageResponse} from './types';

describe('fetchFlakyTests', () => {
  const mockResponse: FlakyTestPageResponse = {
    content: [
      {
        reportName: 'report-1',
        modulePath: 'module/path',
        testTaskName: 'test',
        classname: 'com.example.TestClass',
        name: 'testMethod',
        totalRuns: 10,
        flakyCount: 3,
        errorCount: 1,
        flakinessRate: 0.3,
        lastSeen: '2025-01-01T12:00:00Z',
        testStatus: 'FLAKY',
      },
    ],
    page: 0,
    size: 20,
    totalElements: 1,
    totalPages: 1,
  };

  beforeEach(() => {
    vi.spyOn(globalThis, 'fetch');
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('sends correct query parameters', async () => {
    vi.mocked(globalThis.fetch).mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockResponse),
    } as Response);

    await fetchFlakyTests({
      from: '2025-01-01T00:00:00Z',
      to: '2025-01-07T23:59:59Z',
      page: 0,
      size: 20,
      sort: 'flakinessRate',
      order: 'desc',
      statuses: ['FLAKY'],
    });

    expect(globalThis.fetch).toHaveBeenCalledOnce();
    const url = vi.mocked(globalThis.fetch).mock.calls[0][0] as string;
    expect(url).toContain('/rapi/flaky-tests?');
    expect(url).toContain('from=2025-01-01T00%3A00%3A00Z');
    expect(url).toContain('to=2025-01-07T23%3A59%3A59Z');
    expect(url).toContain('page=0');
    expect(url).toContain('size=20');
    expect(url).toContain('sort=flakinessRate');
    expect(url).toContain('order=desc');
    expect(url).toContain('statuses=FLAKY');
  });

  it('appends multiple statuses as separate params', async () => {
    vi.mocked(globalThis.fetch).mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockResponse),
    } as Response);

    await fetchFlakyTests({
      from: '2025-01-01T00:00:00Z',
      to: '2025-01-07T23:59:59Z',
      page: 0,
      size: 20,
      sort: 'flakinessRate',
      order: 'desc',
      statuses: ['FLAKY', 'FAILED', 'SUCCESSFUL'],
    });

    const url = vi.mocked(globalThis.fetch).mock.calls[0][0] as string;
    const searchParams = new URLSearchParams(url.split('?')[1]);
    const statusValues = searchParams.getAll('statuses');
    expect(statusValues).toEqual(['FLAKY', 'FAILED', 'SUCCESSFUL']);
  });

  it('returns parsed response on success', async () => {
    vi.mocked(globalThis.fetch).mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockResponse),
    } as Response);

    const result = await fetchFlakyTests({
      from: '2025-01-01T00:00:00Z',
      to: '2025-01-07T23:59:59Z',
      page: 0,
      size: 20,
      sort: 'flakinessRate',
      order: 'desc',
      statuses: ['FLAKY'],
    });

    expect(result).toEqual(mockResponse);
    expect(result.content).toHaveLength(1);
    expect(result.content[0].reportName).toBe('report-1');
  });

  it('throws error on non-ok response', async () => {
    vi.mocked(globalThis.fetch).mockResolvedValue({
      ok: false,
      status: 500,
    } as Response);

    await expect(
      fetchFlakyTests({
        from: '2025-01-01T00:00:00Z',
        to: '2025-01-07T23:59:59Z',
        page: 0,
        size: 20,
        sort: 'flakinessRate',
        order: 'desc',
        statuses: ['FLAKY'],
      }),
    ).rejects.toThrow('Failed to fetch flaky tests: 500');
  });
});
