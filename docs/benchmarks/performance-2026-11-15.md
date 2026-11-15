# Rapport de performance TimeFlies

- Date : 2026-11-15T10:44:02.2400699+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

76 événements, 30.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 6.790 | 12.222 | 3.102 |
| Requête par jour × 366 (anniversaires) | 0.502 | 1.923 | 0.305 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.380 | 0.581 | 0.158 |
| Recherche dans les titres × 20 (Mon équivalent) | 1.447 | 2.284 | 0.356 |
| Page Aujourd'hui (GetDailyShock) | 0.175 | 0.603 | 0.095 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 8.808 | 10.301 | 7.838 |

## Synthétique, 500 événements

500 événements, 90.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 8.351 | 14.206 | 5.612 |
| Requête par jour × 366 (anniversaires) | 0.201 | 0.214 | 0.190 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.161 | 0.261 | 0.151 |
| Recherche dans les titres × 20 (Mon équivalent) | 2.059 | 2.294 | 1.902 |
| Page Aujourd'hui (GetDailyShock) | 0.055 | 0.098 | 0.050 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 7.477 | 8.872 | 6.451 |

## Synthétique, 1000 événements

1000 événements, 181.3 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 8.159 | 10.551 | 6.248 |
| Requête par jour × 366 (anniversaires) | 0.190 | 0.278 | 0.115 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.123 | 0.133 | 0.120 |
| Recherche dans les titres × 20 (Mon équivalent) | 5.293 | 7.105 | 4.275 |
| Page Aujourd'hui (GetDailyShock) | 0.030 | 0.043 | 0.027 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 3.310 | 6.503 | 2.774 |

## Synthétique, 5000 événements

5000 événements, 919.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 21.490 | 26.116 | 18.110 |
| Requête par jour × 366 (anniversaires) | 0.048 | 0.090 | 0.045 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.151 | 0.188 | 0.135 |
| Recherche dans les titres × 20 (Mon équivalent) | 23.952 | 28.899 | 21.115 |
| Page Aujourd'hui (GetDailyShock) | 0.038 | 0.060 | 0.035 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 9.828 | 9.971 | 8.987 |

## Synthétique, 10000 événements

10000 événements, 1842.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 41.048 | 43.388 | 39.211 |
| Requête par jour × 366 (anniversaires) | 0.043 | 0.046 | 0.042 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.080 | 0.096 | 0.072 |
| Recherche dans les titres × 20 (Mon équivalent) | 51.970 | 74.014 | 47.106 |
| Page Aujourd'hui (GetDailyShock) | 0.077 | 0.102 | 0.074 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 21.992 | 26.022 | 21.265 |
