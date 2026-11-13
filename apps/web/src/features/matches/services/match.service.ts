import { ehApiException, handleResponse, JSON_HEADERS } from '@/lib/http';
import type { ItemResponse } from '@/features/itens/interfaces/item.interface';
import type { MatchResponse } from '../interfaces/match.interface';

const API_BASE_URL = '/api/matches';

export const matchService = {
  async sugerir(itemId: string): Promise<MatchResponse | null> {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: JSON_HEADERS,
      body: JSON.stringify({ itemId }),
    });
    try {
      return await handleResponse<MatchResponse>(response);
    } catch (err) {
      if (ehApiException(err) && err.errorData.status === 404) return null;
      throw err;
    }
  },

  async confirmar(itemId: string): Promise<ItemResponse> {
    const response = await fetch(`${API_BASE_URL}/confirmacao`, {
      method: 'POST',
      headers: JSON_HEADERS,
      body: JSON.stringify({ itemId }),
    });
    return handleResponse<ItemResponse>(response);
  },
};
