import { useCallback, useEffect, useRef, useState } from 'react';
import { mensagemDeErro } from '@/lib/http';

interface EstadoRequisicao<T> {
  dados: T | null;
  loading: boolean;
  erro: string | null;
  recarregar: () => void;
}

interface Resultado<T> {
  token: string;
  dados: T | null;
  erro: string | null;
}

export function useRequisicao<T>(
  buscar: () => Promise<T>,
  chave: string,
  mensagemErro: string,
): EstadoRequisicao<T> {
  const [resultado, setResultado] = useState<Resultado<T>>({ token: '', dados: null, erro: null });
  const [versao, setVersao] = useState(0);
  const buscarRef = useRef(buscar);
  const token = `${chave}#${versao}`;

  useEffect(() => {
    buscarRef.current = buscar;
  });

  const recarregar = useCallback(() => setVersao((v) => v + 1), []);

  useEffect(() => {
    let cancelado = false;

    buscarRef
      .current()
      .then((dados) => {
        if (!cancelado) setResultado({ token, dados, erro: null });
      })
      .catch((err) => {
        if (!cancelado) setResultado({ token, dados: null, erro: mensagemDeErro(err, mensagemErro) });
      });

    return () => {
      cancelado = true;
    };
  }, [token, mensagemErro]);

  const loading = resultado.token !== token;

  return {
    dados: loading ? null : resultado.dados,
    loading,
    erro: loading ? null : resultado.erro,
    recarregar,
  };
}
