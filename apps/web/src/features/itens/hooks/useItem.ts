import { useCallback } from 'react';
import { useRequisicao } from '@/lib/useRequisicao';
import type { ItemResponse } from '../interfaces/item.interface';
import { itemService } from '../services/item.service';

interface UseItemState {
  item: ItemResponse | null;
  loading: boolean;
  erro: string | null;
  recarregar: () => void;
}

export function useItem(id: string): UseItemState {
  const buscar = useCallback(() => itemService.buscarPorId(id), [id]);
  const { dados, loading, erro, recarregar } = useRequisicao(buscar, id, 'Erro ao carregar item');
  return { item: dados, loading, erro, recarregar };
}
