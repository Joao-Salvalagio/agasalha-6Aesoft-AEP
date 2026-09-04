import { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';
import { ItemForm } from '@/features/itens/components/ItemForm';
import { useItem } from '@/features/itens/hooks/useItem';
import { useItemActions } from '@/features/itens/hooks/useItemActions';
import type { ItemFormData } from '@/features/itens/schemas/item.schema';

export function EditarItemPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { item, loading: carregando, erro: erroCarregamento } = useItem(id!);
  const { atualizar, estado: acaoEstado } = useItemActions();

  useEffect(() => {
    if (item?.status === 'ENTREGUE') navigate(`/itens/${id}`);
  }, [item, id, navigate]);

  async function handleSubmit(dados: ItemFormData) {
    const atualizado = await atualizar(id!, dados);
    if (atualizado) navigate(`/itens/${id}`);
  }

  if (carregando) {
    return (
      <div className="mx-auto max-w-xl">
        <Skeleton className="h-96 rounded-xl" />
      </div>
    );
  }

  if (erroCarregamento || !item) {
    return (
      <div className="flex flex-col items-center gap-4 py-20 text-center">
        <p className="text-muted-foreground">{erroCarregamento ?? 'Item não encontrado'}</p>
        <Button variant="outline" onClick={() => navigate('/')}>
          Voltar ao Mural
        </Button>
      </div>
    );
  }

  const defaultValues: ItemFormData = {
    tipoPeca: item.tipoPeca,
    tamanho: item.tamanho,
    genero: item.genero,
    estadoConservacao: item.estadoConservacao,
    nomeDoador: item.nomeDoador,
    contatoDoador: item.contatoDoador,
  };

  return (
    <div className="mx-auto flex max-w-xl flex-col gap-4">
      <nav className="flex items-center gap-1 text-sm text-muted-foreground">
        <button onClick={() => navigate('/')} className="hover:text-foreground">
          Mural
        </button>
        <span>›</span>
        <button onClick={() => navigate(`/itens/${id}`)} className="hover:text-foreground">
          Detalhes
        </button>
        <span>›</span>
        <span className="text-foreground">Editar</span>
      </nav>

      <Card>
        <CardHeader className="pb-2">
          <h1 className="text-xl font-bold">Editar Doação</h1>
          <p className="text-sm text-muted-foreground">
            Altere os dados descritivos da peça
          </p>
        </CardHeader>
        <CardContent>
          <ItemForm
            defaultValues={defaultValues}
            onSubmit={handleSubmit}
            onCancelar={() => navigate(`/itens/${id}`)}
            loading={acaoEstado.loading}
            erro={acaoEstado.erro}
            rotuloBotao="Salvar Alterações"
          />
        </CardContent>
      </Card>
    </div>
  );
}
