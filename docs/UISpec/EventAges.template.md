## 3. 🔍 Quel âge ? (`EventAgesScreen`)

> Décision du 15/11/2026 : cet écran **remplace « Mon équivalent »** (spécification du 13/11/2026), jugé peu utile : chercher l'équivalent d'un événement, c'est déjà ce que fait Équivalences. Il montre à la place l'âge des deux personnes à la date d'un événement.

### Description
L'utilisateur choisit un événement et voit **quel âge avaient les deux personnes** à sa date : lui et l'autre personne d'Équivalences (plus jeune ou plus âgée). Avant une naissance, l'âge est **négatif** (« −14 ans ») : c'est le « tu n'étais même pas né ».

Comme sur Équivalences, aucune des deux personnes n'a de nom à l'écran : chacune a sa silhouette de couleur devant son âge (décision du 15/11/2026).

### Spécifications fonctionnelles

#### Bandeau des âges
* Le même bandeau qu'Équivalences (voir [`Equivalences.template.md`](Equivalences.template.md)) : « 👤 35 ans · 👤 15 ans », l'écart, « Modifier » pour l'âge de l'autre personne, les âges à la volée (partagés entre les deux écrans, oubliés à la fermeture de l'application, « Revenir à mes réglages ») et l'avertissement sous 5 ans d'écart.

#### Liste des événements, avec les âges sur chaque ligne (décision du 15/11/2026)
* **Pas d'événement à choisir** : chaque ligne porte directement les deux âges, rien à toucher pour les voir.
* Champ de recherche « Cherche un film, une série, un artiste… » : filtre les titres des événements dès 2 caractères, sans tenir compte des accents ni de la casse, du plus récent au plus ancien.
* Sans recherche : tous les événements, groupés par famille (un titre avec la pastille de la famille), du plus récent au plus ancien.
* Chaque ligne : pastille de famille, titre, nature · année et, à droite, l'un sous l'autre, **l'âge des deux personnes à la date de l'événement**, écrit à la main dans leur couleur et précédé de leur silhouette : « 👤 6 ans » (l'utilisateur), « 👤 −14 ans » (l'autre personne).
* **Âge** (décision du 15/11/2026) : en années révolues, comme partout ; avant la naissance, négatif, avec un vrai signe moins (« −14 ans »). Quelques mois avant la naissance donnent « −1 an ».
* Lecture d'écran, une phrase par ligne : « Titanic, Sortie au cinéma, 7 janvier 1998. Toi : 6 ans. L'autre personne : −14 ans. »
* Les événements de moins d'un an n'apparaissent pas (décision du 13/11/2026).
* Toucher une carte d'**Aujourd'hui** (la feuille comprise) ou d'**Équivalences** (l'événement de la colonne de gauche) ouvre cet écran avec le titre de l'événement dans la recherche : sa ligne s'affiche aussitôt.

#### Sans mois et année de naissance
* En haut, à la place du bandeau : « Indique ton mois et ton année de naissance pour voir ton âge à chaque événement. », le bouton **« Ouvrir les réglages »** et le bouton secondaire « Choisir des âges » (âges à la volée). La recherche et la liste restent utilisables, sans les âges.

### Wireframe ASCII (téléphone)
```text
┌────────────────────────────────────────┐
│ Quel âge ?                             │
├────────────────────────────────────────┤
│  👤 35 ans · 👤 15 ans                  │
│  20 ANS D'ÉCART            [Modifier]  │
│  [ Cherche un film, une série…     ]   │
│                                        │
│  ● CINÉMA                              │
│  ● La Reine des neiges     👤 22 ans   │
│    Sortie au cinéma · 2013 👤 2 ans    │
│  ──────────────────────────────────    │
│  ● Titanic                 👤 6 ans    │
│    Sortie au cinéma · 1998 👤 −14 ans  │
│  ──────────────────────────────────    │
│  ● Le Roi Lion             👤 3 ans    │
│    Sortie au cinéma · 1994 👤 −18 ans  │
├────────────────────────────────────────┤
│  📅  ⇄  🔍 Quel âge ?  ⚙                │
└────────────────────────────────────────┘
```

Chaque 👤 est la silhouette de couleur d'une des deux personnes : celle de l'utilisateur en haut, celle de l'autre personne en dessous. Exemple pour une naissance en mai 1991 et une autre personne de 15 ans, le 15 novembre 2026 ; les dates des films illustrent la mise en page et seront vérifiées lors de la saisie de la base.
