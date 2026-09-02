"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { api, ApiError } from "@/lib/api";
import { setToken } from "@/lib/auth";

export default function LoginPage() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const { token } = await api.login(username, password);
      setToken(token);
      router.replace("/");
    } catch (err) {
      setError(
        err instanceof ApiError && err.status === 401
          ? "Usuario o contrasena incorrectos"
          : "No se pudo conectar con el servidor",
      );
      setSubmitting(false);
    }
  }

  return (
    <main className="flex flex-1 items-center justify-center px-6">
      <form
        onSubmit={submit}
        className="w-full max-w-sm space-y-5 rounded-2xl border border-border bg-surface p-8"
      >
        <div>
          <h1 className="text-xl font-semibold">
            <span className="text-accent">●</span> Spotify Analytics Tracker
          </h1>
          <p className="mt-1 text-sm text-muted">Inicia sesion para ver tus estadisticas</p>
        </div>

        <label className="block space-y-1.5">
          <span className="text-sm text-muted">Usuario</span>
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
            className="w-full rounded-lg border border-border bg-bg px-3 py-2 outline-none transition focus:border-accent"
          />
        </label>

        <label className="block space-y-1.5">
          <span className="text-sm text-muted">Contrasena</span>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
            className="w-full rounded-lg border border-border bg-bg px-3 py-2 outline-none transition focus:border-accent"
          />
        </label>

        {error && <p className="text-sm text-danger">{error}</p>}

        <button
          type="submit"
          disabled={submitting}
          className="w-full rounded-full bg-accent py-2.5 font-semibold text-black transition hover:brightness-110 disabled:opacity-60"
        >
          {submitting ? "Entrando…" : "Entrar"}
        </button>
      </form>
    </main>
  );
}
