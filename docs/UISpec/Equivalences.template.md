## 2. ⇄ Équivalences (`EquivalencesScreen`)

> Spécification de conception (13/11/2026), avant le code. Règles d'appariement dans [`EVENTS_DATA.md`](../EVENTS_DATA.md) ; les points marqués **À valider** ne sont pas tranchés.

### Description
Le principe de l'épisode : pour un jeune de l'âge choisi, quels événements ont la même place dans sa vie que tels autres dans la vie de l'utilisateur. **Pas de phrase générée** (décision du 13/11/2026) : chaque carte montre les deux événements, leurs dates et l'âge de chacun.

### Spécifications fonctionnelles

#### Bandeau des générations (en haut)
* « Toi : 35 ans · Le jeune : 15 ans » et, en grand, « **20 ans d'écart** ».
* Bouton « Modifier » : ouvre une feuille avec le curseur de l'âge du jeune (même règle que dans Réglages) ; le changement est enregistré et la liste se recalcule aussitôt. La date de naissance se change dans Réglages.

#### Filtres
* Puces de familles (voir `UI_DESIGN_SPEC.md` § 3), sélection multiple : Toutes (par défaut), Séries & télé, Cinéma, Musique, Sport, Jeux vidéo & tech, Politique & histoire.
* Le filtre choisi est mémorisé jusqu'à la fermeture de l'application (**À valider** : ou durablement).

#### Liste des paires
* Une carte par paire, en deux colonnes : à gauche **« Pour le jeune »**, à droite **« Pour toi »**, une flèche ⇄ entre les deux.
* Chaque côté : titre de l'événement, nature, date (« 15 juillet 2018 »), âge au moment de l'événement (« il avait 6 ans » / « tu avais 7 ans », ou « 2 ans avant sa naissance » / « avant ta naissance »).
* En haut de la carte : pastille et nom du type en clair (« Victoire des Bleus en Coupe du monde »).
* **Événements pris côté jeune** : ceux dont la date va de 5 ans avant sa naissance à aujourd'hui (**À valider**) et qui ont une paire dans la tolérance.
* **Tri** : de l'événement du jeune le plus récent au plus ancien.
* Un événement qui n'a pas de paire n'apparaît pas.
* **Liste vide** (filtre trop restrictif ou base trop pauvre pour cet écart) : « Aucune équivalence pour ces réglages. Essaie un autre âge ou une autre famille. »
* Toucher une carte ouvre **Mon équivalent** sur l'événement du jeune.

### Wireframe ASCII (téléphone)
```text
┌────────────────────────────────────────┐
│ Équivalences                           │
├────────────────────────────────────────┤
│  Toi : 35 ans · Le jeune : 15 ans      │
│  20 ANS D'ÉCART            [Modifier]  │
│                                        │
│  (Toutes) (Séries) (Ciné) (Musique)    │
│  (Sport) (Jeux & tech) (Histoire)  →   │
│                                        │
│  ┌──────────────────────────────────┐  │
│  │ ● Mort d'une star de la chanson  │  │
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
```

Sur Windows, les cartes s'affichent sur deux colonnes au-dessus de 840 dp de large.

Exemple calculé pour une naissance le 14/05/1991 et un jeune de 15 ans, le 2 novembre 2026. Les paires illustrent la mise en page ; leurs dates et leurs types seront vérifiés lors de la saisie de la base.

### Questions ouvertes
1. Côté jeune, la fenêtre « de 5 ans avant sa naissance à aujourd'hui » convient-elle ?
2. Faut-il un mode inverse (partir des événements de ta jeunesse et trouver ceux du jeune), ou « Mon équivalent » suffit-il pour ça ?
