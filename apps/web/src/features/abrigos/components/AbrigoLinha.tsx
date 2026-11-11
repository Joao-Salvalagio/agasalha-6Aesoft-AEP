import { Link } from 'react-router-dom';
import { AlertCircle, ArrowRight, MapPin } from 'lucide-react';
import { ProgressBar } from '@/components/ProgressBar';
import { buttonVariants } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { abrigoQuaseCheio, deficitDoAbrigo, demandasEmAberto } from '../constants/abrigo.constants';
import type { AbrigoResponse } from '../interfaces/abrigo.interface';

interface AbrigoLinhaProps {
  abrigo: AbrigoResponse;
}

export function AbrigoLinha({ abrigo }: AbrigoLinhaProps) {
  const quaseCheio = abrigoQuaseCheio(abrigo);
  const emAberto = demandasEmAberto(abrigo).length;
  const livres = abrigo.capacidadeEstoque - abrigo.estoqueAtual;
  const percentual = Math.round((abrigo.estoqueAtual / abrigo.capacidadeEstoque) * 100);
  const local = [abrigo.endereco.bairro, `${abrigo.endereco.cidade}/${abrigo.endereco.estado}`]
    .filter(Boolean)
    .join(' · ');

  return (
    <li
      className={cn(
        'relative grid items-center gap-x-6 gap-y-4 overflow-hidden rounded-xl p-5 shadow-sm ring-1 ring-border sm:grid-cols-2 lg:grid-cols-[minmax(0,1.35fr)_minmax(0,1.25fr)_minmax(0,0.9fr)_auto] lg:px-[22px]',
        quaseCheio
          ? 'bg-gradient-to-r from-reservado to-card to-55% ring-amber/50 before:absolute before:inset-y-0 before:left-0 before:w-1 before:bg-amber'
          : 'bg-card',
      )}
    >
      <div className="min-w-0">
        <Link
          to={`/abrigos/${abrigo.id}`}
          className="rounded-sm text-[17px] font-bold tracking-tight outline-none hover:underline hover:decoration-primary focus-visible:ring-3 focus-visible:ring-ring/50"
        >
          {abrigo.nome}
        </Link>
        <p className="mt-1 flex items-center gap-1.5 text-[13.5px] text-muted-foreground">
          <MapPin className="size-[15px]" aria-hidden="true" />
          {local}
        </p>
        {quaseCheio && (
          <p className="mt-2.5 flex items-start gap-2 text-[13px] font-semibold text-reservado-foreground">
            <AlertCircle className="mt-0.5 size-4 shrink-0" aria-hidden="true" />
            <span>
              Quase cheio: {livres} {livres === 1 ? 'vaga livre' : 'vagas livres'}. Combine antes de levar novas
              peças.
            </span>
          </p>
        )}
      </div>

      <div>
        <div className="mb-[7px] flex items-baseline justify-between gap-2 text-[13px] text-muted-foreground">
          <span>Estoque</span>
          <strong className="text-sm text-foreground">
            {abrigo.estoqueAtual}/{abrigo.capacidadeEstoque}
          </strong>
        </div>
        <ProgressBar
          valor={abrigo.estoqueAtual}
          maximo={abrigo.capacidadeEstoque}
          rotulo={`Estoque de ${abrigo.nome}`}
          variante={quaseCheio ? 'ambar' : 'musgo'}
        />
        <p className="mt-1.5 text-[12.5px] text-muted-foreground">{percentual}% ocupado</p>
      </div>

      <div>
        <strong className="block text-[22px] leading-tight font-extrabold tracking-tight">{emAberto}</strong>
        <span className="text-[13px] text-muted-foreground">
          {emAberto === 1 ? 'demanda em aberto' : 'demandas em aberto'} · faltam {deficitDoAbrigo(abrigo)}
        </span>
      </div>

      <Link
        to={`/abrigos/${abrigo.id}`}
        className={cn(buttonVariants({ variant: 'outline', size: 'lg' }), 'h-9 w-full rounded-[10px] px-3.5 text-sm font-semibold sm:w-auto lg:justify-self-start')}
      >
        Ver abrigo
        <ArrowRight aria-hidden="true" />
        <span className="sr-only"> {abrigo.nome}</span>
      </Link>
    </li>
  );
}
