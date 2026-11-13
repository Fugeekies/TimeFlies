## 4. ⚙️ Réglages (`SettingsScreen`)

> Spécification de conception (13/11/2026), avant le code. Les points marqués **À valider** ne sont pas tranchés.

### Description
Les réglages qui pilotent toute l'application (mois et année de naissance, âge du jeune, région), le style d'affichage, les réglages propres à la plateforme et les informations sur l'application. Chaque réglage est **enregistré dès qu'il change**, sans bouton « Enregistrer ».

### Spécifications fonctionnelles

#### Panneau « Toi » (eyebrow « GÉNÉRATIONS »)
* **Ton mois et ton année de naissance** : deux listes, comme au premier lancement (de 1920 à l'année en cours moins 5), **sans le jour** (décision du 13/11/2026). Libellé de la valeur : « Mai 1991 · 35 ans ». Non renseigné : « Non renseigné » et le texte « Sans cette information, TimeFlies affiche les anniversaires sans ton âge et ne peut pas calculer d'équivalences. »
* Lien **« Effacer »** : retire le mois et l'année enregistrés (retour au mode réduit).
* **Âge du jeune** : curseur de 5 à 60 ans, **15 ans** par défaut. Au moins 5 ans de moins que l'utilisateur : sinon message « Le jeune doit avoir au moins 5 ans de moins que toi. » et la valeur n'est pas enregistrée.
* Résumé : « 20 ans d'écart ».

#### Calcul des âges et de l'écart (décision du 13/11/2026)
* **Naissance de l'utilisateur** : le **1er jour** du mois choisi. L'âge affiché peut donc avoir un an d'avance seulement pendant le mois de son anniversaire, avant le jour exact.
* **Naissance du jeune** : **aujourd'hui moins son âge**, au jour près (un jeune « de 15 ans » est né il y a exactement 15 ans).
* **Écart** : différence entre les deux dates de naissance, au jour près. Il change légèrement chaque jour, ce qui garde cohérents les âges affichés des deux côtés d'une équivalence.

#### Panneau « Affichage » (eyebrow « AFFICHAGE »)
* **Style** : trois choix avec un aperçu miniature de la feuille du jour (décision du 13/11/2026, voir `UI_DESIGN_SPEC.md` § 1) :
  * **Éphéméride** (par défaut) ;
  * **VHS** ;
  * **Polaroïd**.
  Le style s'applique aussitôt à toute l'application et au widget.
* **Région** : liste des pays, chacun écrit dans la langue de l'interface. Par défaut, la région du système (décision du 13/11/2026). Elle choisit les dates régionales des événements (voir `EVENTS_DATA.md`, « Dates par région »).
* **Langue** : liste des langues disponibles, chacune écrite dans sa langue. Au départ, Français seulement : la ligne est masquée tant qu'il n'y a qu'une langue. Par défaut, la langue du système si elle est disponible, le français sinon.

#### Panneau « Widget » (Android uniquement, eyebrow « ÉCRAN D'ACCUEIL »)
* Texte : « Ajoute le widget TimeFlies sur ton écran d'accueil pour voir ton coup de vieux chaque jour. »
* Bouton **« Ajouter le widget »** : demande au lanceur d'épingler le widget (`requestPinAppWidget`) ; masqué si le lanceur ne le permet pas, avec à la place l'explication « Appui long sur l'écran d'accueil › Widgets › TimeFlies ».

#### Panneau « Notification et démarrage » (Windows uniquement, eyebrow « WINDOWS »)
Valeurs par défaut **À valider**.
* Case **« Lancer TimeFlies au démarrage de Windows »** (proposition : cochée) : nécessaire pour l'icône de la barre des tâches et la notification. Écrit ou retire la clé `HKCU\Software\Microsoft\Windows\CurrentVersion\Run\TimeFlies` (utilisateur courant, sans droits administrateur).
* Case **« Notification du coup de vieux chaque matin »** (proposition : cochée) et **heure** (liste par quart d'heure, proposition : 9:00).
* Case **« Garder TimeFlies dans la barre des tâches quand je ferme la fenêtre »** (proposition : cochée) : fermer la fenêtre la cache au lieu de quitter l'application (voir `WindowsTray.template.md`).

#### Panneau « À propos »
* Nom et version (« TimeFlies v0.1.0 », « version de développement » pour une compilation locale).
* « Inspiré de l'épisode *Bloqués #108 - On est les vieux des jeunes*. »
* Nombre d'événements de la base (« 412 événements »).
* Liens : code source (GitHub), signaler une erreur de date (page des issues GitHub), licence GPL v3, licences des polices et bibliothèques.
* « Aucune donnée ne quitte cet appareil. »

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
│  │ Âge du jeune             15 ans  │  │
│  │ ●━━━━━━━━○─────────────────────  │  │
│  │ 20 ans d'écart                   │  │
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

### Questions ouvertes
1. Valeurs par défaut sur Windows : lancement au démarrage coché, notification à 9:00, fenêtre fermée qui reste dans la barre des tâches, d'accord ?
