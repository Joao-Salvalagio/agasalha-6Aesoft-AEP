import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { itemFormSchema, type ItemFormData } from '../schemas/item.schema';
import {
  TIPO_PECA_OPTIONS,
  TAMANHO_OPTIONS,
  GENERO_OPTIONS,
  ESTADO_CONSERVACAO_OPTIONS,
} from '../constants/item.constants';

interface ItemFormProps {
  defaultValues?: Partial<ItemFormData>;
  onSubmit: (dados: ItemFormData) => void;
  onCancelar: () => void;
  loading: boolean;
  erro: string | null;
  rotuloBotao?: string;
}

export function ItemForm({
  defaultValues,
  onSubmit,
  onCancelar,
  loading,
  erro,
  rotuloBotao = 'Cadastrar Doação',
}: ItemFormProps) {
  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<ItemFormData>({
    resolver: zodResolver(itemFormSchema),
    defaultValues,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-5">
      <div className="grid grid-cols-2 gap-4">
        <div className="flex flex-col gap-1.5">
          <Label htmlFor="tipoPeca">Tipo de peça</Label>
          <Controller
            name="tipoPeca"
            control={control}
            render={({ field }) => (
              <Select
                value={field.value ?? ''}
                onValueChange={(v: string) => field.onChange(v)}
              >
                <SelectTrigger id="tipoPeca" className="w-full" aria-invalid={!!errors.tipoPeca}>
                  <SelectValue placeholder="Selecione…" />
                </SelectTrigger>
                <SelectContent>
                  {TIPO_PECA_OPTIONS.map((o) => (
                    <SelectItem key={o.value} value={o.value}>
                      {o.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          />
          {errors.tipoPeca && (
            <p className="text-xs text-destructive">{errors.tipoPeca.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="tamanho">Tamanho</Label>
          <Controller
            name="tamanho"
            control={control}
            render={({ field }) => (
              <Select
                value={field.value ?? ''}
                onValueChange={(v: string) => field.onChange(v)}
              >
                <SelectTrigger id="tamanho" className="w-full" aria-invalid={!!errors.tamanho}>
                  <SelectValue placeholder="Selecione…" />
                </SelectTrigger>
                <SelectContent>
                  {TAMANHO_OPTIONS.map((o) => (
                    <SelectItem key={o.value} value={o.value}>
                      {o.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          />
          {errors.tamanho && (
            <p className="text-xs text-destructive">{errors.tamanho.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="genero">Gênero</Label>
          <Controller
            name="genero"
            control={control}
            render={({ field }) => (
              <Select
                value={field.value ?? ''}
                onValueChange={(v: string) => field.onChange(v)}
              >
                <SelectTrigger id="genero" className="w-full" aria-invalid={!!errors.genero}>
                  <SelectValue placeholder="Selecione…" />
                </SelectTrigger>
                <SelectContent>
                  {GENERO_OPTIONS.map((o) => (
                    <SelectItem key={o.value} value={o.value}>
                      {o.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          />
          {errors.genero && (
            <p className="text-xs text-destructive">{errors.genero.message}</p>
          )}
        </div>

        <div className="flex flex-col gap-1.5">
          <Label htmlFor="estadoConservacao">Estado de conservação</Label>
          <Controller
            name="estadoConservacao"
            control={control}
            render={({ field }) => (
              <Select
                value={field.value ?? ''}
                onValueChange={(v: string) => field.onChange(v)}
              >
                <SelectTrigger id="estadoConservacao" className="w-full" aria-invalid={!!errors.estadoConservacao}>
                  <SelectValue placeholder="Selecione…" />
                </SelectTrigger>
                <SelectContent>
                  {ESTADO_CONSERVACAO_OPTIONS.map((o) => (
                    <SelectItem key={o.value} value={o.value}>
                      {o.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
          />
          {errors.estadoConservacao && (
            <p className="text-xs text-destructive">{errors.estadoConservacao.message}</p>
          )}
        </div>
      </div>

      <Separator />

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="nomeDoador">Nome do doador</Label>
        <Input
          id="nomeDoador"
          placeholder="Nome completo"
          aria-invalid={!!errors.nomeDoador}
          {...register('nomeDoador')}
        />
        {errors.nomeDoador && (
          <p className="text-xs text-destructive">{errors.nomeDoador.message}</p>
        )}
      </div>

      <div className="flex flex-col gap-1.5">
        <Label htmlFor="contatoDoador">Contato do doador</Label>
        <Input
          id="contatoDoador"
          placeholder="E-mail ou telefone"
          aria-invalid={!!errors.contatoDoador}
          {...register('contatoDoador')}
        />
        {errors.contatoDoador && (
          <p className="text-xs text-destructive">{errors.contatoDoador.message}</p>
        )}
      </div>

      {erro && (
        <p className="rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
          {erro}
        </p>
      )}

      <div className="flex flex-col gap-2 pt-1">
        <Button
          type="submit"
          disabled={loading}
          className="w-full bg-laranja text-laranja-foreground hover:bg-laranja/85"
        >
          {loading ? 'Salvando…' : rotuloBotao}
        </Button>
        <Button type="button" variant="ghost" onClick={onCancelar} className="w-full">
          Cancelar
        </Button>
      </div>
    </form>
  );
}
