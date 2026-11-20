# Rapport de performance TimeFlies

- Date : 2026-11-20T20:18:51.4467778+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

1362 événements, 621.0 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 35.991 | 60.575 | 22.516 |
| Requête par jour × 366 (anniversaires) | 0.333 | 0.468 | 0.268 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.738 | 1.664 | 0.424 |
| Recherche dans les titres × 20 (Quel âge ?) | 7.514 | 19.439 | 5.148 |
| Page Aujourd'hui (GetDailyShock) | 0.304 | 0.703 | 0.172 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 35.058 | 55.871 | 16.516 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 3.873 | 25.674 | 1.902 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 3.465 | 15.426 | 2.358 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 6.713 | 43.732 | 2.778 |
| Page Quel âge ?, liste par famille (SearchEvents) | 2.162 | 8.532 | 1.255 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.896 | 1.495 | 0.649 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.433 | 0.960 | 0.212 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.796 | 1.843 | 0.546 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 1.700 | 2.462 | 0.928 |

## Synthétique, 500 événements

487 événements, 81.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 5.995 | 16.153 | 4.922 |
| Requête par jour × 366 (anniversaires) | 0.274 | 0.353 | 0.238 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.131 | 0.214 | 0.105 |
| Recherche dans les titres × 20 (Quel âge ?) | 1.876 | 2.863 | 1.113 |
| Page Aujourd'hui (GetDailyShock) | 0.043 | 0.053 | 0.041 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 12.030 | 15.240 | 8.326 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.545 | 0.842 | 0.258 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.240 | 0.440 | 0.223 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 0.639 | 1.091 | 0.607 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.111 | 0.197 | 0.100 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.352 | 0.470 | 0.283 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.198 | 0.341 | 0.143 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.269 | 0.337 | 0.155 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 0.637 | 0.985 | 0.461 |

## Synthétique, 1000 événements

973 événements, 159.8 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 6.607 | 9.440 | 4.488 |
| Requête par jour × 366 (anniversaires) | 0.182 | 0.251 | 0.093 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.127 | 0.188 | 0.109 |
| Recherche dans les titres × 20 (Quel âge ?) | 2.218 | 2.534 | 1.798 |
| Page Aujourd'hui (GetDailyShock) | 0.025 | 0.041 | 0.022 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 4.654 | 5.138 | 2.632 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.119 | 2.027 | 0.671 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 1.215 | 2.867 | 0.666 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 2.446 | 4.055 | 1.570 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.259 | 0.346 | 0.237 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.436 | 1.233 | 0.337 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.124 | 0.285 | 0.102 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.314 | 0.421 | 0.179 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 0.494 | 0.758 | 0.425 |

## Synthétique, 5000 événements

4862 événements, 800.4 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 31.555 | 52.327 | 19.894 |
| Requête par jour × 366 (anniversaires) | 0.031 | 0.037 | 0.030 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.059 | 0.112 | 0.056 |
| Recherche dans les titres × 20 (Quel âge ?) | 12.291 | 17.982 | 6.390 |
| Page Aujourd'hui (GetDailyShock) | 0.032 | 0.068 | 0.031 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 9.697 | 10.141 | 9.261 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 9.628 | 29.179 | 6.204 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 7.865 | 12.253 | 5.602 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 20.088 | 66.777 | 15.373 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.979 | 3.647 | 0.915 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.583 | 3.535 | 0.648 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.272 | 0.399 | 0.203 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 1.729 | 9.908 | 0.704 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 1.017 | 1.945 | 0.805 |

## Synthétique, 10000 événements

9723 événements, 1601.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 41.382 | 56.740 | 35.344 |
| Requête par jour × 366 (anniversaires) | 0.032 | 0.035 | 0.031 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.075 | 0.087 | 0.072 |
| Recherche dans les titres × 20 (Quel âge ?) | 12.153 | 17.830 | 9.337 |
| Page Aujourd'hui (GetDailyShock) | 0.059 | 0.066 | 0.056 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 20.166 | 26.958 | 19.404 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 24.210 | 59.016 | 17.396 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 34.132 | 63.548 | 17.059 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 45.556 | 78.801 | 42.707 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.171 | 1.283 | 1.122 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.295 | 1.461 | 1.157 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.144 | 0.171 | 0.141 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 1.250 | 1.565 | 1.176 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 1.362 | 1.632 | 1.169 |
