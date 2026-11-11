import type { ReactNode } from 'react';
import { PackageSearch } from 'lucide-react';
import { cn } from '@/lib/utils';

interface EmptyStateProps {
  titulo?: string;
  descricao?: string;
  acao?: ReactNode;
  tracejado?: boolean;
}

export function EmptyState({
  titulo = 'Nenhum item encontrado',
  descricao = 'Tente ajustar os filtros ou cadastre uma nova doação.',
  acao,
  tracejado = false,
}: EmptyStateProps) {
  return (
    <div
      className={cn(
        'flex flex-col items-center rounded-xl px-6 py-12 text-center',
        tracejado
          ? 'border border-dashed border-input'
          : 'bg-card shadow-sm ring-1 ring-border',
      )}
    >
      <div className="grid size-16 place-items-center rounded-full bg-secondary text-primary">
        <PackageSearch className="size-8" aria-hidden="true" />
      </div>
      <h2 className="mt-4 text-xl font-extrabold tracking-tight">{titulo}</h2>
      <p className="mx-auto mt-2 max-w-[46ch] text-muted-foreground">{descricao}</p>
      {acao && <div className="mt-5">{acao}</div>}
    </div>
  );
}
