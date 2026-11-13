import { useCallback, useState } from 'react';
import { mensagemDeErro } from '@/lib/http';
import type { ItemCreateRequest, ItemResponse } from '../interfaces/item.interface';
import { itemService } from '../services/item.service';

interface UseCriarItemReturn {
  criar: (payload: ItemCreateRequest) => Promise<ItemResponse | null>;
  loading: boolean;
  erro: string | null;
}

export function useCriarItem(): UseCriarItemReturn {
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const criar = useCallback(async (payload: ItemCreateRequest) => {
    setLoading(true);
    setErro(null);
    try {
      return await itemService.criar(payload);
    } catch (err) {
      setErro(mensagemDeErro(err, 'Erro ao cadastrar doação'));
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  return { criar, loading, erro };
}
