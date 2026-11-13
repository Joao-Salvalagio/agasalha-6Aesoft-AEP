import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ArrowRight, ChevronLeft, House, Info, Mail, Pencil, PackageCheck, Phone, Trash2, Truck } from 'lucide-react';
import { ConfirmDialog } from '@/components/ConfirmDialog';
import { ErrorState } from '@/components/ErrorState';
import { PageHeader } from '@/components/PageHeader';
import { StatusBadge } from '@/components/StatusBadge';
import { Button, buttonVariants } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { PecaIcone } from '@/features/itens/components/PecaIcone';
import {
  ESTADO_CONSERVACAO_LABEL,
  GENERO_LABEL,
  TIPO_PECA_LABEL,
} from '@/features/itens/constants/item.constants';
import { useItem } from '@/features/itens/hooks/useItem';
import { useItemActions } from '@/features/itens/hooks/useItemActions';
import { useOrigemItem } from '@/features/itens/hooks/useOrigemItem';
import { MatchSugestao } from '@/features/matches/components/MatchSugestao';
import { useConfirmarMatch } from '@/features/matches/hooks/useConfirmarMatch';
import { useMatchSugerido } from '@/features/matches/hooks/useMatchSugerido';
import { cn } from '@/lib/utils';

const CLASSE_CARD_LATERAL = 'rounded-xl bg-card px-5 py-[18px] shadow-sm ring-1 ring-border';
const CLASSE_TITULO_LATERAL = 'mb-3 text-[12.5px] font-bold tracking-[0.08em] text-muted-foreground uppercase';

function iniciais(nome: string): string {
  return nome
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0])
    .join('')
    .toUpperCase();
}

function LinkContato({ contato }: { contato: string }) {
  const ehEmail = /^\S+@\S+\.\S+$/.test(contato);
  const Icone = ehEmail ? Mail : Phone;
  const href = ehEmail ? `mailto:${contato}` : `tel:${contato.replace(/\D/g, '')}`;
  return (
    <a href={href} className="mt-0.5 inline-flex items-center gap-1.5 text-sm text-primary underline underline-offset-3">
      <Icone className="size-4" aria-hidden="true" />
      {contato}
    </a>
  );
}

