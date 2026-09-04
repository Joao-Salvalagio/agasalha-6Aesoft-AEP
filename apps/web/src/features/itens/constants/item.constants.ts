import type { EstadoConservacao, Genero, StatusItem, Tamanho, TipoPeca } from '../interfaces/item.interface';

export const TIPO_PECA_OPTIONS: { value: TipoPeca; label: string }[] = [
  { value: 'CASACO', label: 'Casaco' },
  { value: 'BLUSA', label: 'Blusa' },
  { value: 'CALCA', label: 'Calça' },
  { value: 'COBERTOR', label: 'Cobertor' },
  { value: 'MEIA', label: 'Meia' },
  { value: 'OUTRO', label: 'Outro' },
];

export const TAMANHO_OPTIONS: { value: Tamanho; label: string }[] = [
  { value: 'PP', label: 'PP' },
  { value: 'P', label: 'P' },
  { value: 'M', label: 'M' },
  { value: 'G', label: 'G' },
  { value: 'GG', label: 'GG' },
];

export const GENERO_OPTIONS: { value: Genero; label: string }[] = [
  { value: 'MASCULINO', label: 'Masculino' },
  { value: 'FEMININO', label: 'Feminino' },
  { value: 'UNISSEX', label: 'Unissex' },
];

export const ESTADO_CONSERVACAO_OPTIONS: { value: EstadoConservacao; label: string }[] = [
  { value: 'NOVO', label: 'Novo' },
  { value: 'USADO_BOM', label: 'Usado — bom estado' },
  { value: 'USADO_REGULAR', label: 'Usado — estado regular' },
];

export const STATUS_OPTIONS: { value: StatusItem; label: string }[] = [
  { value: 'DISPONIVEL', label: 'Disponível' },
  { value: 'RESERVADO', label: 'Reservado' },
  { value: 'ENTREGUE', label: 'Entregue' },
];

export const STATUS_ITEM_CONFIG: Record<StatusItem, { label: string; className: string }> = {
  DISPONIVEL: {
    label: 'Disponível',
    className: 'bg-disponivel text-disponivel-foreground',
  },
  RESERVADO: {
    label: 'Reservado',
    className: 'bg-reservado text-reservado-foreground',
  },
  ENTREGUE: {
    label: 'Entregue',
    className: 'bg-entregue text-entregue-foreground',
  },
};

export const TIPO_PECA_LABEL: Record<TipoPeca, string> = {
  CASACO: 'Casaco',
  BLUSA: 'Blusa',
  CALCA: 'Calça',
  COBERTOR: 'Cobertor',
  MEIA: 'Meia',
  OUTRO: 'Outro',
};

export const GENERO_LABEL: Record<Genero, string> = {
  MASCULINO: 'Masculino',
  FEMININO: 'Feminino',
  UNISSEX: 'Unissex',
};

export const ESTADO_CONSERVACAO_LABEL: Record<EstadoConservacao, string> = {
  NOVO: 'Novo',
  USADO_BOM: 'Usado — bom estado',
  USADO_REGULAR: 'Usado — estado regular',
};
