## 2. 🔍 Quel âge ? (`EventAgesScreen`)

> Décision du 15/11/2026 : cet écran **remplace « Mon équivalent »** (spécification du 13/11/2026), jugé peu utile : chercher l'équivalent d'un événement, c'est déjà ce que fait Équivalences. Il montre à la place l'âge des personnes à la date d'un événement. Décisions du 17/11/2026 (jalon 16) : il passe en **deuxième position**, devant Équivalences, et montre ton âge seul ou jusqu'à **quatre âges** (le tien et 3 âges ajoutés).

### Description
L'utilisateur choisit un événement et voit **quel âge il avait** à sa date et, s'il en a ajouté, quel âge avaient les autres personnes (jusqu'à trois, plus jeunes ou plus âgées). Avant une naissance, l'âge est **négatif** (« −14 ans ») : c'est le « tu n'étais même pas né ».

Comme sur Équivalences, aucune personne n'a de nom à l'écran : chacune a sa silhouette de couleur devant son âge (décisions du 15/11/2026 et du 17/11/2026).

### Spécifications fonctionnelles

#### Bandeau des âges
* Le même bandeau qu'Équivalences (voir [`Equivalences.template.md`](Equivalences.template.md), « Bandeau des âges ») : ton âge seul au lancement, « + Ajouter un âge » jusqu'à 3, toucher un âge pour le changer ou le retirer, l'écart avec chacun, l'âge de l'utilisateur à la volée (« Revenir à mes réglages ») et l'avertissement sous 5 ans d'écart. Les âges ajoutés sont partagés entre les deux écrans et oubliés à la fermeture de l'application.

#### Liste des événements, avec les âges sur chaque ligne (décisions du 15/11/2026 et du 17/11/2026)
* **Pas d'événement à choisir** : chaque ligne porte directement les âges, rien à toucher pour les voir.
* Champ de recherche « Cherche un film, une série, un artiste… » : filtre les titres des événements dès 2 caractères, sans tenir compte des accents ni de la casse, du plus récent au plus ancien.
* **Mes événements** (décision du 19/11/2026) : les événements personnels ajoutés dans Réglages forment la première section, avec leur pastille sarcelle ; ils ne dépendent pas du filtre des thèmes et la recherche les trouve aussi, en premier.
* Sans recherche : tous les événements, groupés par famille (un titre avec la pastille de la famille), du plus récent au plus ancien.
* **Filtre des thèmes** (décision du 18/11/2026) : au-dessus de la recherche, le même bouton **« Thèmes »** et le même résumé (« 4 thèmes sur 6 ») qu'Équivalences, avec le **même choix partagé** et mémorisé : décocher un thème dans un écran le décoche dans l'autre. La liste et la recherche ne montrent que les thèmes cochés ; visible aussi sans date de naissance. Aucun thème coché : « Aucun thème coché. Coche au moins un thème avec le bouton Thèmes. »
* Chaque ligne : pastille de famille, titre, nature · année et, en dessous, **les âges côte à côte, dans l'ordre du bandeau** : « 👤 6 ans  👤 −14 ans  👤 37 ans », chacun dans la couleur de sa personne et précédé de sa silhouette. S'ils ne tiennent pas sur une ligne, le dernier passe à la ligne suivante, toujours dans le même ordre.
* **Âge** (décision du 15/11/2026) : en années révolues, comme partout ; avant la naissance, négatif, avec un vrai signe moins (« −14 ans »). Quelques mois avant la naissance donnent « −1 an ».
* Lecture d'écran, une phrase par ligne : « Titanic, Sortie au cinéma, 7 janvier 1998. Toi : 6 ans. Personne 2 : −14 ans. »
* Les événements de moins d'un an n'apparaissent pas (décision du 13/11/2026).
* Toucher une carte d'**Aujourd'hui** (la feuille comprise) ou d'**Équivalences** (l'événement de l'utilisateur) ouvre cet écran avec le titre de l'événement dans la recherche : sa ligne s'affiche aussitôt.

#### Sans mois et année de naissance
* En haut, à la place du bandeau : « Indique ton mois et ton année de naissance pour voir ton âge à chaque événement. », le bouton **« Ouvrir les réglages »** et le bouton secondaire « Choisir des âges » (ton âge à la volée). La recherche et la liste restent utilisables, sans les âges.

### Wireframe ASCII (téléphone, trois âges ajoutés)
```text
┌────────────────────────────────────────┐
│ Quel âge ?                             │
├────────────────────────────────────────┤
│  👤 35 ans  👤 15 ans                   │
│  👤 66 ans  👤 8 ans                    │
│  👤 20 ans d'écart                      │
│  👤 30 ans d'écart                      │
│  👤 27 ans d'écart                      │
│  [ Thèmes ]  4 thèmes sur 6            │
│  [ Cherche un film, une série…     ]   │
│                                        │
│  ● CINÉMA                              │
│  ● La Reine des neiges                 │
│    Sortie au cinéma · 2013             │
│    👤 22 ans 👤 2 ans 👤 53 ans 👤 −5 ans │
│  ──────────────────────────────────    │
│  ● Titanic                             │
│    Sortie au cinéma · 1998             │
│    👤 6 ans 👤 −14 ans 👤 37 ans         │
│    👤 −21 ans                           │
├────────────────────────────────────────┤
│  📅  🔍 Quel âge ?  ⇄  ⚙                │
└────────────────────────────────────────┘
```

Chaque 👤 est la silhouette de couleur d'une personne, dans l'ordre du bandeau : l'utilisateur, puis les âges ajoutés. Exemple pour une naissance en mai 1991 et des âges ajoutés de 15, 66 et 8 ans, le 17 novembre 2026 ; les dates des films illustrent la mise en page et seront vérifiées lors de la saisie de la base.
