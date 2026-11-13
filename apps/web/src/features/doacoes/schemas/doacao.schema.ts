import { z } from 'zod';
import { itemFormSchema } from '@/features/itens/schemas/item.schema';

export const doacaoFormSchema = itemFormSchema
  .pick({ tipoPeca: true, tamanho: true, genero: true, estadoConservacao: true })
  .extend({
    doadorId: z.string({ error: 'Escolha quem está doando' }).min(1, 'Escolha quem está doando'),
  });

export type DoacaoFormData = z.infer<typeof doacaoFormSchema>;
