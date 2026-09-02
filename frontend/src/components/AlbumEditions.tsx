"use client";

import Link from "next/link";
import { useApi } from "@/hooks/useApi";
import { api } from "@/lib/api";
import { formatDate, formatNumber } from "@/lib/format";

export default function AlbumEditions({ id }: { id: string }) {
  const { data, error, loading } = useApi(() => api.albumEditions(id), `editions-${id}`);
  const total = data?.reduce((sum, e) => sum + e.plays, 0) ?? 0;

  return (
    <main className="mx-auto w-full max-w-4xl flex-1 space-y-6 px-6 py-8">
      <Link href="/" className="text-sm text-muted transition hover:text-text">
        ← Volver al dashboard
      </Link>

      <section className="rounded-xl border border-border bg-surface">
        <div className="flex items-center justify-between border-b border-border px-5 py-3">
          <h1 className="text-sm font-semibold uppercase tracking-wide text-muted">
            Ediciones del album
          </h1>
          {data && (
            <span className="text-sm tabular-nums text-muted">
              {formatNumber(total)} reproducciones en total
            </span>
          )}
        </div>

        {loading && <p className="px-5 py-6 text-sm text-muted">Cargando…</p>}
        {error && <p className="px-5 py-6 text-sm text-danger">{error}</p>}

        {data && (
          <table className="w-full text-sm">
            <thead className="text-left text-xs uppercase tracking-wide text-muted">
              <tr className="border-b border-border">
                <th className="px-5 py-2 font-medium">Nombre en Spotify</th>
                <th className="px-5 py-2 font-medium">Edicion</th>
                <th className="px-5 py-2 font-medium">Lanzamiento</th>
                <th className="px-5 py-2 text-right font-medium">Plays</th>
              </tr>
            </thead>
            <tbody>
              {data.map((edition) => (
                <tr key={edition.id} className="border-b border-border last:border-b-0">
                  <td className="px-5 py-3 font-medium">{edition.name}</td>
                  <td className="px-5 py-3 text-muted">{edition.editionLabel}</td>
                  <td className="px-5 py-3 text-muted">{formatDate(edition.releaseDate)}</td>
                  <td className="px-5 py-3 text-right tabular-nums">{formatNumber(edition.plays)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </main>
  );
}
