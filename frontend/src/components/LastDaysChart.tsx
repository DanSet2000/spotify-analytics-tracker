"use client";

import { Bar, BarChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import type { DailyPlays } from "@/lib/types";
import { formatWeekday } from "@/lib/format";

export default function LastDaysChart({ data }: { data: DailyPlays[] }) {
  const points = data.map((d) => ({ ...d, label: formatWeekday(d.date) }));

  return (
    <section className="rounded-xl border border-border bg-surface p-5">
      <h2 className="mb-4 text-sm font-semibold uppercase tracking-wide text-muted">
        Ultimos 7 dias
      </h2>
      <div className="h-56">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={points} margin={{ top: 4, right: 4, bottom: 0, left: -20 }}>
            <XAxis dataKey="label" tick={{ fill: "#a7a7a7", fontSize: 12 }} axisLine={false} tickLine={false} />
            <YAxis allowDecimals={false} tick={{ fill: "#a7a7a7", fontSize: 12 }} axisLine={false} tickLine={false} />
            <Tooltip
              cursor={{ fill: "#242424" }}
              contentStyle={{ background: "#181818", border: "1px solid #2a2a2a", borderRadius: 8 }}
              labelStyle={{ color: "#a7a7a7" }}
              itemStyle={{ color: "#ffffff" }}
              formatter={(value) => [value, "Reproducciones"]}
              labelFormatter={(_, payload) => payload?.[0]?.payload?.date ?? ""}
            />
            <Bar dataKey="plays" fill="#1db954" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </section>
  );
}
