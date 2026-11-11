import { handleResponse } from '@/lib/http';
import type { DoacaoResponse } from '../interfaces/doacao.interface';

const API_BASE_URL = '/api/doacoes';

export const doacaoService = {
  async buscarPorId(id: string): Promise<DoacaoResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}`);
    return handleResponse<DoacaoResponse>(response);
  },
};
