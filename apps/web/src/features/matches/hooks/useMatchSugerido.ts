import { useCallback } from 'react';
import { useRequisicao } from '@/lib/useRequisicao';
import { matchService } from '../services/match.service';

export function useMatchSugerido(itemId: string, ativo: boolean) {
  const buscar = useCallback(
    () => (ativo ? matchService.sugerir(itemId) : Promise.resolve(null)),
    [itemId, ativo],
  );
  return useRequisicao(buscar, `${itemId}:${ativo}`, 'Erro ao buscar sugestão de abrigo');
}
