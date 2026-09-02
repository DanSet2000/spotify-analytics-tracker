"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { clearToken } from "@/lib/auth";
import SpotifyConnect from "./SpotifyConnect";

export default function Header() {
  const router = useRouter();

  function logout() {
    clearToken();
    router.replace("/login");
  }

  return (
    <header className="border-b border-border bg-surface">
      <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-6 py-4">
        <Link href="/" className="text-lg font-semibold tracking-tight">
          <span className="text-accent">●</span> Spotify Analytics Tracker
        </Link>
        <div className="flex items-center gap-4">
          <SpotifyConnect />
          <button
            type="button"
            onClick={logout}
            className="text-sm text-muted transition hover:text-text"
          >
            Salir
          </button>
        </div>
      </div>
    </header>
  );
}
