import { useAbrigos } from './useAbrigos';

export function useDeficitTotal(): number | null {
  const { dados } = useAbrigos();
  if (!dados) return null;
  return dados.reduce(
    (total, abrigo) => total + abrigo.demandas.reduce((soma, d) => soma + d.deficit, 0),
    0,
  );
}
