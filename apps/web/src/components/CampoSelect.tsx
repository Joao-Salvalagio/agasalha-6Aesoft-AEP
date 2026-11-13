import { useId } from 'react';
import { AlertCircle } from 'lucide-react';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Label } from '@/components/ui/label';
import { cn } from '@/lib/utils';

export interface OpcaoSelect {
  value: string;
  label: string;
}

interface CampoSelectProps {
  rotulo: string;
  opcoes: readonly OpcaoSelect[];
  valor: string;
  onChange: (valor: string) => void;
  placeholder?: string;
  erro?: string;
  className?: string;
  id?: string;
}

export function CampoSelect({
  rotulo,
  opcoes,
  valor,
  onChange,
  placeholder = 'Selecione',
  erro,
  className,
  id,
}: CampoSelectProps) {
  const gerado = useId();
  const campoId = id ?? gerado;
  const erroId = `${campoId}-erro`;

  return (
    <div className={cn('flex min-w-0 flex-col gap-1.5', className)}>
      <Label htmlFor={campoId} className="text-[13.5px] font-semibold">
        {rotulo}
      </Label>
      <Select
        items={opcoes}
        value={valor}
        onValueChange={(v: string | null) => onChange(v ?? '')}
      >
        <SelectTrigger
          id={campoId}
          className="h-11 w-full rounded-xl px-3 text-[15px]"
          aria-invalid={!!erro}
          aria-describedby={erro ? erroId : undefined}
        >
          <SelectValue placeholder={placeholder} />
        </SelectTrigger>
        <SelectContent>
          {opcoes.map((o) => (
            <SelectItem key={o.value} value={o.value}>
              {o.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
      {erro && (
        <p id={erroId} className="flex items-start gap-1.5 text-[13px] font-semibold text-destructive">
          <AlertCircle className="mt-0.5 size-4 shrink-0" aria-hidden="true" />
          {erro}
        </p>
      )}
    </div>
  );
}
