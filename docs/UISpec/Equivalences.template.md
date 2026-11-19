## 3. ⇄ Équivalences (`EquivalencesScreen`)

> Spécification de conception (13/11/2026), avant le code. Règles d'appariement dans [`EVENTS_DATA.md`](../EVENTS_DATA.md) ; les points marqués **À valider** ne sont pas tranchés. Décisions du 17/11/2026 (jalon 16) : jusqu'à trois âges ajoutés, côte à côte, et l'écran passe en troisième position, après « Quel âge ? ».

### Description
Le principe de l'épisode : pour une autre personne de l'âge choisi, quels événements ont la même place dans sa vie que tels autres dans la vie de l'utilisateur. **Pas de phrase générée** (décision du 13/11/2026) : chaque carte montre les événements, leurs dates et l'âge de chacun.

**Dans les deux sens** (décision du 15/11/2026) : un âge ajouté peut être plus jeune (comparer avec son enfant) ou plus âgé (comparer avec ses parents). **Jusqu'à trois âges à la fois** (décision du 17/11/2026) : chaque carte met côte à côte l'événement de l'utilisateur et l'équivalent de chaque âge ajouté.

### Spécifications fonctionnelles

#### Les personnes : un pictogramme de couleur, sans nom (décisions du 15/11/2026 et du 17/11/2026)
* Aucun nom à l'écran, ni « jeune » ni « vieux » ni « toi » : chaque personne est représentée par un **pictogramme de personne** (silhouette) placé devant son âge, chacune avec **sa couleur** : l'utilisateur, puis les trois âges qu'on peut ajouter, quatre couleurs par style (`UI_DESIGN_SPEC.md` § 2). On reconnaît qui est qui à la couleur et à l'ordre, toujours celui du bandeau.
* Le pictogramme est dessiné comme les autres icônes de l'application, pas un emoji (un emoji ne se colore pas et ne s'affiche pas pareil sur Android et Windows). Les couleurs sont prises dans la palette et vérifiées par `ContrastTest`.
* Lecture d'écran : le pictogramme de l'utilisateur se lit « Toi », les autres « Personne 2 », « Personne 3 », « Personne 4 », pour ne pas dépendre de la couleur seule.

