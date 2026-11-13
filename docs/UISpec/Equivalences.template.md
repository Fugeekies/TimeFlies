## 2. ⇄ Équivalences (`EquivalencesScreen`)

> Spécification de conception (13/11/2026), avant le code. Règles d'appariement dans [`EVENTS_DATA.md`](../EVENTS_DATA.md) ; les points marqués **À valider** ne sont pas tranchés.

### Description
Le principe de l'épisode : pour un jeune de l'âge choisi, quels événements ont la même place dans sa vie que tels autres dans la vie de l'utilisateur. **Pas de phrase générée** (décision du 13/11/2026) : chaque carte montre les deux événements, leurs dates et l'âge de chacun.

### Spécifications fonctionnelles

#### Bandeau des générations (en haut)
* « Toi : 35 ans · Le jeune : 15 ans » et, en grand, « **20 ans d'écart** ».
* Bouton « Modifier » : ouvre une feuille avec le curseur de l'âge du jeune (même règle que dans Réglages) ; le changement est enregistré et la liste se recalcule aussitôt. Le mois et l'année de naissance se changent dans Réglages.
* **Âges à la volée** (décision du 13/11/2026) : pour comparer entre amis, le bandeau devient « Le vieux : 35 ans · Le jeune : 15 ans ». Toucher un des deux âges ouvre un curseur (de 5 à 100 ans pour le vieux, de 1 à l'âge du vieux moins 5 pour le jeune) ; la liste se recalcule aussitôt.
  * Ces âges ne sont **pas enregistrés** : Mon équivalent utilise les mêmes, mais tout revient aux réglages à la fermeture de l'application.
  * Tant que des âges à la volée sont actifs, le bandeau porte la mention « Âges temporaires » et un bouton **« Revenir à mes réglages »**.
  * Un âge donné à la volée correspond à une naissance « aujourd'hui moins cet âge », au jour près, comme pour le jeune dans Réglages.
  * Ils marchent aussi sans mois et année de naissance enregistrés : c'est alors le seul moyen de voir des équivalences.

#### Sens de lecture (décision du 13/11/2026)
* Bouton **« Inverser »** (⇄) à côté du bandeau :
  * **Pour le jeune → Pour toi** (par défaut) : colonne de gauche « Pour le jeune », tri par événement du jeune ;
  * **Pour toi → Pour le jeune** : colonnes échangées, tri par événement de l'utilisateur.
* Le sens choisi est mémorisé comme un réglage.

#### Filtres (décision du 13/11/2026)
* Bouton **« Thèmes »** qui ouvre une liste de **cases à cocher**, une par famille (Séries & télé, Cinéma, Musique, Sport, Jeux vidéo & tech, Politique & histoire), toutes cochées par défaut, avec « Tout cocher » / « Tout décocher ».
* Le résumé du filtre s'affiche sous le bandeau (« 4 thèmes sur 6 »).
* Le choix est **mémorisé durablement**, comme un réglage.

#### Liste des paires
* Une carte par paire, en deux colonnes séparées par une flèche ⇄.
* Chaque côté : titre de l'événement, nature, date (« 15 juillet 2018 »), âge au moment de l'événement (« il avait 6 ans » / « tu avais 7 ans », ou « 2 ans avant sa naissance » / « avant ta naissance »).
* En haut de la carte : pastille et nom du type en clair (« Victoire des Bleus en Coupe du monde »).
* **Tous les événements** de la base sont pris en compte, y compris ceux d'avant la naissance des deux personnes (décision du 13/11/2026).
* **Tri** : de l'événement le plus récent au plus ancien (côté jeune, ou côté utilisateur en sens inversé).
* Un événement qui n'a pas de paire dans la tolérance n'apparaît pas.
* **Liste vide** (filtre trop restrictif) : « Aucune équivalence pour ces thèmes. Coche d'autres thèmes ou essaie un autre âge. »
* **Sans mois et année de naissance** : à la place de la liste, « Indique ton mois et ton année de naissance pour voir tes équivalences. » avec le bouton **« Ouvrir les réglages »** (décision du 13/11/2026), et le bouton secondaire « Choisir des âges » qui ouvre les âges à la volée.
* Toucher une carte ouvre **Mon équivalent** sur l'événement de la colonne de gauche.

### Wireframe ASCII (téléphone)
```text
┌────────────────────────────────────────┐
│ Équivalences                           │
├────────────────────────────────────────┤
│  Toi : 35 ans · Le jeune : 15 ans      │
│  20 ANS D'ÉCART     [Modifier] [ ⇄ ]   │
│  [ Thèmes ▾ ]  6 thèmes sur 6          │
│                                        │
│  ┌──────────────────────────────────┐  │
│  │ ● Mort d'une icône de la musique │  │
│  │ POUR LE JEUNE   ⇄   POUR TOI     │  │
│  │ Johnny Hallyday     Barbara      │  │
│  │ Mort                Mort         │  │
│  │ 5 déc. 2017         24 nov. 1997 │  │
│  │ il avait 6 ans      tu avais 6 ans│ │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │ ● Victoire des Bleus au Mondial  │  │
│  │ POUR LE JEUNE   ⇄   POUR TOI     │  │
│  │ Mondial 2018        Mondial 1998 │  │
│  │ Victoire            Victoire     │  │
│  │ 15 juil. 2018       12 juil. 1998│  │
│  │ il avait 6 ans      tu avais 7 ans│ │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │ ● Console Nintendo               │  │
│  │ Nintendo Switch     Nintendo 64  │  │
│  │ Sortie              Sortie       │  │
│  │ 3 mars 2017         1er mars 1997│  │
│  │ il avait 5 ans      tu avais 5 ans│ │
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

Exemple calculé pour une naissance en mai 1991 et un jeune de 15 ans, le 2 novembre 2026. Les paires illustrent la mise en page ; leurs dates et leurs types seront vérifiés lors de la saisie de la base.
