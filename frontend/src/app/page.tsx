"use client";

import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";
import LastDaysChart from "@/components/LastDaysChart";
import StatTile from "@/components/StatTile";
import TopList from "@/components/TopList";
import { useApi } from "@/hooks/useApi";
import { api } from "@/lib/api";
import { formatDuration, formatNumber } from "@/lib/format";

export default function DashboardPage() {
  return (
    <AuthGuard>
      <Header />
      <Dashboard />
    </AuthGuard>
  );
}

function Dashboard() {
  const summary = useApi(api.summary, "summary");
  const artists = useApi(() => api.topArtists(10), "top-artists");
  const albums = useApi(() => api.topAlbums(10), "top-albums");
  const tracks = useApi(() => api.topTracks(10), "top-tracks");

  return (
    <main className="mx-auto w-full max-w-6xl flex-1 space-y-6 px-6 py-8">
      {summary.error && <p className="text-sm text-danger">{summary.error}</p>}

      <div className="grid gap-4 sm:grid-cols-3">
        <StatTile
          label="Reproducciones"
          value={summary.data ? formatNumber(summary.data.totalPlays) : "—"}
        />
        <StatTile
          label="Tiempo escuchado"
          value={summary.data ? formatDuration(summary.data.totalMsPlayed) : "—"}
        />
        <StatTile
          label="Racha"
          value={summary.data ? `${summary.data.streakDays} dias` : "—"}
        />
      </div>

      {summary.data && <LastDaysChart data={summary.data.last7Days} />}

      <div className="grid gap-6 lg:grid-cols-3">
        <TopList
          title="Artistas"
          loading={artists.loading}
          error={artists.error}
          items={
            artists.data?.map((a) => ({
              id: a.id,
              title: a.name,
              plays: a.plays,
              href: `/artists/${a.id}`,
            })) ?? null
          }
        />
        <TopList
          title="Albumes"
          loading={albums.loading}
          error={albums.error}
          items={
            albums.data?.map((a) => ({
              id: a.id,
              title: a.name,
              subtitle: a.artist,
              plays: a.plays,
              href: `/albums/${a.id}`,
            })) ?? null
          }
        />
        <TopList
          title="Canciones"
          loading={tracks.loading}
          error={tracks.error}
          items={
            tracks.data?.map((t) => ({
              id: t.id,
              title: t.name,
              subtitle: `${t.artist} · ${t.album}`,
              plays: t.plays,
            })) ?? null
          }
        />
      </div>
    </main>
  );
}
