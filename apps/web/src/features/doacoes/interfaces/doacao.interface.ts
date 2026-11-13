import type {
  EstadoConservacao,
  Genero,
  Tamanho,
  TipoPeca,
} from '@/features/itens/interfaces/item.interface';

export interface DoacaoResponse {
  id: string;
  doadorId: string;
  abrigoId: string | null;
  dataDoacao: string;
}

export interface DoacaoCreateRequest {
  doadorId: string;
  abrigoId?: string;
}

export interface ItemDoacaoRequest {
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  estadoConservacao: EstadoConservacao;
}
