import { useCallback, useEffect, useState } from 'react';
import type { ItemFiltroParams, ItemSummaryResponse } from '../interfaces/item.interface';
import { ehApiException, itemService } from '../services/item.service';

interface UseItensState {
  itens: ItemSummaryResponse[];
  loading: boolean;
  erro: string | null;
  recarregar: () => void;
}

export function useItens(filtros?: ItemFiltroParams): UseItensState {
  const [itens, setItens] = useState<ItemSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [versao, setVersao] = useState(0);

  const recarregar = useCallback(() => setVersao((v) => v + 1), []);

  useEffect(() => {
    let cancelado = false;
    setLoading(true);
    setErro(null);

    itemService
      .listar(filtros)
      .then((dados) => {
        if (!cancelado) setItens(dados);
      })
      .catch((err) => {
        if (!cancelado) {
          setErro(ehApiException(err) ? err.errorData.erro : 'Erro ao carregar itens');
        }
      })
      .finally(() => {
        if (!cancelado) setLoading(false);
      });

    return () => {
      cancelado = true;
    };
  }, [filtros?.tipoPeca, filtros?.tamanho, filtros?.genero, filtros?.status, versao]);

  return { itens, loading, erro, recarregar };
}
