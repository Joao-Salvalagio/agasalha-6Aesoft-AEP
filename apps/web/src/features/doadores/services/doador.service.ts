import { handleResponse } from '@/lib/http';
import type { DoadorResponse } from '../interfaces/doador.interface';

const API_BASE_URL = '/api/doadores';

export const doadorService = {
  async listar(): Promise<DoadorResponse[]> {
    const response = await fetch(API_BASE_URL);
    return handleResponse<DoadorResponse[]>(response);
  },

  async buscarPorId(id: string): Promise<DoadorResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}`);
    return handleResponse<DoadorResponse>(response);
  },
};
