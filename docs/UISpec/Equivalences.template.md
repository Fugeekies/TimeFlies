## 2. ⇄ Équivalences (`EquivalencesScreen`)

> Spécification de conception (13/11/2026), avant le code. Règles d'appariement dans [`EVENTS_DATA.md`](../EVENTS_DATA.md) ; les points marqués **À valider** ne sont pas tranchés.

### Description
Le principe de l'épisode : pour une autre personne de l'âge choisi, quels événements ont la même place dans sa vie que tels autres dans la vie de l'utilisateur. **Pas de phrase générée** (décision du 13/11/2026) : chaque carte montre les deux événements, leurs dates et l'âge de chacun.

**Dans les deux sens** (décision du 15/11/2026) : l'autre personne peut être plus jeune (comparer avec son enfant) ou plus âgée (comparer avec ses parents).

### Spécifications fonctionnelles

#### Les deux personnes : un pictogramme de couleur, sans nom (décision du 15/11/2026)
* Aucun nom à l'écran, ni « jeune » ni « vieux » ni « toi » : chaque personne est représentée par un **pictogramme de personne** (silhouette) placé devant son âge, chacune avec **sa couleur**. On reconnaît qui est qui à la couleur.
* Le pictogramme est dessiné comme les autres icônes de l'application, pas un emoji (un emoji ne se colore pas et ne s'affiche pas pareil sur Android et Windows). Les deux couleurs sont prises dans la palette, distinctes des pastilles de famille, et vérifiées à l'œil avec le style Polaroïd.
* Lecture d'écran : le pictogramme de l'utilisateur se lit « Toi », l'autre « L'autre personne », pour ne pas dépendre de la couleur seule.

