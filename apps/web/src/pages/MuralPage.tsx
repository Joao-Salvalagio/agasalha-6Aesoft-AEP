import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiltrosMural } from '@/features/itens/components/FiltrosMural';
import { ItemCard } from '@/features/itens/components/ItemCard';
import { useItens } from '@/features/itens/hooks/useItens';
import { Button } from '@/components/ui/button';
import { useItemActions } from '@/features/itens/hooks/useItemActions';
import { EmptyState } from '@/components/EmptyState';
import { Skeleton } from '@/components/ui/skeleton';
import type { ItemFiltroParams } from '@/features/itens/interfaces/item.interface';

export function MuralPage() {
  const navigate = useNavigate();
  const [filtros, setFiltros] = useState<ItemFiltroParams>({});
  const { itens, loading, recarregar } = useItens(filtros);
  const { reservar, estado: acaoEstado } = useItemActions();

  async function handleReservar(id: string) {
    const resultado = await reservar(id);
    if (resultado) recarregar();
  }

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col gap-1">
        <h1 className="text-2xl font-bold text-foreground">Mural de Doações</h1>
        <p className="text-sm text-muted-foreground">
          Agasalhos disponíveis para quem precisa
        </p>
      </div>

      <FiltrosMural onFiltrar={setFiltros} />

      {loading ? (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, i) => (
            <Skeleton key={i} className="h-52 rounded-xl" />
          ))}
        </div>
      ) : itens.length === 0 ? (
        <EmptyState />
      ) : (
        <>
          {acaoEstado.erro && (
            <p className="rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
              {acaoEstado.erro}
            </p>
          )}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {itens.map((item) => (
              <ItemCard
                key={item.id}
                item={item}
                onReservar={handleReservar}
                reservando={acaoEstado.loading}
              />
            ))}
          </div>
          <p className="text-xs text-muted-foreground">
            {itens.length} {itens.length === 1 ? 'item encontrado' : 'itens encontrados'}
          </p>
        </>
      )}

      <Button
        size="lg"
        onClick={() => navigate('/itens/novo')}
        className="fixed bottom-6 right-6 h-14 rounded-full px-6 shadow-lg sm:hidden"
      >
        + Doar
      </Button>
    </div>
  );
}
