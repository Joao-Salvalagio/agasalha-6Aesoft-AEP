import { useNavigate } from 'react-router-dom';
import {
  Shirt,
  BedDouble,
  Layers,
  Wind,
  Footprints,
  HelpCircle,
} from 'lucide-react';
import { Card, CardContent, CardFooter } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { StatusBadge } from '@/components/StatusBadge';
import type { ItemSummaryResponse, TipoPeca } from '../interfaces/item.interface';
import { TIPO_PECA_LABEL, GENERO_LABEL } from '../constants/item.constants';

const ICONE_PECA: Record<TipoPeca, React.ElementType> = {
  CASACO: Shirt,
  BLUSA: Wind,
  CALCA: Layers,
  COBERTOR: BedDouble,
  MEIA: Footprints,
  OUTRO: HelpCircle,
};

interface ItemCardProps {
  item: ItemSummaryResponse;
  onReservar: (id: string) => void;
  reservando: boolean;
}

export function ItemCard({ item, onReservar, reservando }: ItemCardProps) {
  const navigate = useNavigate();
  const Icone = ICONE_PECA[item.tipoPeca];

  return (
    <Card
      className="group flex cursor-pointer flex-col transition-shadow hover:shadow-md"
      onClick={() => navigate(`/itens/${item.id}`)}
    >
      <CardContent className="flex flex-1 flex-col items-center gap-3 pt-6">
        <div className="flex items-start justify-between w-full">
          <StatusBadge status={item.status} />
          <span className="text-xs font-semibold text-muted-foreground bg-muted rounded-full px-2 py-0.5">
            {item.tamanho}
          </span>
        </div>

        <div className="flex flex-col items-center gap-2 py-4">
          <div className="rounded-full bg-secondary p-4">
            <Icone className="size-8 text-primary" />
          </div>
          <p className="text-lg font-semibold text-foreground">
            {TIPO_PECA_LABEL[item.tipoPeca]}
          </p>
          <p className="text-sm text-muted-foreground">{GENERO_LABEL[item.genero]}</p>
        </div>
      </CardContent>

      <CardFooter className="border-t border-border pt-3 pb-4">
        {item.status === 'DISPONIVEL' ? (
          <Button
            className="w-full bg-laranja text-laranja-foreground hover:bg-laranja/85"
            size="sm"
            disabled={reservando}
            onClick={(e) => {
              e.stopPropagation();
              onReservar(item.id);
            }}
          >
            {reservando ? 'Reservando…' : 'Reservar'}
          </Button>
        ) : (
          <span className="w-full text-center text-sm text-muted-foreground">
            {item.status === 'RESERVADO' ? 'Já reservado' : 'Entregue'}
          </span>
        )}
      </CardFooter>
    </Card>
  );
}
