import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainLayout } from '@/layouts/MainLayout';
import { MuralPage } from '@/pages/MuralPage';
import { CadastrarItemPage } from '@/pages/CadastrarItemPage';
import { DetalhesItemPage } from '@/pages/DetalhesItemPage';
import { EditarItemPage } from '@/pages/EditarItemPage';
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
      { path: '404', element: <NotFoundPage /> },
      { path: '*', element: <Navigate to="/404" replace /> },
    ],
  },
]);
