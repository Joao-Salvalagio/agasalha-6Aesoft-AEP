import { Link } from 'react-router-dom';
import { PackageX } from 'lucide-react';
import { PageHeader } from '@/components/PageHeader';
import { buttonVariants } from '@/components/ui/button';
import { cn } from '@/lib/utils';

export function NotFoundPage() {
  return (
    <>
      <PageHeader eyebrow="Ops" titulo="Não encontramos esta página" />
      <div className="flex flex-col items-center rounded-xl bg-card px-6 py-12 text-center shadow-sm ring-1 ring-border">
        <div className="grid size-16 place-items-center rounded-full bg-secondary text-primary">
          <PackageX className="size-8" aria-hidden="true" />
        </div>
        <h2 className="mt-4 text-xl font-extrabold tracking-tight">Nada pendurado aqui</h2>
        <p className="mx-auto mt-2 mb-5 max-w-[46ch] text-muted-foreground">
          O endereço pode ter mudado. O mural continua quentinho por aqui.
        </p>
        <Link to="/" className={cn(buttonVariants({ size: 'lg' }), 'h-10 rounded-xl px-4 text-sm font-semibold')}>
          Ir para o mural
        </Link>
      </div>
    </>
  );
}
