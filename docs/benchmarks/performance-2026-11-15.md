# Rapport de performance TimeFlies

- Date : 2026-11-15T21:46:44.831777+01:00
- JVM : OpenJDK 64-Bit Server VM 21.0.8, 8 processeurs, Windows 10
- Durées en millisecondes : médiane, 95e centile et minimum, après 5 exécutions de chauffe.
- Les jeux synthétiques sont inventés (benchmarks seulement) pour voir comment le chargement évolue vers le seuil des 5 000 événements.

## Fichiers réels

76 événements, 30.2 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 5.689 | 14.599 | 2.928 |
| Requête par jour × 366 (anniversaires) | 0.439 | 0.845 | 0.302 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.446 | 1.509 | 0.209 |
| Recherche dans les titres × 20 (Mon équivalent) | 1.463 | 2.296 | 0.382 |
| Page Aujourd'hui (GetDailyShock) | 0.151 | 0.249 | 0.103 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 5.760 | 7.376 | 3.633 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 0.629 | 1.742 | 0.305 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.221 | 0.427 | 0.177 |
| Page Mon équivalent, liste par famille (SearchEvents) | 0.190 | 0.294 | 0.142 |
| Page Mon équivalent, recherche × 4 (SearchEvents) | 0.144 | 0.299 | 0.113 |
| Page Mon équivalent, résultat × 50 événements (FindEquivalent) | 1.060 | 1.553 | 0.905 |

## Synthétique, 500 événements

500 événements, 90.5 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 7.578 | 10.961 | 3.498 |
| Requête par jour × 366 (anniversaires) | 0.167 | 0.270 | 0.159 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.112 | 0.233 | 0.108 |
| Recherche dans les titres × 20 (Mon équivalent) | 4.350 | 12.765 | 2.478 |
| Page Aujourd'hui (GetDailyShock) | 0.069 | 0.206 | 0.036 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 8.082 | 9.866 | 5.696 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 0.475 | 0.880 | 0.378 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.387 | 0.732 | 0.317 |
| Page Mon équivalent, liste par famille (SearchEvents) | 0.325 | 0.552 | 0.216 |
| Page Mon équivalent, recherche × 4 (SearchEvents) | 0.443 | 0.526 | 0.410 |
| Page Mon équivalent, résultat × 50 événements (FindEquivalent) | 0.379 | 0.604 | 0.365 |

## Synthétique, 1000 événements

1000 événements, 181.3 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 7.119 | 9.168 | 4.801 |
| Requête par jour × 366 (anniversaires) | 0.109 | 0.205 | 0.106 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.127 | 0.149 | 0.123 |
| Recherche dans les titres × 20 (Mon équivalent) | 4.207 | 5.923 | 3.856 |
| Page Aujourd'hui (GetDailyShock) | 0.021 | 0.031 | 0.019 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 3.658 | 3.941 | 3.007 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 1.084 | 1.659 | 0.857 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 0.866 | 1.389 | 0.703 |
| Page Mon équivalent, liste par famille (SearchEvents) | 0.251 | 0.423 | 0.233 |
| Page Mon équivalent, recherche × 4 (SearchEvents) | 0.890 | 1.061 | 0.823 |
| Page Mon équivalent, résultat × 50 événements (FindEquivalent) | 0.456 | 0.485 | 0.449 |

## Synthétique, 5000 événements

5000 événements, 919.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 20.999 | 29.018 | 18.796 |
| Requête par jour × 366 (anniversaires) | 0.042 | 0.073 | 0.040 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.050 | 0.061 | 0.048 |
| Recherche dans les titres × 20 (Mon équivalent) | 25.370 | 29.714 | 21.980 |
| Page Aujourd'hui (GetDailyShock) | 0.043 | 0.052 | 0.037 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 11.982 | 12.074 | 11.718 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 9.687 | 12.358 | 7.046 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 8.506 | 9.701 | 6.242 |
| Page Mon équivalent, liste par famille (SearchEvents) | 1.112 | 1.430 | 0.918 |
| Page Mon équivalent, recherche × 4 (SearchEvents) | 5.375 | 6.596 | 4.002 |
| Page Mon équivalent, résultat × 50 événements (FindEquivalent) | 1.272 | 1.373 | 1.222 |

## Synthétique, 10000 événements

10000 événements, 1842.7 Ko de JSON

| Mesure | Médiane | P95 | Min |
|---|---:|---:|---:|
| Chargement complet (lecture, validation, index) | 39.873 | 44.729 | 38.045 |
| Requête par jour × 366 (anniversaires) | 0.043 | 0.048 | 0.042 |
| Requête par type à ±2 ans × 100 (équivalences) | 0.084 | 0.095 | 0.082 |
| Recherche dans les titres × 20 (Mon équivalent) | 53.687 | 77.135 | 47.179 |
| Page Aujourd'hui (GetDailyShock) | 0.070 | 0.079 | 0.069 |
| Page Aujourd'hui pour chaque jour d'une année × 365 | 24.606 | 24.946 | 23.959 |
| Page Équivalences, autre personne plus jeune (GetEquivalences) | 32.144 | 36.445 | 23.135 |
| Page Équivalences, autre personne plus âgée (GetEquivalences) | 27.017 | 27.984 | 20.375 |
| Page Mon équivalent, liste par famille (SearchEvents) | 1.865 | 2.347 | 1.603 |
| Page Mon équivalent, recherche × 4 (SearchEvents) | 11.318 | 12.147 | 10.191 |
| Page Mon équivalent, résultat × 50 événements (FindEquivalent) | 2.438 | 2.642 | 2.352 |
