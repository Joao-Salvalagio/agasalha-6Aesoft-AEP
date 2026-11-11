import { Link } from 'react-router-dom';
import { Button, buttonVariants } from '@/components/ui/button';
import { StatusBadge } from '@/components/StatusBadge';
import { cn } from '@/lib/utils';
import { GENERO_LABEL, TIPO_PECA_LABEL } from '../constants/item.constants';
import type { ItemSummaryResponse } from '../interfaces/item.interface';
import { PecaIcone } from './PecaIcone';

interface ItemLinhaProps {
  item: ItemSummaryResponse;
  onReservar: (id: string) => void;
  reservando: boolean;
}

export function ItemLinha({ item, onReservar, reservando }: ItemLinhaProps) {
  const descricao = `${TIPO_PECA_LABEL[item.tipoPeca]} ${item.tamanho} · ${GENERO_LABEL[item.genero]}`;

  return (
    <li className="grid grid-cols-[52px_minmax(0,1fr)] items-center gap-3.5 rounded-xl bg-card p-3.5 shadow-sm ring-1 ring-border transition-shadow hover:shadow-md sm:grid-cols-[60px_minmax(0,1fr)_auto] sm:gap-[18px] sm:py-3.5 sm:pr-[18px] sm:pl-3.5">
      <PecaIcone tipoPeca={item.tipoPeca} />
      <div className="min-w-0">
        <h3 className="text-[16.5px] font-bold tracking-tight">
          <Link
            to={`/itens/${item.id}`}
            className="rounded-sm outline-none hover:underline hover:decoration-primary focus-visible:ring-3 focus-visible:ring-ring/50"
          >
            {TIPO_PECA_LABEL[item.tipoPeca]}
          </Link>
        </h3>
        <p className="mt-1.5 flex flex-wrap gap-1.5">
          <span className="rounded-[7px] bg-secondary px-2 py-0.5 text-[12.5px] font-semibold">
            Tamanho {item.tamanho}
          </span>
          <span className="rounded-[7px] bg-secondary px-2 py-0.5 text-[12.5px] font-semibold">
            {GENERO_LABEL[item.genero]}
          </span>
        </p>
      </div>
      <div className="col-span-full flex flex-wrap items-center justify-between gap-3.5 border-t border-border pt-3 sm:col-span-1 sm:justify-end sm:border-t-0 sm:pt-0">
        <StatusBadge status={item.status} />
        <div className="flex gap-2">
          {item.status === 'DISPONIVEL' && (
            <Button
              size="lg"
              className="h-9 rounded-[10px] px-3.5 text-sm font-semibold"
              disabled={reservando}
              onClick={() => onReservar(item.id)}
            >
              Reservar<span className="sr-only"> {descricao}</span>
            </Button>
          )}
          <Link
            to={`/itens/${item.id}`}
            className={cn(buttonVariants({ variant: 'outline', size: 'lg' }), 'h-9 rounded-[10px] px-3.5 text-sm font-semibold')}
          >
            Detalhes<span className="sr-only"> de {descricao}</span>
          </Link>
        </div>
      </div>
    </li>
  );
}
