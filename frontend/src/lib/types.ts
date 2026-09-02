export type ArtistStats = {
  id: string;
  name: string;
  plays: number;
};

export type AlbumStats = {
  id: string;
  name: string;
  artist: string;
  plays: number;
};

export type TrackStats = {
  id: string;
  name: string;
  album: string;
  artist: string;
  plays: number;
};

export type AlbumEdition = {
  id: string;
  name: string;
  editionLabel: string;
  releaseDate: string | null;
  plays: number;
};

export type ArtistDetail = {
  id: string;
  name: string;
  plays: number;
  topAlbums: AlbumStats[];
  topTracks: TrackStats[];
};

export type AlbumDetail = {
  id: string;
  name: string;
  artist: string;
  plays: number;
  topTracks: TrackStats[];
  editions: AlbumEdition[];
};

export type DailyPlays = {
  date: string;
  plays: number;
};

export type StatsSummary = {
  totalPlays: number;
  totalMsPlayed: number;
  streakDays: number;
  last7Days: DailyPlays[];
};

export type SpotifyStatus = {
  connected: boolean;
  connectedAt: string | null;
};

export type TokenResponse = {
  token: string;
  expiresAt: string;
};
