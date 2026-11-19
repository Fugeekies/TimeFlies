# Rapport de performance TimeFlies

- Date : 2026-11-19T19:49:02.2136484+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

993 événements, 472.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 20.006 | 26.489 | 10.956 |
| Requête par jour × 366 (anniversaires) | 0.324 | 0.811 | 0.186 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.235 | 0.610 | 0.175 |
| Recherche dans les titres × 20 (Quel âge ?) | 20.507 | 23.111 | 18.497 |
| Page Aujourd'hui (GetDailyShock) | 0.179 | 0.257 | 0.127 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 9.562 | 13.391 | 8.198 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 2.903 | 6.847 | 1.466 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 1.135 | 1.587 | 0.980 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 2.307 | 3.641 | 1.985 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.876 | 1.326 | 0.645 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 4.581 | 5.358 | 3.949 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.202 | 0.430 | 0.173 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.290 | 1.174 | 0.259 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 4.682 | 6.214 | 3.997 |

## Synthétique, 500 événements

487 événements, 81.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 3.335 | 3.959 | 3.052 |
| Requête par jour × 366 (anniversaires) | 0.264 | 0.308 | 0.244 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.111 | 0.124 | 0.104 |
| Recherche dans les titres × 20 (Quel âge ?) | 3.791 | 5.026 | 3.405 |
| Page Aujourd'hui (GetDailyShock) | 0.021 | 0.033 | 0.020 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 4.429 | 4.786 | 3.787 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.267 | 0.334 | 0.257 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.256 | 0.285 | 0.247 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 0.660 | 0.819 | 0.614 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.206 | 0.293 | 0.126 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.741 | 0.846 | 0.679 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.278 | 0.350 | 0.140 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.175 | 0.393 | 0.162 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 0.865 | 1.374 | 0.828 |

## Synthétique, 1000 événements

973 événements, 159.8 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 4.599 | 7.027 | 4.226 |
| Requête par jour × 366 (anniversaires) | 0.103 | 0.159 | 0.091 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.078 | 0.187 | 0.063 |
| Recherche dans les titres × 20 (Quel âge ?) | 6.476 | 7.696 | 5.501 |
| Page Aujourd'hui (GetDailyShock) | 0.012 | 0.028 | 0.010 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 2.521 | 2.596 | 2.427 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.705 | 1.264 | 0.624 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.627 | 0.702 | 0.586 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 1.567 | 1.914 | 1.484 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.134 | 0.147 | 0.132 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.214 | 1.377 | 1.113 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.098 | 0.134 | 0.094 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.168 | 0.238 | 0.157 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 1.333 | 1.479 | 1.226 |

## Synthétique, 5000 événements

4862 événements, 800.4 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 20.961 | 23.952 | 18.355 |
| Requête par jour × 366 (anniversaires) | 0.030 | 0.039 | 0.029 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.056 | 0.084 | 0.050 |
| Recherche dans les titres × 20 (Quel âge ?) | 30.472 | 34.401 | 24.152 |
| Page Aujourd'hui (GetDailyShock) | 0.030 | 0.035 | 0.029 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 10.815 | 12.888 | 9.187 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 5.920 | 11.070 | 5.616 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 5.738 | 10.671 | 5.210 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 14.566 | 17.262 | 14.086 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.674 | 0.760 | 0.635 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 6.294 | 9.398 | 4.945 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.120 | 0.142 | 0.113 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.714 | 0.848 | 0.682 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 5.846 | 7.573 | 5.198 |

## Synthétique, 10000 événements

9723 événements, 1601.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 39.250 | 50.647 | 33.241 |
| Requête par jour × 366 (anniversaires) | 0.029 | 0.035 | 0.028 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.078 | 0.114 | 0.075 |
| Recherche dans les titres × 20 (Quel âge ?) | 66.146 | 76.791 | 51.174 |
| Page Aujourd'hui (GetDailyShock) | 0.056 | 0.062 | 0.055 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 19.379 | 19.730 | 19.001 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 17.319 | 19.284 | 16.614 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 15.642 | 22.963 | 14.573 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 43.293 | 52.725 | 41.806 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.472 | 1.558 | 1.391 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 14.169 | 20.645 | 12.207 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.143 | 0.167 | 0.136 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 1.559 | 2.827 | 1.495 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 13.162 | 15.946 | 12.408 |
