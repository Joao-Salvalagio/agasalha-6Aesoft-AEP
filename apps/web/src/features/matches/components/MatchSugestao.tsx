import { Link } from 'react-router-dom';
import { Send, Sparkles } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { GENERO_LABEL, TIPO_PECA_LABEL } from '@/features/itens/constants/item.constants';
import type { MatchResponse } from '../interfaces/match.interface';

interface MatchSugestaoProps {
  match: MatchResponse | null;
  loading: boolean;
  erro: string | null;
  onAdicionar: () => void;
  adicionando: boolean;
  erroAcao: string | null;
}

export function MatchSugestao({ match, loading, erro, onAdicionar, adicionando, erroAcao }: MatchSugestaoProps) {
  if (loading) {
    return (
      <div role="status" className="rounded-xl bg-card p-5 ring-1 ring-border">
        <span className="sr-only">Buscando abrigo compatível…</span>
        <Skeleton className="h-3 w-2/5" />
        <Skeleton className="mt-4 h-6 w-4/5" />
        <Skeleton className="mt-3 h-12 w-full" />
      </div>
    );
  }

  if (erro) {
    return (
      <section aria-labelledby="titulo-match" className="rounded-xl border border-dashed border-input bg-secondary p-5">
        <h2 id="titulo-match" className="text-[12.5px] font-bold tracking-[0.08em] text-muted-foreground uppercase">
          Sugestão de match
        </h2>
        <p className="mt-2 text-base font-bold">Não foi possível buscar a sugestão agora</p>
        <p className="mt-2 text-[13.5px] text-muted-foreground">{erro}</p>
      </section>
    );
  }

  if (!match) {
    return (
      <section aria-labelledby="titulo-match" className="rounded-xl border border-dashed border-input bg-secondary p-5">
        <h2 id="titulo-match" className="text-[12.5px] font-bold tracking-[0.08em] text-muted-foreground uppercase">
          Sugestão de match
        </h2>
        <p className="mt-2 text-base font-bold">Nenhum abrigo compatível no momento</p>
        <p className="mt-2 text-[13.5px] text-muted-foreground">
          Ainda não há demanda em aberto para essa combinação de peça. A peça segue no mural e a sugestão
          aparece aqui assim que surgir uma necessidade.
        </p>
      </section>
    );
  }

  return (
    <section
      aria-labelledby="titulo-match"
      className="rounded-xl border-[1.5px] border-primary/40 bg-warm textura-quente p-5 shadow-sm"
    >
      <h2 id="titulo-match" className="flex items-center gap-2 text-[12.5px] font-bold tracking-[0.08em] text-primary uppercase">
        <Sparkles className="size-4" aria-hidden="true" />
        Sugestão de match
      </h2>
      <p className="mt-2 text-[17px] leading-snug font-extrabold tracking-tight">
        Match sugerido: {match.abrigoNome} (déficit {match.deficit})
      </p>
      <p className="mt-2 text-[13.5px] text-muted-foreground">
        O abrigo ainda precisa de {match.deficit} {TIPO_PECA_LABEL[match.tipoPeca].toLowerCase()} tamanho{' '}
        {match.tamanho}, {GENERO_LABEL[match.genero].toLowerCase()}. Ao enviar, a peça entra no estoque do
        abrigo, abate a demanda e fica reservada. Depois é só confirmar a entrega.
      </p>
      {erroAcao && (
        <p role="alert" className="mt-3 rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-[13px] text-destructive">
          {erroAcao}
        </p>
      )}
      <Button
        className="mt-3.5 h-10 w-full rounded-xl px-4 text-sm font-semibold"
        disabled={adicionando}
        onClick={onAdicionar}
      >
        <Send aria-hidden="true" />
        {adicionando ? 'Enviando…' : `Enviar ao ${match.abrigoNome}`}
      </Button>
      <p className="mt-3">
        <Link to={`/abrigos/${match.abrigoId}`} className="font-semibold text-primary underline underline-offset-3">
          Ver demandas do {match.abrigoNome}
        </Link>
      </p>
    </section>
  );
}
