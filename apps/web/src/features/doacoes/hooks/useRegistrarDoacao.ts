import { useCallback, useState } from 'react';
import { mensagemDeErro } from '@/lib/http';
import type { ItemResponse } from '@/features/itens/interfaces/item.interface';
import type { DoacaoFormData } from '../schemas/doacao.schema';
import { doacaoService } from '../services/doacao.service';

interface UseRegistrarDoacaoReturn {
  registrar: (dados: DoacaoFormData) => Promise<ItemResponse | null>;
  loading: boolean;
  erro: string | null;
}

export function useRegistrarDoacao(): UseRegistrarDoacaoReturn {
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const registrar = useCallback(async (dados: DoacaoFormData) => {
    setLoading(true);
    setErro(null);
    try {
      const doacao = await doacaoService.criar({ doadorId: dados.doadorId });
      return await doacaoService.adicionarItem(doacao.id, {
        tipoPeca: dados.tipoPeca,
        tamanho: dados.tamanho,
        genero: dados.genero,
        estadoConservacao: dados.estadoConservacao,
      });
    } catch (err) {
      setErro(mensagemDeErro(err, 'Erro ao registrar a doação'));
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  return { registrar, loading, erro };
}
