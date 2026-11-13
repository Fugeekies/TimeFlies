## 4. ⚙️ Réglages (`SettingsScreen`)

> Spécification de conception (13/11/2026), avant le code. Les points marqués **À valider** ne sont pas tranchés.

### Description
Les deux réglages qui pilotent toute l'application (date de naissance, âge du jeune), les réglages propres à la plateforme et les informations sur l'application. Chaque réglage est **enregistré dès qu'il change**, sans bouton « Enregistrer ».

### Spécifications fonctionnelles

#### Panneau « Toi » (eyebrow « GÉNÉRATIONS »)
* **Ta date de naissance** : même sélecteur que le premier lancement (du 1er janvier 1920 à aujourd'hui moins 5 ans), avec le lien « Année seulement » (date retenue : 1er juillet, **À valider**). Libellé de la valeur : « 14 mai 1991 · 35 ans ».
* **Âge du jeune** : curseur de 5 à 60 ans, **15 ans** par défaut. Au moins 5 ans de moins que l'utilisateur : sinon message « Le jeune doit avoir au moins 5 ans de moins que toi. » et la valeur n'est pas enregistrée.
* Résumé : « 20 ans d'écart ».
* **Calcul de l'écart** : la date de naissance du jeune est **aujourd'hui moins son âge**, au jour près (un jeune « de 15 ans » est né il y a exactement 15 ans). L'écart est la différence entre les deux dates de naissance. Il change donc légèrement chaque jour, ce qui garde les âges affichés cohérents (**À valider**).

#### Panneau « Widget » (Android uniquement, eyebrow « ÉCRAN D'ACCUEIL »)
* Texte : « Ajoute le widget TimeFlies sur ton écran d'accueil pour voir ton coup de vieux chaque jour. »
* Bouton **« Ajouter le widget »** : demande au lanceur d'épingler le widget (`requestPinAppWidget`) ; masqué si le lanceur ne le permet pas, avec à la place l'explication « Appui long sur l'écran d'accueil › Widgets › TimeFlies ».

#### Panneau « Notification et démarrage » (Windows uniquement, eyebrow « WINDOWS »)
* Case **« Lancer TimeFlies au démarrage de Windows »** (cochée par défaut, **À valider**) : nécessaire pour l'icône de la barre des tâches et la notification. Écrit ou retire la clé `HKCU\Software\Microsoft\Windows\CurrentVersion\Run\TimeFlies` (utilisateur courant, sans droits administrateur).
* Case **« Notification du coup de vieux chaque matin »** (cochée par défaut) et **heure** (liste par quart d'heure, **9:00** par défaut, **À valider**).
* Case **« Garder TimeFlies dans la barre des tâches quand je ferme la fenêtre »** (cochée par défaut) : fermer la fenêtre la cache au lieu de quitter l'application (voir `WindowsTray.template.md`).

#### Panneau « Langue » (eyebrow « AFFICHAGE »)
* Liste des langues disponibles, chacune écrite dans sa langue. Au départ, Français seulement : le panneau est masqué tant qu'il n'y a qu'une langue. Par défaut, la langue du système si elle est disponible, le français sinon (**À valider** : l'anglais par défaut une fois traduit ?).

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
│  │ Ta date de naissance             │  │
│  │ 14 mai 1991 · 35 ans          ›  │  │
│  │                                  │  │
│  │ Âge du jeune             15 ans  │  │
│  │ ●━━━━━━━━○─────────────────────  │  │
│  │ 20 ans d'écart                   │  │
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
1. Calcul de l'écart à partir de l'âge du jeune au jour près : convient-il, ou préfères-tu un écart fixe en années entières (35 − 15 = 20 ans) ?
2. Valeurs par défaut sur Windows : lancement au démarrage coché et notification à 9:00, d'accord ?
