export type TipoPeca = 'CASACO' | 'BLUSA' | 'CALCA' | 'COBERTOR' | 'MEIA' | 'OUTRO';
export type Tamanho = 'PP' | 'P' | 'M' | 'G' | 'GG';
export type Genero = 'MASCULINO' | 'FEMININO' | 'UNISSEX';
export type EstadoConservacao = 'NOVO' | 'USADO_BOM' | 'USADO_REGULAR';
export type StatusItem = 'DISPONIVEL' | 'RESERVADO' | 'ENTREGUE';

export interface ItemSummaryResponse {
  id: string;
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  status: StatusItem;
}

export interface ItemResponse {
  id: string;
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  estadoConservacao: EstadoConservacao;
  nomeDoador: string | null;
  contatoDoador: string | null;
  status: StatusItem;
  dataCadastro: string;
  doacaoId: string | null;
  abrigoId: string | null;
}

export interface ItemCreateRequest {
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  estadoConservacao: EstadoConservacao;
  nomeDoador: string;
  contatoDoador: string;
}

export type ItemUpdateRequest = ItemCreateRequest;

export interface ItemFiltroParams {
  tipoPeca?: TipoPeca;
  tamanho?: Tamanho;
  genero?: Genero;
  status?: StatusItem;
}

export type { ApiError } from '@/lib/http';
