import { Button } from '@/components/ui/button';
import { CampoSelect, type OpcaoSelect } from '@/components/CampoSelect';
import type { ItemFiltroParams } from '../interfaces/item.interface';
import {
  GENERO_OPTIONS,
  STATUS_OPTIONS,
  TAMANHO_OPTIONS,
  TIPO_PECA_OPTIONS,
} from '../constants/item.constants';

interface FiltrosMuralProps {
  filtros: ItemFiltroParams;
  onFiltrar: (filtros: ItemFiltroParams) => void;
}

function comTodos(rotulo: string, opcoes: readonly OpcaoSelect[]): OpcaoSelect[] {
  return [{ value: '', label: rotulo }, ...opcoes];
}

export function FiltrosMural({ filtros, onFiltrar }: FiltrosMuralProps) {
  const temFiltroAtivo = Object.values(filtros).some(Boolean);

  function alterar(campo: keyof ItemFiltroParams, valor: string) {
    onFiltrar({ ...filtros, [campo]: valor || undefined });
  }

  return (
    <section
      aria-labelledby="titulo-filtros"
      className="mb-3.5 grid grid-cols-2 items-end gap-3.5 rounded-xl bg-card p-3.5 shadow-sm ring-1 ring-border lg:grid-cols-[repeat(4,minmax(0,1fr))_auto] lg:px-[18px] lg:py-4"
    >
      <h2 id="titulo-filtros" className="sr-only">
        Filtrar peças
      </h2>
      <CampoSelect
        rotulo="Tipo de peça"
        opcoes={comTodos('Todos os tipos', TIPO_PECA_OPTIONS)}
        valor={filtros.tipoPeca ?? ''}
        onChange={(v) => alterar('tipoPeca', v)}
      />
      <CampoSelect
        rotulo="Tamanho"
        opcoes={comTodos('Todos os tamanhos', TAMANHO_OPTIONS)}
        valor={filtros.tamanho ?? ''}
        onChange={(v) => alterar('tamanho', v)}
      />
      <CampoSelect
        rotulo="Gênero"
        opcoes={comTodos('Todos', GENERO_OPTIONS)}
        valor={filtros.genero ?? ''}
        onChange={(v) => alterar('genero', v)}
      />
      <CampoSelect
        rotulo="Status"
        opcoes={comTodos('Todos os status', STATUS_OPTIONS)}
        valor={filtros.status ?? ''}
        onChange={(v) => alterar('status', v)}
      />
      <Button
        variant="ghost"
        className="col-span-2 h-10 justify-self-start px-3 lg:col-span-1"
        disabled={!temFiltroAtivo}
        onClick={() => onFiltrar({})}
      >
        Limpar filtros
      </Button>
    </section>
  );
}
