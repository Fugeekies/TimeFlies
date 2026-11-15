# Rapport de performance TimeFlies

- Date : 2026-11-15T21:58:20.3390968+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

76 événements, 30.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 7.156 | 13.923 | 3.120 |
| Requête par jour × 366 (anniversaires) | 0.570 | 1.551 | 0.331 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.400 | 0.835 | 0.218 |
| Recherche dans les titres × 20 (Quel âge ?) | 1.465 | 1.957 | 0.365 |
| Page Aujourd'hui (GetDailyShock) | 0.129 | 0.332 | 0.099 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 9.296 | 10.881 | 4.982 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 0.561 | 1.525 | 0.315 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.352 | 0.576 | 0.173 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.291 | 0.397 | 0.139 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.146 | 0.272 | 0.124 |

## Synthétique, 500 événements

500 événements, 90.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 6.788 | 9.441 | 3.853 |
| Requête par jour × 366 (anniversaires) | 0.243 | 0.251 | 0.239 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.218 | 0.297 | 0.130 |
| Recherche dans les titres × 20 (Quel âge ?) | 2.837 | 4.058 | 2.261 |
| Page Aujourd'hui (GetDailyShock) | 0.053 | 0.086 | 0.049 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 6.963 | 7.831 | 2.949 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 0.588 | 1.016 | 0.456 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.353 | 0.534 | 0.320 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.389 | 0.499 | 0.215 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.616 | 0.947 | 0.406 |

## Synthétique, 1000 événements

1000 événements, 181.3 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 7.079 | 11.096 | 5.110 |
| Requête par jour × 366 (anniversaires) | 0.211 | 0.312 | 0.107 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.130 | 0.156 | 0.113 |
| Recherche dans les titres × 20 (Quel âge ?) | 4.465 | 7.429 | 3.803 |
| Page Aujourd'hui (GetDailyShock) | 0.020 | 0.030 | 0.019 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 3.576 | 3.948 | 3.157 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 1.101 | 1.765 | 0.784 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.831 | 1.238 | 0.702 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.241 | 0.359 | 0.227 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.904 | 1.576 | 0.799 |

## Synthétique, 5000 événements

5000 événements, 919.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 22.205 | 26.918 | 19.015 |
| Requête par jour × 366 (anniversaires) | 0.044 | 0.055 | 0.043 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.060 | 0.079 | 0.058 |
| Recherche dans les titres × 20 (Quel âge ?) | 23.624 | 27.258 | 20.611 |
| Page Aujourd'hui (GetDailyShock) | 0.035 | 0.069 | 0.033 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 12.097 | 14.729 | 10.025 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 9.447 | 10.362 | 6.866 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 8.149 | 9.806 | 5.896 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.956 | 1.677 | 0.889 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 5.673 | 10.187 | 4.011 |

## Synthétique, 10000 événements

10000 événements, 1842.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 41.896 | 77.415 | 40.389 |
| Requête par jour × 366 (anniversaires) | 0.043 | 0.069 | 0.042 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.083 | 0.107 | 0.081 |
| Recherche dans les titres × 20 (Quel âge ?) | 50.494 | 74.709 | 47.542 |
| Page Aujourd'hui (GetDailyShock) | 0.075 | 0.117 | 0.060 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 25.572 | 26.304 | 24.897 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 30.847 | 32.836 | 23.661 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 26.071 | 26.970 | 19.841 |
| Page Quel âge ?, liste par famille (SearchEvents) | 2.618 | 3.584 | 2.175 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 14.134 | 16.147 | 13.155 |
