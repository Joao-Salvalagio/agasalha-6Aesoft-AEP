import { useCallback } from 'react';
import { useRequisicao } from '@/lib/useRequisicao';
import { abrigoService } from '../services/abrigo.service';

export function useAbrigo(id: string) {
  const buscar = useCallback(() => abrigoService.buscarPorId(id), [id]);
  return useRequisicao(buscar, id, 'Erro ao carregar abrigo');
}
