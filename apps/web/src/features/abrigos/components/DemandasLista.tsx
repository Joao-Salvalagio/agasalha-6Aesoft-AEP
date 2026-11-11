import { Check, Pencil, Trash2 } from 'lucide-react';
import { ProgressBar } from '@/components/ProgressBar';
import { Button } from '@/components/ui/button';
import { GENERO_LABEL, TIPO_PECA_LABEL } from '@/features/itens/constants/item.constants';
import { PecaIcone } from '@/features/itens/components/PecaIcone';
import { TIPO_PECA_PLURAL } from '../constants/abrigo.constants';
import type { DemandaResponse } from '../interfaces/abrigo.interface';

interface DemandasListaProps {
  demandas: DemandaResponse[];
  onEditar: (demanda: DemandaResponse) => void;
  onRemover: (demanda: DemandaResponse) => void;
}

export function DemandasLista({ demandas, onEditar, onRemover }: DemandasListaProps) {
  return (
    <ul aria-labelledby="titulo-demandas" className="divide-y divide-border">
      {demandas.map((d) => {
        const nome = `${TIPO_PECA_LABEL[d.tipoPeca]} ${d.tamanho} · ${GENERO_LABEL[d.genero]}`;
        const completa = d.deficit === 0;
        const percentual = Math.round((d.quantidadeAtendida / d.quantidadeNecessaria) * 100);

        return (
          <li
            key={d.posicao}
            className="grid grid-cols-[minmax(0,1fr)_auto] items-center gap-x-5 gap-y-3 px-5 py-4 md:grid-cols-[minmax(0,1.2fr)_minmax(0,1.5fr)_150px_auto]"
          >
            <div className="flex min-w-0 items-center gap-3">
              <PecaIcone tipoPeca={d.tipoPeca} tamanho="sm" />
              <div className="min-w-0">
                <strong className="block text-[15px]">{TIPO_PECA_LABEL[d.tipoPeca]}</strong>
                <span className="text-[13px] text-muted-foreground">
                  Tamanho {d.tamanho} · {GENERO_LABEL[d.genero]}
                </span>
              </div>
            </div>

            <div className="col-span-2 row-start-2 md:col-span-1 md:row-start-auto md:min-w-[180px]">
              <div className="mb-[7px] flex items-baseline justify-between gap-2 text-[13px] text-muted-foreground">
                <span>
                  {d.quantidadeAtendida} de {d.quantidadeNecessaria} atendidas
                </span>
                <strong className="text-sm text-foreground">{percentual}%</strong>
              </div>
              <ProgressBar
                valor={d.quantidadeAtendida}
                maximo={d.quantidadeNecessaria}
                rotulo={`Progresso de ${nome}`}
                variante={completa ? 'musgo' : 'terracota'}
              />
            </div>

            <div className="col-span-2 row-start-3 md:col-span-1 md:row-start-auto">
              {completa ? (
                <span className="inline-flex items-center gap-1.5 rounded-full bg-disponivel px-[11px] py-1 text-[12.5px] font-bold text-disponivel-foreground">
                  <Check className="size-3.5" aria-hidden="true" />
                  Atendida
                </span>
              ) : (
                <span className="font-bold whitespace-nowrap text-primary">
                  Faltam {d.deficit}
                  <small className="block text-[12.5px] font-medium text-muted-foreground">
                    {TIPO_PECA_PLURAL[d.tipoPeca]}
                  </small>
                </span>
              )}
            </div>

            <div className="col-start-2 row-start-1 flex justify-end gap-1 md:col-start-auto md:row-start-auto">
              <Button
                variant="ghost"
                size="icon"
                className="size-9"
                onClick={() => onEditar(d)}
                aria-label={`Editar demanda ${nome}`}
                title="Editar"
              >
                <Pencil className="size-[18px]" aria-hidden="true" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                className="size-9 text-destructive hover:bg-destructive/10"
                onClick={() => onRemover(d)}
                aria-label={`Remover demanda ${nome}`}
                title="Remover"
              >
                <Trash2 className="size-[18px]" aria-hidden="true" />
              </Button>
            </div>
          </li>
        );
      })}
    </ul>
  );
}
