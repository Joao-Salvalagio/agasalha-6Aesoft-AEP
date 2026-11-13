import { Controller, useForm, useWatch } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { CampoSelect } from '@/components/CampoSelect';
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
import {
  GENERO_OPTIONS,
  TAMANHO_OPTIONS,
  TIPO_PECA_OPTIONS,
} from '@/features/itens/constants/item.constants';
import type { DemandaResponse } from '../interfaces/abrigo.interface';
import { demandaFormSchema, type DemandaFormData } from '../schemas/demanda.schema';

interface DemandaDialogProps {
  abrigoNome: string;
  demanda: DemandaResponse | null;
  onFechar: () => void;
  onSalvar: (dados: DemandaFormData) => void;
  loading: boolean;
  erro: string | null;
}

export function DemandaDialog({ abrigoNome, demanda, onFechar, onSalvar, loading, erro }: DemandaDialogProps) {
  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<DemandaFormData>({
    resolver: zodResolver(demandaFormSchema),
    defaultValues: demanda
      ? {
          tipoPeca: demanda.tipoPeca,
          tamanho: demanda.tamanho,
          genero: demanda.genero,
          quantidadeNecessaria: demanda.quantidadeNecessaria,
          quantidadeAtendida: demanda.quantidadeAtendida,
        }
      : { quantidadeAtendida: 0 },
  });

  const [necessaria, atendida] = useWatch({ control, name: ['quantidadeNecessaria', 'quantidadeAtendida'] });
  const deficit =
    Number.isFinite(necessaria) && Number.isFinite(atendida) ? Math.max(0, necessaria - atendida) : '—';

  return (
    <Dialog open onOpenChange={(aberto) => !aberto && onFechar()}>
      <DialogContent>
        <form onSubmit={handleSubmit(onSalvar)} noValidate className="flex min-h-0 flex-col">
          <DialogHeader>
            <DialogTitle>{demanda ? 'Editar demanda' : 'Adicionar demanda'}</DialogTitle>
            <DialogDescription>{abrigoNome}</DialogDescription>
          </DialogHeader>
          <DialogBody>
            <div className="grid gap-4 sm:grid-cols-2">
              <Controller
                name="tipoPeca"
                control={control}
                render={({ field }) => (
                  <CampoSelect
                    rotulo="Tipo de peça"
                    opcoes={TIPO_PECA_OPTIONS}
                    valor={field.value ?? ''}
                    onChange={field.onChange}
                    erro={errors.tipoPeca?.message}
                  />
                )}
              />
              <Controller
                name="tamanho"
                control={control}
                render={({ field }) => (
                  <CampoSelect
                    rotulo="Tamanho"
                    opcoes={TAMANHO_OPTIONS}
                    valor={field.value ?? ''}
                    onChange={field.onChange}
                    erro={errors.tamanho?.message}
                  />
                )}
              />
              <Controller
                name="genero"
                control={control}
                render={({ field }) => (
                  <CampoSelect
                    className="sm:col-span-2"
                    rotulo="Gênero"
                    opcoes={GENERO_OPTIONS}
                    valor={field.value ?? ''}
                    onChange={field.onChange}
                    erro={errors.genero?.message}
                  />
                )}
              />
              <CampoTexto
                rotulo="Quantidade necessária"
                type="number"
                min={1}
                step={1}
                inputMode="numeric"
                erro={errors.quantidadeNecessaria?.message}
                {...register('quantidadeNecessaria', { valueAsNumber: true })}
              />
              <CampoTexto
                rotulo="Quantidade atendida"
                type="number"
                min={0}
                step={1}
                inputMode="numeric"
                erro={errors.quantidadeAtendida?.message}
                {...register('quantidadeAtendida', { valueAsNumber: true })}
              />
            </div>
            <p className="mt-4 flex justify-between gap-2 rounded-xl bg-secondary px-3.5 py-3 text-sm" aria-live="polite">
              <span>Déficit calculado</span>
              <strong>{deficit}</strong>
            </p>
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
              {loading ? 'Salvando…' : demanda ? 'Salvar alterações' : 'Adicionar demanda'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
