import {describe, expect, it} from 'vitest';
import {ALL_TEST_STATUSES} from './types';

describe('types', () => {
  it('ALL_TEST_STATUSES contains all three statuses', () => {
    expect(ALL_TEST_STATUSES).toEqual(['FLAKY', 'FAILED', 'SUCCESSFUL']);
  });

  it('ALL_TEST_STATUSES has exactly 3 elements', () => {
    expect(ALL_TEST_STATUSES).toHaveLength(3);
  });
});
