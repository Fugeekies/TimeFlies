# AGENTS.md — TimeFlies

Application **Android et Windows** qui donne des « coups de vieux », inspirée de l'épisode *Bloqués #108 - On est les vieux des jeunes*. Kotlin et Compose Multiplatform : un seul code partagé, avec des parties propres à chaque plateforme (widget Android, icône de barre des tâches et notification Windows). La documentation est en **français**, le code et les commentaires en **anglais**. L'interface est en français d'abord, prévue pour être traduite.

**Architecture (décision du 14/11/2026)** : Clean Architecture. `domain` (entités, ports, cas d'usage, règles en fonctions pures) ne dépend de rien d'autre que `kotlinx-datetime` ; `data` implémente les ports (JSON, DataStore, horloge) ; `ui`, le widget Android et l'icône Windows n'appellent que des cas d'usage ; `androidApp` et `desktopApp` assemblent le tout (`AppGraph`, sans framework d'injection). La date du jour est toujours injectée (`Clock`). Événements en fichiers JSON chargés en mémoire, réévaluation à 5 000 événements.

Ce fichier est la **source unique** des instructions pour les agents IA : GitHub Copilot le lit directement, Claude Code via l'import `@AGENTS.md` de `CLAUDE.md`.

## Documentation

| Document | Contenu |
|---|---|
| `docs/ARCHITECTURE.md` | Clean Architecture (`domain`, `data`, `ui`, `androidApp`, `desktopApp`), stack, base d'événements, réglages, langues, distribution. |
| `docs/DEVELOPMENT.md` | Outils, commandes, débogage (version PC, tests, Android, panneau de débogage, logs). |
| `docs/EVENTS_DATA.md` | Format de la base d'événements, natures, types, règles d'appariement et du coup de vieux du jour. |
| `docs/UI_DESIGN_SPEC.md` | Identité visuelle, palette, polices, navigation, langues, accessibilité. |
| `docs/UISpec/*.template.md` | Une spécification et une maquette ASCII par écran : `Onboarding`, `Today`, `Equivalences`, `MyEquivalent`, `Settings`, `AndroidWidget`, `WindowsTray`. |
| `docs/maquettes/` | Maquettes visuelles (HTML et PNG) des écrans Android et Windows. En cas d'écart, les specs `UISpec/` font foi. |
| `docs/ROADMAP.md` | Jalons à faire et terminés. |

Les points marqués **À valider** et les « Questions ouvertes » de ces documents ne sont pas tranchés : poser la question avant de coder un comportement qui en dépend. Une décision prise est datée dans le document (« décision du JJ/MM/AAAA »).

## Fonctionnalités prévues

- **Aujourd'hui** (écran d'ouverture) : le coup de vieux du jour, c'est-à-dire les anniversaires d'événements du jour, chiffres ronds en premier (10, 15, 20, 25 ans…), avec l'âge qu'avait l'utilisateur. À défaut, l'anniversaire le plus proche des jours suivants.
- **Équivalences** : à partir de l'âge de l'utilisateur et de celui d'un jeune (15 ans par défaut), les paires d'événements de **même type** décalées de l'écart d'âge (Friends ↔ Happy Days). Pas de phrase générée : les deux événements, leurs dates et l'âge de chacun.
- **Mon équivalent** : un événement choisi et son équivalent pour l'autre génération, dans les deux sens.
- **Réglages** : mois et année de naissance de l'utilisateur (jamais le jour, décision du 13/11/2026), âge du jeune, région, gardés sur l'appareil. Un seul style d'affichage en V1, **Polaroïd** (âges écrits à la main) ; l'Éphéméride vient dans le jalon suivant, le VHS peut-être plus tard (décision du 13/11/2026, maquettes dans `docs/maquettes/`).
- **Widget Android** (mis à jour chaque jour) ; sur Windows, **icône de barre des tâches** et **notification** le matin.

## Données

- Base d'événements intégrée à l'application, sans connexion ni compte. Aucune donnée ne quitte l'appareil.
- Chaque événement a une date **au jour près** et un **type** précis (sitcom culte, star du foot, mort d'une star de la musique, console…), qui sert à apparier les équivalences.
- Une date doit être vérifiée avant d'être ajoutée : ne jamais inventer une date ou un événement.

## Distribution

- GitHub Actions compile l'**APK** Android et l'**installeur Windows** (`.msi`) et les publie dans les Releases.
- L'APK est signée avec une clé stable, gardée dans les secrets du dépôt, jamais dans le code.

## Règles Git & PR

- **Jamais de commit direct sur `main`** : branche dédiée (`feature/…`, `fix/…`), puis PR.
- **PR créées en brouillon** (`gh pr create --draft`), passées en « Ready for review » (`gh pr ready`) quand elles sont terminées.
- Toute PR touchant le code, la configuration ou les fonctionnalités met à jour `CHANGELOG.md` sous `## [Unreleased]`, catégories Keep a Changelog : `Added`, `Changed`, `Fixed`, `Performance`, `Security`. Entrées en français, concises.

## Méthode de travail

**Zéro invention.** En cas d'ambiguïté sur une règle, un comportement ou un choix d'architecture, s'arrêter et poser la question avant de coder.
