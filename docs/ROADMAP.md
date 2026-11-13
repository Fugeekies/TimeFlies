# Feuille de route

Les jalons à faire sont en haut, par ordre de priorité. Les jalons terminés suivront, du plus récent au plus ancien, avec leur date de fin et la PR qui les a livrés.

## À faire

### Jalon — Spécifications initiales
Décrire l'application avant de coder : architecture, base d'événements, identité visuelle et maquette de chaque écran.
- [x] `docs/ARCHITECTURE.md`, `docs/EVENTS_DATA.md`, `docs/UI_DESIGN_SPEC.md` et les spécifications d'écran `docs/UISpec/`.
- [x] Maquettes Android et Windows (`docs/maquettes/`, 13/11/2026).
- [x] Trancher les points marqués **À valider** et les questions ouvertes (13/11/2026). Maquettes Android et Windows dans `docs/maquettes/` ; la V1 n'a que le style Polaroïd (décision du 13/11/2026).

### Jalon — Chaîne de compilation
Valider dès le départ que GitHub produit une APK et un `.msi` installables, avant d'écrire les fonctionnalités.
- [ ] Projet Kotlin Multiplatform minimal (une fenêtre « TimeFlies » sur Android et Windows), wrapper Gradle commité.
- [ ] Choisir le découpage des modules (`core` + `ui`, ou un seul module partagé), voir `docs/ARCHITECTURE.md`.
- [ ] `ci.yml` : compilation et tests à chaque PR prête (pas sur les brouillons).
- [ ] Clé de signature Android générée et stockée dans les secrets du dépôt ; une sauvegarde gardée hors du dépôt.
- [ ] `release.yml` : sur un tag `vX.Y.Z`, compile l'APK signée et le `.msi`, puis publie la release avec les deux fichiers.
- [ ] README : section Installation (APK sur Android, `.msi` sur Windows et l'avertissement « éditeur inconnu »).
- [ ] Première version de test `v0.1.0` installée sur un téléphone et sur un PC.

### Jalon — Base d'événements v1
- [x] Trancher le format, les natures et le principe des types larges (`docs/EVENTS_DATA.md`, 13/11/2026).
- [ ] Chargement et validation de `events.json` dans `core`, avec les tests de validation et le rapport de couverture.
- [ ] Saisie d'environ 400 événements vérifiés, chacun avec sa source, par lots d'une famille à la fois (une PR par lot pour faciliter la relecture des dates).

### Jalon — Moteur de calcul
- [ ] `Ages` : âge en années révolues à une date, avant/après une naissance.
- [ ] `Anniversaries` : coup de vieux du jour, ordre des chiffres ronds, repli sur les 7 jours suivants, cas du 29 février.
- [ ] `Equivalences` : appariement par type, écart d'âge et tolérance ; « Mon équivalent » dans les deux sens.
- [ ] Tests unitaires sur des dates fixes (pas sur la date du jour).

### Jalon — Écrans communs
- [ ] Finir les jetons du style Polaroïd : version sombre, contraste et taille minimale de l'écriture manuscrite (`docs/UI_DESIGN_SPEC.md` § 1 et 2).
- [ ] Style Polaroïd (seul style de la V1), polices embarquées et navigation adaptative (`docs/UI_DESIGN_SPEC.md`, maquettes dans [`maquettes/`](maquettes/README.md)). Jetons de thème séparés par style, pour accueillir l'Éphéméride sans toucher aux écrans.
- [ ] Premier lancement ([`Onboarding.template.md`](UISpec/Onboarding.template.md)).
- [ ] Aujourd'hui ([`Today.template.md`](UISpec/Today.template.md)).
- [ ] Équivalences ([`Equivalences.template.md`](UISpec/Equivalences.template.md)).
- [ ] Mon équivalent ([`MyEquivalent.template.md`](UISpec/MyEquivalent.template.md)).
- [ ] Réglages ([`Settings.template.md`](UISpec/Settings.template.md)).

### Jalon — Widget Android
- [ ] Widget du coup de vieux du jour ([`AndroidWidget.template.md`](UISpec/AndroidWidget.template.md)), mis à jour juste après minuit.

### Jalon — Barre des tâches et notification Windows
- [ ] Icône de barre des tâches, notification du matin et lancement au démarrage de Windows ([`WindowsTray.template.md`](UISpec/WindowsTray.template.md)).

### Jalon — Style Éphéméride (après la V1)
La feuille du jour en page d'éphéméride, maquettes de tous les écrans dans [`maquettes/`](maquettes/README.md).
- [ ] Jetons du style (palette claire et sombre déjà définies, `docs/UI_DESIGN_SPEC.md` § 2).
- [ ] Ajouter le choix du style dans Réglages (Polaroïd par défaut) et l'appliquer au widget.

### Jalon — Nom et icône définitifs
- [ ] Choisir le nom affiché sous l'icône et dans les menus (le dépôt reste `TimeFlies`).
- [ ] Dessiner l'icône (Android adaptative, `.ico` Windows) et l'image d'aperçu du dépôt.

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

Aucun pour l'instant.
