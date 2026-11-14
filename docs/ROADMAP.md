# Feuille de route

Les jalons à faire sont en haut, dans l'ordre où ils seront menés. Les jalons terminés suivent, du plus récent au plus ancien, avec leur date de fin et la PR qui les a livrés.

Découpage décidé le 14/11/2026 :
- **des jalons courts**, un sujet chacun, testés en **débogage sur PC** (et sur Android quand le jalon le demande) ;
- **la CI et les releases à la fin**, une fois qu'une bonne version tourne en débogage sur PC ;
- **les données par thème**, menées en parallèle du code, par paliers jusqu'à environ 5 000 événements.

La **V1** = les jalons de code 1 à 10 et le palier 1 de chaque thème de données.

## À faire : le code

### Jalon 1 — Création de la solution
Une solution qui compile et s'ouvre dans l'IDE, sans aucune fonctionnalité.
- [x] Projet Kotlin Multiplatform avec les cinq modules `domain`, `data`, `ui`, `androidApp`, `desktopApp` ([`ARCHITECTURE.md`](ARCHITECTURE.md)), wrapper Gradle 9.8.2 commité.
- [x] `gradle/libs.versions.toml` avec les versions stables du moment, figées : Kotlin 2.4.21, Compose Multiplatform 1.12.2, Android Gradle Plugin 9.5.0, Compose Hot Reload 1.2.0 ; `compileSdk` 37, `targetSdk` 36, `minSdk` 26. Identifiant : `io.github.fugeekies.timeflies`.
- [x] Une fenêtre « TimeFlies » vide qui s'ouvre sur PC (`./gradlew :desktopApp:run`).
- [x] L'APK de débogage compile (`./gradlew :androidApp:assembleDebug`, plateforme Android 37 installée).
- [ ] La fenêtre sur Android en débogage (émulateur ou téléphone).
- [ ] Vérifier Compose Hot Reload sur la cible PC : il demande le JetBrains Runtime 21, fourni par Android Studio ou IntelliJ ([`DEVELOPMENT.md`](DEVELOPMENT.md)).
- [x] Découpage en cinq modules confirmé : compilation complète en une vingtaine de secondes, incrémentale en quelques secondes.

