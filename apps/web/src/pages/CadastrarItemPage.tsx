import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { ItemForm } from '@/features/itens/components/ItemForm';
import { useCriarItem } from '@/features/itens/hooks/useCriarItem';
import type { ItemFormData } from '@/features/itens/schemas/item.schema';

export function CadastrarItemPage() {
  const navigate = useNavigate();
  const { criar, loading, erro } = useCriarItem();

  function handleSubmit(dados: ItemFormData) {
    criar(dados);
  }

  return (
    <div className="mx-auto flex max-w-xl flex-col gap-4">
      <nav className="flex items-center gap-1 text-sm text-muted-foreground">
        <button onClick={() => navigate('/')} className="hover:text-foreground">
          Mural
        </button>
        <span>›</span>
        <span className="text-foreground">Cadastrar Doação</span>
      </nav>

      <Card>
        <CardHeader className="pb-2">
          <h1 className="text-xl font-bold">Nova Doação</h1>
          <p className="text-sm text-muted-foreground">
            Preencha os dados da peça que deseja doar
          </p>
        </CardHeader>
        <CardContent>
          <ItemForm
            onSubmit={handleSubmit}
            onCancelar={() => navigate('/')}
            loading={loading}
            erro={erro}
          />
        </CardContent>
      </Card>
    </div>
  );
}
