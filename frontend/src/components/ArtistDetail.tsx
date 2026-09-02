"use client";

import DetailHeader from "@/components/DetailHeader";
import TopList from "@/components/TopList";
import { useApi } from "@/hooks/useApi";
import { api } from "@/lib/api";

export default function ArtistDetail({ id }: { id: string }) {
  const { data, error, loading } = useApi(() => api.artistDetail(id), `artist-${id}`);

  return (
    <main className="mx-auto w-full max-w-6xl flex-1 space-y-6 px-6 py-8">
      {loading && <p className="text-sm text-muted">Cargando…</p>}
      {error && <p className="text-sm text-danger">{error}</p>}

      {data && (
        <>
          <DetailHeader kind="Artista" title={data.name} plays={data.plays} />

          <div className="grid gap-6 lg:grid-cols-2">
            <TopList
              title="Tus albumes mas escuchados"
              loading={false}
              error={null}
              items={data.topAlbums.map((a) => ({
                id: a.id,
                title: a.name,
                plays: a.plays,
                href: `/albums/${a.id}`,
              }))}
            />
            <TopList
              title="Tus canciones mas escuchadas"
              loading={false}
              error={null}
              items={data.topTracks.map((t) => ({
                id: t.id,
                title: t.name,
                subtitle: t.album,
                plays: t.plays,
              }))}
            />
          </div>
        </>
      )}
    </main>
  );
}
