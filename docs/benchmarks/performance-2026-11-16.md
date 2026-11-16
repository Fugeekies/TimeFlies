# Rapport de performance TimeFlies

- Date : 2026-11-16T22:27:40.5364182+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

192 événements, 68.4 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 9.556 | 16.938 | 7.991 |
| Requête par jour × 366 (anniversaires) | 0.344 | 2.478 | 0.230 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.386 | 0.596 | 0.259 |
| Recherche dans les titres × 20 (Quel âge ?) | 4.378 | 6.500 | 3.009 |
| Page Aujourd'hui (GetDailyShock) | 0.133 | 0.298 | 0.098 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 13.858 | 39.039 | 7.987 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 1.262 | 2.847 | 0.452 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.366 | 1.508 | 0.179 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.323 | 0.586 | 0.280 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.795 | 1.467 | 0.584 |

## Synthétique, 500 événements

500 événements, 80.9 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 11.869 | 20.895 | 6.748 |
| Requête par jour × 366 (anniversaires) | 0.289 | 0.312 | 0.269 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.128 | 0.275 | 0.107 |
| Recherche dans les titres × 20 (Quel âge ?) | 11.599 | 36.075 | 5.189 |
| Page Aujourd'hui (GetDailyShock) | 0.046 | 0.068 | 0.038 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 10.530 | 21.185 | 4.905 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 0.864 | 18.762 | 0.596 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.704 | 4.227 | 0.527 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.600 | 2.413 | 0.358 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.677 | 16.777 | 1.149 |

## Synthétique, 1000 événements

1000 événements, 159.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 9.752 | 16.526 | 4.214 |
| Requête par jour × 366 (anniversaires) | 0.188 | 0.278 | 0.157 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.118 | 0.195 | 0.106 |
| Recherche dans les titres × 20 (Quel âge ?) | 21.238 | 55.743 | 7.959 |
| Page Aujourd'hui (GetDailyShock) | 0.024 | 0.057 | 0.023 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 3.817 | 4.272 | 2.578 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 1.758 | 3.589 | 0.905 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 1.230 | 3.631 | 0.645 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.501 | 3.540 | 0.378 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.394 | 3.874 | 1.161 |

## Synthétique, 5000 événements

5000 événements, 800.1 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 21.402 | 29.719 | 19.027 |
| Requête par jour × 366 (anniversaires) | 0.032 | 0.039 | 0.031 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.055 | 0.061 | 0.053 |
| Recherche dans les titres × 20 (Quel âge ?) | 37.604 | 42.553 | 28.104 |
| Page Aujourd'hui (GetDailyShock) | 0.030 | 0.034 | 0.029 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 9.162 | 9.939 | 8.427 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 12.281 | 36.364 | 7.474 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 11.187 | 14.582 | 6.638 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.811 | 2.406 | 1.026 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 9.257 | 12.407 | 7.286 |

## Synthétique, 10000 événements

10000 événements, 1600.9 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 37.396 | 40.076 | 35.898 |
| Requête par jour × 366 (anniversaires) | 0.031 | 0.035 | 0.030 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.164 | 0.213 | 0.150 |
| Recherche dans les titres × 20 (Quel âge ?) | 88.008 | 164.785 | 57.749 |
| Page Aujourd'hui (GetDailyShock) | 0.056 | 0.078 | 0.055 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 21.243 | 24.293 | 19.361 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 24.440 | 27.946 | 22.284 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 20.409 | 23.418 | 18.294 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.699 | 2.456 | 1.553 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 12.567 | 14.624 | 11.594 |
