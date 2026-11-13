import { BedDouble, Footprints, HelpCircle, Layers, Shirt, Wind, type LucideIcon } from 'lucide-react';
import { cn } from '@/lib/utils';
import type { TipoPeca } from '../interfaces/item.interface';

const ICONE_PECA: Record<TipoPeca, LucideIcon> = {
  CASACO: Shirt,
  BLUSA: Wind,
  CALCA: Layers,
  COBERTOR: BedDouble,
  MEIA: Footprints,
  OUTRO: HelpCircle,
};

type TamanhoIcone = 'sm' | 'md' | 'lg';

const CLASSE_BLOCO: Record<TamanhoIcone, string> = {
  sm: 'size-10 rounded-[10px]',
  md: 'size-[60px] rounded-xl',
  lg: 'size-[104px] rounded-2xl',
};

const CLASSE_ICONE: Record<TamanhoIcone, string> = {
  sm: 'size-5',
  md: 'size-7',
  lg: 'size-12',
};

interface PecaIconeProps {
  tipoPeca: TipoPeca;
  tamanho?: TamanhoIcone;
  className?: string;
}

export function PecaIcone({ tipoPeca, tamanho = 'md', className }: PecaIconeProps) {
  const Icone = ICONE_PECA[tipoPeca];
  return (
    <div
      aria-hidden="true"
      className={cn(
        'grid flex-none place-items-center bg-secondary text-primary textura-quente',
        CLASSE_BLOCO[tamanho],
        className,
      )}
    >
      <Icone className={CLASSE_ICONE[tamanho]} />
    </div>
  );
}
