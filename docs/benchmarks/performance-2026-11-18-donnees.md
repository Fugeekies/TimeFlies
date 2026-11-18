# Rapport de performance TimeFlies

- Date : 2026-11-18T19:09:55.9296394+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

512 événements, 234.3 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 13.204 | 16.892 | 11.865 |
| Requête par jour × 366 (anniversaires) | 0.293 | 0.475 | 0.217 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.302 | 0.401 | 0.271 |
| Recherche dans les titres × 20 (Quel âge ?) | 10.590 | 11.872 | 9.796 |
| Page Aujourd'hui (GetDailyShock) | 0.150 | 0.212 | 0.113 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 7.154 | 8.027 | 5.788 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.871 | 3.609 | 1.110 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.823 | 1.172 | 0.513 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 1.007 | 1.413 | 0.906 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.347 | 0.524 | 0.328 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 2.718 | 5.136 | 2.101 |

## Synthétique, 500 événements

500 événements, 80.9 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 7.012 | 11.219 | 3.651 |
| Requête par jour × 366 (anniversaires) | 0.229 | 0.260 | 0.205 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.113 | 0.142 | 0.101 |
| Recherche dans les titres × 20 (Quel âge ?) | 3.657 | 4.305 | 3.433 |
| Page Aujourd'hui (GetDailyShock) | 0.023 | 0.060 | 0.021 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 3.895 | 3.924 | 3.682 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.650 | 0.882 | 0.408 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.807 | 1.245 | 0.413 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 0.971 | 1.882 | 0.889 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.407 | 0.643 | 0.195 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.784 | 0.945 | 0.714 |

## Synthétique, 1000 événements

1000 événements, 159.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 5.371 | 6.649 | 4.800 |
| Requête par jour × 366 (anniversaires) | 0.095 | 0.128 | 0.092 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.070 | 0.117 | 0.069 |
| Recherche dans les titres × 20 (Quel âge ?) | 6.340 | 7.255 | 5.505 |
| Page Aujourd'hui (GetDailyShock) | 0.020 | 0.055 | 0.019 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 4.899 | 5.626 | 4.326 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.062 | 1.720 | 0.941 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 1.047 | 1.920 | 0.927 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 2.455 | 4.435 | 1.947 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.212 | 0.621 | 0.203 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.512 | 2.646 | 1.157 |

## Synthétique, 5000 événements

5000 événements, 800.1 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 26.546 | 38.877 | 19.495 |
| Requête par jour × 366 (anniversaires) | 0.066 | 0.071 | 0.035 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.061 | 0.107 | 0.059 |
| Recherche dans les titres × 20 (Quel âge ?) | 28.069 | 34.836 | 23.188 |
| Page Aujourd'hui (GetDailyShock) | 0.029 | 0.041 | 0.028 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 10.601 | 15.461 | 9.044 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 6.282 | 7.188 | 6.045 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 5.731 | 6.608 | 5.535 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 16.175 | 18.517 | 15.687 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.970 | 1.124 | 0.856 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 6.475 | 8.259 | 5.327 |

## Synthétique, 10000 événements

10000 événements, 1600.9 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 36.939 | 38.887 | 34.463 |
| Requête par jour × 366 (anniversaires) | 0.041 | 0.043 | 0.031 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.078 | 0.088 | 0.074 |
| Recherche dans les titres × 20 (Quel âge ?) | 69.971 | 78.914 | 57.144 |
| Page Aujourd'hui (GetDailyShock) | 0.082 | 0.161 | 0.052 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 21.882 | 22.672 | 18.067 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 20.233 | 31.534 | 18.535 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 17.630 | 33.008 | 16.095 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 48.047 | 51.146 | 46.305 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.545 | 1.804 | 1.502 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 14.693 | 17.211 | 13.478 |
