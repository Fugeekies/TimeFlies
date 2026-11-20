# Rapport de performance TimeFlies

- Date : 2026-11-20T18:50:50.4564094+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

993 événements, 472.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 23.459 | 50.840 | 15.365 |
| Requête par jour × 366 (anniversaires) | 0.307 | 0.561 | 0.200 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.576 | 0.854 | 0.367 |
| Recherche dans les titres × 20 (Quel âge ?) | 5.499 | 8.759 | 3.070 |
| Page Aujourd'hui (GetDailyShock) | 0.214 | 0.507 | 0.110 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 10.968 | 12.706 | 7.448 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.922 | 10.797 | 1.155 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 1.102 | 1.812 | 0.923 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 2.423 | 3.911 | 2.061 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.609 | 0.918 | 0.534 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.561 | 0.668 | 0.488 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.212 | 0.466 | 0.173 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.547 | 0.703 | 0.492 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 0.903 | 1.218 | 0.737 |

## Synthétique, 500 événements

487 événements, 81.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 3.398 | 5.460 | 3.065 |
| Requête par jour × 366 (anniversaires) | 0.296 | 0.347 | 0.121 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.070 | 0.080 | 0.067 |
| Recherche dans les titres × 20 (Quel âge ?) | 1.782 | 2.029 | 1.480 |
| Page Aujourd'hui (GetDailyShock) | 0.020 | 0.039 | 0.020 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 4.573 | 5.764 | 3.912 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.658 | 1.155 | 0.566 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.519 | 0.828 | 0.490 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 1.294 | 1.751 | 1.244 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.097 | 0.111 | 0.095 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.368 | 0.441 | 0.291 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.147 | 0.268 | 0.139 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.136 | 0.174 | 0.130 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 0.529 | 0.656 | 0.424 |

## Synthétique, 1000 événements

973 événements, 159.8 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 4.626 | 7.040 | 4.257 |
| Requête par jour × 366 (anniversaires) | 0.093 | 0.231 | 0.091 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.077 | 0.111 | 0.071 |
| Recherche dans les titres × 20 (Quel âge ?) | 1.977 | 2.248 | 1.771 |
| Page Aujourd'hui (GetDailyShock) | 0.015 | 0.037 | 0.014 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 2.684 | 2.904 | 2.647 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.065 | 1.558 | 0.939 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 1.328 | 2.124 | 0.941 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 2.231 | 3.174 | 2.102 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.127 | 0.171 | 0.125 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.342 | 0.414 | 0.330 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.107 | 0.223 | 0.103 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.160 | 0.202 | 0.156 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 0.469 | 0.568 | 0.444 |

## Synthétique, 5000 événements

4862 événements, 800.4 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 24.003 | 33.819 | 20.557 |
| Requête par jour × 366 (anniversaires) | 0.062 | 0.081 | 0.032 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.096 | 0.122 | 0.085 |
| Recherche dans les titres × 20 (Quel âge ?) | 6.030 | 11.103 | 3.884 |
| Page Aujourd'hui (GetDailyShock) | 0.033 | 0.045 | 0.032 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 10.579 | 11.585 | 10.154 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 6.366 | 8.681 | 5.816 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 6.004 | 6.981 | 5.384 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 15.250 | 17.652 | 14.611 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.562 | 0.690 | 0.549 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.806 | 1.083 | 0.691 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.123 | 0.148 | 0.119 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.635 | 0.982 | 0.590 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 0.920 | 1.095 | 0.839 |

## Synthétique, 10000 événements

9723 événements, 1601.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 36.922 | 39.050 | 33.752 |
| Requête par jour × 366 (anniversaires) | 0.040 | 0.046 | 0.029 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.054 | 0.069 | 0.051 |
| Recherche dans les titres × 20 (Quel âge ?) | 11.794 | 18.507 | 8.956 |
| Page Aujourd'hui (GetDailyShock) | 0.054 | 0.068 | 0.052 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 21.363 | 21.942 | 19.747 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 18.364 | 23.255 | 17.445 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 16.517 | 19.912 | 15.868 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 45.343 | 48.304 | 43.934 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.144 | 1.537 | 1.098 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.610 | 2.339 | 1.312 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.140 | 0.185 | 0.135 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 1.195 | 1.660 | 1.142 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 2.026 | 3.045 | 1.467 |
