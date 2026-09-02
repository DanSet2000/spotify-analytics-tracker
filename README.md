# Spotify Analytics Tracker

Proyecto personal (y pieza de portafolio) que registra el historial real de escuchas
de Spotify y muestra estadísticas propias: artistas, álbumes y canciones más
escuchados —agrupando distintas ediciones de un mismo álbum bajo una versión
canónica— más un dashboard de métricas generales.

## Estructura del repo

```
spotify-analytics-tracker/
├── backend/    Spring Boot 4.1 · Java 25 · Spring Data JPA · Spring Security · PostgreSQL
└── frontend/   Next.js · TypeScript · Recharts
```

Backend y frontend son proyectos independientes que se comunican por REST
(Maven no compila el frontend). Se versionan juntos en este repo por comodidad.

## Stack

| Capa      | Tecnologías |
|-----------|-------------|
| Backend   | Java 25, Spring Boot 4.1, Spring Web MVC, Spring Data JPA, Spring Security, Lombok, PostgreSQL (Neon/Supabase) |
| Frontend  | Next.js, TypeScript, Recharts |

## V1 — alcance

- **Auth**: login propio a la app (Spring Security) + conexión OAuth con Spotify.
- **Recolección**: rastreador en vivo (`@Scheduled`, sondea `/me/player` cada 15–30 s)
  y recolector de respaldo (`/recently-played` cada 30 min).
- **Regla de negocio**: una escucha cuenta si duró más de 1.5 min, o completa si la
  canción dura menos de 1 min. Solo música (se excluyen podcasts y audiolibros).
- **Modelo canónico**: ediciones (remastered, deluxe…) agrupadas bajo un álbum/canción
  canónicos, con desglose por edición.
- **Dashboard**: top artistas / álbumes / canciones + panel de estadísticas
  (reproducciones totales, tiempo total, racha de días, gráfica de los últimos 7 días).
- **Modo demo**: frontend separado con datos de ejemplo, sin login ni Spotify real.

## Fuera de alcance en V1

Multiusuario real, métricas de audio (BPM/energía — Spotify ya no da acceso),
paginación completa (se muestra un top N).

## Restricciones conocidas

- Modo Development de Spotify: máximo 5 cuentas conectadas a la vez.
- El refresh token de Spotify expira cada 6 meses; hay que poder renovarlo.
- Sin acceso a audio features desde noviembre 2024.

## Orden de trabajo

1. Modelo de datos (entidades JPA + repositorios)
2. Login con Spotify (OAuth) — guarda el refresh token
3. Rastreador en vivo (`@Scheduled`, `/me/player`)
4. Recolector de respaldo (`/recently-played` cada 30 min)
5. Controllers REST (artistas / álbumes / canciones / stats)
6. Spring Security (protege el backend)
7. Frontend en Next.js
8. Modo demo con datos quemados
