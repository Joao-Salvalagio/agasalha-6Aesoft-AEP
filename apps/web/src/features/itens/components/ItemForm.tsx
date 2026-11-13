import { useEffect, useRef, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { AlertCircle, Heart } from 'lucide-react';
import { CampoSelect } from '@/components/CampoSelect';
import { CampoTexto } from '@/components/CampoTexto';
import { Button } from '@/components/ui/button';
import { itemFormSchema, type ItemFormData } from '../schemas/item.schema';
import {
  ESTADO_CONSERVACAO_OPTIONS,
  GENERO_OPTIONS,
  TAMANHO_OPTIONS,
  TIPO_PECA_OPTIONS,
} from '../constants/item.constants';

interface ItemFormProps {
  defaultValues?: Partial<ItemFormData>;
  onSubmit: (dados: ItemFormData) => void;
  onCancelar: () => void;
  loading: boolean;
  erro: string | null;
  rotuloBotao?: string;
}

const ROTULO_CAMPO: Record<keyof ItemFormData, string> = {
  tipoPeca: 'Tipo de peça',
  tamanho: 'Tamanho',
  genero: 'Gênero',
  estadoConservacao: 'Estado de conservação',
  nomeDoador: 'Nome do doador',
  contatoDoador: 'Contato do doador',
};

const ID_CAMPO: Record<keyof ItemFormData, string> = {
  tipoPeca: 'campo-tipoPeca',
  tamanho: 'campo-tamanho',
  genero: 'campo-genero',
  estadoConservacao: 'campo-estadoConservacao',
  nomeDoador: 'campo-nomeDoador',
  contatoDoador: 'campo-contatoDoador',
};

export function ItemForm({
  defaultValues,
  onSubmit,
  onCancelar,
  loading,
  erro,
  rotuloBotao = 'Registrar doação',
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
  const [tentativas, setTentativas] = useState(0);
  const resumo = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (tentativas > 0) resumo.current?.focus();
  }, [tentativas]);

  function aoInvalidar() {
    setTentativas((n) => n + 1);
  }

  function focarCampo(campo: keyof ItemFormData) {
    document.getElementById(ID_CAMPO[campo])?.focus();
  }

  const chavesComErro = (Object.keys(errors) as (keyof ItemFormData)[]).filter((k) => errors[k]);

  return (
    <form
      onSubmit={handleSubmit(onSubmit, aoInvalidar)}
      noValidate
      aria-label="Cadastro de doação"
      className="flex flex-col gap-7 rounded-xl bg-card p-6 shadow-sm ring-1 ring-border sm:p-[26px]"
    >
      {tentativas > 0 && chavesComErro.length > 0 && (
        <div
          ref={resumo}
          role="alert"
          tabIndex={-1}
          className="rounded-xl border-[1.5px] border-destructive bg-destructive/5 px-4 py-3.5 outline-none focus-visible:ring-3 focus-visible:ring-ring/50"
        >
          <h2 className="flex items-center gap-2 text-[15px] font-extrabold text-destructive">
            <AlertCircle className="size-[18px]" aria-hidden="true" />
            Faltam {chavesComErro.length} {chavesComErro.length === 1 ? 'ajuste' : 'ajustes'} para salvar
          </h2>
          <ul className="mt-2 pl-7">
            {chavesComErro.map((campo) => (
              <li key={campo} className="my-0.5 list-disc">
                <button
                  type="button"
                  onClick={() => focarCampo(campo)}
                  className="text-left text-sm font-semibold text-destructive underline underline-offset-3"
                >
                  {ROTULO_CAMPO[campo]}: {errors[campo]?.message}
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}

      <fieldset className="min-w-0">
        <legend className="flex items-center gap-2.5 text-[17px] font-extrabold tracking-tight">
          <span className="grid size-[26px] place-items-center rounded-full bg-primary text-[13px] text-primary-foreground" aria-hidden="true">
            1
          </span>
          A peça
        </legend>
        <p className="mt-1 mb-4 text-[13.5px] text-muted-foreground">
          Quanto mais preciso, melhor a sugestão de abrigo.
        </p>
        <div className="grid gap-4 sm:grid-cols-2">
          <Controller
            name="tipoPeca"
            control={control}
            render={({ field }) => (
              <CampoSelect
                id={ID_CAMPO.tipoPeca}
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
                id={ID_CAMPO.tamanho}
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
                id={ID_CAMPO.genero}
                rotulo="Gênero"
                opcoes={GENERO_OPTIONS}
                valor={field.value ?? ''}
                onChange={field.onChange}
                erro={errors.genero?.message}
              />
            )}
          />
          <Controller
            name="estadoConservacao"
            control={control}
            render={({ field }) => (
              <CampoSelect
                id={ID_CAMPO.estadoConservacao}
                rotulo="Estado de conservação"
                opcoes={ESTADO_CONSERVACAO_OPTIONS}
                valor={field.value ?? ''}
                onChange={field.onChange}
                erro={errors.estadoConservacao?.message}
              />
            )}
          />
        </div>
      </fieldset>

      <fieldset className="min-w-0">
        <legend className="flex items-center gap-2.5 text-[17px] font-extrabold tracking-tight">
          <span className="grid size-[26px] place-items-center rounded-full bg-primary text-[13px] text-primary-foreground" aria-hidden="true">
            2
          </span>
          Quem está doando
        </legend>
        <p className="mt-1 mb-4 text-[13.5px] text-muted-foreground">
          Usado apenas para o abrigo combinar a entrega com você.
        </p>
        <div className="grid gap-4 sm:grid-cols-2">
          <CampoTexto
            id={ID_CAMPO.nomeDoador}
            rotulo="Nome completo"
            autoComplete="name"
            erro={errors.nomeDoador?.message}
            {...register('nomeDoador')}
          />
          <CampoTexto
            id={ID_CAMPO.contatoDoador}
            rotulo="E-mail ou telefone"
            dica="Ex.: nome@exemplo.com ou (44) 90000-0000"
            autoComplete="email"
            erro={errors.contatoDoador?.message}
            {...register('contatoDoador')}
          />
        </div>
      </fieldset>

      {erro && (
        <p role="alert" className="rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
          {erro}
        </p>
      )}

      <div className="flex flex-wrap items-center justify-end gap-3 border-t border-border pt-5">
        <Button type="button" variant="ghost" className="h-[42px] px-[18px]" onClick={onCancelar}>
          Cancelar
        </Button>
        <Button type="submit" disabled={loading} className="h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold">
          <Heart aria-hidden="true" />
          {loading ? 'Salvando…' : rotuloBotao}
        </Button>
      </div>
    </form>
  );
}
