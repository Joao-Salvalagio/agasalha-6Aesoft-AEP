import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Heart } from 'lucide-react';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import { PageHeader } from '@/components/PageHeader';
import { SummaryStrip } from '@/components/SummaryStrip';
import { buttonVariants } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { useDeficitTotal } from '@/features/abrigos/hooks/useDeficitTotal';
import { FiltrosMural } from '@/features/itens/components/FiltrosMural';
import { ItemLinha } from '@/features/itens/components/ItemLinha';
import { useItemActions } from '@/features/itens/hooks/useItemActions';
import { useItens } from '@/features/itens/hooks/useItens';
import type { ItemFiltroParams } from '@/features/itens/interfaces/item.interface';
import { cn } from '@/lib/utils';

const CLASSE_CTA = cn(buttonVariants({ size: 'lg' }), 'h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold');

export function MuralPage() {
  const [filtros, setFiltros] = useState<ItemFiltroParams>({});
  const filtrados = useItens(filtros);
  const todos = useItens();
  const deficitTotal = useDeficitTotal();
  const { reservar, estado: acaoEstado } = useItemActions();

  async function handleReservar(id: string) {
    const resultado = await reservar(id);
    if (resultado) {
      filtrados.recarregar();
      todos.recarregar();
    }
  }

  const disponiveis = todos.itens.filter((i) => i.status === 'DISPONIVEL').length;
  const reservadas = todos.itens.filter((i) => i.status === 'RESERVADO').length;
  const temFiltro = Object.values(filtros).some(Boolean);

  const cabecalho = (
    <PageHeader
      eyebrow="Mural de agasalhos"
      titulo="Peças prontas para aquecer alguém"
      descricao="Tudo o que a comunidade separou com carinho para este inverno. Reserve uma peça e combine a entrega com o abrigo."
      acoes={
        <Link to="/itens/novo" className={CLASSE_CTA}>
          <Heart aria-hidden="true" />
          Quero doar
        </Link>
      }
    />
  );

  if (filtrados.erro && !temFiltro) {
    return (
      <>
        {cabecalho}
        <ErrorState
          titulo="Não conseguimos carregar o mural agora"
          onTentarNovamente={() => {
            filtrados.recarregar();
            todos.recarregar();
          }}
        />
      </>
    );
  }

  const semNenhumItem = !todos.loading && !todos.erro && todos.itens.length === 0;

  if (semNenhumItem) {
    return (
      <>
        {cabecalho}
        <EmptyState
          titulo="O mural está quietinho por enquanto"
          descricao="Ainda não há peças cadastradas. Que tal ser a primeira pessoa a aquecer alguém nesta estação?"
          acao={
            <Link to="/itens/novo" className={CLASSE_CTA}>
              <Heart aria-hidden="true" />
              Cadastrar uma doação
            </Link>
          }
        />
      </>
    );
  }

  return (
    <>
      {cabecalho}

      <SummaryStrip
        rotuloAcessivel="Resumo do mural"
        celulas={[
          { rotulo: 'Peças disponíveis', valor: todos.loading ? '—' : disponiveis, rodape: 'prontas para reserva' },
          { rotulo: 'Reservadas', valor: todos.loading ? '—' : reservadas, rodape: 'aguardando entrega' },
          {
            rotulo: 'Ainda faltam nos abrigos',
            valor: deficitTotal ?? '—',
            unidade: 'peças',
            rodape: <Link to="/abrigos" className="text-primary underline underline-offset-3">Ver o que cada abrigo precisa</Link>,
            destaque: true,
          },
        ]}
      />

      <FiltrosMural filtros={filtros} onFiltrar={setFiltros} />

      {acaoEstado.erro && (
        <p role="alert" className="mb-3 rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
          {acaoEstado.erro}
        </p>
      )}

      {filtrados.loading ? (
        <div role="status" className="flex flex-col gap-2.5">
          <span className="sr-only">Carregando peças…</span>
          {Array.from({ length: 4 }).map((_, i) => (
            <Skeleton key={i} className="h-[88px] rounded-xl" />
          ))}
        </div>
      ) : filtrados.erro ? (
        <ErrorState titulo="Não foi possível aplicar os filtros" onTentarNovamente={filtrados.recarregar} />
      ) : filtrados.itens.length === 0 ? (
        <EmptyState
          tracejado
          titulo="Nenhuma peça com essa combinação"
          descricao="Às vezes a peça certa ainda está a caminho. Tente afrouxar um filtro ou volte daqui a pouco."
        />
      ) : (
        <>
          <p className="mx-0.5 mb-3 text-[13.5px] text-muted-foreground" aria-live="polite">
            Mostrando {filtrados.itens.length} de {todos.itens.length}{' '}
            {todos.itens.length === 1 ? 'peça' : 'peças'}
          </p>
          <ul aria-label="Peças do mural" className="flex flex-col gap-2.5">
            {filtrados.itens.map((item) => (
              <ItemLinha
                key={item.id}
                item={item}
                onReservar={handleReservar}
                reservando={acaoEstado.loading}
              />
            ))}
          </ul>
        </>
      )}
    </>
  );
}
