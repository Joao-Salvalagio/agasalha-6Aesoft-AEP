import type { ReactNode } from 'react';
import { cn } from '@/lib/utils';

export interface SummaryCell {
  rotulo: string;
  valor: ReactNode;
  unidade?: string;
  rodape?: ReactNode;
  destaque?: boolean;
}

interface SummaryStripProps {
  celulas: SummaryCell[];
  rotuloAcessivel: string;
}

export function SummaryStrip({ celulas, rotuloAcessivel }: SummaryStripProps) {
  return (
    <section
      aria-label={rotuloAcessivel}
      className="mb-5 grid grid-cols-1 overflow-hidden rounded-xl bg-card shadow-sm ring-1 ring-border sm:grid-cols-3"
    >
      {celulas.map((c) => (
        <div
          key={c.rotulo}
          className={cn(
            'min-w-0 border-t border-border px-5 py-4 first:border-t-0 sm:border-t-0 sm:border-l sm:py-[18px] sm:first:border-l-0',
            c.destaque && 'bg-warm textura-quente',
          )}
        >
          <p className="text-[13px] font-semibold text-muted-foreground">{c.rotulo}</p>
          <p
            className={cn(
              'mt-1 text-[1.75rem] leading-tight font-extrabold tracking-tight',
              c.destaque && 'text-primary',
            )}
          >
            {c.valor}
            {c.unidade && (
              <small className="ml-1 text-[15px] font-semibold tracking-normal text-muted-foreground">
                {c.unidade}
              </small>
            )}
          </p>
          {c.rodape && <p className="mt-0.5 text-[13px] text-muted-foreground">{c.rodape}</p>}
        </div>
      ))}
    </section>
  );
}
