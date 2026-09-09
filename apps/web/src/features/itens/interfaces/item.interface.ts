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
  nomeDoador: string;
  contatoDoador: string;
  status: StatusItem;
  dataCadastro: string;
}

export interface ItemCreateRequest {
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  estadoConservacao: EstadoConservacao;
  nomeDoador: string;
  contatoDoador: string;
}

export interface ItemUpdateRequest extends ItemCreateRequest {}

export interface ItemFiltroParams {
  tipoPeca?: TipoPeca;
  tamanho?: Tamanho;
  genero?: Genero;
  status?: StatusItem;
}

export interface ApiError {
  timestamp: string;
  status: number;
  erro: string;
  detalhes: string[];
}
