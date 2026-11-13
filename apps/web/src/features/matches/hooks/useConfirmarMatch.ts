import { useCallback, useState } from 'react';
import { mensagemDeErro } from '@/lib/http';
import type { ItemResponse } from '@/features/itens/interfaces/item.interface';
import { matchService } from '../services/match.service';

interface UseConfirmarMatchReturn {
  confirmar: (itemId: string) => Promise<ItemResponse | null>;
  loading: boolean;
  erro: string | null;
}

export function useConfirmarMatch(): UseConfirmarMatchReturn {
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const confirmar = useCallback(async (itemId: string) => {
    setLoading(true);
    setErro(null);
    try {
      return await matchService.confirmar(itemId);
    } catch (err) {
      setErro(mensagemDeErro(err, 'Não foi possível adicionar a peça ao abrigo'));
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  return { confirmar, loading, erro };
}
