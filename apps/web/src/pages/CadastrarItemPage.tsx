import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AlertCircle, Check, Info } from 'lucide-react';
import { PageHeader } from '@/components/PageHeader';
import { Button, buttonVariants } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { DoacaoForm } from '@/features/doacoes/components/DoacaoForm';
import { useRegistrarDoacao } from '@/features/doacoes/hooks/useRegistrarDoacao';
import type { DoacaoFormData } from '@/features/doacoes/schemas/doacao.schema';
import { useDoadores } from '@/features/doadores/hooks/useDoadores';
import {
  ESTADO_CONSERVACAO_LABEL,
  GENERO_LABEL,
  TIPO_PECA_LABEL,
} from '@/features/itens/constants/item.constants';
import type { ItemResponse } from '@/features/itens/interfaces/item.interface';
import { useMatchSugerido } from '@/features/matches/hooks/useMatchSugerido';
import { cn } from '@/lib/utils';

interface ResultadoDoacaoProps {
  item: ItemResponse;
  nomeDoador: string;
  onNova: () => void;
}

function ResultadoDoacao({ item, nomeDoador, onNova }: ResultadoDoacaoProps) {
  const { dados: match, loading, erro } = useMatchSugerido(item.id, true);
  const primeiroNome = nomeDoador.split(' ')[0];
  const titulo = useRef<HTMLHeadingElement>(null);

  useEffect(() => {
    titulo.current?.focus();
  }, []);

  return (
    <section aria-labelledby="titulo-sucesso" className="rounded-xl bg-card p-6 text-center shadow-sm ring-1 ring-border sm:p-9">
      <div className="mx-auto mb-4 grid size-16 place-items-center rounded-full bg-disponivel text-disponivel-foreground">
        <Check className="size-8" aria-hidden="true" />
      </div>
      <h2
        id="titulo-sucesso"
        tabIndex={-1}
        ref={titulo}
        className="text-2xl font-extrabold tracking-tight outline-none"
      >
        Doação registrada{primeiroNome ? `. Obrigado, ${primeiroNome}!` : '!'}
      </h2>
      <p className="mt-2 text-muted-foreground">
        {TIPO_PECA_LABEL[item.tipoPeca]} {item.tamanho} · {GENERO_LABEL[item.genero]} (
        {ESTADO_CONSERVACAO_LABEL[item.estadoConservacao].toLowerCase()}) já aparece no mural como disponível.
      </p>

      <div className="mx-auto mt-6 max-w-[620px] text-left" aria-live="polite">
        {loading ? (
          <div role="status" className="rounded-2xl bg-secondary p-6">
            <span className="sr-only">Buscando abrigo compatível…</span>
            <Skeleton className="h-3 w-1/3" />
            <Skeleton className="mt-4 h-7 w-4/5" />
          </div>
        ) : erro ? (
          <div className="rounded-2xl border-[1.5px] border-dashed border-input bg-secondary p-6">
            <p className="flex items-center gap-2 text-xs font-bold tracking-[0.1em] uppercase text-muted-foreground">
              <AlertCircle className="size-4" aria-hidden="true" />
              Para onde sua peça pode ir
            </p>
            <p className="mt-2 text-lg font-extrabold">Não conseguimos buscar a sugestão agora</p>
            <p className="mt-3 text-[13.5px] text-muted-foreground">{erro}</p>
          </div>
        ) : match ? (
          <div className="rounded-2xl bg-primary textura-la p-6 text-primary-foreground shadow-lg sm:px-[26px]">
            <p className="text-xs font-bold tracking-[0.1em] uppercase opacity-90">Para onde sua peça pode ir</p>
            <p className="mt-2 text-[clamp(1.25rem,2.6vw,1.625rem)] leading-tight font-extrabold tracking-tight">
              Match sugerido: {match.abrigoNome}
            </p>
            <p className="mt-3.5 flex flex-wrap gap-2">
              <span className="rounded-lg bg-white/15 px-2.5 py-1 text-[13px] font-semibold">
                Déficit de {match.deficit} {TIPO_PECA_LABEL[match.tipoPeca].toLowerCase()}
              </span>
            </p>
            <p className="mt-3.5 flex gap-2 text-[13.5px] leading-normal">
              <Info className="mt-0.5 size-4 shrink-0" aria-hidden="true" />
              <span>
                É uma sugestão com base nas demandas em aberto agora. Ainda não há reserva nem entrega marcada:
                combine com o abrigo antes de levar a peça.{' '}
                <Link to={`/abrigos/${match.abrigoId}`} className="font-bold underline underline-offset-3">
                  Ver o abrigo
                </Link>
              </span>
            </p>
          </div>
        ) : (
          <div className="rounded-2xl border-[1.5px] border-dashed border-input bg-secondary p-6">
            <p className="text-xs font-bold tracking-[0.1em] uppercase text-muted-foreground">Para onde sua peça pode ir</p>
            <p className="mt-2 text-lg font-extrabold">Nenhum abrigo compatível no momento</p>
            <p className="mt-3 text-[13.5px] text-muted-foreground">
              Sua peça já está no mural e fica disponível para quem precisar. Assim que surgir uma demanda
              compatível, a sugestão aparece na página dela.
            </p>
          </div>
        )}
      </div>

      <div className="mt-6 flex flex-wrap justify-center gap-2.5">
        <Link to={`/itens/${item.id}`} className={cn(buttonVariants(), 'h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold')}>
          Ver minha peça
        </Link>
        <Button variant="outline" className="h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold" onClick={onNova}>
          Registrar outra peça
        </Button>
        <Link to="/" className={cn(buttonVariants({ variant: 'ghost' }), 'h-[42px] px-[18px] text-[14.5px] font-semibold')}>
          Ir para o mural
        </Link>
      </div>
    </section>
  );
}