export function DetalhesItemPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { item, loading, erro, recarregar } = useItem(id!);
  const origem = useOrigemItem(item);
  const podeEnviarAoAbrigo = item !== null && item.status !== 'ENTREGUE' && item.abrigoId === null;
  const match = useMatchSugerido(id!, podeEnviarAoAbrigo);
  const confirmacao = useConfirmarMatch();
  const { reservar, entregar, excluir, estado: acaoEstado } = useItemActions();
  const [confirmandoExclusao, setConfirmandoExclusao] = useState(false);

  async function handleReservar() {
    if (await reservar(id!)) recarregar();
  }

  async function handleEntregar() {
    if (await entregar(id!)) recarregar();
  }

  async function handleEnviarAoAbrigo() {
    if (await confirmacao.confirmar(id!)) recarregar();
  }

  async function handleExcluir() {
    if (await excluir(id!)) navigate('/');
  }

  const voltar = (
    <Link to="/" className="mb-3.5 inline-flex items-center gap-1.5 rounded-lg py-1 pr-1.5 pl-0.5 text-sm font-semibold text-muted-foreground hover:bg-secondary hover:text-foreground">
      <ChevronLeft className="size-4" aria-hidden="true" />
      Voltar ao mural
    </Link>
  );

  if (loading) {
    return (
      <>
        {voltar}
        <div role="status" className="grid gap-5 lg:grid-cols-[minmax(0,1.55fr)_minmax(0,1fr)]">
          <span className="sr-only">Carregando peça…</span>
          <Skeleton className="h-80 rounded-xl" />
          <div className="flex flex-col gap-4">
            <Skeleton className="h-28 rounded-xl" />
            <Skeleton className="h-28 rounded-xl" />
            <Skeleton className="h-36 rounded-xl" />
          </div>
        </div>
      </>
    );
  }

  if (erro || !item) {
    return (
      <>
        {voltar}
        <ErrorState titulo={erro ?? 'Essa peça não está mais no mural'} descricao="Ela pode ter sido entregue ou removida. Que tal ver as outras peças disponíveis?" onTentarNovamente={recarregar} />
      </>
    );
  }

  const entregue = item.status === 'ENTREGUE';
  const daDoacao = item.doacaoId !== null;
  const edicaoBloqueada = entregue || daDoacao;
  const semAbrigo = item.abrigoId === null;
  const descricao = `${TIPO_PECA_LABEL[item.tipoPeca]} ${item.tamanho} · ${GENERO_LABEL[item.genero]}`;
  const doador = origem.dados?.doador ?? null;
  const destino = origem.dados?.destino ?? null;

  return (
    <>
      {voltar}
      <PageHeader eyebrow="Detalhe da peça" titulo={descricao} selo={<StatusBadge status={item.status} className="text-sm" />} />

      <div className="grid items-start gap-5 lg:grid-cols-[minmax(0,1.55fr)_minmax(0,1fr)]">
        <section aria-labelledby="titulo-peca" className="rounded-xl bg-card p-6 shadow-sm ring-1 ring-border">
          <div className="flex flex-col items-start gap-[22px] sm:flex-row sm:items-center">
            <PecaIcone tipoPeca={item.tipoPeca} tamanho="lg" />
            <div>
              <h2 id="titulo-peca" className="text-xl font-extrabold tracking-tight">
                {TIPO_PECA_LABEL[item.tipoPeca]} tamanho {item.tamanho}
              </h2>
              <p className="mt-1 text-sm text-muted-foreground">
                {GENERO_LABEL[item.genero]} · {ESTADO_CONSERVACAO_LABEL[item.estadoConservacao]}
              </p>
            </div>
          </div>

          <dl className="mt-6 grid gap-x-6 gap-y-4 border-t border-border pt-5 sm:grid-cols-2">
            {[
              ['Tipo de peça', TIPO_PECA_LABEL[item.tipoPeca]],
              ['Tamanho', item.tamanho],
              ['Gênero', GENERO_LABEL[item.genero]],
              ['Estado de conservação', ESTADO_CONSERVACAO_LABEL[item.estadoConservacao]],
              ['Cadastrada em', new Date(item.dataCadastro).toLocaleDateString('pt-BR')],
              ['Origem', daDoacao ? 'Doação registrada' : 'Doação avulsa'],
            ].map(([rotulo, valor]) => (
              <div key={rotulo}>
                <dt className="text-[12.5px] font-semibold text-muted-foreground">{rotulo}</dt>
                <dd className="mt-0.5 text-[15px] font-bold">{valor}</dd>
              </div>
            ))}
          </dl>

          <div className="mt-[22px] flex flex-wrap items-center gap-2.5 border-t border-border pt-5">
            {acaoEstado.erro && (
              <p role="alert" className="w-full rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
                {acaoEstado.erro}
              </p>
            )}
            {item.status === 'DISPONIVEL' && (
              <Button className="h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold" disabled={acaoEstado.loading || semAbrigo} onClick={handleReservar}>
                <PackageCheck aria-hidden="true" />
                {acaoEstado.loading ? 'Processando…' : 'Reservar'}
              </Button>
            )}
            {item.status === 'RESERVADO' && (
              <Button className="h-[42px] rounded-xl bg-moss px-[18px] text-[14.5px] font-semibold text-white hover:bg-moss/90" disabled={acaoEstado.loading || semAbrigo} onClick={handleEntregar}>
                <Truck aria-hidden="true" />
                {acaoEstado.loading ? 'Processando…' : 'Confirmar entrega'}
              </Button>
            )}
            {edicaoBloqueada ? (
              <Button variant="outline" className="h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold" disabled aria-describedby="nota-edicao">
                <Pencil aria-hidden="true" />
                Editar
              </Button>
            ) : (
              <Link to={`/itens/${item.id}/editar`} className={cn(buttonVariants({ variant: 'outline' }), 'h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold')}>
                <Pencil aria-hidden="true" />
                Editar
              </Link>
            )}
            <span className="flex-1" />
            <Button variant="ghost" className="h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold text-destructive hover:bg-destructive/10" onClick={() => setConfirmandoExclusao(true)}>
              <Trash2 aria-hidden="true" />
              Excluir
            </Button>
            {semAbrigo && !entregue && (
              <p className="flex w-full items-center gap-1.5 text-[13px] text-muted-foreground">
                <Info className="size-4 shrink-0" aria-hidden="true" />
                Envie a peça a um abrigo: ela fica reservada e a entrega é liberada.
              </p>
            )}
            {edicaoBloqueada && (
              <p id="nota-edicao" className="flex w-full items-center gap-1.5 text-[13px] text-muted-foreground">
                <Info className="size-4 shrink-0" aria-hidden="true" />
                {entregue
                  ? 'Peças já entregues não podem ser editadas.'
                  : 'Peças vindas de uma doação são editadas pela própria doação.'}
              </p>
            )}
          </div>
        </section>

        <div className="flex flex-col gap-4">
          <section aria-labelledby="titulo-doador" className={CLASSE_CARD_LATERAL}>
            <h2 id="titulo-doador" className={CLASSE_TITULO_LATERAL}>Doador</h2>
            {origem.loading ? (
              <Skeleton className="h-11 w-full" />
            ) : doador ? (
              <div className="flex items-center gap-3">
                <span className="grid size-11 flex-none place-items-center rounded-full bg-accent font-extrabold" aria-hidden="true">
                  {iniciais(doador.nome)}
                </span>
                <div className="min-w-0">
                  <strong className="block text-[15.5px]">{doador.nome}</strong>
                  {doador.contato && <LinkContato contato={doador.contato} />}
                </div>
              </div>
            ) : (
              <p className="text-sm text-muted-foreground">
                {origem.erro ?? 'Não foi possível identificar o doador desta peça.'}
              </p>
            )}
          </section>

          <section aria-labelledby="titulo-destino" className={CLASSE_CARD_LATERAL}>
            <h2 id="titulo-destino" className={CLASSE_TITULO_LATERAL}>Destino</h2>
            {origem.loading ? (
              <Skeleton className="h-14 w-full" />
            ) : destino ? (
              <Link to={`/abrigos/${destino.id}`} className="flex items-center gap-3 rounded-xl bg-secondary p-3 hover:bg-accent">
                <House className="size-[22px] flex-none" aria-hidden="true" />
                <span className="min-w-0">
                  <strong className="block">{destino.nome}</strong>
                  <span className="text-[13px] text-muted-foreground">
                    {destino.cidade}/{destino.estado} ·{' '}
                    {item.abrigoId ? 'enviada ao estoque do abrigo' : 'destino da doação'}
                  </span>
                </span>
                <ArrowRight className="ml-auto size-[18px] flex-none" aria-hidden="true" />
              </Link>
            ) : (
              <p className="text-sm text-muted-foreground">
                Sem destino definido. A peça fica no mural geral até alguém reservar.
              </p>
            )}
          </section>

          {podeEnviarAoAbrigo && (
            <MatchSugestao
              match={match.dados}
              loading={match.loading}
              erro={match.erro}
              onAdicionar={handleEnviarAoAbrigo}
              adicionando={confirmacao.loading}
              erroAcao={confirmacao.erro}
            />
          )}
        </div>
      </div>

      <ConfirmDialog
        aberto={confirmandoExclusao}
        onAbertoChange={setConfirmandoExclusao}
        titulo="Excluir esta peça?"
        descricao={`${descricao} sai do mural e deixa de aparecer nas sugestões de match. Essa ação não pode ser desfeita.`}
        rotuloConfirmar="Excluir peça"
        onConfirmar={handleExcluir}
        processando={acaoEstado.loading}
        erro={acaoEstado.erro}
      />
    </>
  );
}
