# Rapport de performance TimeFlies

- Date : 2026-11-15T21:20:57.4419424+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

76 événements, 30.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 4.234 | 7.259 | 2.309 |
| Requête par jour × 366 (anniversaires) | 0.464 | 0.819 | 0.278 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.233 | 0.572 | 0.170 |
| Recherche dans les titres × 20 (Mon équivalent) | 1.438 | 2.026 | 0.366 |
| Page Aujourd'hui (GetDailyShock) | 0.159 | 0.229 | 0.086 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 8.843 | 9.869 | 6.852 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 0.582 | 1.058 | 0.347 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.264 | 0.452 | 0.209 |

## Synthétique, 500 événements

500 événements, 90.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 7.806 | 11.037 | 4.659 |
| Requête par jour × 366 (anniversaires) | 0.264 | 0.397 | 0.169 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.200 | 0.239 | 0.140 |
| Recherche dans les titres × 20 (Mon équivalent) | 2.691 | 4.199 | 2.511 |
| Page Aujourd'hui (GetDailyShock) | 0.045 | 0.093 | 0.038 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 7.697 | 8.750 | 5.144 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 0.870 | 1.678 | 0.634 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.414 | 0.782 | 0.327 |

## Synthétique, 1000 événements

1000 événements, 181.3 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 6.090 | 7.840 | 4.643 |
| Requête par jour × 366 (anniversaires) | 0.112 | 0.213 | 0.105 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.125 | 0.177 | 0.073 |
| Recherche dans les titres × 20 (Mon équivalent) | 5.068 | 9.390 | 4.041 |
| Page Aujourd'hui (GetDailyShock) | 0.017 | 0.044 | 0.014 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 3.120 | 3.252 | 2.981 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 0.981 | 1.641 | 0.830 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 1.019 | 1.638 | 0.814 |

## Synthétique, 5000 événements

5000 événements, 919.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 24.871 | 29.545 | 21.628 |
| Requête par jour × 366 (anniversaires) | 0.045 | 0.057 | 0.043 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.059 | 0.076 | 0.059 |
| Recherche dans les titres × 20 (Mon équivalent) | 26.366 | 33.096 | 22.967 |
| Page Aujourd'hui (GetDailyShock) | 0.036 | 0.059 | 0.033 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 9.641 | 9.926 | 9.104 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 7.050 | 10.634 | 6.284 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 5.997 | 6.646 | 5.497 |

## Synthétique, 10000 événements

10000 événements, 1842.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 42.838 | 46.999 | 38.275 |
| Requête par jour × 366 (anniversaires) | 0.041 | 0.044 | 0.040 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.080 | 0.090 | 0.080 |
| Recherche dans les titres × 20 (Mon équivalent) | 62.347 | 77.959 | 50.572 |
| Page Aujourd'hui (GetDailyShock) | 0.082 | 0.125 | 0.061 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 20.529 | 21.589 | 20.134 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 20.342 | 22.084 | 19.331 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 17.557 | 18.991 | 17.025 |
