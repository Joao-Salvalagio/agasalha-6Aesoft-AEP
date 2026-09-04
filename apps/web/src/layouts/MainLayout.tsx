import { Link, Outlet, useNavigate } from 'react-router-dom';
import { Shirt } from 'lucide-react';
import { Button } from '@/components/ui/button';

export function MainLayout() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <header className="sticky top-0 z-50 border-b border-border/50 bg-primary shadow-sm">
        <div className="mx-auto flex h-14 max-w-6xl items-center justify-between px-4">
          <Link
            to="/"
            className="flex items-center gap-2 text-primary-foreground no-underline"
          >
            <Shirt className="size-5" />
            <span className="text-lg font-semibold tracking-tight">Agasalha</span>
          </Link>

          <Button
            onClick={() => navigate('/itens/novo')}
            className="bg-laranja text-laranja-foreground hover:bg-laranja/85 h-8 px-4 text-sm font-medium"
          >
            Quero Doar
          </Button>
        </div>
      </header>

      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-8">
        <Outlet />
      </main>

      <footer className="border-t border-border py-4 text-center text-xs text-muted-foreground">
        Agasalha — ODS 1 · Erradicação da Pobreza
      </footer>
    </div>
  );
}
