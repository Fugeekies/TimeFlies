## 4. ⚙️ Réglages (`SettingsScreen`)

> Spécification de conception (13/11/2026), avant le code.

### Description
Les réglages qui pilotent toute l'application (mois et année de naissance, région), le style d'affichage, les réglages propres à la plateforme et les informations sur l'application. Chaque réglage est **enregistré dès qu'il change**, sans bouton « Enregistrer ».

### Spécifications fonctionnelles

#### Panneau « Toi » (eyebrow « GÉNÉRATIONS »)
* **Ton mois et ton année de naissance** : deux listes, comme au premier lancement (de 1920 à l'année en cours moins 5), **sans le jour** (décision du 13/11/2026). Libellé de la valeur : « Mai 1991 · 35 ans ». Non renseigné : « Non renseigné » et le texte « Sans cette information, TimeFlies affiche les anniversaires sans ton âge et ne peut pas calculer d'équivalences. »
* Lien **« Effacer »** : retire le mois et l'année enregistrés (retour au mode réduit).
* **Pas d'autres âges dans Réglages** (décision du 15/11/2026) : ils s'ajoutent là où ils servent, avec « + » dans le bandeau de « Quel âge ? » et d'Équivalences (voir `Equivalences.template.md`), pas au premier lancement non plus. Depuis le 17/11/2026, ils ne sont **plus enregistrés** : jusqu'à 3, oubliés à la fermeture de l'application ; au lancement, seul ton âge s'affiche.

#### Calcul des âges et de l'écart (décision du 13/11/2026)
* **Naissance de l'utilisateur** : le **1er jour** du mois choisi. L'âge affiché peut donc avoir un an d'avance seulement pendant le mois de son anniversaire, avant le jour exact.
* **Naissance d'un âge ajouté** : **aujourd'hui moins cet âge**, au jour près (une personne « de 15 ans » est née il y a exactement 15 ans). Elle peut être plus jeune ou plus âgée que l'utilisateur (décision du 15/11/2026).
* **Écart** : différence entre les deux dates de naissance, au jour près, dans un sens ou dans l'autre ; un écart de moins de 5 ans est permis avec un avertissement (voir `Equivalences.template.md`). Il change légèrement chaque jour, ce qui garde cohérents les âges affichés des deux côtés d'une équivalence.

#### Panneau « Événements » (eyebrow « ÉVÉNEMENTS », décision du 16/11/2026)
* « Décoche ce qui ne t'intéresse pas : ces événements n'apparaissent plus nulle part, ni sur Aujourd'hui, ni dans le widget, ni dans les équivalences. »
* Arbre famille › type › origine › sous-catégorie, replié au départ : chaque ligne a une case à trois états (cochée, décochée, en partie) et son nombre d'événements ; « ▸ » la déplie. Toucher une case coche ou décoche tout ce qu'elle contient. Tout est coché par défaut.
* Un groupe décoché n'est plus chargé nulle part (Aujourd'hui, widget, notification Windows, Équivalences, « Quel âge ? ») ; le nombre d'événements de « À propos » suit. Rangement des groupes : [`EVENTS_DATA.md`](../EVENTS_DATA.md), « Fichiers et stockage ».

