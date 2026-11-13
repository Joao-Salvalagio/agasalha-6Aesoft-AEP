export interface ApiError {
  timestamp: string;
  status: number;
  erro: string;
  detalhes: string[];
}

export interface ApiExceptionData {
  errorData: ApiError;
}

export function criarApiException(errorData: ApiError): Error & ApiExceptionData {
  const err = new Error(errorData.erro || 'Erro na requisição') as Error & ApiExceptionData;
  err.errorData = errorData;
  return err;
}

export function ehApiException(err: unknown): err is Error & ApiExceptionData {
  return err instanceof Error && 'errorData' in err;
}

export function mensagemDeErro(err: unknown, padrao: string): string {
  return ehApiException(err) ? err.errorData.erro : padrao;
}

export async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorData: ApiError = await response.json().catch(() => ({
      timestamp: new Date().toISOString(),
      status: response.status,
      erro: `Erro na comunicação com o servidor (${response.status})`,
      detalhes: [],
    }));
    throw criarApiException(errorData);
  }
  if (response.status === 204) {
    return {} as T;
  }
  return response.json();
}

export const JSON_HEADERS = { 'Content-Type': 'application/json' };
