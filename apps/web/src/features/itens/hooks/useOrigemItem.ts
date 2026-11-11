import { useCallback } from 'react';
import { useRequisicao } from '@/lib/useRequisicao';
import { abrigoService } from '@/features/abrigos/services/abrigo.service';
import { doacaoService } from '@/features/doacoes/services/doacao.service';
import { doadorService } from '@/features/doadores/services/doador.service';
import type { ItemResponse } from '../interfaces/item.interface';

export interface OrigemItem {
  doador: { nome: string; contato: string } | null;
  destino: { id: string; nome: string; cidade: string; estado: string } | null;
}

export function useOrigemItem(item: ItemResponse | null) {
  const buscar = useCallback(async (): Promise<OrigemItem> => {
    if (!item) return { doador: null, destino: null };

    let doador = item.nomeDoador
      ? { nome: item.nomeDoador, contato: item.contatoDoador ?? '' }
      : null;
    let abrigoId = item.abrigoId;

    if (item.doacaoId) {
      const doacao = await doacaoService.buscarPorId(item.doacaoId);
      abrigoId = abrigoId ?? doacao.abrigoId;
      if (!doador) {
        const d = await doadorService.buscarPorId(doacao.doadorId);
        doador = { nome: d.nome, contato: d.contato };
      }
    }

    const abrigo = abrigoId ? await abrigoService.buscarPorId(abrigoId) : null;
    return {
      doador,
      destino: abrigo
        ? {
            id: abrigo.id,
            nome: abrigo.nome,
            cidade: abrigo.endereco.cidade,
            estado: abrigo.endereco.estado,
          }
        : null,
    };
  }, [item]);

  const chave = item ? `${item.id}:${item.doacaoId}:${item.abrigoId}` : 'sem-item';
  return useRequisicao(buscar, chave, 'Erro ao carregar a origem da peça');
}
