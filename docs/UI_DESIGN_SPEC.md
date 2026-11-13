# Spécification visuelle & design UI - TimeFlies

> Document de conception écrit avant le code (13/11/2026). Les trois styles d'affichage sont décidés (13/11/2026) ; leurs détails et les règles d'ergonomie restent des propositions **à valider** tant qu'ils ne sont pas cochés dans la feuille de route.

Ce document définit l'identité visuelle, la charte graphique et l'ergonomie de **TimeFlies**, sur Android et sur Windows. Les maquettes et les règles de chaque écran sont dans `UISpec/`.

---

## 🎨 Identité & charte graphique

### 1. Trois styles d'affichage (décision du 13/11/2026)
L'utilisateur choisit le style dans **Réglages › Affichage** ; il s'applique à toute l'application et au widget. Chaque style a son propre jeu de jetons (couleurs, polices, ornements) ; la mise en page des écrans, elle, est la même dans les trois. Le style **Éphéméride** est celui par défaut, et celui que montrent les maquettes de `UISpec/`.

#### Éphéméride (par défaut)
Le coup de vieux du jour est une page d'**éphéméride**, ce calendrier dont on arrache une feuille chaque matin : un grand chiffre rouge, le jour en dessous, du papier. L'écran Aujourd'hui et le widget reprennent cette feuille ; le reste de l'application reste sobre pour la laisser au premier plan.

- Le **grand chiffre** n'est pas le jour du mois mais le **nombre d'années** écoulées (« 15 ans »), en rouge éphéméride.
- Haut de la feuille : deux petits anneaux de reliure et une ligne de perforation (pointillés) ; c'est le seul ornement de l'application.
- Le reste (listes, réglages) utilise des cartes simples, sans perforation.
- Palette et polices : § 2 et § 5 ci-dessous.

#### VHS (**À valider** : palette et polices à dessiner)
La feuille du jour devient une **jaquette de cassette VHS** : étiquette blanche collée de travers, nombre d'années écrit comme un titre de film enregistré, petit « ▶ REC » et compteur de bande `00:15:00` en guise d'ornement. Ambiance sombre par défaut, couleurs saturées (magenta, cyan, jaune), léger effet de lignes de balayage sur la feuille seulement.

#### Polaroïd (**À valider** : palette et polices à dessiner)
La feuille du jour devient un **Polaroïd** : cadre blanc avec bande épaisse en bas, où le nombre d'années et le titre sont écrits à la main ; à la place de la photo, un aplat de la couleur de la famille de l'événement, légèrement passé. Ambiance claire et chaude, ruban adhésif en coin comme ornement.

#### Règles communes aux trois styles
- Contraste AA dans les trois, en clair et en sombre (VHS peut ne proposer que le sombre).
- Les couleurs des familles (§ 3) et la puce « Chiffre rond » (§ 4) gardent leur sens dans les trois styles.
- Aucune image photographique d'une œuvre réelle (affiche, pochette) : les styles n'utilisent que des formes dessinées et du texte.

### 2. Palette (clair et sombre)
L'application suit le thème du système (clair ou sombre). Toutes les couleurs sont des jetons du thème Compose (`ColorScheme` Material 3 personnalisé), jamais des couleurs écrites en dur dans un écran.

| Jeton | Clair | Sombre | Usage |
|---|---|---|---|
| `background` | `#F3F2EE` papier gris chaud | `#16141A` | fond des écrans |
| `surface` | `#FFFFFF` | `#221F27` | cartes, feuille d'éphéméride |
| `onSurface` | `#1F1B24` encre | `#EDEAF2` | texte principal |
| `onSurfaceVariant` | `#5E5866` | `#A8A1B2` | libellés, dates, textes secondaires |
| `primary` | `#D7263D` rouge éphéméride | `#FF5C6C` | grand chiffre, onglet actif, boutons principaux |
| `outline` | `#D9D5DE` | `#3A3542` | bordures fines, perforation |

### 3. Couleurs des familles d'événements
Une pastille de couleur (point de 8 dp et puce de filtre) indique la famille de chaque événement. Couleurs choisies pour rester lisibles sur les deux fonds et ne pas reprendre le rouge `primary`.

| Famille | Couleur |
|---|---|
| Séries & télé | `#7C5CFF` violet |
| Cinéma | `#E09F1F` ambre |
| Musique | `#E5487C` rose |
| Sport | `#1FA463` vert |
| Jeux vidéo & tech | `#2E8BFF` bleu |
| Politique & histoire | `#8A7F72` taupe |

