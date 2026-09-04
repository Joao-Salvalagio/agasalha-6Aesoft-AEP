import { useNavigate } from 'react-router-dom';
import { PackageX } from 'lucide-react';
import { Button } from '@/components/ui/button';

export function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col items-center justify-center gap-4 py-24 text-center">
      <PackageX className="size-14 text-muted-foreground opacity-40" />
      <h1 className="text-2xl font-bold">Página não encontrada</h1>
      <p className="max-w-xs text-sm text-muted-foreground">
        O endereço que você acessou não existe ou foi removido.
      </p>
      <Button onClick={() => navigate('/')} className="mt-2">
        Voltar ao Mural
      </Button>
    </div>
  );
}
