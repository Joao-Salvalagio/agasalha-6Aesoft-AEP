import { cn } from '@/lib/utils';

type VarianteBarra = 'musgo' | 'ambar' | 'terracota';

interface ProgressBarProps {
  valor: number;
  maximo: number;
  rotulo: string;
  variante?: VarianteBarra;
  className?: string;
}

const COR: Record<VarianteBarra, string> = {
  musgo: 'bg-moss',
  ambar: 'bg-amber',
  terracota: 'bg-primary',
};

export function ProgressBar({ valor, maximo, rotulo, variante = 'musgo', className }: ProgressBarProps) {
  const percentual = maximo > 0 ? Math.min(100, Math.round((valor / maximo) * 100)) : 0;

  return (
    <div
      role="progressbar"
      aria-label={rotulo}
      aria-valuemin={0}
      aria-valuemax={maximo}
      aria-valuenow={valor}
      aria-valuetext={`${valor} de ${maximo}`}
      className={cn('h-2.5 overflow-hidden rounded-full bg-secondary ring-1 ring-border ring-inset', className)}
    >
      <span
        className={cn(
          'block h-full rounded-full bg-[repeating-linear-gradient(135deg,oklch(1_0_0/0.2)_0_4px,transparent_4px_9px)]',
          COR[variante],
        )}
        style={{ width: `${percentual}%`, minWidth: percentual === 0 ? 0 : 4 }}
      />
    </div>
  );
}
