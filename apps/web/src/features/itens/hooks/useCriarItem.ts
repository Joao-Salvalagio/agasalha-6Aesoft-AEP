import { useCallback, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { ItemCreateRequest, ItemResponse } from '../interfaces/item.interface';
import { ehApiException, itemService } from '../services/item.service';

interface UseCriarItemReturn {
  criar: (payload: ItemCreateRequest) => Promise<void>;
  loading: boolean;
  erro: string | null;
}

export function useCriarItem(): UseCriarItemReturn {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const criar = useCallback(
    async (payload: ItemCreateRequest) => {
      setLoading(true);
      setErro(null);
      try {
        const item: ItemResponse = await itemService.criar(payload);
        navigate(`/itens/${item.id}`);
      } catch (err) {
        setErro(
          ehApiException(err) ? err.errorData.erro : 'Erro ao cadastrar doação',
        );
        setLoading(false);
      }
    },
    [navigate],
  );

  return { criar, loading, erro };
}
