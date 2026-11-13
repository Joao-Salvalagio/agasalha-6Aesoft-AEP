import { NavLink } from 'react-router-dom';
import { Heart, HeartHandshake, House, LayoutList, PanelLeftClose, Users, X } from 'lucide-react';
import { cn } from '@/lib/utils';

interface AppSidebarProps {
  recolhida: boolean;
  gavetaAberta: boolean;
  inerte: boolean;
  deficitTotal: number | null;
  onAlternarRecolhimento: () => void;
  onFecharGaveta: () => void;
}

const LINKS = [
  { para: '/', rotulo: 'Mural', Icone: LayoutList, fim: true },
  { para: '/abrigos', rotulo: 'Abrigos', Icone: House, fim: false },
  { para: '/doadores', rotulo: 'Doadores', Icone: Users, fim: false },
];

const CLASSE_LINK =
  'flex items-center gap-3 rounded-xl px-3 py-[11px] font-semibold text-sidebar-foreground outline-none hover:bg-sidebar-accent/60 focus-visible:ring-3 focus-visible:ring-sidebar-ring aria-[current=page]:bg-sidebar-accent aria-[current=page]:shadow-[inset_3px_0_0_var(--primary)]';

export function AppSidebar({
  recolhida,
  gavetaAberta,
  inerte,
  deficitTotal,
  onAlternarRecolhimento,
  onFecharGaveta,
}: AppSidebarProps) {
  const rotulo = cn(recolhida && 'md:sr-only');

  return (
    <aside
      id="menu-lateral"
      aria-label="Navegação principal"
      inert={inerte}
      className={cn(
        'bg-sidebar textura-la text-sidebar-foreground flex flex-col gap-1.5 overflow-y-auto px-3.5 py-[18px]',
        'fixed inset-y-0 left-0 z-60 h-full w-[min(300px,86vw)] shadow-xl transition-transform duration-200',
        gavetaAberta ? 'translate-x-0' : '-translate-x-[102%]',
        'md:sticky md:top-0 md:z-auto md:h-screen md:w-auto md:translate-x-0 md:shadow-none',
      )}
    >
      <div className={cn('flex items-center justify-between gap-2 px-0.5 pb-[18px]', recolhida && 'md:flex-col md:gap-3.5')}>
        <NavLink to="/" className="flex min-w-0 items-center gap-3 rounded-xl outline-none focus-visible:ring-3 focus-visible:ring-sidebar-ring" onClick={onFecharGaveta}>
          <span className="grid size-[42px] flex-none place-items-center rounded-xl bg-primary text-primary-foreground shadow-[inset_0_-3px_0_oklch(0_0_0/0.15)]">
            <HeartHandshake className="size-6" aria-hidden="true" />
          </span>
          <span className={cn('flex flex-col leading-tight', rotulo)}>
            <span className="text-xl font-extrabold tracking-tight">Agasalha</span>
            <span className="text-xs text-sidebar-foreground/70">Agasalhos que encontram quem precisa</span>
          </span>
        </NavLink>
        <button
          type="button"
          onClick={onAlternarRecolhimento}
          aria-label={recolhida ? 'Expandir menu' : 'Recolher menu'}
          aria-expanded={!recolhida}
          aria-controls="menu-lateral"
          className="hidden size-9 flex-none place-items-center rounded-[10px] bg-sidebar-foreground/10 outline-none hover:bg-sidebar-foreground/20 focus-visible:ring-3 focus-visible:ring-sidebar-ring md:grid"
        >
          <PanelLeftClose className={cn('size-5', recolhida && 'scale-x-[-1]')} aria-hidden="true" />
        </button>
        <button
          type="button"
          onClick={onFecharGaveta}
          aria-label="Fechar menu"
          className="grid size-9 flex-none place-items-center rounded-[10px] bg-sidebar-foreground/10 outline-none hover:bg-sidebar-foreground/20 focus-visible:ring-3 focus-visible:ring-sidebar-ring md:hidden"
        >
          <X className="size-5" aria-hidden="true" />
        </button>
      </div>

      <nav aria-label="Seções">
        <p className={cn('px-3 pb-2 text-[11.5px] font-bold tracking-[0.09em] text-sidebar-foreground/70 uppercase', rotulo)}>
          Navegar
        </p>
        <ul className="flex flex-col gap-1">
          {LINKS.map(({ para, rotulo: texto, Icone, fim }) => (
            <li key={para}>
              <NavLink
                to={para}
                end={fim}
                title={texto}
                onClick={onFecharGaveta}
                className={cn(CLASSE_LINK, recolhida && 'md:justify-center md:px-0')}
              >
                <Icone className="size-5 flex-none" aria-hidden="true" />
                <span className={rotulo}>{texto}</span>
              </NavLink>
            </li>
          ))}
        </ul>
        <NavLink
          to="/itens/novo"
          title="Quero doar"
          onClick={onFecharGaveta}
          className={cn(
            'mt-3.5 flex items-center gap-3 rounded-xl bg-primary px-3.5 py-[13px] font-bold text-primary-foreground shadow-[inset_0_-3px_0_oklch(0_0_0/0.14)] outline-none hover:bg-primary/90 focus-visible:ring-3 focus-visible:ring-sidebar-ring aria-[current=page]:ring-2 aria-[current=page]:ring-sidebar-ring',
            recolhida && 'md:justify-center md:px-0',
          )}
        >
          <Heart className="size-5 flex-none" aria-hidden="true" />
          <span className={rotulo}>Quero doar</span>
        </NavLink>
      </nav>

      {deficitTotal !== null && (
        <p className={cn('mt-5 rounded-xl bg-sidebar-foreground/5 p-3.5 text-[13px] leading-normal text-sidebar-foreground/80', recolhida && 'md:hidden')}>
          Neste inverno, ainda faltam{' '}
          <strong className="text-sidebar-foreground">{deficitTotal} peças</strong> nos abrigos da região.
        </p>
      )}

      <div className={cn('mt-auto flex items-center gap-3 rounded-xl bg-sidebar-foreground/10 p-3 text-xs leading-snug', recolhida && 'md:justify-center md:bg-transparent md:px-0')} title="ODS 1 · Erradicação da Pobreza">
        <span className="grid size-[38px] flex-none place-items-center rounded-[9px] bg-destructive text-lg font-extrabold text-white" aria-hidden="true">
          1
        </span>
        <span className={rotulo}>
          <strong className="block text-[13px]">ODS 1</strong>
          <span className="text-sidebar-foreground/70">Erradicação da Pobreza</span>
        </span>
      </div>
    </aside>
  );
}
