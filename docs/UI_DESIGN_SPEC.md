# Spécification visuelle & design UI - TimeFlies

> Document de conception écrit avant le code (13/11/2026). L'ordre des styles est décidé (Polaroïd en V1, puis Éphéméride, VHS en idée) ; les détails et les règles d'ergonomie restent des propositions **à valider** tant qu'ils ne sont pas cochés dans la feuille de route.

Ce document définit l'identité visuelle, la charte graphique et l'ergonomie de **TimeFlies**, sur Android et sur Windows. Les maquettes et les règles de chaque écran sont dans `UISpec/`.

---

## 🎨 Identité & charte graphique

### 1. Styles d'affichage (décisions du 13/11/2026)
Trois styles sont prévus. Chaque style a son propre jeu de jetons (couleurs, polices, ornements) ; la mise en page des écrans, elle, est la même dans tous. Maquettes : [`maquettes/`](maquettes/README.md).

| Style | Version | Pourquoi |
|---|---|---|
| **Polaroïd** | **V1**, seul style livré (décision du 13/11/2026, qui remplace celle donnant l'Éphéméride en V1) | Le plus beau pour la feuille du jour ; les âges écrits à la main sur les bandeaux. |
| **Éphéméride** | Jalon « Style Éphéméride », juste après la V1 | Le plus clair pour les menus et les listes. |
| **VHS** | Idée, peut-être plus tard | — |

Tant qu'il n'y a qu'un style, Réglages ne propose pas de choix. À partir de l'Éphéméride, l'utilisateur choisit dans **Réglages › Affichage** (Polaroïd par défaut), et le style s'applique à toute l'application et au widget. Le code prévoit dès la V1 des jetons de thème séparés par style, pour que l'ajout de l'Éphéméride ne touche pas aux écrans.

Les maquettes des écrans autres qu'Aujourd'hui sont dessinées en Éphéméride (`maquettes/`) : leur **mise en page** vaut pour la V1, mais leurs couleurs et leurs polices sont celles du Polaroïd (voir plus bas). Elles ne seront pas redessinées avant le code (décision du 13/11/2026).

#### Éphéméride (jalon après la V1)
Le coup de vieux du jour est une page d'**éphéméride**, ce calendrier dont on arrache une feuille chaque matin : un grand chiffre rouge, le jour en dessous, du papier. L'écran Aujourd'hui et le widget reprennent cette feuille ; le reste de l'application reste sobre pour la laisser au premier plan.

- Le **grand chiffre** n'est pas le jour du mois mais le **nombre d'années** écoulées (« 15 ans »), en rouge éphéméride.
- Haut de la feuille : deux petits anneaux de reliure et une ligne de perforation (pointillés) ; c'est le seul ornement de l'application.
- Le reste (listes, réglages) utilise des cartes simples, sans perforation.
- Palette et polices : § 2 et § 5 ci-dessous.

#### VHS (idée, peut-être plus tard)
La feuille du jour devient une **jaquette de cassette VHS** : étiquette blanche collée de travers, nombre d'années écrit comme un titre de film enregistré, petit « ▶ REC » et compteur de bande `00:15:00` en guise d'ornement. Ambiance sombre par défaut, couleurs saturées (magenta, cyan, jaune), léger effet de lignes de balayage sur la feuille seulement.

#### Polaroïd (V1, maquette validée le 13/11/2026)
La feuille du jour devient un **Polaroïd** : cadre blanc avec bande épaisse en bas, où le titre et la légende (« sorti le 2 novembre 2011, tu avais 20 ans ») sont écrits à la main ; à la place de la photo, un aplat de la couleur de la famille de l'événement, légèrement passé, qui porte le nombre d'années en grand et en blanc. La photo est inclinée, un ruban adhésif la tient en haut. Ambiance claire et chaude.

Règles du style Polaroïd dans toute l'application :
- **Écriture manuscrite** (`Caveat`) pour le nombre d'années partout où il apparaît : la photo du jour, et le **bandeau de gauche des cartes** des listes (« 18 ans », « 37 ans »), en accent. Aussi pour le titre de la photo, sa légende et les titres de section (« Bientôt… »).
- **Texte lisible** (`Atkinson Hyperlegible`) pour tout le reste : titres et détails des cartes, menus, réglages, boutons. Les menus restent aussi clairs qu'en Éphéméride.
- Cartes des listes : fond blanc cassé, coins de 6 px, ombre légère, sans bordure.

Jetons de la maquette (clair) : fond `#E8DFD2`, cadre et cartes `#FBFAF7`, encre `#2B2420`, texte secondaire `#6A5D52`, accent `#9A4E1C`. À finir avant les écrans de la V1 : la **version sombre** et le **contraste** de la légende manuscrite (taille minimale, couleur), voir la feuille de route.

#### Règles communes aux trois styles
- Contraste AA dans les trois, en clair et en sombre (VHS peut ne proposer que le sombre).
- Les couleurs des familles (§ 3) et la puce « Chiffre rond » (§ 4) gardent leur sens dans les trois styles.
- Aucune image photographique d'une œuvre réelle (affiche, pochette) : les styles n'utilisent que des formes dessinées et du texte.

### 2. Palettes (clair et sombre)
L'application suit le thème du système (clair ou sombre). Toutes les couleurs sont des jetons du thème Compose (`ColorScheme` Material 3 personnalisé), un jeu par style, jamais des couleurs écrites en dur dans un écran.

#### Polaroïd (V1)

| Jeton | Clair | Sombre | Usage |
|---|---|---|---|
| `background` | `#E8DFD2` papier kraft | à définir | fond des écrans |
| `surface` | `#FBFAF7` blanc cassé | à définir | cadre de la photo, cartes |
| `onSurface` | `#2B2420` encre | à définir | texte principal |
| `onSurfaceVariant` | `#6A5D52` | à définir | libellés, dates, textes secondaires |
| `primary` | `#9A4E1C` brun cuivré | à définir | âges manuscrits, onglet actif, boutons principaux |
| `outline` | `#D6CBBC` | à définir | séparateurs |

La photo du jour prend la couleur de la famille de l'événement (§ 3), légèrement passée.

#### Éphéméride (jalon après la V1)

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

* **Interface et textes (tous les styles) :** `Atkinson Hyperlegible`, conçue pour la lisibilité, y compris aux grandes tailles de police du système.
* **Polaroïd (V1) :** `Caveat` (manuscrite) pour les nombres d'années, le titre et la légende de la photo, le nom de l'application et les titres de section. Jamais en dessous de 20 sp, pour rester lisible.
* **Éphéméride (après la V1) :** `Big Shoulders Display` (gras, condensée), le style des chiffres d'éphéméride, pour le grand chiffre, les nombres d'années et les titres d'écran.
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
* Animations réduites quand le système le demande (photo ou feuille affichée directement, sans effet).
* L'écriture manuscrite n'est jamais la seule porteuse d'une information : la description TalkBack et le Narrateur lisent le texte, et les détails de chaque carte sont aussi en police lisible.

## ✨ Animation

Une seule animation marquante, à l'ouverture de l'écran Aujourd'hui, une fois par jour (pas à chaque retour sur l'écran) :
* **Polaroïd (V1)** : la photo du jour **se développe**, l'aplat de couleur apparaît peu à peu sur le blanc, puis l'écriture (**À valider**).
* **Éphéméride (après la V1)** : la feuille de la veille s'arrache vers le haut et laisse apparaître celle du jour.

Le reste se limite aux transitions standard de Material 3.

---

## Questions ouvertes

1. Le nom affiché sous l'icône reste à choisir (jalon « Nom et icône définitifs ») : les maquettes affichent « TimeFlies » en attendant.
