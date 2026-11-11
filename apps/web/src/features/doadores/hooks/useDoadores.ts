import { useCallback } from 'react';
import { useRequisicao } from '@/lib/useRequisicao';
import { doadorService } from '../services/doador.service';

export function useDoadores() {
  const buscar = useCallback(() => doadorService.listar(), []);
  return useRequisicao(buscar, 'doadores', 'Erro ao carregar doadores');
}
