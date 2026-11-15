## 3. 🔍 Mon équivalent (`MyEquivalentScreen`)

> Spécification de conception (13/11/2026), avant le code. Règles d'appariement dans [`EVENTS_DATA.md`](../EVENTS_DATA.md) ; les points marqués **À valider** ne sont pas tranchés.

> Décision du 15/11/2026 : l'autre personne peut être plus jeune ou plus âgée, et l'écran ne nomme plus personne (ni « jeune » ni « vieux ») : un pictogramme de personne de couleur devant chaque âge, comme dans [`Equivalences.template.md`](Equivalences.template.md). Le choix du sens est revu en conséquence ; les autres libellés qui nomment « le jeune » suivent la même règle au jalon 8.

### Description
L'utilisateur choisit un événement et voit son équivalent pour l'autre génération, **dans les deux sens** : « c'était quoi pour moi ? » et « c'est quoi pour le jeune ? ».

### Spécifications fonctionnelles

#### Choix du sens (décision du 15/11/2026, qui remplace les libellés « Du jeune vers moi » / « De moi vers le jeune »)
* Bouton segmenté, deux choix, chacun dessiné **« 👤 → 👤 »** avec les silhouettes de couleur d'Équivalences, sans texte :
  * **silhouette de l'autre personne → la tienne** (par défaut) : l'événement choisi appartient à la vie de l'autre personne ; l'équivalent est cherché dans la tienne ;
  * **ta silhouette → celle de l'autre personne** : l'événement choisi appartient à ta vie ; l'équivalent est cherché dans celle de l'autre personne.
  * La date cible suit la règle d'[`EVENTS_DATA.md`](../EVENTS_DATA.md) (`E.date + (n_B − n_A)`) : l'autre personne pouvant être plus jeune ou plus âgée, les deux sens peuvent chercher plus tôt ou plus tard. Une date cible dans le futur ne donne pas d'équivalent.
  * Lecture d'écran : « De l'autre personne vers toi » / « De toi vers l'autre personne ».
* Les cartes du résultat portent la silhouette de la personne concernée devant son âge, comme sur Équivalences.
* Ouvert depuis une carte d'**Équivalences**, l'écran arrive avec l'événement de la colonne de gauche et le sens qui part de sa personne.
* Ouvert depuis une carte d'**Aujourd'hui** (décision du 15/11/2026) : l'événement est considéré comme **le tien** (Aujourd'hui parle de ta vie) ; le sens est « ta silhouette → celle de l'autre personne ».

#### Recherche
* Champ de recherche « Cherche un film, une série, un artiste… » : filtre les titres des événements dès 2 caractères, sans tenir compte des accents ni de la casse.
* Sans recherche : liste des événements par famille (mêmes puces que l'écran Équivalences), du plus récent au plus ancien.
* Chaque ligne : pastille de famille, titre, nature · année.

#### Résultat (quand un événement est choisi)
* Carte « Ton choix » : titre, nature, date, âge de la personne concernée à ce moment.
* Flèche vers le bas et l'écart (« 20 ans plus tôt » / « 20 ans plus tard »).
* Carte **« Son équivalent »** : le meilleur candidat (même type, plus proche de la date cible, dans la tolérance), avec titre, nature, date et âge de l'autre personne.
* En dessous, « **Autres candidats** » : les autres événements du même type dans la tolérance, au plus 3, du plus proche au plus éloigné de la date cible, avec l'écart à la date cible (« à 8 mois près »).
* **Aucun équivalent** : « Pas d'équivalent dans la base pour ce type d'événement à cette époque. » suivi des événements du même type les plus proches hors tolérance, au plus 3, avec leur écart (« 4 ans trop tôt »), pour montrer ce qui existe.
* Bouton « Changer d'événement » : revient à la recherche.

### Wireframe ASCII (téléphone)
```text
┌────────────────────────────────────────┐
│ Mon équivalent                         │
├────────────────────────────────────────┤
│  👤 35 ans · 👤 15 ans                  │
│  20 ANS D'ÉCART            [Modifier]  │
│  [   👤 → 👤    |    👤 → 👤    ]       │
│  TON CHOIX                             │
│  ┌──────────────────────────────────┐  │
│  │ ● Film d'animation familial      │  │
│  │ La Reine des neiges              │  │
│  │ Sortie au cinéma · 4 déc. 2013   │  │
│  │ 👤 2 ans                         │  │
│  └──────────────────────────────────┘  │
│              ↓  20 ans plus tôt        │
│  SON ÉQUIVALENT                        │
│  ┌──────────────────────────────────┐  │
│  │ ● Film d'animation familial      │  │
│  │ Le Roi Lion                      │  │
│  │ Sortie au cinéma · 9 nov. 1994   │  │
│  │ 👤 3 ans                         │  │
│  └──────────────────────────────────┘  │
│                                        │
│  AUTRES CANDIDATS                      │
│  ● …                   à 1 an près     │
│                                        │
│  [ Changer d'événement ]               │
├────────────────────────────────────────┤
│  📅  ⇄  🔍 Mon équivalent  ⚙            │
└────────────────────────────────────────┘
```

Chaque 👤 est la silhouette de couleur d'une des deux personnes, comme sur Équivalences ; le bouton de gauche (sélectionné) part de l'autre personne vers toi. Sans recherche, la liste par famille s'affiche sous le choix du sens ; quand il n'y a pas d'équivalent, « Les plus proches » liste au plus 3 événements hors tolérance (« 4 ans trop tôt »).

Exemple pour une naissance en mai 1991 et une autre personne de 15 ans (20 ans d'écart), le 2 novembre 2026 : la date cible est le 4 décembre 1993, Le Roi Lion en est à moins d'un an. Les dates seront vérifiées lors de la saisie de la base.

#### Sans mois et année de naissance
* À la place du résultat : « Indique ton mois et ton année de naissance pour voir tes équivalences. » et le bouton **« Ouvrir les réglages »** (décision du 13/11/2026). La recherche reste utilisable. Le bandeau des âges n'est pas affiché.

#### Âges à la volée (décision du 13/11/2026)
* Même bandeau et mêmes règles que dans [`Equivalences.template.md`](Equivalences.template.md) : « 👤 35 ans · 👤 15 ans », modifiable sans toucher aux réglages, partagé entre les deux écrans, oublié à la fermeture de l'application, avec le bouton « Revenir à mes réglages ».
* Sans mois et année de naissance, le bouton secondaire « Choisir des âges » ouvre ce bandeau.
