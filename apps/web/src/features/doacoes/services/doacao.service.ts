import { handleResponse, JSON_HEADERS } from '@/lib/http';
import type { ItemResponse } from '@/features/itens/interfaces/item.interface';
import type {
  DoacaoCreateRequest,
  DoacaoResponse,
  ItemDoacaoRequest,
} from '../interfaces/doacao.interface';

const API_BASE_URL = '/api/doacoes';

export const doacaoService = {
  async buscarPorId(id: string): Promise<DoacaoResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}`);
    return handleResponse<DoacaoResponse>(response);
  },

  async criar(payload: DoacaoCreateRequest): Promise<DoacaoResponse> {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: JSON_HEADERS,
      body: JSON.stringify(payload),
    });
    return handleResponse<DoacaoResponse>(response);
  },

  async adicionarItem(doacaoId: string, payload: ItemDoacaoRequest): Promise<ItemResponse> {
    const response = await fetch(`${API_BASE_URL}/${doacaoId}/itens`, {
      method: 'POST',
      headers: JSON_HEADERS,
      body: JSON.stringify(payload),
    });
    return handleResponse<ItemResponse>(response);
  },
};