export function CadastrarItemPage() {
  const navigate = useNavigate();
  const { registrar, loading, erro } = useRegistrarDoacao();
  const doadores = useDoadores();
  const [criado, setCriado] = useState<{ item: ItemResponse; nomeDoador: string } | null>(null);
  const [rodada, setRodada] = useState(0);

  async function handleSubmit(dados: DoacaoFormData) {
    const item = await registrar(dados);
    if (!item) return;
    const doador = doadores.dados?.find((d) => d.id === dados.doadorId);
    setCriado({ item, nomeDoador: doador?.nome ?? '' });
  }

  return (
    <>
      <PageHeader
        eyebrow="Quero doar"
        titulo="Registrar uma doação"
        descricao="Conte que peça você tem. A gente cruza com o que os abrigos precisam agora e sugere um destino. A entrega você combina direto com a equipe."
      />

      {criado ? (
        <ResultadoDoacao
          item={criado.item}
          nomeDoador={criado.nomeDoador}
          onNova={() => {
            setCriado(null);
            setRodada((r) => r + 1);
          }}
        />
      ) : (
        <div className="grid items-start gap-[22px] lg:grid-cols-[minmax(0,1.6fr)_minmax(0,1fr)]">
          <DoacaoForm
            key={rodada}
            doadores={doadores.dados}
            carregandoDoadores={doadores.loading}
            erroDoadores={doadores.erro}
            onSubmit={handleSubmit}
            onCancelar={() => navigate('/')}
            loading={loading}
            erro={erro}
          />
          <aside aria-labelledby="titulo-como" className="rounded-xl bg-card p-[22px] shadow-sm ring-1 ring-border lg:sticky lg:top-6">
            <h2 id="titulo-como" className="mb-3.5 text-[17px] font-extrabold">
              Como funciona o match
            </h2>
            <ol className="flex flex-col gap-3.5">
              {[
                'Você escolhe o doador e descreve a peça: tipo, tamanho e gênero.',
                'Cruzamos com as demandas em aberto dos abrigos.',
                'Sugerimos o abrigo com maior déficit para essa peça.',
              ].map((passo, i) => (
                <li key={passo} className="grid grid-cols-[28px_1fr] gap-2.5 text-sm">
                  <span className="grid size-7 place-items-center rounded-full bg-secondary text-[13px] font-extrabold">
                    {i + 1}
                  </span>
                  <span>{passo}</span>
                </li>
              ))}
            </ol>
            <p className="mt-3.5 flex gap-2 text-[13px] text-muted-foreground">
              <Info className="mt-0.5 size-4 shrink-0" aria-hidden="true" />
              <span>O match é uma sugestão. Ele não reserva a peça nem garante a entrega.</span>
            </p>
          </aside>
        </div>
      )}
    </>
  );
}
