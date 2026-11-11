import { useCallback, useState } from 'react';
import { mensagemDeErro } from '@/lib/http';
import type { DemandaRequest } from '../interfaces/abrigo.interface';
import { abrigoService } from '../services/abrigo.service';

interface AcaoState {
  loading: boolean;
  erro: string | null;
}

interface UseDemandaActionsReturn {
  criar: (abrigoId: string, payload: DemandaRequest) => Promise<boolean>;
  atualizar: (abrigoId: string, posicao: number, payload: DemandaRequest) => Promise<boolean>;
  remover: (abrigoId: string, posicao: number) => Promise<boolean>;
  limparErro: () => void;
  estado: AcaoState;
}

export function useDemandaActions(): UseDemandaActionsReturn {
  const [estado, setEstado] = useState<AcaoState>({ loading: false, erro: null });

  const executar = useCallback(async (acao: () => Promise<unknown>): Promise<boolean> => {
    setEstado({ loading: true, erro: null });
    try {
      await acao();
      setEstado({ loading: false, erro: null });
      return true;
    } catch (err) {
      setEstado({ loading: false, erro: mensagemDeErro(err, 'Ocorreu um erro inesperado') });
      return false;
    }
  }, []);

  const criar = useCallback(
    (abrigoId: string, payload: DemandaRequest) =>
      executar(() => abrigoService.criarDemanda(abrigoId, payload)),
    [executar],
  );

  const atualizar = useCallback(
    (abrigoId: string, posicao: number, payload: DemandaRequest) =>
      executar(() => abrigoService.atualizarDemanda(abrigoId, posicao, payload)),
    [executar],
  );

  const remover = useCallback(
    (abrigoId: string, posicao: number) =>
      executar(() => abrigoService.removerDemanda(abrigoId, posicao)),
    [executar],
  );

  const limparErro = useCallback(() => setEstado({ loading: false, erro: null }), []);

  return { criar, atualizar, remover, limparErro, estado };
}
