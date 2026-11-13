export interface DoadorResponse {
  id: string;
  nome: string;
  contato: string;
}

export interface DoadorCreateRequest {
  nome: string;
  contato: string;
}
