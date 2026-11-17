# AGENTS.md — TimeFlies

Application **Android et Windows** qui donne des « coups de vieux », inspirée de l'épisode *Bloqués #108 - On est les vieux des jeunes*. Kotlin et Compose Multiplatform : un seul code partagé, avec des parties propres à chaque plateforme (widget Android, icône de barre des tâches et notification Windows). La documentation est en **français**, le code et les commentaires en **anglais**. L'interface est en français d'abord, prévue pour être traduite.

**Architecture (décision du 14/11/2026)** : Clean Architecture. `domain` (entités, ports, cas d'usage, règles en fonctions pures) ne dépend de rien d'autre que `kotlinx-datetime` et `kotlinx-coroutines-core` (pour `Flow`, décision du 15/11/2026) ; `data` implémente les ports (JSON, DataStore, horloge) ; `ui`, le widget Android et l'icône Windows n'appellent que des cas d'usage ; `androidApp` et `desktopApp` assemblent le tout (`AppGraph`, sans framework d'injection). La date du jour est toujours injectée (`Clock`). Événements en fichiers JSON chargés en mémoire, réévaluation à 5 000 événements.

Ce fichier est la **source unique** des instructions pour les agents IA : GitHub Copilot le lit directement, Claude Code via l'import `@AGENTS.md` de `CLAUDE.md`.

## Documentation

| Document | Contenu |
|---|---|
| `docs/ARCHITECTURE.md` | Clean Architecture (`domain`, `data`, `ui`, `androidApp`, `desktopApp`), stack, base d'événements, réglages, langues, distribution. |
| `docs/DEVELOPMENT.md` | Outils, commandes, débogage (version PC, tests, Android, panneau de débogage, logs). |
| `docs/EVENTS_DATA.md` | Format de la base d'événements, natures, types, règles d'appariement et du coup de vieux du jour. |
| `docs/UI_DESIGN_SPEC.md` | Identité visuelle, palette, polices, navigation, langues, accessibilité. |
| `docs/UISpec/*.template.md` | Une spécification et une maquette ASCII par écran : `Onboarding`, `Today`, `EventAges` (« Quel âge ? »), `Equivalences`, `Settings`, `AndroidWidget`, `WindowsTray`. |
| `docs/maquettes/` | Maquettes visuelles (HTML et PNG) des écrans Android et Windows. En cas d'écart, les specs `UISpec/` font foi. |
| `docs/ROADMAP.md` | Jalons à faire et terminés. |
| `docs/guide/<langue>/` | Guide d'utilisation en cinq langues (`en`, `fr`, `de`, `it`, `es`), une page courte par sujet, comme LMU-DeltaRacing (décision du 17/11/2026). |

Les points marqués **À valider** et les « Questions ouvertes » de ces documents ne sont pas tranchés : poser la question avant de coder un comportement qui en dépend. Une décision prise est datée dans le document (« décision du JJ/MM/AAAA »).

## Fonctionnalités prévues

- **Aujourd'hui** (écran d'ouverture) : le coup de vieux du jour, c'est-à-dire les anniversaires d'événements du jour, chiffres ronds en premier (10, 15, 20, 25 ans…), avec l'âge qu'avait l'utilisateur. À défaut, l'anniversaire le plus proche des jours suivants.
- **Quel âge ?** (deuxième écran, décision du 17/11/2026) : un événement choisi et l'âge de chaque personne à sa date, côte à côte, négatif avant une naissance (« −14 ans »), décision du 15/11/2026 qui remplace « Mon équivalent ». Ton âge seul par défaut, et jusqu'à 3 âges à ajouter, chacun avec sa couleur, partagés avec Équivalences et oubliés à la fermeture (décision du 17/11/2026).
- **Équivalences** : à partir de l'âge de l'utilisateur et de chaque âge ajouté, plus jeune ou plus âgé (15 ans au départ, décision du 15/11/2026), les événements de **même type** décalés de l'écart d'âge (Friends ↔ Happy Days), une carte par événement de l'utilisateur avec l'équivalent de chaque âge côte à côte (décision du 17/11/2026). Pas de phrase générée : les événements, leurs dates et l'âge de chacun.
- **Réglages** : mois et année de naissance de l'utilisateur (jamais le jour, décision du 13/11/2026) et région, gardés sur l'appareil. Les autres âges ne se règlent ni dans Réglages ni au premier lancement, et ne sont pas enregistrés (décisions du 15/11/2026 et du 17/11/2026). À l'écran, aucune personne n'a de nom : un pictogramme de personne de couleur devant chaque âge (décision du 15/11/2026). Styles d'affichage au choix dans Réglages › Affichage : **Polaroïd** par défaut (âges écrits à la main, seul style de la V1, décision du 13/11/2026), **VHS**, toujours sombre (jalon 14, décision du 17/11/2026), et **Éphéméride** (jalon 15) ; maquettes dans `docs/maquettes/`.
- **Widget Android** (mis à jour chaque jour) ; sur Windows, **icône de barre des tâches** et **notification** le matin.

