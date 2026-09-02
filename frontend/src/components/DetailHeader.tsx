import Link from "next/link";
import { formatNumber } from "@/lib/format";

type Props = {
  kind: string;
  title: string;
  subtitle?: string;
  plays: number;
};

export default function DetailHeader({ kind, title, subtitle, plays }: Props) {
  return (
    <div className="space-y-4">
      <Link href="/" className="text-sm text-muted transition hover:text-text">
        ← Volver al dashboard
      </Link>
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-xs font-medium uppercase tracking-wide text-muted">{kind}</p>
          <h1 className="mt-1 text-3xl font-semibold">{title}</h1>
          {subtitle && <p className="mt-1 text-muted">{subtitle}</p>}
        </div>
        <div className="text-right">
          <p className="text-3xl font-semibold tabular-nums">{formatNumber(plays)}</p>
          <p className="text-xs uppercase tracking-wide text-muted">reproducciones</p>
        </div>
      </div>
    </div>
  );
}