### 4. Chiffres ronds
Un anniversaire rond est signalé par une puce « Chiffre rond » : fond `primary` à 12 %, texte `primary`. Multiples de 10 et de 5 ont la même puce ; l'ordre d'affichage suffit à les distinguer (voir `EVENTS_DATA.md`).

### 5. Typographies (embarquées)
Les polices sont **embarquées** dans l'application (ressources Compose, licence SIL OFL) : ni Android ni Windows ne les fournissent, et les télécharger serait une connexion réseau.

* **Grand chiffre et titres d'écran :** `Big Shoulders Display` (gras, condensée), le style des chiffres d'éphéméride. Réservée au grand chiffre, aux nombres d'années et aux titres d'écran.
* **Interface et textes :** `Atkinson Hyperlegible`, conçue pour la lisibilité, y compris aux grandes tailles de police du système.
* **Échelle (sp)** : grand chiffre 96 (téléphone) / 120 (PC) ; titre d'écran 28 ; titre d'événement 20 ; texte 16 ; libellé 13 (majuscules, espacement des lettres 0,06 em).

---

## 🧭 Navigation

Quatre destinations, dans cet ordre :

| Destination | Icône (Material Symbols) | Spécification |
|---|---|---|
| Aujourd'hui | `calendar_today` | [`Today.template.md`](UISpec/Today.template.md) |
| Équivalences | `compare_arrows` | [`Equivalences.template.md`](UISpec/Equivalences.template.md) |
| Mon équivalent | `search` | [`MyEquivalent.template.md`](UISpec/MyEquivalent.template.md) |
| Réglages | `settings` | [`Settings.template.md`](UISpec/Settings.template.md) |

* **Navigation adaptative** (`NavigationSuiteScaffold`) : barre en bas sous 600 dp de large (téléphone), rail à gauche au-dessus (tablette, fenêtre Windows).
* L'application s'ouvre sur **Aujourd'hui**, sauf au premier lancement ([`Onboarding.template.md`](UISpec/Onboarding.template.md)).
* Sur Windows, la fenêtre s'ouvre à 1 000 × 700 dp, taille minimale 420 × 640 ; sa taille et sa position sont mémorisées.
* Le bouton Retour d'Android revient à Aujourd'hui depuis les autres destinations, puis quitte l'application.

### Hors de l'application
* **Widget Android** : [`AndroidWidget.template.md`](UISpec/AndroidWidget.template.md).
* **Icône de barre des tâches et notification Windows** : [`WindowsTray.template.md`](UISpec/WindowsTray.template.md).

---

## 🌍 Langues et formats

* Français d'abord, interface prévue pour la traduction : tous les textes dans les ressources (`strings.xml` de Compose Multiplatform), aucun texte écrit en dur dans un écran. Les maquettes de `UISpec/` gardent les libellés français, langue de référence.
* Dates longues : « 2 novembre 2011 ». Dates courtes (listes) : « 02/11/2011 ».
* Âges et durées : « 1 an », « 15 ans » ; avant une naissance : « 3 ans avant ta naissance ».
* Un texte traduit peut être plus long que le français : les mises en page ne supposent pas une largeur de texte fixe.

## ♿ Accessibilité

* Contraste AA au minimum sur les deux thèmes (texte 4,5:1, grand chiffre 3:1).
* Les tailles de police suivent le réglage du système ; à 200 %, les cartes passent sur plusieurs lignes au lieu de couper le texte.
* Chaque carte a une description pour TalkBack et le Narrateur Windows, par exemple : « Intouchables, sortie au cinéma le 2 novembre 2011, il y a 15 ans. Tu avais 20 ans. »
* Zones tactiles de 48 dp minimum. Sur Windows, tout se fait aussi au clavier (Tab, Entrée, flèches dans les listes).
* Animations réduites quand le système le demande (feuille d'éphéméride affichée sans l'effet d'arrachage).

## ✨ Animation

Une seule animation marquante : à l'ouverture de l'écran Aujourd'hui, la feuille de la veille s'arrache vers le haut et laisse apparaître celle du jour (une fois par jour, pas à chaque retour sur l'écran). Le reste se limite aux transitions standard de Material 3.

---

## Questions ouvertes

1. Palettes et polices des styles VHS et Polaroïd, à dessiner avant le jalon « Écrans communs ».
2. Le nom affiché sous l'icône reste à choisir (jalon « Nom et icône définitifs ») : les maquettes affichent « TimeFlies » en attendant.
