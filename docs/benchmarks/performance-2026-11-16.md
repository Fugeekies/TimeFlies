# Rapport de performance TimeFlies

- Date : 2026-11-16T21:01:44.0050742+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

192 événements, 74.8 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 7.849 | 11.421 | 4.523 |
| Requête par jour × 366 (anniversaires) | 0.326 | 0.908 | 0.278 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.432 | 0.784 | 0.160 |
| Recherche dans les titres × 20 (Quel âge ?) | 4.042 | 5.622 | 2.902 |
| Page Aujourd'hui (GetDailyShock) | 0.187 | 0.426 | 0.090 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 8.115 | 11.226 | 6.024 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 1.635 | 3.767 | 0.822 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.557 | 1.189 | 0.274 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.409 | 0.928 | 0.218 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.804 | 1.361 | 0.539 |

## Synthétique, 500 événements

500 événements, 90.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 6.603 | 11.331 | 3.820 |
| Requête par jour × 366 (anniversaires) | 0.220 | 0.510 | 0.184 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.115 | 0.168 | 0.077 |
| Recherche dans les titres × 20 (Quel âge ?) | 6.432 | 9.196 | 3.616 |
| Page Aujourd'hui (GetDailyShock) | 0.048 | 0.072 | 0.025 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 7.597 | 9.602 | 4.710 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 0.409 | 0.788 | 0.326 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.385 | 0.822 | 0.304 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.221 | 0.393 | 0.208 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 0.742 | 0.991 | 0.681 |

## Synthétique, 1000 événements

1000 événements, 181.3 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 6.012 | 7.691 | 4.583 |
| Requête par jour × 366 (anniversaires) | 0.088 | 0.096 | 0.086 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.072 | 0.117 | 0.070 |
| Recherche dans les titres × 20 (Quel âge ?) | 5.883 | 7.228 | 5.201 |
| Page Aujourd'hui (GetDailyShock) | 0.012 | 0.027 | 0.011 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 5.998 | 6.446 | 3.085 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 1.684 | 3.926 | 0.786 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 1.039 | 1.805 | 0.673 |
| Page Quel âge ?, liste par famille (SearchEvents) | 0.250 | 0.503 | 0.187 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 1.967 | 8.604 | 1.025 |

## Synthétique, 5000 événements

5000 événements, 919.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 23.169 | 29.297 | 19.363 |
| Requête par jour × 366 (anniversaires) | 0.024 | 0.028 | 0.023 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.111 | 0.125 | 0.054 |
| Recherche dans les titres × 20 (Quel âge ?) | 32.800 | 46.536 | 24.544 |
| Page Aujourd'hui (GetDailyShock) | 0.030 | 0.076 | 0.029 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 9.187 | 9.482 | 8.523 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 7.004 | 8.274 | 6.312 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 5.909 | 6.682 | 5.482 |
| Page Quel âge ?, liste par famille (SearchEvents) | 1.083 | 2.798 | 0.868 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 7.214 | 9.909 | 5.046 |

## Synthétique, 10000 événements

10000 événements, 1842.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 41.851 | 62.584 | 35.110 |
| Requête par jour × 366 (anniversaires) | 0.024 | 0.027 | 0.023 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.088 | 0.156 | 0.081 |
| Recherche dans les titres × 20 (Quel âge ?) | 88.543 | 96.701 | 71.558 |
| Page Aujourd'hui (GetDailyShock) | 0.059 | 0.130 | 0.056 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 19.943 | 21.875 | 19.229 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 20.585 | 24.323 | 19.548 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 17.214 | 18.462 | 16.541 |
| Page Quel âge ?, liste par famille (SearchEvents) | 2.975 | 5.244 | 2.475 |
| Page Quel âge ?, recherche × 4 (SearchEvents) | 17.583 | 21.241 | 16.003 |
