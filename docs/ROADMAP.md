# Feuille de route

Les jalons à faire sont en haut, dans l'ordre où ils seront menés. Les jalons terminés sont en bas, du plus ancien au plus récent, avec leur date de fin et la PR qui les a livrés (rangement repris de LMU-DeltaRacing, ordre décidé le 16/11/2026).

Découpage décidé le 14/11/2026 :
- **des jalons courts**, un sujet chacun, testés en **débogage sur PC** (et sur Android quand le jalon le demande) ;
- **les releases à la fin**, une fois qu'une bonne version tourne en débogage sur PC ; la **CI des tests et des benchmarks**, elle, est avancée après le jalon 3 (décision du 14/11/2026) ;
- **les données par thème**, menées en parallèle du code, par paliers jusqu'à environ 5 000 événements.

La **V1** = les jalons de code 1 à 10 et le palier 1 de chaque thème de données.

## À faire : le code

### Jalon — Restes des jalons terminés
Points laissés ouverts par des jalons livrés, regroupés ici pour ne pas garder ces jalons ouverts.
- [ ] (CI) Rendre le job « Tests et compilation » obligatoire avant fusion dans la règle de protection de `main`.
- [ ] (CI) Benchmarks de rendu des écrans (Compose), si les calculs ne suffisent plus à juger la fluidité.
- [ ] (Jalon 1) Vérifier Compose Hot Reload sur la cible PC : il demande le JetBrains Runtime 21, fourni par Android Studio ou IntelliJ ([`DEVELOPMENT.md`](DEVELOPMENT.md)).
- [ ] (Jalon 3) Reste de la spec d'Aujourd'hui : actualisation par glisser vers le bas ou `F5`.
- [ ] (Jalon 10) Sur l'application installée : valeur `Run` écrite au chemin de l'exécutable, démarrage avec Windows fenêtre cachée, nom de l'application dans l'en-tête de la notification.
- [ ] (Jalon 11) Widget vu en 4 × 3 sur l'émulateur (16/11/2026) ; reste à voir le bouton « suivant », la taille 2 × 2, le thème sombre et la mise à jour après minuit.
- [ ] (Jalon 12) Image d'aperçu du dépôt GitHub (1280 × 640, à partir de `assets/icon/`) : à téléverser à la main dans les réglages du dépôt, GitHub n'ayant pas d'API pour ça.
- [ ] (Jalon 13) `v0.1.0` installée sur un téléphone et sur un PC : icône dans l'assistant d'installation et dans « Applications installées », mise à jour par-dessus en gardant les réglages, désinstallation.
- [ ] (Jalon 14) Widget en style VHS à voir sur l'émulateur, et les deux styles sur l'application installée.
- [ ] (Jalon 15) Widget en style Éphéméride à voir sur l'émulateur (clair et sombre), et l'arrachage de la feuille à juger à l'écran.
- [ ] (Jalon 16) Les quatre âges à voir sur un téléphone : cartes d'Équivalences à quatre colonnes et âges de « Quel âge ? » aux grandes tailles de police.
- [ ] (Jalon 17) Les cinq langues à voir sur un téléphone (widget compris) et sur l'application installée (menu de l'icône, notification) ; textes allemands et espagnols longs aux grandes tailles de police. Faire relire les traductions par des personnes dont c'est la langue.

## À faire : les données, par thème

Chaque thème est un jalon à part, mené en parallèle du code dès que le jalon 2 (lecture des données) est terminé. Une PR par lot d'une cinquantaine d'événements, pour que les dates restent faciles à relire. Chaque événement est réel, daté au jour près et sourcé ([`EVENTS_DATA.md`](EVENTS_DATA.md)).

Paliers communs à tous les thèmes (répartition indicative, à ajuster selon ce qui existe) :

| Palier | Par thème | Total environ | But |
|---|---|---|---|
| 1 | ~70 | ~400 | V1 : presque un anniversaire par jour, et des équivalences pour chaque type |
| 2 | ~300 | ~1 800 | plusieurs anniversaires par jour, des équivalences pour la plupart des écarts d'âge |
| 3 | ~850 | ~5 000 | objectif final |

Chaque type a son **dessin au trait** pour la photo du Polaroïd (`TypeDrawings.kt`, docs/UI_DESIGN_SPEC.md § 1) : les 32 types sont dessinés (décision du 17/11/2026), et tout nouveau type arrive avec le sien.

