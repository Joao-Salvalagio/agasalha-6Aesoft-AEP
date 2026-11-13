import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { CampoTexto } from '@/components/CampoTexto';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogBody,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { doadorFormSchema, type DoadorFormData } from '../schemas/doador.schema';

interface DoadorDialogProps {
  onFechar: () => void;
  onSalvar: (dados: DoadorFormData) => void;
  loading: boolean;
  erro: string | null;
}

export function DoadorDialog({ onFechar, onSalvar, loading, erro }: DoadorDialogProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<DoadorFormData>({ resolver: zodResolver(doadorFormSchema) });

  return (
    <Dialog open onOpenChange={(aberto) => !aberto && onFechar()}>
      <DialogContent>
        <form onSubmit={handleSubmit(onSalvar)} noValidate className="flex min-h-0 flex-col">
          <DialogHeader>
            <DialogTitle>Cadastrar doador</DialogTitle>
            <DialogDescription>
              O contato serve só para combinar a entrega com o abrigo.
            </DialogDescription>
          </DialogHeader>
          <DialogBody>
            <div className="grid gap-4">
              <CampoTexto
                rotulo="Nome completo"
                autoComplete="name"
                erro={errors.nome?.message}
                {...register('nome')}
              />
              <CampoTexto
                rotulo="E-mail ou telefone"
                dica="Ex.: nome@exemplo.com ou (44) 90000-0000"
                autoComplete="email"
                erro={errors.contato?.message}
                {...register('contato')}
              />
            </div>
            {erro && (
              <p role="alert" className="mt-3 rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
                {erro}
              </p>
            )}
          </DialogBody>
          <DialogFooter>
            <Button type="button" variant="ghost" className="h-10 px-4" onClick={onFechar}>
              Cancelar
            </Button>
            <Button type="submit" className="h-10 px-4" disabled={loading}>
              {loading ? 'Salvando…' : 'Cadastrar doador'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
