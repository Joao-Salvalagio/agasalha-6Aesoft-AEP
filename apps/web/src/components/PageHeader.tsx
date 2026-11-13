import type { ReactNode } from 'react';

interface PageHeaderProps {
  eyebrow: string;
  titulo: ReactNode;
  descricao?: ReactNode;
  acoes?: ReactNode;
  selo?: ReactNode;
}

export function PageHeader({ eyebrow, titulo, descricao, acoes, selo }: PageHeaderProps) {
  return (
    <header className="mb-6 flex flex-wrap items-end justify-between gap-x-7 gap-y-5">
      <div className="min-w-0 flex-[1_1_420px]">
        <p className="inline-flex items-center gap-2 text-xs font-bold tracking-[0.09em] text-primary uppercase before:h-0.5 before:w-[18px] before:rounded-full before:bg-current">
          {eyebrow}
        </p>
        <h1 className="mt-2 mb-2.5 flex flex-wrap items-center gap-3 text-[clamp(1.625rem,3.2vw,2.25rem)] leading-[1.12] font-extrabold tracking-tight">
          {titulo}
          {selo}
        </h1>
        {descricao && <div className="max-w-[62ch] text-[15.5px] text-muted-foreground">{descricao}</div>}
      </div>
      {acoes && <div className="flex flex-wrap gap-2.5">{acoes}</div>}
    </header>
  );
}
