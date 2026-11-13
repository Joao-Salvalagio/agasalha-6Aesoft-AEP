import { useCallback, useState } from 'react';
import { mensagemDeErro } from '@/lib/http';
import type { DoadorCreateRequest, DoadorResponse } from '../interfaces/doador.interface';
import { doadorService } from '../services/doador.service';

interface UseCriarDoadorReturn {
  criar: (payload: DoadorCreateRequest) => Promise<DoadorResponse | null>;
  loading: boolean;
  erro: string | null;
}

export function useCriarDoador(): UseCriarDoadorReturn {
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const criar = useCallback(async (payload: DoadorCreateRequest) => {
    setLoading(true);
    setErro(null);
    try {
      return await doadorService.criar(payload);
    } catch (err) {
      setErro(mensagemDeErro(err, 'Erro ao cadastrar doador'));
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  return { criar, loading, erro };
}
