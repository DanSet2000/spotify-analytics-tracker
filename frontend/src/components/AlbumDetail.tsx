"use client";

import DetailHeader from "@/components/DetailHeader";
import TopList from "@/components/TopList";
import { useApi } from "@/hooks/useApi";
import { api } from "@/lib/api";
import { formatDate, formatNumber } from "@/lib/format";
import type { AlbumEdition } from "@/lib/types";

export default function AlbumDetail({ id }: { id: string }) {
  const { data, error, loading } = useApi(() => api.albumDetail(id), `album-${id}`);

  return (
    <main className="mx-auto w-full max-w-6xl flex-1 space-y-6 px-6 py-8">
      {loading && <p className="text-sm text-muted">Cargando…</p>}
      {error && <p className="text-sm text-danger">{error}</p>}

      {data && (
        <>
          <DetailHeader kind="Album" title={data.name} subtitle={data.artist} plays={data.plays} />

          <div className="grid gap-6 lg:grid-cols-2">
            <TopList
              title="Tus canciones mas escuchadas"
              loading={false}
              error={null}
              items={data.topTracks.map((t) => ({ id: t.id, title: t.name, plays: t.plays }))}
            />
            <EditionsTable editions={data.editions} />
          </div>
        </>
      )}
    </main>
  );
}

function EditionsTable({ editions }: { editions: AlbumEdition[] }) {
  return (
    <section className="rounded-xl border border-border bg-surface">
      <h2 className="border-b border-border px-5 py-3 text-sm font-semibold uppercase tracking-wide text-muted">
        Ediciones
      </h2>
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
          {editions.map((edition) => (
            <tr key={edition.id} className="border-b border-border last:border-b-0">
              <td className="px-5 py-3 font-medium">{edition.name}</td>
              <td className="px-5 py-3 text-muted">{edition.editionLabel}</td>
              <td className="px-5 py-3 text-muted">{formatDate(edition.releaseDate)}</td>
              <td className="px-5 py-3 text-right tabular-nums">{formatNumber(edition.plays)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
