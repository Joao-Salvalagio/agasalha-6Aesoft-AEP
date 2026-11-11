import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainLayout } from '@/layouts/MainLayout';
import { AbrigosPage } from '@/pages/AbrigosPage';
import { CadastrarItemPage } from '@/pages/CadastrarItemPage';
import { DetalhesAbrigoPage } from '@/pages/DetalhesAbrigoPage';
import { DetalhesItemPage } from '@/pages/DetalhesItemPage';
import { DoadoresPage } from '@/pages/DoadoresPage';
import { EditarItemPage } from '@/pages/EditarItemPage';
import { MuralPage } from '@/pages/MuralPage';
import { NotFoundPage } from '@/pages/NotFoundPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <MuralPage /> },
      { path: 'itens/novo', element: <CadastrarItemPage /> },
      { path: 'itens/:id', element: <DetalhesItemPage /> },
      { path: 'itens/:id/editar', element: <EditarItemPage /> },
      { path: 'abrigos', element: <AbrigosPage /> },
      { path: 'abrigos/:id', element: <DetalhesAbrigoPage /> },
      { path: 'doadores', element: <DoadoresPage /> },
      { path: '404', element: <NotFoundPage /> },
      { path: '*', element: <Navigate to="/404" replace /> },
    ],
  },
]);
