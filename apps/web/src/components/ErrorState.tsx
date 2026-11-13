import { CloudOff, RefreshCw } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface ErrorStateProps {
  titulo: string;
  descricao?: string;
  onTentarNovamente: () => void;
}

export function ErrorState({
  titulo,
  descricao = 'Pode ser só uma instabilidade passageira. Nenhuma informação foi perdida.',
  onTentarNovamente,
}: ErrorStateProps) {
  return (
    <div
      role="alert"
      className="flex flex-col items-center rounded-xl bg-card px-6 py-12 text-center shadow-sm ring-1 ring-border"
    >
      <div className="grid size-16 place-items-center rounded-full bg-destructive/10 text-destructive">
        <CloudOff className="size-8" aria-hidden="true" />
      </div>
      <h2 className="mt-4 text-xl font-extrabold tracking-tight">{titulo}</h2>
      <p className="mx-auto mt-2 mb-5 max-w-[46ch] text-muted-foreground">{descricao}</p>
      <Button className="h-10 px-4" onClick={onTentarNovamente}>
        <RefreshCw aria-hidden="true" />
        Tentar de novo
      </Button>
    </div>
  );
}
