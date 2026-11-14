# Changelog - TimeFlies

Toutes les modifications notables apportées au projet **TimeFlies** sont documentées dans ce fichier.

Le format est basé sur [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/),
et ce projet adhère au [Semantic Versioning](https://semver.org/lang/fr/).

---

## [Unreleased]

### Added

- Écran Aujourd'hui, style Polaroïd : coup de vieux du jour (chiffres ronds d'abord, 29 février fêté le 28, repli sur les 7 jours suivants), « Aussi aujourd'hui », « Bientôt… », navigation adaptative à quatre destinations, polices Caveat et Atkinson Hyperlegible embarquées, recalcul au changement de jour.
- Lecture des données : entités et ports du domaine (`Event`, `EventType`, `EventRepository`, `Clock`), `JsonEventRepository` (fichiers par thème, validation, index par jour et par type, dates régionales, recherche sans accents), rapport de couverture, tests.
- Données Cinéma, palier 1 : 76 films avec leur date de sortie nationale en France, sourcée et recoupée avec Wikidata (`cinema.json`).
- Création de la solution : projet Kotlin Multiplatform en cinq modules rangés dans `src/` (`domain`, `data`, `ui`, `androidApp`, `desktopApp`), wrapper Gradle, versions figées dans `gradle/libs.versions.toml`, fenêtre « TimeFlies » vide sur PC et Android.
- Création du dépôt : licence GPL v3, README, règles du projet (`AGENTS.md`).
- Spécifications initiales : architecture, base d'événements, identité visuelle, feuille de route et spécifications des écrans (`docs/`).
- Maquettes visuelles des écrans Android et Windows, en HTML et PNG (`docs/maquettes/`).
- Style d'affichage de la V1 : Polaroïd (âges écrits à la main) ; Éphéméride au jalon suivant, VHS en idée.
- Architecture technique : Clean Architecture (`domain`, `data`, `ui`, applications Android et Windows), stack, événements en JSON chargés en mémoire jusqu'à 5 000, réglages DataStore partagés (`docs/ARCHITECTURE.md`).
- Guide développeur : outils, commandes et débogage (`docs/DEVELOPMENT.md`).
- Feuille de route redécoupée en jalons courts (solution, lecture des données, Aujourd'hui, Réglages…), données par thème en paliers jusqu'à 5 000 événements, CI et releases à la fin.
