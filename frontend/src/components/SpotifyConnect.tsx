"use client";

import { useState } from "react";
import { api } from "@/lib/api";
import { useApi } from "@/hooks/useApi";

export default function SpotifyConnect() {
  const { data: status } = useApi(api.spotifyStatus, "spotify-status");
  const [redirecting, setRedirecting] = useState(false);

  async function connect() {
    setRedirecting(true);
    try {
      const { url } = await api.spotifyLoginUrl();
      window.location.assign(url);
    } catch {
      setRedirecting(false);
    }
  }

  if (!status) return null;

  if (status.connected) {
    return (
      <span className="rounded-full border border-accent/40 bg-accent/10 px-3 py-1 text-xs font-medium text-accent">
        Spotify conectado
      </span>
    );
  }

  return (
    <button
      type="button"
      onClick={connect}
      disabled={redirecting}
      className="rounded-full bg-accent px-4 py-1.5 text-xs font-semibold text-black transition hover:brightness-110 disabled:opacity-60"
    >
      {redirecting ? "Redirigiendo…" : "Conectar Spotify"}
    </button>
  );
}
