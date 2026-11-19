# Rapport de performance TimeFlies

- Date : 2026-11-19T19:13:22.1606208+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

993 événements, 472.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 20.764 | 25.425 | 9.942 |
| Requête par jour × 366 (anniversaires) | 0.339 | 0.434 | 0.194 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.342 | 0.515 | 0.228 |
| Recherche dans les titres × 20 (Quel âge ?) | 24.832 | 35.801 | 19.590 |
| Page Aujourd'hui (GetDailyShock) | 0.190 | 0.339 | 0.132 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 10.035 | 12.816 | 8.671 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 3.122 | 8.568 | 1.493 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 1.116 | 2.146 | 0.968 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 2.289 | 3.426 | 2.023 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.851 | 1.480 | 0.747 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 4.462 | 8.276 | 3.851 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.213 | 0.447 | 0.170 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.283 | 1.052 | 0.258 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 5.184 | 6.219 | 4.258 |

## Synthétique, 500 événements

487 événements, 81.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 3.245 | 4.841 | 2.794 |
| Requête par jour × 366 (anniversaires) | 0.263 | 0.430 | 0.119 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.111 | 0.185 | 0.066 |
| Recherche dans les titres × 20 (Quel âge ?) | 3.825 | 5.056 | 3.291 |
| Page Aujourd'hui (GetDailyShock) | 0.021 | 0.026 | 0.020 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 4.091 | 5.128 | 3.721 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 0.703 | 1.062 | 0.280 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 0.668 | 0.891 | 0.237 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 1.306 | 1.908 | 1.241 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.137 | 0.269 | 0.127 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.680 | 0.781 | 0.664 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.146 | 0.222 | 0.138 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.175 | 0.323 | 0.164 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 0.846 | 1.074 | 0.797 |

## Synthétique, 1000 événements

973 événements, 159.8 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 4.921 | 7.055 | 4.174 |
| Requête par jour × 366 (anniversaires) | 0.105 | 0.284 | 0.093 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.070 | 0.117 | 0.067 |
| Recherche dans les titres × 20 (Quel âge ?) | 6.143 | 8.134 | 5.388 |
| Page Aujourd'hui (GetDailyShock) | 0.014 | 0.034 | 0.013 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 2.733 | 3.336 | 2.523 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 1.648 | 2.361 | 1.374 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 1.419 | 2.405 | 0.706 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 1.707 | 2.133 | 1.626 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.135 | 0.173 | 0.129 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.197 | 1.548 | 1.102 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.101 | 0.142 | 0.095 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.171 | 0.226 | 0.163 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 1.354 | 1.768 | 1.224 |

## Synthétique, 5000 événements

4862 événements, 800.4 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 21.711 | 25.056 | 17.927 |
| Requête par jour × 366 (anniversaires) | 0.030 | 0.041 | 0.029 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.058 | 0.104 | 0.053 |
| Recherche dans les titres × 20 (Quel âge ?) | 27.352 | 47.974 | 22.611 |
| Page Aujourd'hui (GetDailyShock) | 0.029 | 0.032 | 0.028 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 8.924 | 9.435 | 8.384 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 6.075 | 7.088 | 5.528 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 5.202 | 5.958 | 4.881 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 14.384 | 16.283 | 13.438 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.664 | 0.899 | 0.638 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 6.297 | 7.684 | 4.700 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.116 | 0.148 | 0.111 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 0.709 | 1.004 | 0.672 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 5.911 | 7.319 | 4.849 |

## Synthétique, 10000 événements

9723 événements, 1601.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 36.290 | 38.145 | 34.032 |
| Requête par jour × 366 (anniversaires) | 0.031 | 0.039 | 0.029 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.082 | 0.090 | 0.079 |
| Recherche dans les titres × 20 (Quel âge ?) | 73.533 | 83.023 | 59.944 |
| Page Aujourd'hui (GetDailyShock) | 0.054 | 0.079 | 0.052 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 18.982 | 20.916 | 18.797 |
| Page Équivalences, un âge plus jeune (GetEquivalences) | 16.698 | 17.858 | 16.300 |
| Page Équivalences, un âge plus âgé (GetEquivalences) | 14.970 | 16.049 | 14.054 |
| Page Équivalences, trois âges côte à côte (GetEquivalences) | 41.906 | 44.467 | 40.541 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.533 | 2.388 | 1.394 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 15.889 | 19.427 | 12.580 |
| Page Aujourd'hui, 100 événements personnels (GetDailyShock) | 0.143 | 0.188 | 0.134 |
| Page Quel âge ?, liste par famille, 100 événements personnels | 1.797 | 2.248 | 1.586 |
| Page Quel âge ?, recherche × 4, 100 événements personnels | 15.860 | 18.081 | 14.280 |
