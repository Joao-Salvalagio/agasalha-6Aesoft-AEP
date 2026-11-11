import { useCallback } from 'react';
import { useRequisicao } from '@/lib/useRequisicao';
import type { ItemFiltroParams, ItemSummaryResponse } from '../interfaces/item.interface';
import { itemService } from '../services/item.service';

interface UseItensState {
  itens: ItemSummaryResponse[];
  loading: boolean;
  erro: string | null;
  recarregar: () => void;
}

export function useItens(filtros?: ItemFiltroParams): UseItensState {
  const tipoPeca = filtros?.tipoPeca;
  const tamanho = filtros?.tamanho;
  const genero = filtros?.genero;
  const status = filtros?.status;

  const buscar = useCallback(
    () => itemService.listar({ tipoPeca, tamanho, genero, status }),
    [tipoPeca, tamanho, genero, status],
  );

  const chave = [tipoPeca, tamanho, genero, status].join('|');
  const { dados, loading, erro, recarregar } = useRequisicao(buscar, chave, 'Erro ao carregar itens');

  return { itens: dados ?? [], loading, erro, recarregar };
}