#### Bandeau des générations (en haut)
* « 👤 35 ans · 👤 15 ans » (pictogramme de l'utilisateur, puis celui de l'autre personne) et, en grand, « **20 ans d'écart** ».
* Bouton « Modifier » : ouvre une feuille avec le curseur de l'âge de l'autre personne : de **1 à 100 ans**, **15 ans** par défaut, plus jeune ou plus âgée que l'utilisateur (décision du 15/11/2026). Le changement est enregistré et la liste se recalcule aussitôt. C'est le seul endroit où se règle cet âge : il n'est ni dans Réglages ni au premier lancement (décision du 15/11/2026). Le mois et l'année de naissance se changent dans Réglages.
* **Petit écart** (décision du 15/11/2026, qui remplace l'interdiction d'un écart de moins de 5 ans) : un écart de moins de 5 ans, dans un sens ou dans l'autre, est permis. Une fenêtre prévient alors : « Attention : avec un écart aussi petit, les équivalences ne sont pas forcément représentatives. » (bouton « OK »). Elle s'affiche quand un âge choisi donne un tel écart, et à l'ouverture de l'écran si l'écart est déjà sous 5 ans (une fois par lancement de l'application).
* **Âges à la volée** (décision du 13/11/2026) : pour comparer entre amis, toucher un des deux âges du bandeau ouvre un curseur de 1 à 100 ans ; la liste se recalcule aussitôt. Le même avertissement s'applique sous 5 ans d'écart.
  * Ces âges ne sont **pas enregistrés** : Mon équivalent utilise les mêmes, mais tout revient aux réglages à la fermeture de l'application.
  * Tant que des âges à la volée sont actifs, le bandeau porte la mention « Âges temporaires » et un bouton **« Revenir à mes réglages »**.
  * Un âge donné à la volée correspond à une naissance « aujourd'hui moins cet âge », au jour près, comme pour l'autre personne (voir `Settings.template.md`, « Calcul des âges et de l'écart »).
  * Ils marchent aussi sans mois et année de naissance enregistrés : c'est alors le seul moyen de voir des équivalences.

#### Sens de lecture (décision du 13/11/2026)
* Bouton **« Inverser »** (⇄) à côté du bandeau :
  * **par défaut** : colonne de gauche pour l'autre personne, tri par ses événements ;
  * **inversé** : colonnes échangées, tri par les événements de l'utilisateur.
* En tête de chaque colonne, le pictogramme de la personne et son âge actuel.
* Le sens choisi est mémorisé comme un réglage.

#### Filtres (décision du 13/11/2026)
* Bouton **« Thèmes »** qui ouvre une liste de **cases à cocher**, une par famille (Séries & télé, Cinéma, Musique, Sport, Jeux vidéo & tech, Politique & histoire), toutes cochées par défaut, avec « Tout cocher » / « Tout décocher ».
* Le résumé du filtre s'affiche sous le bandeau (« 4 thèmes sur 6 »).
* Le choix est **mémorisé durablement**, comme un réglage.

#### Liste des paires
* Une carte par paire, en deux colonnes séparées par une flèche ⇄.
* Chaque côté : titre de l'événement, nature, date (« 15 juillet 2018 »), âge au moment de l'événement, précédé du pictogramme de la personne (« 👤 6 ans », « 👤 moins d'un an » ou « 👤 2 ans avant sa naissance »).
* En haut de la carte : pastille et nom du type en clair (« Victoire des Bleus en Coupe du monde »).
* **Tous les événements** de la base sont pris en compte, y compris ceux d'avant la naissance des deux personnes (décision du 13/11/2026).
* **Tri** : de l'événement le plus récent au plus ancien (côté de la colonne de gauche).
* Un événement qui n'a pas de paire dans la tolérance n'apparaît pas, ni celui dont la date cible tombe dans le futur.
* **Liste vide** (filtre trop restrictif) : « Aucune équivalence pour ces thèmes. Coche d'autres thèmes ou essaie un autre âge. »
* **Sans mois et année de naissance** : à la place de la liste, « Indique ton mois et ton année de naissance pour voir tes équivalences. » avec le bouton **« Ouvrir les réglages »** (décision du 13/11/2026), et le bouton secondaire « Choisir des âges » qui ouvre les âges à la volée.
* Toucher une carte ouvre **Mon équivalent** sur l'événement de la colonne de gauche.

### Wireframe ASCII (téléphone)
```text
┌────────────────────────────────────────┐
│ Équivalences                           │
├────────────────────────────────────────┤
│  👤 35 ans · 👤 15 ans                  │
│  20 ANS D'ÉCART     [Modifier] [ ⇄ ]   │
│  [ Thèmes ▾ ]  6 thèmes sur 6          │
│                                        │
│  ┌──────────────────────────────────┐  │
│  │ ● Mort d'une icône de la musique │  │
│  │ 👤 15 ANS       ⇄   👤 35 ANS    │  │
│  │ Johnny Hallyday     Barbara      │  │
│  │ Mort                Mort         │  │
│  │ 5 déc. 2017         24 nov. 1997 │  │
│  │ 👤 6 ans            👤 6 ans     │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │ ● Victoire des Bleus au Mondial  │  │
│  │ 👤 15 ANS       ⇄   👤 35 ANS    │  │
│  │ Mondial 2018        Mondial 1998 │  │
│  │ Victoire            Victoire     │  │
│  │ 15 juil. 2018       12 juil. 1998│  │
│  │ 👤 6 ans            👤 7 ans     │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │ ● Console Nintendo               │  │
│  │ Nintendo Switch     Nintendo 64  │  │
│  │ Sortie              Sortie       │  │
│  │ 3 mars 2017         1er mars 1997│  │
│  │ 👤 5 ans            👤 5 ans     │  │
│  └──────────────────────────────────┘  │
├────────────────────────────────────────┤
│  📅  ⇄ Équivalences  🔍  ⚙              │
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

Sur Windows, les cartes s'affichent sur deux colonnes au-dessus de 840 dp de large.

Chaque 👤 est le pictogramme de couleur d'une des deux personnes : en tête de colonne, celui de l'autre personne à gauche et celui de l'utilisateur à droite (sens par défaut).

Exemple calculé pour une naissance en mai 1991 et une autre personne de 15 ans, le 2 novembre 2026. Les paires illustrent la mise en page ; leurs dates et leurs types seront vérifiés lors de la saisie de la base.
