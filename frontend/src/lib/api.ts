import { clearToken, getToken } from "./auth";
import type {
  AlbumDetail,
  AlbumStats,
  ArtistDetail,
  ArtistStats,
  SpotifyStatus,
  StatsSummary,
  TokenResponse,
  TrackStats,
} from "./types";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://127.0.0.1:8080";

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    message: string,
  ) {
    super(message);
  }
}

async function request<T>(path: string, init: RequestInit = {}, withAuth = true): Promise<T> {
  const headers = new Headers(init.headers);
  headers.set("Accept", "application/json");
  if (init.body) headers.set("Content-Type", "application/json");
  if (withAuth) {
    const token = getToken();
    if (token) headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(`${BASE_URL}${path}`, { ...init, headers });

  if (response.status === 401 && withAuth) {
    clearToken();
  }
  if (!response.ok) {
    throw new ApiError(response.status, await safeMessage(response));
  }
  return response.json() as Promise<T>;
}

async function safeMessage(response: Response): Promise<string> {
  try {
    const body = await response.json();
    return body.error ?? body.message ?? `Error ${response.status}`;
  } catch {
    return `Error ${response.status}`;
  }
}

export const api = {
  login: (username: string, password: string) =>
    request<TokenResponse>(
      "/api/auth/login",
      { method: "POST", body: JSON.stringify({ username, password }) },
      false,
    ),

  summary: () => request<StatsSummary>("/api/stats/summary"),
  topArtists: (limit = 10) => request<ArtistStats[]>(`/api/stats/top-artists?limit=${limit}`),
  topAlbums: (limit = 10) => request<AlbumStats[]>(`/api/stats/top-albums?limit=${limit}`),
  topTracks: (limit = 10) => request<TrackStats[]>(`/api/stats/top-tracks?limit=${limit}`),
  artistDetail: (id: string) => request<ArtistDetail>(`/api/stats/artists/${id}`),
  albumDetail: (id: string) => request<AlbumDetail>(`/api/stats/albums/${id}`),

  spotifyStatus: () => request<SpotifyStatus>("/api/spotify/status"),
  spotifyLoginUrl: () => request<{ url: string }>("/api/spotify/login"),
};
