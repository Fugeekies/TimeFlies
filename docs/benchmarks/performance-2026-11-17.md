# Rapport de performance TimeFlies

- Date : 2026-11-17T19:20:31.8612765+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

192 événements, 70.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 6.931 | 11.095 | 4.607 |
| Requête par jour × 366 (anniversaires) | 0.278 | 0.475 | 0.218 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.249 | 0.334 | 0.184 |
| Recherche dans les titres × 20 (Quel âge ?) | 3.640 | 5.638 | 2.667 |
| Page Aujourd'hui (GetDailyShock) | 0.124 | 0.256 | 0.095 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 7.573 | 7.895 | 5.056 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.198 | 1.985 | 0.748 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.516 | 1.108 | 0.354 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 0.812 | 1.403 | 0.509 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.197 | 0.371 | 0.158 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.551 | 0.868 | 0.476 |

## Synthétique, 500 événements

500 événements, 80.9 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 6.449 | 7.799 | 5.160 |
| Requête par jour × 366 (anniversaires) | 0.142 | 0.390 | 0.139 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.089 | 0.122 | 0.085 |
| Recherche dans les titres × 20 (Quel âge ?) | 3.643 | 4.156 | 3.293 |
| Page Aujourd'hui (GetDailyShock) | 0.056 | 0.134 | 0.027 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 7.005 | 8.423 | 5.049 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.474 | 0.916 | 0.439 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.467 | 0.728 | 0.410 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 0.932 | 1.650 | 0.845 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.286 | 0.548 | 0.267 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.755 | 1.044 | 0.672 |

## Synthétique, 1000 événements

1000 événements, 159.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 7.034 | 10.927 | 5.640 |
| Requête par jour × 366 (anniversaires) | 0.098 | 0.199 | 0.096 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.142 | 0.175 | 0.082 |
| Recherche dans les titres × 20 (Quel âge ?) | 8.924 | 15.006 | 5.673 |
| Page Aujourd'hui (GetDailyShock) | 0.014 | 0.033 | 0.013 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 2.696 | 2.980 | 2.456 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.085 | 1.796 | 0.940 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.971 | 1.534 | 0.903 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 2.287 | 3.231 | 1.947 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.202 | 0.275 | 0.196 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.254 | 1.952 | 1.155 |

## Synthétique, 5000 événements

5000 événements, 800.1 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 22.334 | 24.237 | 19.703 |
| Requête par jour × 366 (anniversaires) | 0.031 | 0.066 | 0.029 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.108 | 0.124 | 0.056 |
| Recherche dans les titres × 20 (Quel âge ?) | 29.727 | 32.612 | 24.290 |
| Page Aujourd'hui (GetDailyShock) | 0.028 | 0.040 | 0.026 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 9.277 | 9.678 | 8.567 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 6.402 | 8.040 | 5.838 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 5.588 | 6.279 | 5.362 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 16.122 | 18.262 | 15.147 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.998 | 1.181 | 0.903 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 6.581 | 8.145 | 4.762 |

## Synthétique, 10000 événements

10000 événements, 1600.9 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 35.044 | 37.815 | 32.582 |
| Requête par jour × 366 (anniversaires) | 0.035 | 0.038 | 0.028 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.085 | 0.110 | 0.081 |
| Recherche dans les titres × 20 (Quel âge ?) | 74.882 | 84.737 | 59.685 |
| Page Aujourd'hui (GetDailyShock) | 0.055 | 0.070 | 0.054 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 19.121 | 19.396 | 18.443 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 19.086 | 22.358 | 18.246 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 16.774 | 18.643 | 15.867 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 47.447 | 50.245 | 45.705 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.586 | 2.375 | 1.494 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 16.097 | 18.829 | 13.790 |
