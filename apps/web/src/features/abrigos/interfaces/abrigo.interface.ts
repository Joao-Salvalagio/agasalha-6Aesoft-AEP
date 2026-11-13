import type { Genero, Tamanho, TipoPeca } from '@/features/itens/interfaces/item.interface';

export interface Endereco {
  logradouro: string;
  numero: string | null;
  bairro: string | null;
  cidade: string;
  estado: string;
}

export interface DemandaResponse {
  posicao: number;
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  quantidadeNecessaria: number;
  quantidadeAtendida: number;
  deficit: number;
}

export interface AbrigoResponse {
  id: string;
  nome: string;
  endereco: Endereco;
  capacidadeEstoque: number;
  estoqueAtual: number;
  demandas: DemandaResponse[];
}

export interface DemandaRequest {
  tipoPeca: TipoPeca;
  tamanho: Tamanho;
  genero: Genero;
  quantidadeNecessaria: number;
  quantidadeAtendida: number;
}
