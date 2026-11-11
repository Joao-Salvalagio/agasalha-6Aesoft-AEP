import { PageHeader } from '@/components/PageHeader';
import { EmptyState } from '@/components/EmptyState';
import { ErrorState } from '@/components/ErrorState';
import { SummaryStrip } from '@/components/SummaryStrip';
import { Skeleton } from '@/components/ui/skeleton';
import { AbrigoLinha } from '@/features/abrigos/components/AbrigoLinha';
import { deficitDoAbrigo, demandasEmAberto } from '@/features/abrigos/constants/abrigo.constants';
import { useAbrigos } from '@/features/abrigos/hooks/useAbrigos';

const CABECALHO = {
  eyebrow: 'Rede de acolhimento',
  titulo: 'Abrigos',
  descricao: 'Onde as doações viram cuidado. Veja a lotação de cada casa e o que ainda falta para este inverno.',
};

export function AbrigosPage() {
  const { dados: abrigos, loading, erro, recarregar } = useAbrigos();

  if (loading) {
    return (
      <>
        <PageHeader {...CABECALHO} />
        <div role="status" className="flex flex-col gap-3">
          <span className="sr-only">Carregando abrigos…</span>
          <Skeleton className="h-20 rounded-xl" />
          <Skeleton className="h-28 rounded-xl" />
          <Skeleton className="h-28 rounded-xl" />
          <Skeleton className="h-28 rounded-xl" />
        </div>
      </>
    );
  }

  if (erro || !abrigos) {
    return (
      <>
        <PageHeader {...CABECALHO} />
        <ErrorState
          titulo="Os abrigos não carregaram desta vez"
          descricao="Estamos tentando reconectar. Tente de novo em alguns segundos."
          onTentarNovamente={recarregar}
        />
      </>
    );
  }

  if (abrigos.length === 0) {
    return (
      <>
        <PageHeader {...CABECALHO} />
        <EmptyState
          titulo="Nenhum abrigo cadastrado ainda"
          descricao="Quando um abrigo da região entrar na rede, ele aparece aqui com tudo o que precisa."
        />
      </>
    );
  }

  const emAberto = abrigos.reduce((total, a) => total + demandasEmAberto(a).length, 0);
  const vagas = abrigos.reduce((total, a) => total + Math.max(0, a.capacidadeEstoque - a.estoqueAtual), 0);
  const deficit = abrigos.reduce((total, a) => total + deficitDoAbrigo(a), 0);
  const cidades = [...new Set(abrigos.map((a) => a.endereco.cidade))];

  return (
    <>
      <PageHeader {...CABECALHO} />
      <SummaryStrip
        rotuloAcessivel="Resumo dos abrigos"
        celulas={[
          { rotulo: 'Abrigos na rede', valor: abrigos.length, rodape: cidades.join(' e ') },
          {
            rotulo: 'Demandas em aberto',
            valor: emAberto,
            rodape: `${vagas} ${vagas === 1 ? 'vaga livre' : 'vagas livres'} nos estoques`,
          },
          { rotulo: 'Déficit total', valor: deficit, unidade: 'peças', rodape: 'somando todas as demandas', destaque: true },
        ]}
      />
      <ul aria-label="Lista de abrigos" className="flex flex-col gap-3">
        {abrigos.map((abrigo) => (
          <AbrigoLinha key={abrigo.id} abrigo={abrigo} />
        ))}
      </ul>
    </>
  );
}
