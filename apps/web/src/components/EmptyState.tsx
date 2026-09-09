import { PackageSearch } from 'lucide-react';

interface EmptyStateProps {
  titulo?: string;
  descricao?: string;
}

export function EmptyState({
  titulo = 'Nenhum item encontrado',
  descricao = 'Tente ajustar os filtros ou cadastre uma nova doação.',
}: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-20 text-center text-muted-foreground">
      <PackageSearch className="size-12 opacity-40" />
      <p className="text-base font-medium">{titulo}</p>
      <p className="max-w-xs text-sm">{descricao}</p>
    </div>
  );
}