### Données — Séries & télé
Sitcoms et séries cultes, téléréalité, émissions jeunesse, dessins animés à la télé.
- [ ] Palier 1 · [ ] Palier 2 · [ ] Palier 3

### Données — Cinéma
Blockbusters, films d'animation, comédies françaises, sagas.
- [x] Palier 1 : 76 films (17 blockbusters, 20 films d'animation, 19 comédies françaises, 20 épisodes de sagas), sortie nationale en France recoupée avec Wikidata (14/11/2026, PR [#6](https://github.com/Fugeekies/TimeFlies/pull/6), méthode dans [`EVENTS_DATA.md`](EVENTS_DATA.md)) · [ ] Palier 2 · [ ] Palier 3

### Données — Musique
Morts d'icônes de la musique, divas, boys bands, albums de rap et de pop, tubes de l'été.
- [x] Palier 1 : 78 événements (25 morts d'icônes, 12 albums pop, 12 albums de rap, 10 tubes de divas, 10 tubes de boys bands, 9 tubes de l'été), date de sortie originale pour les albums et les singles (15/11/2026, PR [#18](https://github.com/Fugeekies/TimeFlies/pull/18), méthode dans [`EVENTS_DATA.md`](EVENTS_DATA.md)) · [ ] Palier 2 · [ ] Palier 3

### Données — Sport
Stars du foot, victoires des Bleus, Jeux olympiques, records.
- [x] Palier 1 : 38 événements (23 sommets de stars du foot : Ballons d'or et matchs, 2 victoires des Bleus en Coupe du monde, 5 Jeux olympiques en France, 8 records et exploits) (15/11/2026, PR [#19](https://github.com/Fugeekies/TimeFlies/pull/19), méthode dans [`EVENTS_DATA.md`](EVENTS_DATA.md)) · [ ] Palier 2 · [ ] Palier 3

### Données — Jeux vidéo & tech
Consoles, jeux phénomènes, téléphones, réseaux sociaux, formats de musique.
- [ ] Palier 1 · [ ] Palier 2 · [ ] Palier 3

### Données — Politique & histoire
Élections, acteurs devenus politiciens, grands événements, familles royales.
- [ ] Palier 1 · [ ] Palier 2 · [ ] Palier 3

### Jalon — Cap des 5 000 événements
- [ ] Mesurer le chargement et la mémoire sur un vrai téléphone, puis décider : rester en JSON ou passer à une base SQLite précompilée ([`ARCHITECTURE.md`](ARCHITECTURE.md), « Base d'événements »).
- [ ] Décider comment tenir le rythme de saisie au-delà du palier 2 : à la main, ou import depuis Wikidata relu par PR.

## Après la V1

### Jalon — Traductions et régions
- [x] Interface en anglais, en plus du français de référence : fait au jalon 17, avec l'allemand, l'italien et l'espagnol.
- [ ] Titres des événements traduits quand ils diffèrent (anglais, allemand, italien, espagnol), dans une PR à part du jalon 17.
- [ ] Événements pour d'autres régions du monde : dates régionales (`regionalDates`) sourcées pour les pays visés, et événements propres à ces pays.
- [ ] Choix de la région dans Réglages (liste des pays dans la langue de l'interface, région du système par défaut) : en V1, France seulement (décision du 15/11/2026).

### Liste d'idées à prioriser
- Notification du matin sur Android quand un anniversaire rond tombe.
- Partager un coup de vieux en image.
- Ajouter ses propres événements (souvenirs personnels).
- Page de présentation sur GitHub Pages, comme LMU-DeltaRacing. Icône et favicon à reprendre d'`assets/icon/` (`timeflies-icon.svg` en favicon SVG, `timeflies-512.png`, `timeflies.ico`), image d'aperçu des réseaux sociaux comme celle du dépôt.

## Terminés

### Jalon 13 — Releases — 16/11/2026 · PR [#22](https://github.com/Fugeekies/TimeFlies/pull/22)
Après une bonne version en débogage sur PC (releases à la fin, décision du 14/11/2026).
- [x] Clé de signature Android générée (16/11/2026, RSA 4096, valable jusqu'en 2056) et stockée dans les secrets du dépôt ; une sauvegarde gardée hors du dépôt ([`DEVELOPMENT.md`](DEVELOPMENT.md), « Releases »).
- [x] `release.yml` (décision du 16/11/2026 : comme LMU-DeltaRacing, sans WiX) : lancé à la main (Majeur, Mineur, Patch), version tirée du dernier tag, APK signée et installeur **Inno Setup** (`scripts/installer.iss`, sur le dossier `createDistributable`), tag et release avec les notes des PR fusionnées ; « Publier » décoché pour un essai. Version passée à l'APK (`versionName`, `versionCode`) et à l'application Windows (Réglages › À propos).
- [x] Premier essai sans publier, puis première version publiée : [v0.1.0](https://github.com/Fugeekies/TimeFlies/releases/tag/v0.1.0) (16/11/2026, APK de 11 Mo, installeur de 51 Mo).
- [x] README : section Installation (APK, installeur Windows, avertissement SmartScreen).

### Jalon 11 — Widget Android — 16/11/2026 · PR [#21](https://github.com/Fugeekies/TimeFlies/pull/21)
- [x] Widget en trois tailles avec le bouton « suivant », mis à jour juste après minuit ([`AndroidWidget.template.md`](UISpec/AndroidWidget.template.md)). `domain` : règle `WidgetSheets` (feuilles du jour, lignes compactes, retour au premier), tests. `ui` : `WidgetTexts`, `WidgetPalette`. `androidApp` : Glance 1.2.0 (`TimeFliesWidget`, âge manuscrit dessiné en image avec Caveat), WorkManager 2.12.0 (`MidnightWorker`), `WidgetEventsReceiver` (démarrage, heure, fuseau), recalcul quand les réglages changent.
- [x] Panneau « Écran d'accueil » des Réglages.
- [x] Panneau de débogage : forcer tout de suite la mise à jour du widget.

### Jalon 10 — Barre des tâches et notification Windows — 16/11/2026 · PR [#20](https://github.com/Fugeekies/TimeFlies/pull/20)
- [x] Icône, infobulle et menu ; notification du matin ; fenêtre fermée qui reste dans la barre des tâches ; lancement au démarrage ([`WindowsTray.template.md`](UISpec/WindowsTray.template.md)). `domain` : `WindowsSettings`, règle `MorningNotification` (heure atteinte, une fois par jour, au premier réveil), cas d'usage `SendMorningNotification`, tests. `data` : `settings.json` en version 3 (objet `windows`), tests. `desktopApp` : `WindowsTray` (sur le `TrayIcon` d'AWT pour le clic gauche simple), `TrayUpdater` (infobulle, vérification chaque minute), `WindowsStartup` (`reg.exe`, argument `--background`).
- [x] Panneau Windows des Réglages.
- [x] Panneau de débogage : envoyer tout de suite la notification du matin.

### Jalon 9 — Finitions du style Polaroïd — 15/11/2026 · PR [#16](https://github.com/Fugeekies/TimeFlies/pull/16)
- [x] Version sombre « chambre noire » (décision du 15/11/2026), le thème suit le système, fenêtre Android jour/nuit sans éclair blanc. Contrastes AA vérifiés par `ContrastTest` (jetons, silhouettes, cadres, tampon des décennies, encre de la photo choisie selon la couleur de la famille) ; écriture manuscrite jamais sous 20 sp.
- [x] Animation de la photo qui se développe (1,6 s, une fois par jour), coupée quand le système réduit les animations (Android : échelle des animations ; Windows : `MinAnimate`).
- [x] Texte agrandi à 200 % : le grand chiffre de la photo garde sa taille, le bouton du premier lancement grandit avec son texte.
- [x] Panneau de débogage : forcer le thème clair ou sombre, pour la session.

### Jalon 12 — Nom et icône définitifs — 15/11/2026 · PR [#15](https://github.com/Fugeekies/TimeFlies/pull/15)
- [x] Nom affiché sous l'icône et dans les menus : **TimeFlies**, comme le dépôt (décision du 15/11/2026).
- [x] Icône « le sablier ailé » (choisie le 15/11/2026 parmi douze pistes) : dessin maître `assets/icon/timeflies-icon.svg`, icône adaptative Android avec sa couche monochrome (icônes à thème d'Android 13+), `.ico` Windows en 9 tailles pour l'installeur, fenêtre et barre des tâches PC ([`UI_DESIGN_SPEC.md`](UI_DESIGN_SPEC.md), « Icône »).

### Jalon 8 — Quel âge ? — 15/11/2026 · PR [#14](https://github.com/Fugeekies/TimeFlies/pull/14)
- [x] Décision du 15/11/2026 : « Mon équivalent » (chercher l'équivalent d'un événement) est remplacé par « Quel âge ? » : pour un événement choisi, l'âge des deux personnes à sa date, négatif avant une naissance (« −14 ans »).
- [x] `domain` : cas d'usage `GetEventAges` (âges en années révolues, négatifs avant la naissance) et `SearchEvents` (recherche dès 2 caractères, liste par famille, sans les événements de moins d'un an), tests.
- [x] Écran « Quel âge ? » ([`EventAges.template.md`](UISpec/EventAges.template.md)) : bandeau des âges partagé avec Équivalences (même `GenerationsViewModel`, âges à la volée communs), recherche ou liste par famille avec les deux âges directement sur chaque ligne (« 👤 6 ans » / « 👤 −14 ans », rien à toucher, décision du 15/11/2026), mode réduit avec la liste sans les âges. Une carte touchée sur Aujourd'hui ou Équivalences y mène, avec son titre dans la recherche.
- [x] Benchmark de la page ([`benchmarks/performance-2026-11-15.md`](benchmarks/performance-2026-11-15.md) : environ 1 ms pour la liste par famille, 5 000 événements).

### Jalon 7 — Équivalences — 15/11/2026 · PR [#12](https://github.com/Fugeekies/TimeFlies/pull/12)
- [x] Décision du 15/11/2026 : l'autre personne peut être plus jeune ou plus âgée (1 à 100 ans), un écart de moins de 5 ans est permis avec un avertissement, et aucune des deux personnes n'a de nom à l'écran : un pictogramme de personne de couleur devant chaque âge.
- [x] `domain` : `Generations` dans les deux sens (âge de l'autre personne de 1 à 100 ans, `youngAge` renommé `otherAge`), règles `Equivalences` (même type, ±2 ans, date cible `E.date + (n_B − n_A)` au jour près, plus proche puis plus importante, jamais avec lui-même, événements d'un an au moins, date cible future écartée), entité `Equivalence`, cas d'usage `GetEquivalences`, réglages du sens et des thèmes, tests.
- [x] `data` : `settings.json` en version 2 (`otherAge`, `equivalencesReversed`, `equivalenceFamilies`), lecture d'un fichier de version 1 par `youngAge`, tests.
- [x] Écran Équivalences ([`Equivalences.template.md`](UISpec/Equivalences.template.md)) : silhouettes de couleur, thèmes à cocher, bouton Inverser, âges à la volée, bouton « Modifier » pour l'âge de l'autre personne, seul endroit où il se règle (ni Réglages ni premier lancement, décision du 15/11/2026), avertissement sous 5 ans d'écart, « Choisir des âges » en mode réduit, deux colonnes au-delà de 840 dp, noms des types en clair.
- [x] Benchmark de la page Équivalences ([`benchmarks/performance-2026-11-15.md`](benchmarks/performance-2026-11-15.md) : environ 7 ms pour 5 000 événements).
- [x] Reporté au jalon 8 et fait : toucher une carte ouvre « Quel âge ? », et les âges à la volée partagés avec cet écran.

### Jalon 6 — Panneau de débogage — 15/11/2026 · PR [#11](https://github.com/Fugeekies/TimeFlies/pull/11)
Pour tester tous les cas sans attendre le bon jour.
- [x] Destination « Débogage » en version de débogage seulement ([`DEVELOPMENT.md`](DEVELOPMENT.md)) : fausse date du jour (`AdjustableClock`, raccourcis 29 février, jour sans anniversaire…), réglages forcés (naissance, région, revoir le premier lancement, valeurs par défaut), statistiques de la base. Port `DebugTools` dans `domain`, `DataDebugTools` dans `data`, tests.
- [x] Logs Kermit (console, fichier par jour sur PC, Logcat sur Android), sans donnée personnelle, exceptions non rattrapées écrites dans les logs.

### Jalon 5 — Premier lancement — 15/11/2026 · PR [#10](https://github.com/Fugeekies/TimeFlies/pull/10)
- [x] Écran de premier lancement, avec « Plus tard » ([`Onboarding.template.md`](UISpec/Onboarding.template.md)) : mois et année de naissance seulement (décision du 15/11/2026), « C'est parti » désactivé tant que le mois ou l'année manque, colonne de 480 dp sur une fenêtre large. `domain` : champ `onboardingDone` des réglages, cas d'usage `CompleteOnboarding` (une seule écriture), tests.
- [x] Mode réduit sans mois et année de naissance : Aujourd'hui sans « Tu avais N ans » (jalon 4), Équivalences et Mon équivalent (devenu « Quel âge ? » au jalon 8) affichent le message et le bouton « Ouvrir les réglages ».

### Jalon 4 — Menu Réglages — 15/11/2026 · PR [#9](https://github.com/Fugeekies/TimeFlies/pull/9), [#17](https://github.com/Fugeekies/TimeFlies/pull/17)
Les réglages enregistrés, et l'âge de l'utilisateur sur Aujourd'hui.
- [x] `domain` : entité `Settings`, port `SettingsRepository` (en `Flow` : `domain` dépend aussi de `kotlinx-coroutines-core`, décision du 15/11/2026), cas d'usage `UpdateSettings`, `ObserveSettings` et `CountEvents`, règles `Generations` (naissance au 1er du mois, jeune né aujourd'hui moins son âge, écart au jour près, 5 ans d'écart au moins), tests.
- [x] `data` : `DataStoreSettingsRepository` (DataStore 1.3.0 et Okio, JSON lisible avec un champ `version`, fichier illisible remplacé par les valeurs par défaut), `settings.json` dans le dossier privé de l'application sur Android et dans `%APPDATA%\TimeFlies` sur PC, tests.
- [x] Écran Réglages ([`Settings.template.md`](UISpec/Settings.template.md)) : mois et année de naissance (deux listes, lien « Effacer »), région affichée « France » sans liste (décision du 15/11/2026), à propos (version, nombre d'événements, liens GitHub, licences).
- [x] Aujourd'hui affiche « Tu avais N ans. », « Tu avais moins d'un an. », « C'était l'année de ta naissance. » ou, avant la naissance, « Tu avais −N ans. » (décisions du 15/11/2026, PR #17), et se recalcule quand les réglages changent.

### Jalon — CI des tests et benchmarks — 14/11/2026 · PR [#8](https://github.com/Fugeekies/TimeFlies/pull/8)
Avancé après le jalon 3, à la demande (décision du 14/11/2026).
- [x] `.github/workflows/ci.yml` : sur chaque PR prête (pas les brouillons) et sur `main`, `./gradlew check` (tests et validation des fichiers d'événements), compilation des applications PC et Android, rapports de tests en artefact si échec.
- [x] Module `src/benchmarks` (`./gradlew :benchmarks:benchmark`, hors de `check`) : chargement complet, requêtes par jour, par type et par titre, calcul de la page Aujourd'hui, sur les fichiers réels et sur des jeux synthétiques de 500 à 10 000 événements.
- [x] Rapport Markdown dans le résumé du run de CI et en artefact ; premier rapport dans [`benchmarks/performance-2026-11-14.md`](benchmarks/performance-2026-11-14.md) (5 000 événements : chargement complet ≈ 24 ms sur un PC de développement).

### Jalon 3 — Première interface : Aujourd'hui — 14/11/2026 · PR [#7](https://github.com/Fugeekies/TimeFlies/pull/7)
Le coup de vieux du jour à l'écran, en style Polaroïd.
- [x] `domain` : `Ages`, `Anniversaries` (chiffres ronds, puis importance, puis le plus récent ; repli sur les 7 jours suivants ; 10 anniversaires à venir au plus ; 29 février fêté le 28 les années non bissextiles), cas d'usage `GetDailyShock`, 20 tests sur dates fixes. Le port `Clock` était arrivé au jalon 2 ; `SystemClock` dans `data`.
- [x] Jetons du style Polaroïd (clair), polices embarquées Caveat et Atkinson Hyperlegible (licences SIL OFL dans `licenses/`) ([`UI_DESIGN_SPEC.md`](UI_DESIGN_SPEC.md)).
- [x] Navigation adaptative avec les quatre destinations (`NavigationSuiteScaffold` : barre en bas sur téléphone, rail sur une fenêtre large). Icônes dessinées comme sur les maquettes.
- [x] Écran Aujourd'hui ([`Today.template.md`](UISpec/Today.template.md)) : photo Polaroïd du jour (ou « Dans N jours »), « Aussi aujourd'hui », « Bientôt… » par jour, deux colonnes au-delà de 840 dp, recalcul au changement de jour, descriptions pour la lecture d'écran.
- [x] Polaroïd enrichi (décision du 14/11/2026) : cadre de la couleur de la décennie, chiffre de la décennie écrit à la main dans le coin, dessin au trait du type dans la photo (4 types du cinéma, un dessin par famille pour les autres).
- [x] Toucher une carte ouvre « Quel âge ? » (fait au jalon 8).

### Jalon 2 — Lecture des données — 14/11/2026 · PR [#6](https://github.com/Fugeekies/TimeFlies/pull/6)
Les événements sont lus, validés et interrogeables, sans encore d'écran.
- [x] `domain` : entités (`Event`, `EventKind`, `EventType`, `Family`, `Region`, `MonthDay`), ports `EventRepository` et `Clock` (avancé du jalon 3 : la validation a besoin de la date du jour).
- [x] `data` : `JsonEventRepository` (fichiers par famille, validation, index par jour de l'année et par type, dates régionales, recherche sans accents) ([`EVENTS_DATA.md`](EVENTS_DATA.md)), `CoverageReport`.
- [x] Jeu d'essai : données d'essai inventées **dans les tests seulement** (une date régionale, un 29 février, un doublon, chaque cas d'erreur) ; les vraies données sont arrivées avec le palier 1 du Cinéma, dans la même PR.
- [x] Tests : 22 tests sur la validation, les index et la recherche, plus la validation des vrais fichiers livrés (`./gradlew check`), avec le rapport de couverture.

### Jalon 1 — Création de la solution — 14/11/2026 · PR [#5](https://github.com/Fugeekies/TimeFlies/pull/5)
Une solution qui compile et s'ouvre dans l'IDE, sans aucune fonctionnalité.
- [x] Projet Kotlin Multiplatform avec les cinq modules `domain`, `data`, `ui`, `androidApp`, `desktopApp` ([`ARCHITECTURE.md`](ARCHITECTURE.md)), wrapper Gradle 9.8.2 commité.
- [x] `gradle/libs.versions.toml` avec les versions stables du moment, figées : Kotlin 2.4.21, Compose Multiplatform 1.12.2, Android Gradle Plugin 9.5.0, Compose Hot Reload 1.2.0 ; `compileSdk` 37, `targetSdk` 36, `minSdk` 26. Identifiant : `io.github.fugeekies.timeflies`.
- [x] Une fenêtre « TimeFlies » vide qui s'ouvre sur PC (`./gradlew :desktopApp:run`).
- [x] L'APK de débogage compile (`./gradlew :androidApp:assembleDebug`, plateforme Android 37 installée).
- [x] L'application sur Android en débogage : vue sur l'émulateur Android 36 le 16/11/2026.
- [x] Découpage en cinq modules confirmé : compilation complète en une vingtaine de secondes, incrémentale en quelques secondes.

### Jalon — Spécifications — 14/11/2026 · PR [#1](https://github.com/Fugeekies/TimeFlies/pull/1), [#2](https://github.com/Fugeekies/TimeFlies/pull/2), [#3](https://github.com/Fugeekies/TimeFlies/pull/3), [#4](https://github.com/Fugeekies/TimeFlies/pull/4)
Décrire l'application avant de coder.
- [x] Architecture, base d'événements, identité visuelle et spécifications d'écran (`docs/`).
- [x] Questions ouvertes tranchées (13/11/2026) ; maquettes Android et Windows (`docs/maquettes/`) ; style Polaroïd en V1.
- [x] Clean Architecture, événements en JSON jusqu'à 5 000, guide de débogage (14/11/2026).
- [x] Feuille de route : jalons courts, données par thème, releases à la fin (14/11/2026).
### Jalon — Optimisations, instance unique et nouvelles versions — 16/11/2026 · PR [#25](https://github.com/Fugeekies/TimeFlies/pull/25)
Issu de l'analyse du 16/11/2026 (corrections dans la PR [#24](https://github.com/Fugeekies/TimeFlies/pull/24)).
- [x] APK de release réduite par R8 : 2,7 Mo au lieu de 11 Mo, vérifiée sur l'émulateur (écrans, réglages, widget, tâche de minuit).
- [x] Widget recalculé seulement quand la naissance, la région ou le premier lancement changent.
- [x] Mémoire de l'application Windows mesurée (≈ 140 Mo fenêtre cachée, venant du rendu natif de Compose) : ni limite de tas ni fenêtre détruite ne font gagner assez, rien n'est changé.
- [x] Décision du 16/11/2026 : une seule instance sur Windows, un second lancement ramène la fenêtre (`SingleInstance`).
- [x] Décision du 16/11/2026 : recherche des nouvelles versions sur GitHub, une fois par jour au plus, bandeau sur Aujourd'hui, désactivable dans Réglages › À propos (`ReleaseUpdates`, `GitHubReleases`, `settings.json` en version 4).

### Jalon — Base d'événements rangée et filtre — 16/11/2026 · PR [#26](https://github.com/Fugeekies/TimeFlies/pull/26)
- [x] Décision du 16/11/2026 : base sortie du code, `assets/events/<famille>/<type>/<origine>[/<sous-catégorie>].json` ; un pays par fichier, `international` pour les très gros événements mixtes ; sous-catégories : genre des morts d'icônes, discipline des records ([`EVENTS_DATA.md`](EVENTS_DATA.md), « Fichiers et stockage »).
- [x] 192 événements classés en 52 fichiers ; le type vient du dossier ; validation du chemin (`EventGroup`).
- [x] Filtre appliqué partout : arbre de Réglages › Événements, pastilles thèmes et origines au premier lancement, `settings.json` en version 5.

### Jalon 14 — Style VHS — 17/11/2026 · PR [#27](https://github.com/Fugeekies/TimeFlies/pull/27)
Décision du 17/11/2026 : le style VHS est avancé avant l'Éphéméride, comme sur la maquette ([`maquettes/android/TodayVhs.png`](maquettes/android/TodayVhs.png)).
- [x] Jetons du style VHS, toujours sombre ([`UI_DESIGN_SPEC.md`](UI_DESIGN_SPEC.md) § 1, 2 et 5) ; polices embarquées Bungee, VT323 (SIL OFL) et Permanent Marker (Apache 2.0). `StyleTokens` porte les polices de chaque rôle (`accent`, `section`, `handwriting`), les écrans ne connaissent plus le Caveat.
- [x] Aujourd'hui : la cassette (« ● REC », compteur de bande qui défile une fois par jour, bobines) et son étiquette inclinée à lignes de balayage ; en-tête « TIMEFLIES » et « ▶ PLAY » ; cartes des listes avec l'âge en Bungee, titres de section « » BIENTÔT… » (`VhsTape`).
- [x] Autres écrans aux couleurs et polices du VHS (âges, écarts, titres), sans changer leur mise en page ; sur Android, icônes des barres système claires en VHS même quand le système est clair.
- [x] `domain` : `DisplayStyle`, `Settings.displayStyle`, `UpdateSettings.setDisplayStyle`. `data` : `settings.json` en version 6 (`style`, valeur inconnue lue comme Polaroïd). Tests.
- [x] Choix du style dans Réglages › Affichage (Polaroïd par défaut), avec un aperçu de chaque feuille dans ses propres couleurs (`StylePicker`).
- [x] Widget Android aux couleurs du style choisi (étiquette blanche et nombre en Bungee magenta), recalculé quand le style change.
- [x] `ContrastTest` étendu aux jetons et aux ornements du VHS.

### Jalon 15 — Style Éphéméride — 17/11/2026 · PR [#28](https://github.com/Fugeekies/TimeFlies/pull/28)
La feuille du jour en page d'éphéméride, comme sur la maquette ([`maquettes/android/Today.png`](maquettes/android/Today.png)), troisième style après le Polaroïd et le VHS.
- [x] Jetons du style Éphéméride, clair et sombre selon le thème du système ([`UI_DESIGN_SPEC.md`](UI_DESIGN_SPEC.md) § 2) ; rouge clair assombri d'un cran (`#D3243B`) pour lire à 4,5:1 sur le fond ; police embarquée Big Shoulders Display (SIL OFL).
- [x] Aujourd'hui : la page (anneaux de reliure, ligne de perforation, jour, grand chiffre rouge et « ans », titre, légende, puce « Chiffre rond », famille) ; cartes des listes à bordure fine avec l'âge en Big Shoulders Display, titres de section en petites capitales (`EphemerisSheet`).
- [x] Animation : la feuille de la veille s'arrache vers le haut depuis la perforation, une fois par jour, comme le Polaroïd et le VHS.
- [x] Autres écrans aux couleurs et polices de l'Éphéméride, sans changer leur mise en page.
- [x] `domain` : `DisplayStyle.EPHEMERIS` (`"ephemeride"` dans `settings.json`, toujours en version 6). Tests.
- [x] Éphéméride ajoutée au choix de Réglages › Affichage avec son aperçu ; les trois choix passent l'un sous l'autre sur un téléphone étroit.
- [x] Widget Android en petite page blanche (sombre dans le thème sombre), nombre en Big Shoulders Display rouge.
- [x] `ContrastTest` étendu aux jetons de l'Éphéméride, clairs et sombres, et au nombre du widget.

### Jalon 16 — Jusqu'à quatre âges — 17/11/2026 · PR [#29](https://github.com/Fugeekies/TimeFlies/pull/29)
Décisions du 17/11/2026 : « Quel âge ? » passe en deuxième position, devant Équivalences ; ton âge seul par défaut, et jusqu'à 3 autres âges à ajouter, chacun avec sa couleur, partagés par « Quel âge ? » et Équivalences et oubliés à la fermeture de l'application.
- [x] Navigation : Aujourd'hui, Quel âge ?, Équivalences, Réglages.
- [x] Bandeau des âges : ton âge, puis « + Ajouter un âge » (3 au plus, 15 ans au départ) ; toucher un âge le change ou le retire (« Retirer ») ; l'écart avec chacun. Quatre couleurs de pictogramme par style, vérifiées par `ContrastTest`.
- [x] « Quel âge ? » : sur chaque ligne, les âges côte à côte sous le titre, dans l'ordre du bandeau.
- [x] Équivalences : une carte par événement de l'utilisateur, avec l'équivalent de chaque âge ajouté côte à côte (« — » s'il n'y en a pas), colonnes compactes à partir de trois ; tri par tes événements, « Inverser » et « Modifier » disparaissent.
- [x] `domain` : `GetEquivalences(userBirth, otherBirths, …)`, `Equivalence(user, others)`, `GetEventAges(event, births)`, `Generations.MAX_OTHER_PEOPLE`. `data` : `settings.json` en version 7, sans `otherAge` ni `equivalencesReversed`. Tests.
- [x] Benchmark d'Équivalences avec trois âges ([rapport du 17/11/2026](benchmarks/performance-2026-11-17.md) : 0,8 ms sur la base réelle, 16 ms à 5 000 événements).

### Jalon 17 — Interface traduite — 17/11/2026 · PR [#31](https://github.com/Fugeekies/TimeFlies/pull/31)
Décision du 17/11/2026 : l'interface est traduite dans les langues du guide d'utilisation (anglais, allemand, italien, espagnol, en plus du français de référence) ; les titres des événements viendront dans une PR à part.
- [x] Textes de l'interface dans `values-en`, `values-de`, `values-it` et `values-es` (tous les textes, panneau de débogage compris), avec les libellés du guide ; description du widget Android traduite aussi. `TranslationTest` : mêmes textes, mêmes paramètres, listes de même longueur.
- [x] Dates dans l'ordre de chaque langue (« 2 November 2011 », « 2. November 2011 », « 2 de noviembre de 2011 », « 02.11.2011 ») : `DatePatterns` lu dans les ressources, `DateTextsTest`. Tampon de décennie du Polaroïd traduisible (« the 70s », « die 70er »).
- [x] `domain` : `AppLanguage` (choix, sinon langue du système si proposée, sinon français), `Settings.language`, `UpdateSettings.setLanguage`. `data` : `settings.json` en version 8 (`language`). Tests.
- [x] Réglages › Affichage › Langue : chaque langue écrite dans sa langue ; appliquée aussitôt à l'application (écran affiché et âges ajoutés gardés), au widget, à l'icône et à la notification Windows (`AppLocale`).
- [x] Guide d'utilisation : la ligne Langue dans les cinq langues.
