import { Link } from 'react-router-dom';
import { Heart, Mail, Phone } from 'lucide-react';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import { PageHeader } from '@/components/PageHeader';
import { buttonVariants } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { useDoadores } from '@/features/doadores/hooks/useDoadores';
import { cn } from '@/lib/utils';

const CLASSE_CTA = cn(buttonVariants({ size: 'lg' }), 'h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold');

function iniciais(nome: string): string {
  return nome
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0])
    .join('')
    .toUpperCase();
}

export function DoadoresPage() {
  const { dados: doadores, loading, erro, recarregar } = useDoadores();

  const cabecalho = (
    <PageHeader
      eyebrow="Quem aquece a rede"
      titulo="Doadores"
      descricao="As pessoas por trás de cada peça. Os contatos servem só para combinar entregas com os abrigos."
      acoes={
        <Link to="/itens/novo" className={CLASSE_CTA}>
          <Heart aria-hidden="true" />
          Registrar doação
        </Link>
      }
    />
  );

  if (loading) {
    return (
      <>
        {cabecalho}
        <div role="status" className="flex flex-col gap-3">
          <span className="sr-only">Carregando doadores…</span>
          <Skeleton className="h-[76px] rounded-xl" />
          <Skeleton className="h-[76px] rounded-xl" />
          <Skeleton className="h-[76px] rounded-xl" />
        </div>
      </>
    );
  }

  if (erro || !doadores) {
    return (
      <>
        {cabecalho}
        <ErrorState titulo="Não conseguimos carregar os doadores" descricao="Os dados estão guardados com cuidado. Vamos tentar de novo?" onTentarNovamente={recarregar} />
      </>
    );
  }

  if (doadores.length === 0) {
    return (
      <>
        {cabecalho}
        <EmptyState
          titulo="Ainda ninguém por aqui"
          descricao="Quando a primeira doação for registrada, quem doou aparece nesta lista."
        />
      </>
    );
  }

  return (
    <>
      {cabecalho}
      <p className="mx-0.5 mb-3 text-[13.5px] text-muted-foreground">
        {doadores.length} {doadores.length === 1 ? 'doador cadastrado' : 'doadores cadastrados'}
      </p>
      <ul aria-label="Lista de doadores" className="flex flex-col gap-3">
        {doadores.map((d) => {
          const ehEmail = /^\S+@\S+\.\S+$/.test(d.contato);
          const Icone = ehEmail ? Mail : Phone;
          const href = ehEmail ? `mailto:${d.contato}` : `tel:${d.contato.replace(/\D/g, '')}`;
          return (
            <li key={d.id} className="flex items-center gap-3.5 rounded-xl bg-card px-5 py-[18px] shadow-sm ring-1 ring-border">
              <span className="grid size-11 flex-none place-items-center rounded-full bg-accent font-extrabold" aria-hidden="true">
                {iniciais(d.nome)}
              </span>
              <div className="min-w-0">
                <h2 className="text-[16.5px] font-bold tracking-tight">{d.nome}</h2>
                <a href={href} className="mt-0.5 inline-flex items-center gap-1.5 text-sm text-primary underline underline-offset-3">
                  <Icone className="size-4" aria-hidden="true" />
                  {d.contato}
                </a>
              </div>
            </li>
          );
        })}
      </ul>
    </>
  );
}
