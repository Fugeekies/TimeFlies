# Rapport de performance TimeFlies

- Date : 2026-11-17T23:09:01.0702563+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

192 événements, 87.0 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 9.656 | 13.317 | 6.170 |
| Requête par jour × 366 (anniversaires) | 0.356 | 0.721 | 0.222 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.362 | 0.746 | 0.165 |
| Recherche dans les titres × 20 (Quel âge ?) | 6.663 | 9.766 | 5.407 |
| Page Aujourd'hui (GetDailyShock) | 0.133 | 0.270 | 0.088 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 6.685 | 7.071 | 5.658 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.053 | 1.940 | 0.799 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.567 | 0.959 | 0.410 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 0.852 | 1.794 | 0.567 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.186 | 0.343 | 0.173 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.031 | 1.393 | 0.910 |

## Synthétique, 500 événements

500 événements, 80.9 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 4.704 | 6.147 | 3.826 |
| Requête par jour × 366 (anniversaires) | 0.208 | 0.274 | 0.139 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.083 | 0.095 | 0.080 |
| Recherche dans les titres × 20 (Quel âge ?) | 4.369 | 5.553 | 3.954 |
| Page Aujourd'hui (GetDailyShock) | 0.027 | 0.072 | 0.024 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 6.204 | 8.893 | 5.209 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.474 | 0.975 | 0.424 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.455 | 0.873 | 0.416 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 1.031 | 1.811 | 0.854 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.214 | 0.323 | 0.203 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.789 | 0.992 | 0.720 |

## Synthétique, 1000 événements

1000 événements, 159.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 5.436 | 8.911 | 4.780 |
| Requête par jour × 366 (anniversaires) | 0.172 | 0.195 | 0.152 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.076 | 0.118 | 0.071 |
| Recherche dans les titres × 20 (Quel âge ?) | 7.155 | 9.588 | 5.997 |
| Page Aujourd'hui (GetDailyShock) | 0.017 | 0.025 | 0.017 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 4.775 | 7.299 | 4.172 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.716 | 1.286 | 0.634 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.639 | 1.193 | 0.562 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 1.645 | 2.175 | 1.469 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.203 | 0.282 | 0.192 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.364 | 2.017 | 1.170 |

## Synthétique, 5000 événements

5000 événements, 800.1 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 23.526 | 28.706 | 18.117 |
| Requête par jour × 366 (anniversaires) | 0.034 | 0.070 | 0.032 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.064 | 0.119 | 0.055 |
| Recherche dans les titres × 20 (Quel âge ?) | 34.838 | 41.167 | 29.016 |
| Page Aujourd'hui (GetDailyShock) | 0.028 | 0.036 | 0.026 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 9.154 | 9.846 | 8.375 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 6.349 | 7.879 | 5.716 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 5.682 | 6.722 | 5.376 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 16.012 | 18.690 | 14.758 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.008 | 2.265 | 0.888 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 8.142 | 9.818 | 6.008 |

## Synthétique, 10000 événements

10000 événements, 1600.9 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 37.762 | 40.892 | 35.288 |
| Requête par jour × 366 (anniversaires) | 0.031 | 0.034 | 0.030 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.087 | 0.097 | 0.084 |
| Recherche dans les titres × 20 (Quel âge ?) | 88.713 | 95.954 | 69.297 |
| Page Aujourd'hui (GetDailyShock) | 0.054 | 0.066 | 0.053 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 22.985 | 25.069 | 19.497 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 19.224 | 22.781 | 18.134 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 16.285 | 17.907 | 15.659 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 48.188 | 50.449 | 45.203 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.565 | 2.326 | 1.465 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 17.704 | 19.862 | 15.679 |
