import Link from "next/link";
import { formatNumber } from "@/lib/format";

export type TopItem = {
  id: string;
  title: string;
  subtitle?: string;
  plays: number;
  href?: string;
};

type Props = {
  title: string;
  items: TopItem[] | null;
  loading: boolean;
  error: string | null;
};

export default function TopList({ title, items, loading, error }: Props) {
  return (
    <section className="rounded-xl border border-border bg-surface">
      <h2 className="border-b border-border px-5 py-3 text-sm font-semibold uppercase tracking-wide text-muted">
        {title}
      </h2>
      {loading && <p className="px-5 py-6 text-sm text-muted">Cargando…</p>}
      {error && <p className="px-5 py-6 text-sm text-danger">{error}</p>}
      {items && items.length === 0 && (
        <p className="px-5 py-6 text-sm text-muted">Todavia no hay escuchas.</p>
      )}
      {items && items.length > 0 && (
        <ol>
          {items.map((item, index) => {
            const content = (
              <>
                <span className="w-6 text-right text-sm tabular-nums text-muted">{index + 1}</span>
                <span className="min-w-0 flex-1">
                  <span className="block truncate font-medium">{item.title}</span>
                  {item.subtitle && (
                    <span className="block truncate text-xs text-muted">{item.subtitle}</span>
                  )}
                </span>
                <span className="text-sm tabular-nums text-muted">{formatNumber(item.plays)}</span>
              </>
            );
            const className =
              "flex items-center gap-4 px-5 py-3 transition hover:bg-surface-hover";
            return (
              <li key={item.id} className="border-b border-border last:border-b-0">
                {item.href ? (
                  <Link href={item.href} className={className}>
                    {content}
                  </Link>
                ) : (
                  <div className={className}>{content}</div>
                )}
              </li>
            );
          })}
        </ol>
      )}
    </section>
  );
}
