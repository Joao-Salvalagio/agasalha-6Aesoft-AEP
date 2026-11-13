import { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { Controller, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { AlertCircle, Heart, UserPlus } from 'lucide-react';
import { CampoSelect } from '@/components/CampoSelect';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import type { DoadorResponse } from '@/features/doadores/interfaces/doador.interface';
import {
  ESTADO_CONSERVACAO_OPTIONS,
  GENERO_OPTIONS,
  TAMANHO_OPTIONS,
  TIPO_PECA_OPTIONS,
} from '@/features/itens/constants/item.constants';
import { doacaoFormSchema, type DoacaoFormData } from '../schemas/doacao.schema';

interface DoacaoFormProps {
  doadores: DoadorResponse[] | null;
  carregandoDoadores: boolean;
  erroDoadores: string | null;
  onSubmit: (dados: DoacaoFormData) => void;
  onCancelar: () => void;
  loading: boolean;
  erro: string | null;
}

const ROTULO_CAMPO: Record<keyof DoacaoFormData, string> = {
  doadorId: 'Doador',
  tipoPeca: 'Tipo de peça',
  tamanho: 'Tamanho',
  genero: 'Gênero',
  estadoConservacao: 'Estado de conservação',
};

const ID_CAMPO: Record<keyof DoacaoFormData, string> = {
  doadorId: 'campo-doadorId',
  tipoPeca: 'campo-tipoPeca',
  tamanho: 'campo-tamanho',
  genero: 'campo-genero',
  estadoConservacao: 'campo-estadoConservacao',
};

const CLASSE_LEGENDA = 'flex items-center gap-2.5 text-[17px] font-extrabold tracking-tight';
const CLASSE_NUMERO =
  'grid size-[26px] place-items-center rounded-full bg-primary text-[13px] text-primary-foreground';

export function DoacaoForm({
  doadores,
  carregandoDoadores,
  erroDoadores,
  onSubmit,
  onCancelar,
  loading,
  erro,
}: DoacaoFormProps) {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<DoacaoFormData>({ resolver: zodResolver(doacaoFormSchema) });
  const [tentativas, setTentativas] = useState(0);
  const resumo = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (tentativas > 0) resumo.current?.focus();
  }, [tentativas]);

  const opcoesDoadores = (doadores ?? []).map((d) => ({ value: d.id, label: `${d.nome} · ${d.contato}` }));
  const semDoadores = !carregandoDoadores && !erroDoadores && opcoesDoadores.length === 0;
  const chavesComErro = (Object.keys(errors) as (keyof DoacaoFormData)[]).filter((k) => errors[k]);

  return (
    <form
      onSubmit={handleSubmit(onSubmit, () => setTentativas((n) => n + 1))}
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
                  onClick={() => document.getElementById(ID_CAMPO[campo])?.focus()}
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
        <legend className={CLASSE_LEGENDA}>
          <span className={CLASSE_NUMERO} aria-hidden="true">1</span>
          Quem está doando
        </legend>
        <p className="mt-1 mb-4 text-[13.5px] text-muted-foreground">
          Escolha um doador já cadastrado. O contato fica com ele, não com a peça.
        </p>
        {carregandoDoadores ? (
          <div role="status">
            <span className="sr-only">Carregando doadores…</span>
            <Skeleton className="h-3 w-24" />
            <Skeleton className="mt-2 h-11 w-full rounded-xl" />
          </div>
        ) : erroDoadores ? (
          <p role="alert" className="rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
            {erroDoadores}
          </p>
        ) : semDoadores ? (
          <div className="rounded-xl border border-dashed border-input px-4 py-5 text-center">
            <p className="font-semibold">Nenhum doador cadastrado ainda</p>
            <p className="mt-1 text-[13.5px] text-muted-foreground">Cadastre quem vai doar para continuar.</p>
          </div>
        ) : (
          <Controller
            name="doadorId"
            control={control}
            render={({ field }) => (
              <CampoSelect
                id={ID_CAMPO.doadorId}
                rotulo="Doador"
                placeholder="Selecione o doador"
                opcoes={opcoesDoadores}
                valor={field.value ?? ''}
                onChange={field.onChange}
                erro={errors.doadorId?.message}
              />
            )}
          />
        )}
        <Link
          to="/doadores"
          className="mt-3 inline-flex items-center gap-1.5 text-sm font-semibold text-primary underline underline-offset-3"
        >
          <UserPlus className="size-4" aria-hidden="true" />
          Cadastrar novo doador
        </Link>
      </fieldset>

      <fieldset className="min-w-0">
        <legend className={CLASSE_LEGENDA}>
          <span className={CLASSE_NUMERO} aria-hidden="true">2</span>
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

      {erro && (
        <p role="alert" className="rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-sm text-destructive">
          {erro}
        </p>
      )}

      <div className="flex flex-wrap items-center justify-end gap-3 border-t border-border pt-5">
        <Button type="button" variant="ghost" className="h-[42px] px-[18px]" onClick={onCancelar}>
          Cancelar
        </Button>
        <Button
          type="submit"
          disabled={loading || semDoadores || carregandoDoadores}
          className="h-[42px] rounded-xl px-[18px] text-[14.5px] font-semibold"
        >
          <Heart aria-hidden="true" />
          {loading ? 'Salvando…' : 'Registrar doação'}
        </Button>
      </div>
    </form>
  );
}
