import { z } from 'zod';

export const itemFormSchema = z.object({
  tipoPeca: z.enum(['CASACO', 'BLUSA', 'CALCA', 'COBERTOR', 'MEIA', 'OUTRO'], {
    error: 'Selecione o tipo da peça',
  }),
  tamanho: z.enum(['PP', 'P', 'M', 'G', 'GG'], {
    error: 'Selecione o tamanho',
  }),
  genero: z.enum(['MASCULINO', 'FEMININO', 'UNISSEX'], {
    error: 'Selecione o gênero',
  }),
  estadoConservacao: z.enum(['NOVO', 'USADO_BOM', 'USADO_REGULAR'], {
    error: 'Selecione o estado de conservação',
  }),
  nomeDoador: z
    .string()
    .trim()
    .min(1, 'Nome do doador é obrigatório')
    .max(100, 'Nome não pode exceder 100 caracteres'),
  contatoDoador: z
    .string()
    .trim()
    .min(1, 'Contato do doador é obrigatório')
    .max(100, 'Contato não pode exceder 100 caracteres'),
});

export type ItemFormData = z.infer<typeof itemFormSchema>;
