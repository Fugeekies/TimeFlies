# Changelog - TimeFlies

Toutes les modifications notables apportées au projet **TimeFlies** sont documentées dans ce fichier.

Le format est basé sur [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/),
et ce projet adhère au [Semantic Versioning](https://semver.org/lang/fr/).

---

## [Unreleased]

### Added

- Création du dépôt : licence GPL v3, README, règles du projet (`AGENTS.md`).
- Spécifications initiales : architecture, base d'événements, identité visuelle, feuille de route et spécifications des écrans (`docs/`).
- Maquettes visuelles des écrans Android et Windows, en HTML et PNG (`docs/maquettes/`).
- Style d'affichage de la V1 : Polaroïd (âges écrits à la main) ; Éphéméride au jalon suivant, VHS en idée.
- Architecture technique : Clean Architecture (`domain`, `data`, `ui`, applications Android et Windows), stack, événements en JSON chargés en mémoire jusqu'à 5 000, réglages DataStore partagés (`docs/ARCHITECTURE.md`).
- Guide développeur : outils, commandes et débogage (`docs/DEVELOPMENT.md`).
- Feuille de route redécoupée en jalons courts (solution, lecture des données, Aujourd'hui, Réglages…), données par thème en paliers jusqu'à 5 000 événements, CI et releases à la fin.
