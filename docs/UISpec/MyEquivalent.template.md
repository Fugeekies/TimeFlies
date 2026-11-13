## 3. 🔍 Mon équivalent (`MyEquivalentScreen`)

> Spécification de conception (13/11/2026), avant le code. Règles d'appariement dans [`EVENTS_DATA.md`](../EVENTS_DATA.md) ; les points marqués **À valider** ne sont pas tranchés.

### Description
L'utilisateur choisit un événement et voit son équivalent pour l'autre génération, **dans les deux sens** : « c'était quoi pour moi ? » et « c'est quoi pour le jeune ? ».

### Spécifications fonctionnelles

#### Choix du sens
* Bouton segmenté, deux choix :
  * **« Du jeune vers moi »** (par défaut) : l'événement choisi appartient à la vie du jeune ; l'équivalent est cherché à `date − écart`.
  * **« De moi vers le jeune »** : l'événement choisi appartient à la vie de l'utilisateur ; l'équivalent est cherché à `date + écart`. Une date cible dans le futur ne donne pas d'équivalent.
* Ouvert depuis une carte d'Aujourd'hui ou d'Équivalences, l'écran arrive avec l'événement déjà choisi et le sens « Du jeune vers moi ».

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
│  [ Du jeune vers moi | De moi vers  ]  │
│  [                   | le jeune     ]  │
│  TON CHOIX                             │
│  ┌──────────────────────────────────┐  │
│  │ ● La Reine des neiges            │  │
│  │ Sortie au cinéma · 4 déc. 2013   │  │
│  │ Le jeune avait 2 ans.            │  │
│  └──────────────────────────────────┘  │
│              ↓  20 ans plus tôt        │
│  SON ÉQUIVALENT POUR TOI               │
│  ┌──────────────────────────────────┐  │
│  │ ● Le Roi Lion                    │  │
│  │ Sortie au cinéma · 9 nov. 1994   │  │
│  │ Tu avais 3 ans.                  │  │
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

Exemple pour une naissance le 14/05/1991 et un jeune de 15 ans (20 ans d'écart), le 2 novembre 2026 : la date cible est le 4 décembre 1993, Le Roi Lion en est à moins d'un an. Les dates seront vérifiées lors de la saisie de la base.

### Questions ouvertes
1. Faut-il pouvoir choisir un âge de jeune différent ici, sans changer le réglage général ?
