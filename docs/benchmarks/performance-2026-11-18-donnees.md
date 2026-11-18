# Rapport de performance TimeFlies

- Date : 2026-11-18T20:41:03.259799+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

993 événements, 472.0 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 21.520 | 26.320 | 12.463 |
| Requête par jour × 366 (anniversaires) | 0.316 | 0.607 | 0.216 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.317 | 0.476 | 0.268 |
| Recherche dans les titres × 20 (Quel âge ?) | 20.372 | 28.317 | 19.621 |
| Page Aujourd'hui (GetDailyShock) | 0.179 | 0.289 | 0.126 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 7.717 | 8.514 | 7.498 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 2.676 | 10.462 | 1.509 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 1.094 | 1.490 | 0.968 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 3.772 | 11.159 | 2.108 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.377 | 2.590 | 0.863 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 7.049 | 9.469 | 4.384 |

## Synthétique, 500 événements

500 événements, 81.1 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 6.337 | 8.293 | 3.286 |
| Requête par jour × 366 (anniversaires) | 0.235 | 0.306 | 0.213 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.108 | 0.132 | 0.099 |
| Recherche dans les titres × 20 (Quel âge ?) | 4.303 | 7.271 | 3.311 |
| Page Aujourd'hui (GetDailyShock) | 0.021 | 0.054 | 0.021 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 4.350 | 6.504 | 3.839 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.734 | 0.806 | 0.401 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.411 | 0.722 | 0.379 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 0.947 | 1.971 | 0.787 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.131 | 0.151 | 0.128 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.789 | 0.955 | 0.724 |

## Synthétique, 1000 événements

1000 événements, 159.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 4.771 | 7.551 | 4.206 |
| Requête par jour × 366 (anniversaires) | 0.096 | 0.163 | 0.091 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.068 | 0.084 | 0.066 |
| Recherche dans les titres × 20 (Quel âge ?) | 6.630 | 10.276 | 5.576 |
| Page Aujourd'hui (GetDailyShock) | 0.033 | 0.068 | 0.018 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 5.548 | 6.430 | 2.696 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.024 | 1.453 | 0.896 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.939 | 1.283 | 0.873 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 2.023 | 3.025 | 1.868 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.235 | 0.463 | 0.192 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.205 | 1.538 | 1.099 |

## Synthétique, 5000 événements

5000 événements, 800.4 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 23.348 | 30.301 | 20.204 |
| Requête par jour × 366 (anniversaires) | 0.030 | 0.040 | 0.030 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.056 | 0.069 | 0.052 |
| Recherche dans les titres × 20 (Quel âge ?) | 26.843 | 31.611 | 23.622 |
| Page Aujourd'hui (GetDailyShock) | 0.029 | 0.038 | 0.027 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 8.744 | 8.866 | 8.584 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 5.925 | 8.599 | 5.488 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 5.272 | 6.297 | 5.055 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 14.899 | 17.407 | 14.004 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.970 | 1.302 | 0.877 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 6.043 | 7.683 | 4.610 |

## Synthétique, 10000 événements

10000 événements, 1601.1 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 37.558 | 41.056 | 35.774 |
| Requête par jour × 366 (anniversaires) | 0.030 | 0.037 | 0.028 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.081 | 0.087 | 0.077 |
| Recherche dans les titres × 20 (Quel âge ?) | 71.300 | 84.981 | 59.080 |
| Page Aujourd'hui (GetDailyShock) | 0.129 | 0.218 | 0.058 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 21.460 | 23.988 | 18.777 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 17.817 | 20.007 | 16.818 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 15.813 | 27.966 | 14.834 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 44.157 | 46.982 | 42.486 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.615 | 2.169 | 1.512 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 15.316 | 17.925 | 13.712 |
