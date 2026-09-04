import { useCallback, useState } from 'react';
import type { ItemResponse, ItemUpdateRequest } from '../interfaces/item.interface';
import { ehApiException, itemService } from '../services/item.service';

interface AcaoState {
  loading: boolean;
  erro: string | null;
}

interface UseItemActionsReturn {
  reservar: (id: string) => Promise<ItemResponse | null>;
  entregar: (id: string) => Promise<ItemResponse | null>;
  excluir: (id: string) => Promise<boolean>;
  atualizar: (id: string, payload: ItemUpdateRequest) => Promise<ItemResponse | null>;
  estado: AcaoState;
}

export function useItemActions(): UseItemActionsReturn {
  const [estado, setEstado] = useState<AcaoState>({ loading: false, erro: null });

  const executar = useCallback(async <T>(acao: () => Promise<T>): Promise<T | null> => {
    setEstado({ loading: true, erro: null });
    try {
      const resultado = await acao();
      setEstado({ loading: false, erro: null });
      return resultado;
    } catch (err) {
      const mensagem =
        ehApiException(err) ? err.errorData.erro : 'Ocorreu um erro inesperado';
      setEstado({ loading: false, erro: mensagem });
      return null;
    }
  }, []);

  const reservar = useCallback(
    (id: string) => executar(() => itemService.reservar(id)),
    [executar],
  );

  const entregar = useCallback(
    (id: string) => executar(() => itemService.entregar(id)),
    [executar],
  );

  const excluir = useCallback(
    async (id: string) => {
      const resultado = await executar(() => itemService.excluir(id));
      return resultado !== null;
    },
    [executar],
  );

  const atualizar = useCallback(
    (id: string, payload: ItemUpdateRequest) =>
      executar(() => itemService.atualizar(id, payload)),
    [executar],
  );

  return { reservar, entregar, excluir, atualizar, estado };
}
