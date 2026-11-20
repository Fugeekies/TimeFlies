# Changelog - TimeFlies

Toutes les modifications notables apportées au projet **TimeFlies** sont documentées dans ce fichier.

Le format est basé sur [Keep a Changelog](https://keepachangelog.com/fr/1.0.0/),
et ce projet adhère au [Semantic Versioning](https://semver.org/lang/fr/).

---

## [Unreleased]

### Added

- Événements personnels : Réglages › Mes événements pour ajouter, modifier ou supprimer ses propres dates (titre, date au jour près). Leurs anniversaires arrivent sur Aujourd'hui, le widget Android et la notification Windows, et ils ouvrent la liste de « Quel âge ? » ; pas d'équivalence. Dessin au trait et couleur à eux. **Exporter** et **Importer** un fichier `timeflies-events.json` par le sélecteur de fichiers du système pour changer de téléphone ou de PC, sans doublon ni effacement ; rien n'est envoyé sur Internet.
- Page de présentation en anglais sur [fugeekies.github.io/TimeFlies](https://fugeekies.github.io/TimeFlies/), publiée par le workflow « Pages », avec les captures des vrais écrans (`./gradlew :desktopApp:siteScreenshots`) et une image d'aperçu pour les réseaux sociaux.

- « Quel âge ? » : le bouton **Thèmes** d'Équivalences, au-dessus de la recherche. La liste et la recherche n'affichent que les thèmes cochés, et c'est le même choix, gardé, pour les deux écrans.
- Base d'événements passée de 993 à 1 362 événements : Séries 264, Cinéma 252, Musique 301 (morts d'icônes du monde entier, albums pop et rap, tubes), Sport 162 (titres du Grand Chelem, victoires au Tour de France), Jeux vidéo & tech 176, Politique & histoire 207 (élections américaines depuis 1848, grands événements). Chaque date est lue dans l'article de Wikipédia et recoupée avec Wikidata ; titres dans les cinq langues quand les articles existent.
- Base d'événements passée de 192 à 993 événements, au moins 150 par thème, chacun daté au jour près d'après Wikipédia et recoupé avec Wikidata. Nouveaux thèmes : Séries & télé (166 : sitcoms, séries dramatiques et françaises, téléréalité, émissions jeunesse, dessins animés), Jeux vidéo & tech (161 : consoles, jeux phénomènes, réseaux sociaux, formats de musique) et Politique & histoire (172 : présidentielles françaises et américaines, acteurs devenus politiciens, grands événements, familles royales). Cinéma (173) et Musique (171) complétés. Titres dans les cinq langues.
- Sport (150) : trois nouveaux types, chacun avec son dessin au trait : titre mondial des autres nations (football, football féminin, rugby), titre du Grand Chelem au tennis et victoire au Tour de France, le premier et le dernier de chacun ; records du monde actuels de 14 disciplines et Ballons d'or féminins.
- Réglages › Événements : noms de 38 nouveaux pays d'origine dans les cinq langues.
- Titres des événements dans la langue de l'interface : titres des films tels qu'ils sont sortis dans chaque pays (d'après les liens entre les Wikipédias), titres du sport traduits ; les chansons et les artistes gardent leur nom. Sans titre dans une langue, le titre français s'affiche. Un titre dans une langue inconnue fait rejeter l'événement.
- Polaroïd : un dessin au trait pour chacun des 32 types dans la photo (canapé pour les sitcoms, micro pour les divas, urne pour les présidentielles…), au lieu du dessin commun du thème.
- Interface en anglais, allemand, italien et espagnol, en plus du français : choix dans Réglages › Affichage › Langue (la langue du système par défaut, le français si elle n'est pas proposée), appliqué aussitôt à l'application, au widget, à l'icône et à la notification Windows. Dates dans l'ordre de chaque langue. Les titres des événements restent en français pour l'instant.
- Guide d'utilisation en cinq langues (anglais, français, allemand, italien, espagnol) dans `docs/guide/`, comme celui de LMU-DeltaRacing, et README avec une section par langue ; `UserGuideTest` vérifie les pages, le sélecteur de langue et les liens.
- « Quel âge ? » et Équivalences : jusqu'à 3 âges à ajouter à côté du tien avec « + », chacun avec sa couleur de pictogramme ; toucher un âge le change ou le retire. Les âges ajoutés sont partagés par les deux écrans et oubliés à la fermeture de l'application.
- Style Éphéméride, comme sur la maquette : Aujourd'hui en page de calendrier à arracher (anneaux de reliure, ligne de perforation, grand chiffre rouge en Big Shoulders Display), la feuille de la veille arrachée vers le haut une fois par jour, cartes à bordure fine ; clair ou sombre selon le système. Troisième choix de Réglages › Affichage et du widget Android.
- Style VHS, comme sur la maquette : Aujourd'hui en cassette (« ● REC », compteur de bande, bobines, étiquette inclinée à lignes de balayage), âges en Bungee, titres de section « » BIENTÔT… », couleurs saturées sur fond sombre. Le VHS est toujours sombre.
- Réglages › Affichage : choix du style, Polaroïd ou VHS, avec un aperçu de chaque feuille ; il s'applique aussitôt à toute l'application et au widget Android.
- Filtre des événements : Réglages › Événements, un arbre famille › type › origine › sous-catégorie à cocher (exemple : garder le rap français sans le rap US) ; ce qui est décoché n'apparaît plus nulle part (Aujourd'hui, widget, notification Windows, Équivalences, « Quel âge ? »).
- Premier lancement : « Ce qui t'intéresse », une pastille par thème et par origine pour écarter d'emblée un thème ou un pays.
- Nouvelles versions : quand une version plus récente est publiée sur GitHub, un bandeau sur Aujourd'hui l'annonce, avec « Télécharger » et « Plus tard ». GitHub est interrogé une fois par jour au plus, sans aucune donnée personnelle ; la case « Chercher les nouvelles versions au démarrage » de Réglages › À propos le désactive.
- Windows : une seule instance de TimeFlies ; l'ouvrir de nouveau ramène la fenêtre de celle qui tourne déjà au lieu d'en lancer une seconde.

### Changed

- Notification Windows au lancement de TimeFlies (au démarrage du PC ou à la main), une fois par jour, au lieu de chaque matin à une heure choisie : plus de réglage d'heure ni de vérification toutes les minutes, l'application ne fait plus rien en attendant dans la barre des tâches. L'infobulle se met à jour à minuit et quand les réglages changent. `settings.json` passe en version 9 ; le choix d'activer la notification est gardé.
- Revue du code : les cas d'usage des écrans passent en un seul lot (`AppUseCases`) au lieu de quinze paramètres de `App()` ; les petits composables communs (titre d'écran, message, panneau) sont dans `ui/components` au lieu d'être recopiés d'écran en écran ; une seule fabrique DataStore pour `settings.json` et `personal-events.json` ; une seule aide `recoverFailures` remplace les `try`/`catch` recopiés dans les ViewModels ; imports triés et inutiles retirés. Aucun changement visible.
- Sport sans répétition : le premier et le dernier Ballon d'or d'un même joueur seulement (Messi 2009 et 2023, Cristiano Ronaldo 2008 et 2017), et seulement le record du monde actuel de chaque discipline (Beamon, Lavillenie, Kipchoge et le premier record de Duplantis retirés).
- Polaroïd : retour au cadre blanc cassé de la maquette pour tous les événements, sans couleur ni chiffre de décennie ; la photo garde la couleur du thème et le dessin du type. Widget Android aussi.
- Premier lancement revu : titre « Ça ne nous rajeunit pas », texte qui explique l'application, une ligne claire par thème (contenu et nombre d'événements, case à cocher) ; plus de choix par pays sur cet écran, il reste dans Réglages › Événements.
- Installeur Windows en anglais seulement ; l'application démarre dans la langue de Windows quand elle la propose.
- Réglages : `settings.json` passe en version 8 (`language`).
- « Quel âge ? » passe en deuxième position dans la navigation, devant Équivalences.
- « Quel âge ? » : les âges de chaque ligne sont côte à côte sous le titre, dans l'ordre du bandeau ; au lancement, seul ton âge s'affiche.
- Équivalences : une carte par événement de ta vie, avec l'équivalent de chaque âge ajouté côte à côte (« — » s'il n'y en a pas) ; le bouton « Inverser » et « Modifier » disparaissent.
- Réglages : `settings.json` passe en version 7, sans l'âge de l'autre personne ni le sens de lecture d'Équivalences (un ancien fichier se lit toujours).
- Android : la permission Internet, pour la seule recherche de nouvelles versions.
- `settings.json` passe en version 4 (objet `releaseCheck`), puis en version 5 (`excludedEventGroups`), puis en version 6 (`style`).
- Base d'événements sortie du code : `assets/events/<famille>/<type>/<origine>[/<sous-catégorie>].json`, 52 fichiers pour les 192 événements (origine : pays de l'artiste, du joueur ou du studio ; sous-catégorie : genre des morts d'icônes, discipline des records). Le type vient du dossier.

### Performance

- Recherche de « Quel âge ? » : les titres sont mis en minuscules et sans accents une seule fois, et non plus à chaque lettre tapée ; 20 recherches sur la vraie base en 5,5 ms au lieu de 20,5 ms (`docs/benchmarks/performance-2026-11-20-revue.md`). Les listes d'Équivalences et de « Quel âge ? » demandent directement les événements d'au moins un an au lieu de tout lire puis filtrer.
- APK Android réduite par R8 : 2,7 Mo au lieu de 11 Mo.
- Widget Android : recalculé seulement quand ce qu'il affiche change (naissance, région, premier lancement, style), plus à chaque réglage d'Équivalences.

### Fixed

- « Quel âge ? » et Équivalences : un âge ajouté naît le même jour de l'année que toi, l'écart est donc la différence des âges affichés. Né en février 1995 (31 ans), un âge « 32 » affichait « Moins d'un an d'écart » au lieu de « 1 an d'écart ».
- Premier lancement : quand l'écran revenait dans la même session (« Revoir le premier lancement » du panneau de débogage), « C'est parti » et « Plus tard » restaient désactivés.
- Les événements se chargent hors du fil de l'interface : le premier écran ne se fige plus pendant la lecture de la base, qui grossira jusqu'à 5 000 événements.
- Équivalences, « Quel âge ? » et Réglages se recalculent aussi à minuit quand l'application reste ouverte, comme Aujourd'hui.
- Windows, en débogage : fermer la fenêtre quitte l'application, au lieu de la cacher en gardant le port du débogueur (F5 ne relançait plus rien).

## [0.1.0] - 2026-11-16

Première version publiée : [v0.1.0](https://github.com/Fugeekies/TimeFlies/releases/tag/v0.1.0).

### Added

- Releases : workflow « Release » lancé à la main (Majeur, Mineur, Patch), qui publie l'APK Android signée et l'installeur Windows `TimeFlies-X.Y.Z-Setup.exe` (Inno Setup, sans WiX, installation sans droits administrateur), avec les notes des PR fusionnées. Essai possible sans publier.
- Le numéro de version s'affiche dans Réglages › À propos des versions publiées ; l'APK prend un `versionCode` toujours croissant pour s'installer par-dessus la précédente.
- README : section Installation (APK, installeur Windows, avertissement SmartScreen, mise à jour par-dessus).

- Widget Android : le coup de vieux du jour sur un petit Polaroïd (âge écrit à la main, cadre de la couleur de la décennie), en 2 × 2, 4 × 2 et 4 × 3 avec les deux anniversaires suivants ; bouton « › » pour passer aux autres anniversaires du jour ; le toucher ouvre Aujourd'hui. Clair ou sombre selon le système.
- Widget Android : mis à jour juste après minuit, au démarrage du téléphone, au changement d'heure ou de fuseau, et quand les réglages changent.
- Réglages (Android) : panneau « Écran d'accueil » avec le bouton « Ajouter le widget », ou la marche à suivre si le lanceur ne le permet pas.
- Panneau de débogage (Android) : mettre à jour le widget tout de suite.

- Windows : icône dans la zone de notification, avec le coup de vieux du jour en infobulle, clic gauche pour ouvrir la fenêtre et menu « Ouvrir TimeFlies », « Coup de vieux du jour », « Réglages », « Quitter ». Fermer la fenêtre la garde dans la barre des tâches (expliqué par une notification la première fois).
- Windows : notification du coup de vieux chaque matin (9:00 par défaut), une seule fois par jour, envoyée au premier réveil quand le PC était éteint ou en veille ; la toucher ouvre Aujourd'hui.
- Windows : lancement au démarrage de Windows, fenêtre cachée (clé `Run` de l'utilisateur, sans droits administrateur).
- Réglages : panneau « Windows » (lancement au démarrage, notification du matin et son heure au quart d'heure, garder dans la barre des tâches) ; `settings.json` passe en version 3.
- Panneau de débogage (Windows) : envoyer tout de suite la notification du matin.
- Données : palier 1 du Sport, 38 événements vérifiés (Ballons d'or et matchs de légende, victoires des Bleus en Coupe du monde, Jeux olympiques en France, records).
- Données : palier 1 de la Musique, 78 événements vérifiés (morts d'icônes, albums pop et rap, tubes de divas, de boys bands et de l'été).
- Version sombre du style Polaroïd, « chambre noire » (brun nuit, crème, cuivre clair) : l'application suit le thème du système ; sur Android, la fenêtre prend aussi le fond sombre avant la première image.
- La photo du jour se développe à l'ouverture d'Aujourd'hui (la couleur monte sur le blanc, puis l'écriture), une fois par jour, et pas quand le système réduit les animations.
- Test de contraste AA des couleurs, en clair et en sombre (`ContrastTest`).
- Panneau de débogage : thème clair, sombre ou du système, pour essayer les deux palettes (pas enregistré).
- Icône « le sablier ailé » : icône adaptative Android avec sa version monochrome (icônes à thème), icône Windows pour l'installeur, la fenêtre et la barre des tâches ; dessin maître et fichiers tirés dans `assets/icon/`.
- Écran « Quel âge ? », à la place de Mon équivalent : un événement cherché dans les titres (dès 2 caractères, sans accents ni casse) ou dans la liste par famille, avec sur chaque ligne l'âge des deux personnes à sa date (« 👤 6 ans », « 👤 −14 ans »), négatif avant une naissance, sans avoir à toucher la ligne. Même bandeau des âges qu'Équivalences, âges à la volée partagés entre les deux écrans.
- Toucher une carte d'Aujourd'hui ou d'Équivalences ouvre « Quel âge ? » sur cet événement.
- Benchmarks : page « Quel âge ? » (liste par famille, recherche), environ 1 ms pour 5 000 événements.
- Écran Équivalences : les paires d'événements de même type qui ont la même place dans la vie des deux personnes (à 2 ans près), de la plus récente à la plus ancienne, sur deux colonnes au-delà de 840 dp. Les deux personnes n'ont pas de nom : une silhouette de couleur devant chaque âge. Bouton « Modifier » (âge de l'autre personne, enregistré), bouton Inverser et thèmes à cocher (mémorisés), âges à la volée non enregistrés avec « Revenir à mes réglages », « Choisir des âges » sans mois et année de naissance, avertissement sous 5 ans d'écart.
- Benchmarks : calcul de la page Équivalences (autre personne plus jeune ou plus âgée), environ 7 ms pour 5 000 événements.

- Panneau de débogage, dans les versions de débogage seulement (Android de débogage, `-Dtimeflies.debug=true` sur PC) : fausse date du jour, réglages forcés (naissance, région, revoir le premier lancement, valeurs par défaut), statistiques de la base.
- Logs Kermit : Logcat sur Android, console et un fichier par jour dans `%APPDATA%\TimeFlies\logs` sur PC, sans donnée personnelle ; les plantages y sont écrits.

- Écran de premier lancement : mois et année de naissance, boutons « C'est parti » et « Plus tard » ; il ne revient plus ensuite.
- Mode réduit sans mois et année de naissance : Équivalences et Mon équivalent invitent à les indiquer, avec un bouton « Ouvrir les réglages ».
- Écran Réglages : mois et année de naissance (lien « Effacer »), région (France en V1), à propos (version, nombre d'événements, liens GitHub et licences). Chaque réglage est enregistré dès qu'il change.
- Réglages gardés sur l'appareil avec DataStore : `settings.json` dans le dossier privé de l'application sur Android, dans `%APPDATA%\TimeFlies` sur PC ; fichier illisible remplacé par les valeurs par défaut.
- Aujourd'hui affiche l'âge de l'utilisateur à la date de chaque événement (« Tu avais 20 ans. », « Tu avais moins d'un an. », « C'était l'année de ta naissance. », « C'était 3 ans avant ta naissance. ») et se recalcule quand les réglages changent.
- Feuille de route : jalon « Traductions et régions » (événements et choix de la région pour d'autres pays).
- CI GitHub Actions sur chaque PR prête et sur `main` : tests, compilation des applications PC et Android, benchmarks avec rapport dans le résumé du run.
- Benchmarks de chargement des données et des pages (`src/benchmarks`), sur les fichiers réels et sur des jeux synthétiques jusqu'à 10 000 événements ; premier rapport dans `docs/benchmarks/`.
- Écran Aujourd'hui, style Polaroïd (cadre de la couleur de la décennie avec son chiffre écrit à la main, dessin du type dans la photo) : coup de vieux du jour (chiffres ronds d'abord, 29 février fêté le 28, repli sur les 7 jours suivants), « Aussi aujourd'hui », « Bientôt… », navigation adaptative à quatre destinations, polices Caveat et Atkinson Hyperlegible embarquées, recalcul au changement de jour.
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

### Changed

- Avant la naissance, l'âge devient négatif comme sur « Quel âge ? » : « Tu avais −3 ans. » sur Aujourd'hui au lieu de « C'était 3 ans avant ta naissance. », et « 👤 −2 ans » sur Équivalences au lieu de « 2 ans avant sa naissance ». Il se compte en années révolues (quelques mois avant la naissance : « −1 an »), donc le même nombre sur les trois écrans.
- `domain` dépend aussi de `kotlinx-coroutines-core`, pour le `Flow` des réglages (`docs/ARCHITECTURE.md`).
- L'autre personne des équivalences peut être plus jeune ou plus âgée que l'utilisateur (1 à 100 ans) ; un écart de moins de 5 ans n'est plus refusé. `settings.json` passe en version 2 : `youngAge` devient `otherAge` (un ancien fichier se lit toujours), avec le sens et les thèmes de l'écran Équivalences.

### Fixed

- Contrastes : le tampon de la décennie sur le cadre d'avant 1960, l'étiquette de la photo violette (Séries & télé) et le grand chiffre sur la photo ambre (Cinéma) passent l'AA ; « années » du tampon passe de 15 à 20 sp.
- Les boîtes de dialogue prenaient le lavande par défaut de Material au lieu du blanc cassé du Polaroïd.
- À 200 % de taille de texte, le grand chiffre de la photo ne déborde plus et le bouton « C'est parti » n'est plus coupé.
