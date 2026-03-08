import {Button, Stack} from '@mui/material';
import {DatePicker} from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';
import {useState} from 'react';

interface DateRangeFilterProps {
  from: dayjs.Dayjs;
  to: dayjs.Dayjs;
  onChange: (from: dayjs.Dayjs, to: dayjs.Dayjs) => void;
}

export default function DateRangeFilter({ from, to, onChange }: DateRangeFilterProps) {
  const [localFrom, setLocalFrom] = useState<dayjs.Dayjs | null>(from);
  const [localTo, setLocalTo] = useState<dayjs.Dayjs | null>(to);

  const handleApply = () => {
    if (localFrom && localTo) {
      onChange(localFrom.startOf('day'), localTo.endOf('day'));
    }
  };

  const handlePreset = (days: number) => {
    const newTo = dayjs();
    const newFrom = newTo.subtract(days, 'day');
    setLocalFrom(newFrom);
    setLocalTo(newTo);
    onChange(newFrom.startOf('day'), newTo.endOf('day'));
  };

  const dateFieldSx = {
    width: 170,
    '& .MuiOutlinedInput-root': {
      '& fieldset': { borderColor: '#2A4A5C' },
      '&:hover fieldset': { borderColor: '#2EC4B6' },
    },
  };

  return (
    <>
      <DatePicker
        label="From"
        value={localFrom}
        onChange={(v) => setLocalFrom(v)}
        slotProps={{ textField: { size: 'small' } }}
        sx={dateFieldSx}
      />
      <DatePicker
        label="To"
        value={localTo}
        onChange={(v) => setLocalTo(v)}
        slotProps={{ textField: { size: 'small' } }}
        sx={dateFieldSx}
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
    </>
  );
}
