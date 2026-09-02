"use client";

import { useEffect, useState } from "react";

type Result<T> = {
  key: string;
  data: T | null;
  error: string | null;
};

export function useApi<T>(load: () => Promise<T>, key = "default") {
  const [result, setResult] = useState<Result<T>>({ key: "", data: null, error: null });

  useEffect(() => {
    let active = true;
    load()
      .then((data) => {
        if (active) setResult({ key, data, error: null });
      })
      .catch((err: Error) => {
        if (active) setResult({ key, data: null, error: err.message });
      });
    return () => {
      active = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [key]);

  const current = result.key === key;
  return {
    data: current ? result.data : null,
    error: current ? result.error : null,
    loading: !current,
  };
}
