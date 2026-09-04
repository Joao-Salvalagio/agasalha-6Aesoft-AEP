import { useEffect, useState } from 'react';
import type { ItemResponse } from '../interfaces/item.interface';
import { ehApiException, itemService } from '../services/item.service';

interface UseItemState {
  item: ItemResponse | null;
  loading: boolean;
  erro: string | null;
}

export function useItem(id: string): UseItemState {
  const [item, setItem] = useState<ItemResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    let cancelado = false;
    setLoading(true);
    setErro(null);

    itemService
      .buscarPorId(id)
      .then((dados) => {
        if (!cancelado) setItem(dados);
      })
      .catch((err) => {
        if (!cancelado) {
          setErro(ehApiException(err) ? err.errorData.erro : 'Erro ao carregar item');
        }
      })
      .finally(() => {
        if (!cancelado) setLoading(false);
      });

    return () => {
      cancelado = true;
    };
  }, [id]);

  return { item, loading, erro };
}
