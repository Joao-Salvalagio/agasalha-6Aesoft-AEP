import { z } from 'zod';

export const demandaFormSchema = z
  .object({
    tipoPeca: z.enum(['CASACO', 'BLUSA', 'CALCA', 'COBERTOR', 'MEIA', 'OUTRO'], {
      error: 'Escolha o tipo de peça',
    }),
    tamanho: z.enum(['PP', 'P', 'M', 'G', 'GG'], { error: 'Escolha o tamanho' }),
    genero: z.enum(['MASCULINO', 'FEMININO', 'UNISSEX'], { error: 'Escolha o gênero' }),
    quantidadeNecessaria: z
      .number({ error: 'Informe um número inteiro a partir de 1' })
      .int('Informe um número inteiro a partir de 1')
      .min(1, 'Informe um número inteiro a partir de 1'),
    quantidadeAtendida: z
      .number({ error: 'Informe um número inteiro a partir de 0' })
      .int('Informe um número inteiro a partir de 0')
      .min(0, 'Informe um número inteiro a partir de 0'),
  })
  .refine((d) => d.quantidadeAtendida <= d.quantidadeNecessaria, {
    path: ['quantidadeAtendida'],
    message: 'A quantidade atendida não pode ser maior que a necessária',
  });

export type DemandaFormData = z.infer<typeof demandaFormSchema>;
