# Rapport de performance TimeFlies

- Date : 2026-11-14T18:30:59.1656036+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

76 événements, 30.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 9.613 | 71.689 | 3.163 |
| Requête par jour × 366 (anniversaires) | 0.560 | 1.523 | 0.455 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.318 | 2.075 | 0.236 |
| Recherche dans les titres × 20 (Mon équivalent) | 1.115 | 3.221 | 0.711 |
| Page Aujourd'hui (GetDailyShock) | 0.272 | 1.275 | 0.130 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 18.356 | 62.353 | 9.923 |

## Synthétique, 500 événements

500 événements, 90.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 15.148 | 47.266 | 9.278 |
| Requête par jour × 366 (anniversaires) | 0.382 | 0.678 | 0.300 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.387 | 1.248 | 0.258 |
| Recherche dans les titres × 20 (Mon équivalent) | 5.623 | 12.981 | 4.235 |
| Page Aujourd'hui (GetDailyShock) | 0.070 | 0.265 | 0.062 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 6.133 | 25.029 | 4.085 |

## Synthétique, 1000 événements

1000 événements, 181.3 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 13.505 | 37.773 | 9.058 |
| Requête par jour × 366 (anniversaires) | 0.280 | 0.391 | 0.186 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.296 | 1.784 | 0.222 |
| Recherche dans les titres × 20 (Mon équivalent) | 13.858 | 33.796 | 7.872 |
| Page Aujourd'hui (GetDailyShock) | 0.042 | 0.057 | 0.038 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 8.417 | 12.952 | 5.597 |

## Synthétique, 5000 événements

5000 événements, 919.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 107.180 | 201.058 | 69.126 |
| Requête par jour × 366 (anniversaires) | 0.180 | 1.143 | 0.088 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.118 | 0.294 | 0.105 |
| Recherche dans les titres × 20 (Mon équivalent) | 119.916 | 346.179 | 62.195 |
| Page Aujourd'hui (GetDailyShock) | 0.085 | 0.205 | 0.059 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 31.490 | 41.985 | 19.434 |

## Synthétique, 10000 événements

10000 événements, 1842.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 142.949 | 236.934 | 95.678 |
| Requête par jour × 366 (anniversaires) | 0.109 | 0.400 | 0.090 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.142 | 0.463 | 0.123 |
| Recherche dans les titres × 20 (Mon équivalent) | 131.641 | 231.202 | 63.144 |
| Page Aujourd'hui (GetDailyShock) | 0.143 | 0.509 | 0.110 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 79.708 | 102.974 | 49.388 |