### Jalon 2 — Lecture des données
Les événements sont lus, validés et interrogeables, sans encore d'écran.
- [ ] `domain` : entités (`Event`, `EventType`, `Family`, `Region`), port `EventRepository`.
- [ ] `data` : `JsonEventRepository` (fichiers par famille, validation, index par jour de l'année et par type, dates régionales) ([`EVENTS_DATA.md`](EVENTS_DATA.md)).
- [ ] Jeu d'essai d'une vingtaine d'événements réels et sourcés, qui couvre chaque famille, une date régionale et un 29 février.
- [ ] Tests : validation des fichiers, index, rapport de couverture (jours sans anniversaire, types trop pauvres).

### Jalon 3 — Première interface : Aujourd'hui
Le coup de vieux du jour à l'écran, en style Polaroïd.
- [ ] `domain` : port `Clock`, `Ages`, `Anniversaries` (chiffres ronds, repli sur les 7 jours suivants, 29 février), cas d'usage `GetDailyShock`, tests sur dates fixes.
- [ ] Jetons du style Polaroïd (clair d'abord), polices embarquées ([`UI_DESIGN_SPEC.md`](UI_DESIGN_SPEC.md)).
- [ ] Navigation adaptative avec les quatre destinations (les trois autres vides pour l'instant).
- [ ] Écran Aujourd'hui ([`Today.template.md`](UISpec/Today.template.md)), sans âge de l'utilisateur tant que les réglages n'existent pas.

### Jalon 4 — Menu Réglages
Les réglages enregistrés, et l'âge de l'utilisateur sur Aujourd'hui.
- [ ] `domain` : entité `Settings`, port `SettingsRepository`, cas d'usage `UpdateSettings`.
- [ ] `data` : `DataStoreSettingsRepository` (JSON, mêmes réglages sur PC et Android).
- [ ] Écran Réglages ([`Settings.template.md`](UISpec/Settings.template.md)) : mois et année de naissance, âge du jeune, région, à propos. Le panneau Windows et celui du widget viendront avec leurs jalons.
- [ ] Aujourd'hui affiche « Tu avais N ans ».

### Jalon 5 — Premier lancement
- [ ] Écran de premier lancement, avec « Plus tard » ([`Onboarding.template.md`](UISpec/Onboarding.template.md)).
- [ ] Mode réduit sans mois et année de naissance.

### Jalon 6 — Panneau de débogage
Pour tester tous les cas sans attendre le bon jour.
- [ ] Fausse date du jour, réglages forcés, statistiques de la base ([`DEVELOPMENT.md`](DEVELOPMENT.md)).
- [ ] Logs Kermit (console, fichier sur PC, Logcat sur Android), sans donnée personnelle.

### Jalon 7 — Équivalences
- [ ] `domain` : `Generations`, `Equivalences` (même type, ±2 ans), cas d'usage `GetEquivalences`, tests.
- [ ] Écran Équivalences ([`Equivalences.template.md`](UISpec/Equivalences.template.md)) : thèmes à cocher, bouton Inverser, âges à la volée.

### Jalon 8 — Mon équivalent
- [ ] Cas d'usage `FindEquivalent` (deux sens, autres candidats), recherche dans les titres.
- [ ] Écran Mon équivalent ([`MyEquivalent.template.md`](UISpec/MyEquivalent.template.md)) ; une carte touchée sur Aujourd'hui ou Équivalences y mène.

### Jalon 9 — Finitions du style Polaroïd
- [ ] Version sombre, contraste et taille minimale de l'écriture manuscrite.
- [ ] Animation de la photo qui se développe (à valider à l'écran), accessibilité (TalkBack, Narrateur, texte agrandi à 200 %).

### Jalon 10 — Barre des tâches et notification Windows
- [ ] Icône, infobulle et menu ; notification du matin ; fenêtre fermée qui reste dans la barre des tâches ; lancement au démarrage ([`WindowsTray.template.md`](UISpec/WindowsTray.template.md)).
- [ ] Panneau Windows des Réglages.

### Jalon 11 — Widget Android
- [ ] Widget en trois tailles avec le bouton « suivant », mis à jour juste après minuit ([`AndroidWidget.template.md`](UISpec/AndroidWidget.template.md)).
- [ ] Panneau « Écran d'accueil » des Réglages.

### Jalon 12 — Nom et icône définitifs
- [ ] Choisir le nom affiché sous l'icône et dans les menus (le dépôt reste `TimeFlies`).
- [ ] Dessiner l'icône (Android adaptative, `.ico` Windows) et l'image d'aperçu du dépôt.

### Jalon 13 — CI et releases (à la fin, décision du 14/11/2026)
Seulement quand une bonne version tourne en débogage sur PC.
- [ ] `ci.yml` : compilation et tests à chaque PR prête (pas sur les brouillons).
- [ ] Clé de signature Android générée et stockée dans les secrets du dépôt ; une sauvegarde gardée hors du dépôt.
- [ ] `release.yml` : sur un tag `vX.Y.Z`, APK signée et `.msi`, publiés dans la release ; vérifier WiX sur l'exécuteur Windows.
- [ ] README : section Installation (APK, `.msi`, avertissement « éditeur inconnu »).
- [ ] Première version publiée, installée sur un téléphone et sur un PC.

## À faire : les données, par thème

Chaque thème est un jalon à part, mené en parallèle du code dès que le jalon 2 (lecture des données) est terminé. Une PR par lot d'une cinquantaine d'événements, pour que les dates restent faciles à relire. Chaque événement est réel, daté au jour près et sourcé ([`EVENTS_DATA.md`](EVENTS_DATA.md)).

Paliers communs à tous les thèmes (répartition indicative, à ajuster selon ce qui existe) :

| Palier | Par thème | Total environ | But |
|---|---|---|---|
| 1 | ~70 | ~400 | V1 : presque un anniversaire par jour, et des équivalences pour chaque type |
| 2 | ~300 | ~1 800 | plusieurs anniversaires par jour, des équivalences pour la plupart des écarts d'âge |
| 3 | ~850 | ~5 000 | objectif final |

### Données — Séries & télé
Sitcoms et séries cultes, téléréalité, émissions jeunesse, dessins animés à la télé.
- [ ] Palier 1 · [ ] Palier 2 · [ ] Palier 3

### Données — Cinéma
Blockbusters, films d'animation, comédies françaises, sagas.
- [ ] Palier 1 · [ ] Palier 2 · [ ] Palier 3

### Données — Musique
Morts d'icônes de la musique, divas, boys bands, albums de rap et de pop, tubes de l'été.
- [ ] Palier 1 · [ ] Palier 2 · [ ] Palier 3

### Données — Sport
Stars du foot, victoires des Bleus, Jeux olympiques, records.
- [ ] Palier 1 · [ ] Palier 2 · [ ] Palier 3

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

### Jalon — Style Éphéméride
La feuille du jour en page d'éphéméride, maquettes de tous les écrans dans [`maquettes/`](maquettes/README.md).
- [ ] Jetons du style (palette claire et sombre déjà définies, `docs/UI_DESIGN_SPEC.md` § 2).
- [ ] Ajouter le choix du style dans Réglages (Polaroïd par défaut) et l'appliquer au widget.

### Jalon — Traductions
- [ ] Interface en anglais, en plus du français de référence.
- [ ] Titres des événements en anglais quand ils diffèrent.

### Liste d'idées à prioriser
- Style VHS (cassette, étiquette au feutre, compteur de bande), maquette dans [`maquettes/`](maquettes/README.md).
- Notification du matin sur Android quand un anniversaire rond tombe.
- Partager un coup de vieux en image.
- Ajouter ses propres événements (souvenirs personnels).
- Page de présentation sur GitHub Pages, comme LMU-DeltaRacing.
- Vérification des nouvelles versions au démarrage (une seule requête vers GitHub, désactivable).

## Terminés

### Jalon — Spécifications — 14/11/2026 · PR [#1](https://github.com/Fugeekies/TimeFlies/pull/1), [#2](https://github.com/Fugeekies/TimeFlies/pull/2), [#3](https://github.com/Fugeekies/TimeFlies/pull/3)
Décrire l'application avant de coder.
- [x] Architecture, base d'événements, identité visuelle et spécifications d'écran (`docs/`).
- [x] Questions ouvertes tranchées (13/11/2026) ; maquettes Android et Windows (`docs/maquettes/`) ; style Polaroïd en V1.
- [x] Clean Architecture, événements en JSON jusqu'à 5 000, guide de débogage (14/11/2026).
