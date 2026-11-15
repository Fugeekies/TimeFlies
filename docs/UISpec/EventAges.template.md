## 3. 🔍 Quel âge ? (`EventAgesScreen`)

> Décision du 15/11/2026 : cet écran **remplace « Mon équivalent »** (spécification du 13/11/2026), jugé peu utile : chercher l'équivalent d'un événement, c'est déjà ce que fait Équivalences. Il montre à la place l'âge des deux personnes à la date d'un événement.

### Description
L'utilisateur choisit un événement et voit **quel âge avaient les deux personnes** à sa date : lui et l'autre personne d'Équivalences (plus jeune ou plus âgée). Avant une naissance, l'âge est **négatif** (« −14 ans ») : c'est le « tu n'étais même pas né ».

Comme sur Équivalences, aucune des deux personnes n'a de nom à l'écran : chacune a sa silhouette de couleur devant son âge (décision du 15/11/2026).

### Spécifications fonctionnelles

#### Bandeau des âges
* Le même bandeau qu'Équivalences (voir [`Equivalences.template.md`](Equivalences.template.md)) : « 👤 35 ans · 👤 15 ans », l'écart, « Modifier » pour l'âge de l'autre personne, les âges à la volée (partagés entre les deux écrans, oubliés à la fermeture de l'application, « Revenir à mes réglages ») et l'avertissement sous 5 ans d'écart.

#### Choix de l'événement
* Champ de recherche « Cherche un film, une série, un artiste… » : filtre les titres des événements dès 2 caractères, sans tenir compte des accents ni de la casse, du plus récent au plus ancien.
* Sans recherche : tous les événements, groupés par famille (un titre avec la pastille de la famille), du plus récent au plus ancien.
* Chaque ligne : pastille de famille, titre, nature · année.
* Les événements de moins d'un an n'apparaissent pas (décision du 13/11/2026).
* Toucher une carte d'**Aujourd'hui** (la feuille comprise) ou d'**Équivalences** (l'événement de la colonne de gauche) ouvre cet écran sur cet événement.

#### Résultat (quand un événement est choisi)
* Une carte : pastille et nom du type, titre, nature et date (« Sortie au cinéma · 7 janvier 1998 »).
* Puis une ligne par personne : sa silhouette, son âge aujourd'hui en petit, une flèche, et **son âge à la date de l'événement** en grand, écrit à la main dans sa couleur : « 👤 35 ans → 6 ans », « 👤 15 ans → −14 ans ».
* **Âge** (décision du 15/11/2026) : en années révolues, comme partout ; avant la naissance, négatif, avec un vrai signe moins (« −14 ans »). Quelques mois avant la naissance donnent « −1 an ».
* Lecture d'écran : « Toi : 35 ans aujourd'hui, 6 ans à cette date. » et « L'autre personne : 15 ans aujourd'hui, −14 ans à cette date. »
* Bouton « Changer d'événement » : revient à la recherche.

#### Sans mois et année de naissance
* La recherche reste utilisable. À la place des âges : « Indique ton mois et ton année de naissance pour voir ton âge à cette date. », le bouton **« Ouvrir les réglages »** et le bouton secondaire « Choisir des âges » (âges à la volée). Le bandeau n'est pas affiché.

### Wireframe ASCII (téléphone)
```text
┌────────────────────────────────────────┐
│ Quel âge ?                             │
├────────────────────────────────────────┤
│  👤 35 ans · 👤 15 ans                  │
│  20 ANS D'ÉCART            [Modifier]  │
│                                        │
│  ┌──────────────────────────────────┐  │
│  │ ● Blockbuster                    │  │
│  │ Titanic                          │  │
│  │ Sortie au cinéma · 7 janvier 1998│  │
│  │                                  │  │
│  │ 👤 35 ans  →  6 ans              │  │
│  │ 👤 15 ans  →  −14 ans            │  │
│  └──────────────────────────────────┘  │
│                                        │
│  [ Changer d'événement ]               │
├────────────────────────────────────────┤
│  📅  ⇄  🔍 Quel âge ?  ⚙                │
└────────────────────────────────────────┘
```

Chaque 👤 est la silhouette de couleur d'une des deux personnes : celle de l'utilisateur sur la première ligne, celle de l'autre personne sur la seconde. Exemple pour une naissance en mai 1991 et une autre personne de 15 ans, le 15 novembre 2026 ; la date de Titanic illustre la mise en page et sera vérifiée lors de la saisie de la base.
