# Architecture technique - TimeFlies

> Document de conception écrit avant le code (13/11/2026). Les choix marqués **À valider** sont des propositions : ils ne sont pas tranchés.

## Principes

- **Un seul code, deux plateformes.** Kotlin et Compose Multiplatform : base d'événements, calculs et écrans sont écrits une fois pour Android et Windows. Seul ce qui dépend du système est écrit à part : widget Android, icône de barre des tâches et notification Windows, stockage des réglages.
- **Hors ligne, sans compte.** La base d'événements est livrée dans l'application. Aucune donnée ne quitte l'appareil.
- **Calculs testables sans interface.** Les règles (anniversaires, âges, équivalences) vivent dans un module sans dépendance à Compose, couvert par des tests unitaires.

## Modules (proposition, **À valider**)

| Module | Rôle |
|---|---|
| `core` | Kotlin pur (commun aux deux plateformes). Modèle (`Event`, `EventType`, `TypeFamily`, `Generations`), chargement et validation de la base (`events.json`), calculs : `Anniversaries` (coup de vieux du jour), `Ages` (âge à une date), `Equivalences` (appariement par type et écart d'âge). Aucune dépendance à l'interface. |
| `ui` | Compose Multiplatform : thème, navigation adaptative, les quatre écrans et le premier lancement. Textes dans les ressources Compose (`strings.xml`, français de référence). |
| `androidApp` | Point d'entrée Android, **widget** (Jetpack Glance), mise à jour quotidienne (WorkManager), stockage des réglages (DataStore). |
| `desktopApp` | Point d'entrée Windows (Compose Desktop), **icône de barre des tâches** (`Tray`), **notification** du matin, lancement au démarrage de Windows, stockage des réglages (fichier JSON dans `%APPDATA%\TimeFlies`). |

Le découpage peut se réduire à un module partagé (`shared`, avec `core` et `ui` en paquets) si deux modules alourdissent la compilation sans bénéfice : à décider au jalon « Chaîne de compilation ».

## Flux de données

1. Au démarrage, `core` lit `events.json` (ressource embarquée) et vérifie chaque entrée : date valide et passée, type connu, identifiant unique. Une entrée invalide fait échouer les tests, jamais l'application en production (elle est écartée et journalisée).
2. Les réglages (date de naissance, âge du jeune) sont lus depuis le stockage de la plateforme.
3. Les écrans demandent leurs données à `core` pour la date du jour, dans le **fuseau horaire de l'appareil** : le « jour » d'un anniversaire est le jour civil local.
4. Le widget Android et la notification Windows appellent le même calcul `Anniversaries` que l'écran Aujourd'hui : ils affichent toujours le même coup de vieux.

## Règles de calcul

Détaillées dans [`EVENTS_DATA.md`](EVENTS_DATA.md) (format et types) et dans les spécifications d'écran (`UISpec/`). En résumé :

- **Âge à une date** : années civiles révolues entre la date de naissance et la date, comme un âge réel (on ne prend pas d'année de 365,25 jours).
- **Anniversaire** : même jour et même mois qu'une date d'événement, au moins un an après. Un événement du 29 février est fêté le 28 février les années non bissextiles (**À valider**).
- **Écart d'âge** : différence entre la date de naissance de l'utilisateur et celle du jeune, calculée à partir de son âge (voir `UISpec/Settings.template.md`).
- **Équivalence** : événement de **même type**, décalé de l'écart d'âge, dans une tolérance (proposition : ±2 ans, **À valider**).

## Stockage des réglages

| Plateforme | Emplacement | Contenu |
|---|---|---|
| Android | DataStore (Preferences), privé à l'application | date de naissance, âge du jeune, premier lancement terminé |
| Windows | `%APPDATA%\TimeFlies\settings.json`, écrit d'un bloc | les mêmes, plus l'heure de la notification et le lancement au démarrage |

Une désinstallation Android efface les réglages. Sur Windows, le dossier `%APPDATA%\TimeFlies` reste après une désinstallation (**À valider** : le supprimer ou non).

## Distribution

- **GitHub Actions** compile à chaque version :
  - l'**APK** Android signée (clé stable dans les secrets du dépôt : `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`) ;
  - l'**installeur Windows** `.msi` (tâche `packageMsi` de Compose Desktop, sur un exécuteur Windows), qui embarque son propre Java : rien à installer à part.
- Une version est publiée dans les Releases à partir d'un tag `vX.Y.Z` (Semantic Versioning). Le numéro de version de l'application vient du tag ; une compilation locale reste en `0.0.0-dev`.
- L'installeur Windows n'est pas signé par un éditeur reconnu : Windows affiche « éditeur inconnu » au premier lancement. Le README l'explique.

## Versions minimales (proposition, **À valider**)

- Android 8.0 (API 26) et plus : WorkManager et Glance y fonctionnent, et cela couvre la quasi-totalité des téléphones en service.
- Windows 10 et 11, 64 bits.
