export interface FlakyTestSummary {
  classname: string;
  name: string;
  totalRuns: number;
  flakyCount: number;
  flakinessRate: number;
  lastSeen: string;
}

export interface FlakyTestPageResponse {
  content: FlakyTestSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export type SortField = 'flakyCount' | 'flakinessRate' | 'totalRuns' | 'lastSeen' | 'classname' | 'name';
export type SortOrder = 'asc' | 'desc';
