import { useEffect, useRef, useState } from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import { HeartHandshake, Menu } from 'lucide-react';
import { AppSidebar } from '@/components/AppSidebar';
import { buttonVariants } from '@/components/ui/button';
import { useDeficitTotal } from '@/features/abrigos/hooks/useDeficitTotal';
import { useMediaQuery } from '@/lib/useMediaQuery';
import { cn } from '@/lib/utils';

export function MainLayout() {
  const [recolhida, setRecolhida] = useState(false);
  const [gavetaAberta, setGavetaAberta] = useState(false);
  const ehCelular = useMediaQuery('(max-width: 767px)');
  const { pathname } = useLocation();
  const botaoMenu = useRef<HTMLButtonElement>(null);
  const deficitTotal = useDeficitTotal();

  const gavetaVisivel = ehCelular && gavetaAberta;

  function fecharGaveta() {
    setGavetaAberta(false);
  }

  useEffect(() => {
    if (!gavetaVisivel) return;
    function aoTeclar(e: KeyboardEvent) {
      if (e.key === 'Escape') {
        setGavetaAberta(false);
        botaoMenu.current?.focus();
      }
    }
    document.addEventListener('keydown', aoTeclar);
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', aoTeclar);
      document.body.style.overflow = '';
    };
  }, [gavetaVisivel]);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [pathname]);

  return (
    <div
      className="grid min-h-screen grid-cols-1 transition-[grid-template-columns] duration-200 md:grid-cols-[var(--largura-menu)_minmax(0,1fr)]"
      style={{ '--largura-menu': recolhida ? '84px' : '272px' } as React.CSSProperties}
    >
      <header className="sticky top-0 z-40 flex items-center gap-2.5 border-b border-border bg-background/90 px-3.5 py-2.5 backdrop-blur-sm md:hidden">
        <button
          ref={botaoMenu}
          type="button"
          aria-label="Abrir menu"
          aria-expanded={gavetaVisivel}
          aria-controls="menu-lateral"
          onClick={() => setGavetaAberta(true)}
          className={cn(buttonVariants({ variant: 'ghost', size: 'icon' }), 'size-10')}
        >
          <Menu className="size-5" aria-hidden="true" />
        </button>
        <Link to="/" className="mr-auto flex items-center gap-2.5 text-lg font-extrabold">
          <span className="grid size-[34px] place-items-center rounded-[10px] bg-primary text-primary-foreground">
            <HeartHandshake className="size-5" aria-hidden="true" />
          </span>
          Agasalha
        </Link>
        <Link to="/itens/novo" className={cn(buttonVariants(), 'h-9 rounded-xl px-3.5 text-sm font-bold')}>
          Quero doar
        </Link>
      </header>

      <div
        aria-hidden="true"
        onClick={fecharGaveta}
        className={cn(
          'fixed inset-0 z-55 bg-foreground/45 transition-opacity duration-200 md:hidden',
          gavetaVisivel ? 'opacity-100' : 'pointer-events-none opacity-0',
        )}
      />

      <AppSidebar
        recolhida={recolhida && !ehCelular}
        gavetaAberta={gavetaAberta}
        inerte={ehCelular && !gavetaAberta}
        deficitTotal={deficitTotal}
        onAlternarRecolhimento={() => setRecolhida((r) => !r)}
        onFecharGaveta={fecharGaveta}
      />

      <div className="min-w-0">
        <main className="mx-auto w-full max-w-[1180px] px-4 pt-5 pb-16 md:px-[clamp(1rem,4vw,3.25rem)] md:pt-7">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
