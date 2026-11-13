import { z } from 'zod';

export const doadorFormSchema = z.object({
  nome: z
    .string()
    .trim()
    .min(1, 'Informe o nome de quem está doando')
    .max(100, 'Nome não pode exceder 100 caracteres'),
  contato: z
    .string()
    .trim()
    .min(1, 'Informe um e-mail ou telefone')
    .max(100, 'Contato não pode exceder 100 caracteres'),
});

export type DoadorFormData = z.infer<typeof doadorFormSchema>;
