import { ehApiException, handleResponse, JSON_HEADERS } from '@/lib/http';
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
};
