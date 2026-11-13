import { handleResponse, JSON_HEADERS } from '@/lib/http';
import type {
  AbrigoResponse,
  DemandaRequest,
  DemandaResponse,
} from '../interfaces/abrigo.interface';

const API_BASE_URL = '/api/abrigos';

export const abrigoService = {
  async listar(): Promise<AbrigoResponse[]> {
    const response = await fetch(API_BASE_URL);
    return handleResponse<AbrigoResponse[]>(response);
  },

  async buscarPorId(id: string): Promise<AbrigoResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}`);
    return handleResponse<AbrigoResponse>(response);
  },

  async criarDemanda(id: string, payload: DemandaRequest): Promise<DemandaResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}/demandas`, {
      method: 'POST',
      headers: JSON_HEADERS,
      body: JSON.stringify(payload),
    });
    return handleResponse<DemandaResponse>(response);
  },

  async atualizarDemanda(
    id: string,
    posicao: number,
    payload: DemandaRequest,
  ): Promise<DemandaResponse> {
    const response = await fetch(`${API_BASE_URL}/${id}/demandas/${posicao}`, {
      method: 'PUT',
      headers: JSON_HEADERS,
      body: JSON.stringify(payload),
    });
    return handleResponse<DemandaResponse>(response);
  },

  async removerDemanda(id: string, posicao: number): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/${id}/demandas/${posicao}`, {
      method: 'DELETE',
    });
    return handleResponse<void>(response);
  },
};
