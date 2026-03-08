import {FlakyTestPageResponse, SortField, SortOrder, TestStatus} from './types';

interface FetchFlakyTestsParams {
  from: string;
  to: string;
  page: number;
  size: number;
  sort: SortField;
  order: SortOrder;
  statuses: TestStatus[];
}

export async function fetchFlakyTests(params: FetchFlakyTestsParams): Promise<FlakyTestPageResponse> {
  const searchParams = new URLSearchParams({
    from: params.from,
    to: params.to,
    page: params.page.toString(),
    size: params.size.toString(),
    sort: params.sort,
    order: params.order,
  });

  for (const status of params.statuses) {
    searchParams.append('statuses', status);
  }

  const response = await fetch(`/rapi/flaky-tests?${searchParams.toString()}`);

  if (!response.ok) {
    throw new Error(`Failed to fetch flaky tests: ${response.status}`);
  }

  return response.json();
}
