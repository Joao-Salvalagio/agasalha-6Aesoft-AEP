import { useState } from 'react';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Button } from '@/components/ui/button';
import { X } from 'lucide-react';
import type { ItemFiltroParams } from '../interfaces/item.interface';
import {
  GENERO_OPTIONS,
  STATUS_OPTIONS,
  TAMANHO_OPTIONS,
  TIPO_PECA_OPTIONS,
} from '../constants/item.constants';

interface FiltrosMuralProps {
  onFiltrar: (filtros: ItemFiltroParams) => void;
}

const VAZIO = '';

export function FiltrosMural({ onFiltrar }: FiltrosMuralProps) {
  const [tipoPeca, setTipoPeca] = useState<string>(VAZIO);
  const [tamanho, setTamanho] = useState<string>(VAZIO);
  const [genero, setGenero] = useState<string>(VAZIO);
  const [status, setStatus] = useState<string>(VAZIO);

  const temFiltroAtivo = tipoPeca !== VAZIO || tamanho !== VAZIO || genero !== VAZIO || status !== VAZIO;

  function handleChange(
    campo: keyof ItemFiltroParams,
    valor: string,
    setValor: (v: string) => void,
  ) {
    setValor(valor);
    const proximos: ItemFiltroParams = {
      tipoPeca: tipoPeca as ItemFiltroParams['tipoPeca'] || undefined,
      tamanho: tamanho as ItemFiltroParams['tamanho'] || undefined,
      genero: genero as ItemFiltroParams['genero'] || undefined,
      status: status as ItemFiltroParams['status'] || undefined,
      [campo]: valor || undefined,
    };
    onFiltrar(proximos);
  }

  function limpar() {
    setTipoPeca(VAZIO);
    setTamanho(VAZIO);
    setGenero(VAZIO);
    setStatus(VAZIO);
    onFiltrar({});
  }

  return (
    <div className="flex flex-wrap items-center gap-2">
      <Select
        value={tipoPeca}
        onValueChange={(v: string) => handleChange('tipoPeca', v, setTipoPeca)}
      >
        <SelectTrigger className="w-40">
          <SelectValue placeholder="Tipo de peça" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value={VAZIO}>Todos os tipos</SelectItem>
          {TIPO_PECA_OPTIONS.map((o) => (
            <SelectItem key={o.value} value={o.value}>
              {o.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>

      <Select
        value={tamanho}
        onValueChange={(v: string) => handleChange('tamanho', v, setTamanho)}
      >
        <SelectTrigger className="w-32">
          <SelectValue placeholder="Tamanho" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value={VAZIO}>Todos</SelectItem>
          {TAMANHO_OPTIONS.map((o) => (
            <SelectItem key={o.value} value={o.value}>
              {o.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>

      <Select
        value={genero}
        onValueChange={(v: string) => handleChange('genero', v, setGenero)}
      >
        <SelectTrigger className="w-36">
          <SelectValue placeholder="Gênero" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value={VAZIO}>Todos</SelectItem>
          {GENERO_OPTIONS.map((o) => (
            <SelectItem key={o.value} value={o.value}>
              {o.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>

      <Select
        value={status}
        onValueChange={(v: string) => handleChange('status', v, setStatus)}
      >
        <SelectTrigger className="w-36">
          <SelectValue placeholder="Status" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value={VAZIO}>Todos</SelectItem>
          {STATUS_OPTIONS.map((o) => (
            <SelectItem key={o.value} value={o.value}>
              {o.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>

      {temFiltroAtivo && (
        <Button
          variant="ghost"
          size="sm"
          onClick={limpar}
          className="gap-1 text-muted-foreground"
        >
          <X className="size-3.5" />
          Limpar
        </Button>
      )}
    </div>
  );
}
