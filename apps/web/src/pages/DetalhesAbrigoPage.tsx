import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { AlertCircle, ChevronLeft, MapPin, Plus } from 'lucide-react';
import { ConfirmDialog } from '@/components/ConfirmDialog';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import { PageHeader } from '@/components/PageHeader';
import { ProgressBar } from '@/components/ProgressBar';
import { SummaryStrip } from '@/components/SummaryStrip';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { DemandaDialog } from '@/features/abrigos/components/DemandaDialog';
import { DemandasLista } from '@/features/abrigos/components/DemandasLista';
import {
  abrigoQuaseCheio,
  deficitDoAbrigo,
  demandasEmAberto,
} from '@/features/abrigos/constants/abrigo.constants';
import { useAbrigo } from '@/features/abrigos/hooks/useAbrigo';
import { useDemandaActions } from '@/features/abrigos/hooks/useDemandaActions';
import type { DemandaResponse } from '@/features/abrigos/interfaces/abrigo.interface';
import type { DemandaFormData } from '@/features/abrigos/schemas/demanda.schema';
import { GENERO_LABEL, TIPO_PECA_LABEL } from '@/features/itens/constants/item.constants';

type DialogoDemanda = { demanda: DemandaResponse | null } | null;

export function DetalhesAbrigoPage() {
  const { id } = useParams<{ id: string }>();
  const { dados: abrigo, loading, erro, recarregar } = useAbrigo(id!);
  const { criar, atualizar, remover, limparErro, estado } = useDemandaActions();
  const [dialogo, setDialogo] = useState<DialogoDemanda>(null);
  const [removendo, setRemovendo] = useState<DemandaResponse | null>(null);

  const voltar = (
    <Link to="/abrigos" className="mb-3.5 inline-flex items-center gap-1.5 rounded-lg py-1 pr-1.5 pl-0.5 text-sm font-semibold text-muted-foreground hover:bg-secondary hover:text-foreground">
      <ChevronLeft className="size-4" aria-hidden="true" />
      Todos os abrigos
    </Link>
  );

  if (loading) {
    return (
      <>
        {voltar}
        <div role="status" className="flex flex-col gap-4">
          <span className="sr-only">Carregando abrigo…</span>
          <Skeleton className="h-24 rounded-xl" />
          <Skeleton className="h-20 rounded-xl" />
          <Skeleton className="h-64 rounded-xl" />
        </div>
      </>
    );
  }

  if (erro || !abrigo) {
    return (
      <>
        {voltar}
        <ErrorState
          titulo={erro ?? 'Não foi possível abrir este abrigo'}
          descricao="As informações continuam salvas. Vamos tentar buscar de novo?"
          onTentarNovamente={recarregar}
        />
      </>
    );
  }

  const quaseCheio = abrigoQuaseCheio(abrigo);
  const { endereco } = abrigo;
  const enderecoCompleto = [
    [endereco.logradouro, endereco.numero].filter(Boolean).join(', '),
    endereco.bairro,
    `${endereco.cidade}/${endereco.estado}`,
  ]
    .filter(Boolean)
    .join(', ');

  function abrirDialogo(demanda: DemandaResponse | null) {
    limparErro();
    setDialogo({ demanda });
  }

  async function handleSalvar(dados: DemandaFormData) {
    const demanda = dialogo?.demanda ?? null;
    const ok = demanda
      ? await atualizar(abrigo!.id, demanda.posicao, dados)
      : await criar(abrigo!.id, dados);
    if (ok) {
      setDialogo(null);
      recarregar();
    }
  }

  async function handleRemover() {
    if (!removendo) return;
    const ok = await remover(abrigo!.id, removendo.posicao);
    if (ok) {
      setRemovendo(null);
      recarregar();
    }
  }

  return (
    <>
      {voltar}
      <PageHeader
        eyebrow={`${endereco.cidade}/${endereco.estado}`}
        titulo={abrigo.nome}
        selo={
          quaseCheio ? (
            <span className="inline-flex items-center gap-1.5 rounded-full bg-reservado px-[11px] py-1 text-sm font-bold text-reservado-foreground">
              <AlertCircle className="size-3.5" aria-hidden="true" />
              Quase cheio
            </span>
          ) : undefined
        }
        descricao={
          <span className="flex items-center gap-1.5">
            <MapPin className="size-[17px] shrink-0" aria-hidden="true" />
            {enderecoCompleto}
          </span>
        }
      />

      <SummaryStrip
        rotuloAcessivel="Situação do abrigo"
        celulas={[
          {
            rotulo: 'Estoque atual',
            valor: (
              <>
                {abrigo.estoqueAtual}
                <small className="ml-1 text-[15px] font-semibold tracking-normal text-muted-foreground">
                  / {abrigo.capacidadeEstoque}
                </small>
              </>
            ),
            rodape: (
              <ProgressBar
                valor={abrigo.estoqueAtual}
                maximo={abrigo.capacidadeEstoque}
                rotulo="Estoque do abrigo"
                variante={quaseCheio ? 'ambar' : 'musgo'}
                className="mt-1"
              />
            ),
          },
          {
            rotulo: 'Demandas em aberto',
            valor: demandasEmAberto(abrigo).length,
            rodape: `de ${abrigo.demandas.length} ${abrigo.demandas.length === 1 ? 'demanda cadastrada' : 'demandas cadastradas'}`,
          },
          { rotulo: 'Peças em falta', valor: deficitDoAbrigo(abrigo), rodape: 'déficit = necessária − atendida', destaque: true },
        ]}
      />

      <section aria-labelledby="titulo-demandas" className="mt-[22px] overflow-hidden rounded-xl bg-card shadow-sm ring-1 ring-border">
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-border px-5 py-[18px]">
          <div>
            <h2 id="titulo-demandas" className="text-lg font-extrabold tracking-tight">Demandas</h2>
            <p className="mt-0.5 text-[13.5px] text-muted-foreground">O que este abrigo ainda precisa receber.</p>
          </div>
          <Button className="h-9 rounded-[10px] px-3.5 text-sm font-semibold" onClick={() => abrirDialogo(null)}>
            <Plus aria-hidden="true" />
            Adicionar demanda
          </Button>
        </div>

        {abrigo.demandas.length === 0 ? (
          <div className="p-5">
            <EmptyState
              tracejado
              titulo="Nenhuma demanda cadastrada"
              descricao="Conte à comunidade o que este abrigo precisa agora. Cada demanda ajuda o match a sugerir as peças certas."
              acao={
                <Button className="h-10 px-4" onClick={() => abrirDialogo(null)}>
                  <Plus aria-hidden="true" />
                  Adicionar primeira demanda
                </Button>
              }
            />
          </div>
        ) : (
          <DemandasLista demandas={abrigo.demandas} onEditar={abrirDialogo} onRemover={(d) => { limparErro(); setRemovendo(d); }} />
        )}
      </section>

      {dialogo && (
        <DemandaDialog
          key={dialogo.demanda?.posicao ?? 'nova'}
          abrigoNome={abrigo.nome}
          demanda={dialogo.demanda}
          onFechar={() => setDialogo(null)}
          onSalvar={handleSalvar}
          loading={estado.loading}
          erro={estado.erro}
        />
      )}

      <ConfirmDialog
        aberto={removendo !== null}
        onAbertoChange={(aberto) => !aberto && setRemovendo(null)}
        titulo="Remover demanda?"
        descricao={
          removendo
            ? `A demanda de ${TIPO_PECA_LABEL[removendo.tipoPeca]} ${removendo.tamanho} · ${GENERO_LABEL[removendo.genero]} deixa de aparecer para doadores e no cálculo do déficit.`
            : ''
        }
        rotuloConfirmar="Remover"
        onConfirmar={handleRemover}
        processando={estado.loading}
        erro={estado.erro}
      />
    </>
  );
}
