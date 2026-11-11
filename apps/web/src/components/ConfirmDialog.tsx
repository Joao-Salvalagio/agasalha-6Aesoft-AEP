import { Trash2 } from 'lucide-react';
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

interface ConfirmDialogProps {
  aberto: boolean;
  onAbertoChange: (aberto: boolean) => void;
  titulo: string;
  descricao: string;
  rotuloConfirmar: string;
  onConfirmar: () => void;
  processando?: boolean;
  erro?: string | null;
}

export function ConfirmDialog({
  aberto,
  onAbertoChange,
  titulo,
  descricao,
  rotuloConfirmar,
  onConfirmar,
  processando = false,
  erro,
}: ConfirmDialogProps) {
  return (
    <Dialog open={aberto} onOpenChange={onAbertoChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{titulo}</DialogTitle>
        </DialogHeader>
        <DialogBody>
          <DialogDescription>{descricao}</DialogDescription>
          {erro && (
            <p className="mt-3 rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
              {erro}
            </p>
          )}
        </DialogBody>
        <DialogFooter>
          <Button variant="ghost" className="h-10 px-4" onClick={() => onAbertoChange(false)}>
            Cancelar
          </Button>
          <Button
            className="h-10 bg-destructive px-4 text-white hover:bg-destructive/90"
            disabled={processando}
            onClick={onConfirmar}
          >
            <Trash2 aria-hidden="true" />
            {processando ? 'Processando…' : rotuloConfirmar}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
