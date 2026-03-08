import {Button, Paper, Stack, TextField, Typography} from '@mui/material';
import {FilterList} from '@mui/icons-material';
import dayjs from 'dayjs';
import {useState} from 'react';

interface DateRangeFilterProps {
  from: dayjs.Dayjs;
  to: dayjs.Dayjs;
  onChange: (from: dayjs.Dayjs, to: dayjs.Dayjs) => void;
}

export default function DateRangeFilter({ from, to, onChange }: DateRangeFilterProps) {
  const [localFrom, setLocalFrom] = useState(from.format('YYYY-MM-DD'));
  const [localTo, setLocalTo] = useState(to.format('YYYY-MM-DD'));

  const handleApply = () => {
    onChange(dayjs(localFrom).startOf('day'), dayjs(localTo).endOf('day'));
  };

  const handlePreset = (days: number) => {
    const newTo = dayjs();
    const newFrom = newTo.subtract(days, 'day');
    setLocalFrom(newFrom.format('YYYY-MM-DD'));
    setLocalTo(newTo.format('YYYY-MM-DD'));
    onChange(newFrom.startOf('day'), newTo.endOf('day'));
  };

  return (
    <Paper
      variant="outlined"
      sx={{
        display: 'flex',
        alignItems: 'center',
        gap: 2,
        flexWrap: 'wrap',
        p: 2,
        borderColor: '#2A4A5C',
        borderRadius: 2,
      }}
    >
      <FilterList sx={{ color: '#2EC4B6' }} />
      <Typography variant="body2" sx={{ color: '#8BA8B8', fontWeight: 600, textTransform: 'uppercase', fontSize: '0.75rem', letterSpacing: '0.05em' }}>
        Date Range
      </Typography>
      <TextField
        label="From"
        type="date"
        size="small"
        value={localFrom}
        onChange={e => setLocalFrom(e.target.value)}
        slotProps={{ inputLabel: { shrink: true } }}
        sx={{
          '& .MuiOutlinedInput-root': {
            '& fieldset': { borderColor: '#2A4A5C' },
            '&:hover fieldset': { borderColor: '#2EC4B6' },
          },
        }}
      />
      <TextField
        label="To"
        type="date"
        size="small"
        value={localTo}
        onChange={e => setLocalTo(e.target.value)}
        slotProps={{ inputLabel: { shrink: true } }}
        sx={{
          '& .MuiOutlinedInput-root': {
            '& fieldset': { borderColor: '#2A4A5C' },
            '&:hover fieldset': { borderColor: '#2EC4B6' },
          },
        }}
      />
      <Button variant="contained" size="small" onClick={handleApply}>
        Apply
      </Button>
      <Stack direction="row" spacing={1}>
        <Button variant="outlined" size="small" onClick={() => handlePreset(7)}>
          Last 7d
        </Button>
        <Button variant="outlined" size="small" onClick={() => handlePreset(14)}>
          Last 14d
        </Button>
        <Button variant="outlined" size="small" onClick={() => handlePreset(30)}>
          Last 30d
        </Button>
      </Stack>
    </Paper>
  );
}
