import type { Genero, Tamanho, TipoPeca } from '@/features/itens/interfaces/item.interface';

export interface MatchResponse {
  itemId: string;
  abrigoId: string;
  abrigoNome: string;
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  deficit: number;
}
