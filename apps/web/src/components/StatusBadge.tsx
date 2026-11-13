import { cn } from '@/lib/utils';
import { STATUS_ITEM_CONFIG } from '@/features/itens/constants/item.constants';
import type { StatusItem } from '@/features/itens/interfaces/item.interface';

interface StatusBadgeProps {
  status: StatusItem;
  className?: string;
}

export function StatusBadge({ status, className }: StatusBadgeProps) {
  const config = STATUS_ITEM_CONFIG[status];
  return (
    <span
      className={cn(
        'inline-flex items-center gap-[7px] rounded-full py-1 pr-[11px] pl-[9px] text-[12.5px] font-bold whitespace-nowrap before:size-[7px] before:rounded-full before:bg-current',
        config.className,
        className,
      )}
    >
      {config.label}
    </span>
  );
}
