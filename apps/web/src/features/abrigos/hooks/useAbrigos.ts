import { useCallback } from 'react';
import { useRequisicao } from '@/lib/useRequisicao';
import { abrigoService } from '../services/abrigo.service';

export function useAbrigos() {
  const buscar = useCallback(() => abrigoService.listar(), []);
  return useRequisicao(buscar, 'abrigos', 'Erro ao carregar abrigos');
}