#### Bandeau des âges (en haut, le même que sur « Quel âge ? »)
* « 👤 35 ans », puis les âges ajoutés (« 👤 15 ans  👤 66 ans »), puis le bouton **« + Ajouter un âge »** tant qu'il y en a moins de trois. **Au lancement, seul l'âge de l'utilisateur** est là, avec l'indication « Ajoute jusqu'à 3 âges pour comparer, chacun avec sa couleur. »
* « + » ouvre le curseur d'âge, de **1 à 100 ans**, **15 ans** au départ (décision du 15/11/2026), plus jeune ou plus âgé que l'utilisateur ; l'âge s'ajoute après les autres sur « OK ».
* Toucher un âge ajouté rouvre son curseur pour le changer ; le bouton **« Retirer »** l'enlève, les suivants prennent sa place (et sa couleur).
* Sous les âges, l'**écart** avec chacun, précédé de son pictogramme : « 👤 20 ans d'écart » (en grand quand il n'y a qu'un âge ajouté).
* Les âges ajoutés **ne sont pas enregistrés** (décision du 17/11/2026, qui remplace l'âge de l'autre personne enregistré avec « Modifier ») : partagés entre « Quel âge ? » et Équivalences, ils sont oubliés à la fermeture de l'application. Ils ne se règlent ni dans Réglages ni au premier lancement.
* **Petit écart** (décision du 15/11/2026) : un écart de moins de 5 ans, dans un sens ou dans l'autre, est permis. Une fenêtre prévient alors : « Attention : avec un écart aussi petit, les équivalences ne sont pas forcément représentatives. » (bouton « OK »). Elle s'affiche quand un âge choisi donne un tel écart, et à l'ouverture de l'écran si un écart est déjà sous 5 ans (une fois par lancement de l'application).
* **Âge de l'utilisateur à la volée** (décision du 13/11/2026) : toucher son âge ouvre un curseur de 1 à 100 ans ; la liste se recalcule aussitôt. Tant qu'il est actif, le bandeau porte la mention « Âges temporaires » et un bouton **« Revenir à mes réglages »**, qui rend l'âge calculé depuis la naissance (les âges ajoutés restent). Il marche aussi sans mois et année de naissance enregistrés : « Choisir des âges » part alors de 35 ans (l'âge de l'exemple des maquettes).
* Un âge donné au curseur correspond à une naissance le même jour de l'année que celle de l'utilisateur (aujourd'hui moins cet âge sans naissance de l'utilisateur, décision du 19/11/2026, voir `Settings.template.md`, « Calcul des âges et de l'écart »).

#### Ordre des colonnes (décision du 17/11/2026, qui remplace le bouton « Inverser » du 13/11/2026)
* Première colonne pour l'utilisateur, puis une colonne par âge ajouté, dans l'ordre du bandeau. Tri par les événements de l'utilisateur.

#### Filtres (décision du 13/11/2026)
* Bouton **« Thèmes »** qui ouvre une liste de **cases à cocher**, une par famille (Séries & télé, Cinéma, Musique, Sport, Jeux vidéo & tech, Politique & histoire), toutes cochées par défaut, avec « Tout cocher » / « Tout décocher ».
* Le résumé du filtre s'affiche sous le bandeau (« 4 thèmes sur 6 »).
* Le choix est **mémorisé durablement**, comme un réglage, et **partagé avec « Quel âge ? »**, qui a le même bouton (décision du 18/11/2026).

#### Liste des cartes
* **Sans âge ajouté** : à la place de la liste, « Ajoute un âge avec « + » pour voir tes équivalences. »
* Une carte par **événement de l'utilisateur** qui a un équivalent pour **au moins un** des âges ajoutés ; les colonnes sont côte à côte, dans l'ordre du bandeau. Une personne sans équivalent pour cet événement a son pictogramme suivi de « — ».
* Chaque colonne : titre de l'événement, nature, date (« 15 juillet 2018 »), âge au moment de l'événement, précédé du pictogramme de la personne (« 👤 6 ans », « 👤 moins d'un an » ou, avant sa naissance, « 👤 −2 ans » comme sur « Quel âge ? », décision du 15/11/2026). Avec deux colonnes, une flèche ⇄ les sépare ; à partir de trois, les colonnes sont plus compactes : titre plus petit, sans la nature, date courte (« 15/07/2018 »).
* En haut de la carte : pastille et nom du type en clair (« Victoire des Bleus en Coupe du monde »).
* **Tous les événements** de la base sont pris en compte, y compris ceux d'avant la naissance des personnes (décision du 13/11/2026).
* **Tri** : de l'événement de l'utilisateur le plus récent au plus ancien.
* Un équivalent dont la date cible tombe dans le futur n'est pas montré ; une carte sans aucun équivalent n'apparaît pas.
* **Liste vide** (filtre trop restrictif) : « Aucune équivalence pour ces thèmes. Coche d'autres thèmes ou essaie un autre âge. »
* **Sans mois et année de naissance** : à la place de la liste, « Indique ton mois et ton année de naissance pour voir tes équivalences. » avec le bouton **« Ouvrir les réglages »** (décision du 13/11/2026), et le bouton secondaire « Choisir des âges ».
* Toucher une carte ouvre **« Quel âge ? »** sur l'événement de l'utilisateur (décision du 15/11/2026).
* Lecture d'écran, une phrase par carte : « Mort d'une icône de la musique. Toi : Barbara, Mort, 24 novembre 1997, 6 ans. Personne 2 : Johnny Hallyday, Mort, 5 décembre 2017, 6 ans. Personne 3 : pas d'équivalent. »

### Wireframe ASCII (téléphone, trois âges ajoutés)
```text
┌────────────────────────────────────────┐
│ Équivalences                           │
├────────────────────────────────────────┤
│  👤 35 ans  👤 15 ans                   │
│  👤 66 ans  👤 8 ans                    │
│  👤 20 ans d'écart                      │
│  👤 30 ans d'écart                      │
│  👤 27 ans d'écart                      │
│  [ Thèmes ]  6 thèmes sur 6            │
│                                        │
│  ┌──────────────────────────────────┐  │
│  │ ● Mort d'une icône de la musique │  │
│  │ Barbara    Johnny    👤 —  Une    │  │
│  │            Hallyday        star   │  │
│  │ 24/11/1997 05/12/2017      15/10/ │  │
│  │                            2024   │  │
│  │ 👤 6 ans   👤 6 ans        👤 5 ans│  │
│  └──────────────────────────────────┘  │
├────────────────────────────────────────┤
│  📅  🔍  ⇄ Équivalences  ⚙              │
└────────────────────────────────────────┘

Liste des thèmes (feuille ouverte par « Thèmes »)
┌────────────────────────────────────────┐
│  THÈMES                                │
│  ☑ ● Séries & télé                     │
│  ☑ ● Cinéma                            │
│  ☑ ● Musique                           │
│  ☑ ● Sport                             │
│  ☑ ● Jeux vidéo & tech                 │
│  ☑ ● Politique & histoire              │
│  Tout cocher · Tout décocher           │
└────────────────────────────────────────┘
```

Sur Windows, avec un seul âge ajouté, les cartes s'affichent sur deux colonnes au-dessus de 840 dp de large ; avec deux ou trois, une seule, pour laisser la place aux colonnes de chaque carte.

Chaque 👤 est le pictogramme de couleur d'une personne, dans l'ordre du bandeau : l'utilisateur, puis les âges ajoutés.

Exemple pour une naissance en mai 1991 et des âges ajoutés de 15, 66 et 8 ans, le 17 novembre 2026. Les événements illustrent la mise en page ; leurs dates et leurs types seront vérifiés lors de la saisie de la base.
