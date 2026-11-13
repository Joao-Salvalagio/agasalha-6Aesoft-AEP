import type { AbrigoResponse, DemandaResponse } from '../interfaces/abrigo.interface';

export const LIMITE_QUASE_CHEIO = 0.9;

export function abrigoQuaseCheio(abrigo: AbrigoResponse): boolean {
  return abrigo.estoqueAtual / abrigo.capacidadeEstoque >= LIMITE_QUASE_CHEIO;
}

export function demandasEmAberto(abrigo: AbrigoResponse): DemandaResponse[] {
  return abrigo.demandas.filter((d) => d.deficit > 0);
}

export function deficitDoAbrigo(abrigo: AbrigoResponse): number {
  return abrigo.demandas.reduce((total, d) => total + d.deficit, 0);
}

export const TIPO_PECA_PLURAL: Record<string, string> = {
  CASACO: 'casacos',
  BLUSA: 'blusas',
  CALCA: 'calças',
  COBERTOR: 'cobertores',
  MEIA: 'pares de meia',
  OUTRO: 'peças',
};