#### Panneau « Affichage » (eyebrow « AFFICHAGE »)
* **Style** (jalon 14, décision du 17/11/2026 ; Éphéméride au jalon 15) : « Le style de toute l'application et du widget. Le VHS est toujours sombre. », puis un choix par style livré, chacun avec un aperçu miniature de sa feuille du jour dans ses propres couleurs (voir `UI_DESIGN_SPEC.md` § 1) :
  * **Polaroïd** (par défaut) ;
  * **VHS** ;
  * **Éphéméride**.
  Les trois choix sont côte à côte quand ils tiennent, l'un sous l'autre sur un téléphone étroit.
  Le style s'applique aussitôt à toute l'application et au widget, et il est gardé dans `settings.json` (champ `style`, version 6 ; version 7 depuis le jalon 16, sans l'âge de l'autre personne ni le sens de lecture d'Équivalences).
* **Région** : liste des pays, chacun écrit dans la langue de l'interface. Par défaut, la région du système (décision du 13/11/2026). Elle choisit les dates régionales des événements (voir `EVENTS_DATA.md`, « Dates par région »). **En V1, France seulement** (décision du 15/11/2026) : la ligne affiche « Région : France » sans liste ; le choix des pays arrive avec le jalon « Traductions et régions ».
* **Langue** (jalon 17, décision du 17/11/2026) : Français, English, Deutsch, Italiano, Español, chacune écrite dans sa langue ; toucher la ligne ouvre la liste. Par défaut, la langue du système si elle est disponible, le français sinon. Le choix est gardé dans `settings.json` (`language`, version 8) et s'applique aussitôt à toute l'application, au widget, à l'icône et à la notification Windows, sans changer l'écran affiché. Les titres des événements suivent la langue (PR #35, méthode dans `EVENTS_DATA.md`).

#### Panneau « Widget » (Android uniquement, eyebrow « ÉCRAN D'ACCUEIL »)
* Texte : « Ajoute le widget TimeFlies sur ton écran d'accueil pour voir ton coup de vieux chaque jour. »
* Bouton **« Ajouter le widget »** : demande au lanceur d'épingler le widget (`requestPinAppWidget`) ; masqué si le lanceur ne le permet pas, avec à la place l'explication « Appui long sur l'écran d'accueil › Widgets › TimeFlies ».

#### Panneau « Notification et démarrage » (Windows uniquement, eyebrow « WINDOWS »)
Valeurs par défaut décidées le 13/11/2026.
* Case **« Lancer TimeFlies au démarrage de Windows »** (cochée par défaut) : nécessaire pour l'icône de la barre des tâches et la notification. Écrit ou retire la clé `HKCU\Software\Microsoft\Windows\CurrentVersion\Run\TimeFlies` (utilisateur courant, sans droits administrateur).
* Case **« Notification du coup de vieux chaque matin »** (cochée par défaut) et **heure** (liste par quart d'heure, 9:00 par défaut).
* Case **« Garder TimeFlies dans la barre des tâches quand je ferme la fenêtre »** (cochée par défaut) : fermer la fenêtre la cache au lieu de quitter l'application (voir `WindowsTray.template.md`).

#### Panneau « À propos »
* Nom et version (« TimeFlies v0.1.0 », « version de développement » pour une compilation locale).
* « Inspiré de l'épisode *Bloqués #108 - On est les vieux des jeunes*. »
* Nombre d'événements de la base (« 412 événements »).
* Liens : code source (GitHub), signaler une erreur de date (page des issues GitHub), licence GPL v3, licences des polices et bibliothèques.
* Case « Chercher les nouvelles versions au démarrage » (cochée par défaut), avec « Une fois par jour au plus, TimeFlies demande à GitHub la dernière version publiée. Rien d'autre n'est envoyé. » (décision du 16/11/2026).
* « Aucune donnée personnelle ne quitte cet appareil. »

### Wireframe ASCII (téléphone, Android)
```text
┌────────────────────────────────────────┐
│ Réglages                               │
├────────────────────────────────────────┤
│  GÉNÉRATIONS                           │
│  ┌──────────────────────────────────┐  │
│  │ Ton mois et ton année de         │  │
│  │ naissance                        │  │
│  │ Mai 1991 · 35 ans             ›  │  │
│  │                         Effacer  │  │
│  └──────────────────────────────────┘  │
│                                        │
│  AFFICHAGE                             │
│  ┌──────────────────────────────────┐  │
│  │ Style                            │  │
│  │ ┌──────┐  ┌──────┐  ┌──────┐     │  │
│  │ │ ◯  ◯ │  │▶ REC │  │┌────┐│     │  │
│  │ │  15  │  │  15  │  ││ 15 ││     │  │
│  │ └──────┘  └──────┘  │└────┘│     │  │
│  │ (●)Éphém. ( )VHS    └──────┘     │  │
│  │                     ( )Polaroïd  │  │
│  │ Région             France     ›  │  │
│  └──────────────────────────────────┘  │
│                                        │
│  ÉCRAN D'ACCUEIL                       │
│  ┌──────────────────────────────────┐  │
│  │ Ajoute le widget TimeFlies sur   │  │
│  │ ton écran d'accueil pour voir    │  │
│  │ ton coup de vieux chaque jour.   │  │
│  │ [ Ajouter le widget ]            │  │
│  └──────────────────────────────────┘  │
│                                        │
│  À PROPOS                              │
│  ┌──────────────────────────────────┐  │
│  │ TimeFlies v0.1.0                 │  │
│  │ Inspiré de Bloqués #108          │  │
│  │ 412 événements                   │  │
│  │ Code source ›   Signaler une     │  │
│  │ erreur de date ›   Licences ›    │  │
│  │ Aucune donnée ne quitte cet      │  │
│  │ appareil.                        │  │
│  └──────────────────────────────────┘  │
├────────────────────────────────────────┤
│  📅  ⇄  🔍  ⚙ Réglages                  │
└────────────────────────────────────────┘
```

### Wireframe ASCII (fenêtre Windows, panneau propre à Windows)
```text
│  WINDOWS                                                          │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │ ☑ Lancer TimeFlies au démarrage de Windows                  │  │
│  │ ☑ Notification du coup de vieux chaque matin à [ 09:00 ▼ ]  │  │
│  │ ☑ Garder TimeFlies dans la barre des tâches quand je ferme  │  │
│  │   la fenêtre                                                │  │
│  └─────────────────────────────────────────────────────────────┘  │
```

Le chiffre « 412 événements » est un exemple : il vient de la base réelle.

