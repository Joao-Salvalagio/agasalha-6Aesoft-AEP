import { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ChevronLeft } from 'lucide-react';
import { ErrorState } from '@/components/ErrorState';
import { PageHeader } from '@/components/PageHeader';
import { Skeleton } from '@/components/ui/skeleton';
import { ItemForm } from '@/features/itens/components/ItemForm';
import { useItem } from '@/features/itens/hooks/useItem';
import { useItemActions } from '@/features/itens/hooks/useItemActions';
import type { ItemFormData } from '@/features/itens/schemas/item.schema';

export function EditarItemPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { item, loading, erro, recarregar } = useItem(id!);
  const { atualizar, estado: acaoEstado } = useItemActions();

  const bloqueada = item !== null && (item.status === 'ENTREGUE' || item.doacaoId !== null);

  useEffect(() => {
    if (bloqueada) navigate(`/itens/${id}`, { replace: true });
  }, [bloqueada, id, navigate]);

  async function handleSubmit(dados: ItemFormData) {
    const atualizado = await atualizar(id!, dados);
    if (atualizado) navigate(`/itens/${id}`);
  }

  const voltar = (
    <Link to={`/itens/${id}`} className="mb-3.5 inline-flex items-center gap-1.5 rounded-lg py-1 pr-1.5 pl-0.5 text-sm font-semibold text-muted-foreground hover:bg-secondary hover:text-foreground">
      <ChevronLeft className="size-4" aria-hidden="true" />
      Voltar aos detalhes
    </Link>
  );

  if (loading) {
    return (
      <div role="status">
        <span className="sr-only">Carregando peça…</span>
        <Skeleton className="h-96 max-w-2xl rounded-xl" />
      </div>
    );
  }

  if (erro || !item) {
    return (
      <>
        {voltar}
        <ErrorState titulo={erro ?? 'Essa peça não está mais no mural'} onTentarNovamente={recarregar} />
      </>
    );
  }

  const defaultValues: ItemFormData = {
    tipoPeca: item.tipoPeca,
    tamanho: item.tamanho,
    genero: item.genero,
    estadoConservacao: item.estadoConservacao,
    nomeDoador: item.nomeDoador ?? '',
    contatoDoador: item.contatoDoador ?? '',
  };

  return (
    <>
      {voltar}
      <PageHeader eyebrow="Editar doação" titulo="Ajustar os dados da peça" descricao="Altere os dados descritivos da peça. Status e data de cadastro não mudam por aqui." />
      <div className="max-w-2xl">
        <ItemForm
          defaultValues={defaultValues}
          onSubmit={handleSubmit}
          onCancelar={() => navigate(`/itens/${id}`)}
          loading={acaoEstado.loading}
          erro={acaoEstado.erro}
          rotuloBotao="Salvar alterações"
        />
      </div>
    </>
  );
}
