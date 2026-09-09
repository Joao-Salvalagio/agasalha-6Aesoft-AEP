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
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold',
        config.className,
        className,
      )}
    >
      {config.label}
    </span>
  );
}
