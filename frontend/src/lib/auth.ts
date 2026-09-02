const TOKEN_KEY = "sat.token";
const listeners = new Set<() => void>();

function notify() {
  listeners.forEach((listener) => listener());
}

export function getToken(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string) {
  window.localStorage.setItem(TOKEN_KEY, token);
  notify();
}

export function clearToken() {
  window.localStorage.removeItem(TOKEN_KEY);
  notify();
}

export function subscribeToken(callback: () => void) {
  listeners.add(callback);
  window.addEventListener("storage", callback);
  return () => {
    listeners.delete(callback);
    window.removeEventListener("storage", callback);
  };
}
