import { useNavigate, useParams } from 'react-router-dom';
import {
  Shirt,
  BedDouble,
  Layers,
  Wind,
  Footprints,
  HelpCircle,
  Calendar,
  User,
  Phone,
} from 'lucide-react';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { StatusBadge } from '@/components/StatusBadge';
import { useItem } from '@/features/itens/hooks/useItem';
import { useItemActions } from '@/features/itens/hooks/useItemActions';
import {
  TIPO_PECA_LABEL,
  GENERO_LABEL,
  ESTADO_CONSERVACAO_LABEL,
} from '@/features/itens/constants/item.constants';
import type { TipoPeca } from '@/features/itens/interfaces/item.interface';

const ICONE_PECA: Record<TipoPeca, React.ElementType> = {
  CASACO: Shirt,
  BLUSA: Wind,
  CALCA: Layers,
  COBERTOR: BedDouble,
  MEIA: Footprints,
  OUTRO: HelpCircle,
};

export function DetalhesItemPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { item, loading, erro } = useItem(id!);
  const { reservar, entregar, excluir, estado: acaoEstado } = useItemActions();

  async function handleReservar() {
    const atualizado = await reservar(id!);
    if (atualizado) navigate(0);
  }

  async function handleEntregar() {
    const atualizado = await entregar(id!);
    if (atualizado) navigate(0);
  }

  async function handleExcluir() {
    const ok = await excluir(id!);
    if (ok) navigate('/');
  }

  if (loading) {
    return (
      <div className="grid gap-6 lg:grid-cols-[1fr_300px]">
        <Skeleton className="h-80 rounded-xl" />
        <div className="flex flex-col gap-4">
          <Skeleton className="h-28 rounded-xl" />
          <Skeleton className="h-44 rounded-xl" />
        </div>
      </div>
    );
  }

  if (erro || !item) {
    return (
      <div className="flex flex-col items-center gap-4 py-20 text-center">
        <p className="text-muted-foreground">{erro ?? 'Item não encontrado'}</p>
        <Button variant="outline" onClick={() => navigate('/')}>
          Voltar ao Mural
        </Button>
      </div>
    );
  }

  const Icone = ICONE_PECA[item.tipoPeca];
  const dataFormatada = new Date(item.dataCadastro).toLocaleDateString('pt-BR');

  return (
    <div className="flex flex-col gap-4">
      <nav className="flex items-center gap-1 text-sm text-muted-foreground">
        <button onClick={() => navigate('/')} className="hover:text-foreground">
          Mural
        </button>
        <span>›</span>
        <span className="text-foreground">Detalhes</span>
      </nav>

      <div className="grid gap-6 lg:grid-cols-[1fr_300px]">
        <Card>
          <CardContent className="flex flex-col items-center gap-6 pt-8 pb-8">
            <div className="flex flex-col items-center gap-3">
              <div className="rounded-2xl bg-secondary p-6">
                <Icone className="size-12 text-primary" />
              </div>
              <StatusBadge status={item.status} className="text-sm px-3 py-1" />
            </div>

            <Separator />

            <dl className="w-full max-w-xs grid grid-cols-2 gap-x-4 gap-y-3 text-sm">
              <dt className="text-muted-foreground">Tipo</dt>
              <dd className="font-medium">{TIPO_PECA_LABEL[item.tipoPeca]}</dd>

              <dt className="text-muted-foreground">Tamanho</dt>
              <dd className="font-medium">{item.tamanho}</dd>

              <dt className="text-muted-foreground">Gênero</dt>
              <dd className="font-medium">{GENERO_LABEL[item.genero]}</dd>

              <dt className="text-muted-foreground">Estado</dt>
              <dd className="font-medium">{ESTADO_CONSERVACAO_LABEL[item.estadoConservacao]}</dd>

              <dt className="text-muted-foreground flex items-center gap-1">
                <Calendar className="size-3.5" /> Cadastro
              </dt>
              <dd className="font-medium">{dataFormatada}</dd>
            </dl>
          </CardContent>
        </Card>

        <div className="flex flex-col gap-4">
          <Card>
            <CardHeader className="pb-2">
              <h2 className="text-sm font-semibold text-muted-foreground uppercase tracking-wide">
                Dados do Doador
              </h2>
            </CardHeader>
            <CardContent className="flex flex-col gap-2 text-sm">
              <div className="flex items-center gap-2">
                <User className="size-4 text-muted-foreground shrink-0" />
                <span>{item.nomeDoador}</span>
              </div>
              <div className="flex items-center gap-2">
                <Phone className="size-4 text-muted-foreground shrink-0" />
                <span>{item.contatoDoador}</span>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <h2 className="text-sm font-semibold text-muted-foreground uppercase tracking-wide">
                Ações
              </h2>
            </CardHeader>
            <CardContent className="flex flex-col gap-2">
              {acaoEstado.erro && (
                <p className="rounded-md border border-destructive/30 bg-destructive/10 px-3 py-2 text-xs text-destructive">
                  {acaoEstado.erro}
                </p>
              )}

              {item.status === 'DISPONIVEL' && (
                <Button
                  className="w-full"
                  disabled={acaoEstado.loading}
                  onClick={handleReservar}
                >
                  {acaoEstado.loading ? 'Processando…' : 'Reservar Item'}
                </Button>
              )}

              {item.status === 'RESERVADO' && (
                <Button
                  className="w-full bg-primary text-primary-foreground hover:bg-primary/85"
                  disabled={acaoEstado.loading}
                  onClick={handleEntregar}
                >
                  {acaoEstado.loading ? 'Processando…' : 'Confirmar Entrega'}
                </Button>
              )}

              <Button
                variant="outline"
                className="w-full"
                onClick={() => navigate(`/itens/${id}/editar`)}
                disabled={item.status === 'ENTREGUE'}
              >
                Editar Dados
              </Button>

              <Button
                variant="destructive"
                className="w-full"
                disabled={acaoEstado.loading}
                onClick={handleExcluir}
              >
                Excluir Doação
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
