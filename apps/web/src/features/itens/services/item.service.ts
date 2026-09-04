import type {
  ApiError,
  ItemCreateRequest,
  ItemFiltroParams,
  ItemResponse,
  ItemSummaryResponse,
  ItemUpdateRequest,
} from '../interfaces/item.interface';

const API_BASE_URL = '/api/itens';

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

async function handleResponse<T>(response: Response): Promise<T> {
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

export const itemService = {
  async listar(filtros?: ItemFiltroParams): Promise<ItemSummaryResponse[]> {
    const query = new URLSearchParams();
    if (filtros?.tipoPeca) query.append('tipoPeca', filtros.tipoPeca);
    if (filtros?.tamanho) query.append('tamanho', filtros.tamanho);
    if (filtros?.genero) query.append('genero', filtros.genero);
    if (filtros?.status) query.append('status', filtros.status);

    const url = query.toString() ? `${API_BASE_URL}?${query.toString()}` : API_BASE_URL;
    const response = await fetch(url);
    return handleResponse<ItemSummaryResponse[]>(response);
  },

  async buscarPorId(id: string): Promise<ItemResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}`);
    return handleResponse<ItemResponse>(response);
  },

  async criar(payload: ItemCreateRequest): Promise<ItemResponse> {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return handleResponse<ItemResponse>(response);
  },

  async atualizar(id: string, payload: ItemUpdateRequest): Promise<ItemResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    return handleResponse<ItemResponse>(response);
  },

  async reservar(id: string): Promise<ItemResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}/reserva`, { method: 'POST' });
    return handleResponse<ItemResponse>(response);
  },

  async entregar(id: string): Promise<ItemResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}/entrega`, { method: 'POST' });
    return handleResponse<ItemResponse>(response);
  },

  async excluir(id: string): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/${id}`, { method: 'DELETE' });
    return handleResponse<void>(response);
  },
};
