import { useId, type ComponentProps } from 'react';
import { AlertCircle } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/lib/utils';

interface CampoTextoProps extends Omit<ComponentProps<'input'>, 'id'> {
  rotulo: string;
  erro?: string;
  dica?: string;
  id?: string;
  classeContainer?: string;
}

export function CampoTexto({
  rotulo,
  erro,
  dica,
  id,
  classeContainer,
  className,
  ...props
}: CampoTextoProps) {
  const gerado = useId();
  const campoId = id ?? gerado;
  const erroId = `${campoId}-erro`;
  const dicaId = `${campoId}-dica`;
  const descricao = [erro ? erroId : '', dica ? dicaId : ''].filter(Boolean).join(' ') || undefined;

  return (
    <div className={cn('flex min-w-0 flex-col gap-1.5', classeContainer)}>
      <Label htmlFor={campoId} className="text-[13.5px] font-semibold">
        {rotulo}
      </Label>
      <Input
        id={campoId}
        aria-invalid={!!erro}
        aria-describedby={descricao}
        className={cn('h-11 rounded-xl px-3 text-[15px]', className)}
        {...props}
      />
      {dica && !erro && (
        <p id={dicaId} className="text-[13px] text-muted-foreground">
          {dica}
        </p>
      )}
      {erro && (
        <p id={erroId} className="flex items-start gap-1.5 text-[13px] font-semibold text-destructive">
          <AlertCircle className="mt-0.5 size-4 shrink-0" aria-hidden="true" />
          {erro}
        </p>
      )}
    </div>
  );
}
