import { Link, Outlet, useNavigate } from 'react-router-dom';
import { HeartHandshake } from 'lucide-react';
import { Button } from '@/components/ui/button';

export function MainLayout() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <header className="sticky top-0 z-10 flex items-center justify-between border-b bg-background px-6 py-4 shadow-sm">
        <Link 
          to="/" 
          className="flex items-center gap-2 text-xl font-bold text-foreground hover:opacity-90"
        >
          <HeartHandshake className="size-6 text-primary" />
          <span>Agasalha</span>
        </Link>
        <Button 
          onClick={() => navigate('/itens/novo')}
          className="h-9 px-4 text-sm font-medium"
        >
          Quero Doar
        </Button>
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
