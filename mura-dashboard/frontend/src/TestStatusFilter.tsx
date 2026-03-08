import {
    Box,
    Checkbox,
    Chip,
    FormControl,
    InputLabel,
    ListItemText,
    MenuItem,
    OutlinedInput,
    Select,
    SelectChangeEvent,
} from '@mui/material';
import {ALL_TEST_STATUSES, TestStatus} from './types';

interface TestStatusFilterProps {
  statuses: TestStatus[];
  onChange: (statuses: TestStatus[]) => void;
}

const STATUS_COLORS: Record<TestStatus, 'warning' | 'error' | 'success'> = {
  FLAKY: 'warning',
  FAILED: 'error',
  SUCCESSFUL: 'success',
};

const STATUS_LABELS: Record<TestStatus, string> = {
  FLAKY: 'Flaky',
  FAILED: 'Failed',
  SUCCESSFUL: 'Successful',
};

export default function TestStatusFilter({ statuses, onChange }: TestStatusFilterProps) {
  const handleChange = (event: SelectChangeEvent<string[]>) => {
    const value = event.target.value;
    const selected = (typeof value === 'string' ? value.split(',') : value) as TestStatus[];
    if (selected.length > 0) {
      onChange(selected);
    }
  };

  return (
    <FormControl size="small" sx={{ minWidth: 220 }}>
      <InputLabel
        id="test-status-filter-label"
        sx={{
          color: '#8BA8B8',
          '&.Mui-focused': { color: '#2EC4B6' },
        }}
      >
        Test Status
      </InputLabel>
      <Select
        labelId="test-status-filter-label"
        multiple
        value={statuses}
        onChange={handleChange}
        input={<OutlinedInput label="Test Status" />}
        renderValue={(selected) => (
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
            {(selected as TestStatus[]).map((status) => (
              <Chip
                key={status}
                label={STATUS_LABELS[status]}
                color={STATUS_COLORS[status]}
                size="small"
                variant="filled"
                sx={{ height: 22, fontSize: '0.75rem' }}
              />
            ))}
          </Box>
        )}
        sx={{
          '& .MuiOutlinedInput-notchedOutline': { borderColor: '#2A4A5C' },
          '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: '#2EC4B6' },
          '&.Mui-focused .MuiOutlinedInput-notchedOutline': { borderColor: '#2EC4B6' },
        }}
        MenuProps={{
          PaperProps: {
            sx: {
              backgroundColor: '#1B2D3A',
              borderColor: '#2A4A5C',
              '& .MuiMenuItem-root': {
                '&:hover': { backgroundColor: 'rgba(46,196,182,0.08)' },
              },
            },
          },
        }}
      >
        {ALL_TEST_STATUSES.map((status) => (
          <MenuItem key={status} value={status}>
            <Checkbox
              checked={statuses.includes(status)}
              sx={{
                color: '#2A4A5C',
                '&.Mui-checked': { color: '#2EC4B6' },
              }}
            />
            <ListItemText primary={STATUS_LABELS[status]} />
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
}
