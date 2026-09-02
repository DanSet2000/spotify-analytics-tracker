const LOCALE = "es-EC";

export function formatNumber(value: number): string {
  return value.toLocaleString(LOCALE);
}

export function formatDuration(ms: number): string {
  const totalMinutes = Math.floor(ms / 60_000);
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;
  return hours > 0 ? `${hours} h ${minutes} min` : `${minutes} min`;
}

export function formatWeekday(isoDate: string): string {
  const date = new Date(`${isoDate}T00:00:00`);
  return date.toLocaleDateString(LOCALE, { weekday: "short" });
}

export function formatDate(isoDate: string | null): string {
  if (!isoDate) return "—";
  const date = new Date(`${isoDate}T00:00:00`);
  return date.toLocaleDateString(LOCALE, { year: "numeric", month: "short", day: "numeric" });
}