## Données

- Base d'événements intégrée à l'application, sans compte. Aucune donnée personnelle ne quitte l'appareil. Seule connexion : la recherche d'une nouvelle version sur GitHub, une fois par jour au plus, désactivable (décision du 16/11/2026).
- Chaque événement a une date **au jour près** et un **type** précis (sitcom culte, star du foot, mort d'une star de la musique, console…), qui sert à apparier les équivalences.
- Les événements sont rangés à part du code, dans `assets/events/<famille>/<type>/<origine>[/<sous-catégorie>].json` (décision du 16/11/2026, `docs/EVENTS_DATA.md`) : un fichier par groupe, qui sert aussi au filtre de Réglages. Le type vient du dossier.
- Une date doit être vérifiée avant d'être ajoutée : ne jamais inventer une date ou un événement.

## Distribution

- La **CI** (`.github/workflows/ci.yml`) lance les tests, la compilation des deux applications et les benchmarks sur chaque PR prête et sur `main` (décision du 14/11/2026). Les **releases** viennent **à la fin** (jalon 13) : le workflow « Release » (`.github/workflows/release.yml`), lancé à la main, compile l'**APK** Android signée et l'**installeur Windows** Inno Setup (`TimeFlies-X.Y.Z-Setup.exe`, sans WiX, comme LMU-DeltaRacing, décision du 16/11/2026) et les publie dans les Releases.
- L'APK est signée avec une clé stable, gardée dans les secrets du dépôt, jamais dans le code.

## Règles Git & PR

- **Jamais de commit direct sur `main`** : branche dédiée (`feature/…`, `fix/…`), puis PR.
- **PR créées en brouillon** (`gh pr create --draft`) : la CI ignore les brouillons. Les passer en « Ready for review » (`gh pr ready`) quand elles sont terminées, ce qui lance la CI, et attendre qu'elle soit verte avant d'annoncer la PR prête.
- Toute PR qui modifie le chargement des données (`data/events`) ou le calcul d'une page committe un rapport de benchmark à jour dans `docs/benchmarks/` (`./gradlew :benchmarks:benchmark`, rapport dans `src/benchmarks/build/reports/benchmarks/`).
- Toute PR touchant le code, la configuration ou les fonctionnalités met à jour `CHANGELOG.md` sous `## [Unreleased]`, catégories Keep a Changelog : `Added`, `Changed`, `Fixed`, `Performance`, `Security`. Entrées en français, concises.
- **Guide d'utilisation multilingue** (décision du 17/11/2026, comme LMU-DeltaRacing) : `README.md` est une courte page d'accueil avec une section par langue ; le guide est dans `docs/guide/<langue>/` (`en`, `fr`, `de`, `it`, `es`), une page courte par sujet, avec les mêmes noms de fichiers dans chaque langue. Toute PR qui change ce que voit l'utilisateur met à jour la page concernée **dans les cinq langues** (le français sert de référence). Chaque page commence par le sélecteur de langue vers la même page ; chaque guide cite les libellés de l'écran traduits dans sa langue (l'interface sera traduite). `UserGuideTest` (module `ui`) vérifie les pages, le sélecteur et les liens. Les autres documents de `docs/` restent en français.

## Méthode de travail

**Zéro invention.** En cas d'ambiguïté sur une règle, un comportement ou un choix d'architecture, s'arrêter et poser la question avant de coder.
