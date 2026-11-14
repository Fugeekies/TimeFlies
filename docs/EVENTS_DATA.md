# Base d'événements - TimeFlies

> Document de conception écrit avant le code (13/11/2026). Les choix marqués **À valider** sont des propositions : ils ne sont pas tranchés.

La base d'événements est le cœur de l'application : le coup de vieux du jour, les équivalences et « Mon équivalent » en dépendent tous. Elle est livrée dans l'application (un fichier JSON par famille, voir « Fichiers et stockage »), sans connexion.

## Règle d'or : zéro invention

- Chaque événement a une **source** vérifiable (page Wikipédia ou source officielle) qui confirme sa date **au jour près**.
- Un événement dont on ne connaît pas le jour exact (« l'été 1969 ») **n'entre pas dans la base**, même pour les équivalences seulement (décision du 13/11/2026).
- Quand la date dépend du pays (sortie d'un film, d'une console), la date de référence est la **première sortie mondiale**, et chaque pays dont la date diffère a sa propre date, elle aussi sourcée (décision du 13/11/2026, voir « Dates par région »).

## Format d'un événement (validé le 13/11/2026)

```json
{
  "id": "film-intouchables",
  "date": "2011-11-02",
  "regionalDates": { "US": "2012-05-25" },
  "title": { "fr": "Intouchables" },
  "kind": "film-release",
  "type": "french-comedy-hit",
  "importance": 3,
  "source": "https://fr.wikipedia.org/wiki/Intouchables",
  "note": "Première sortie : France."
}
```

| Champ | Obligatoire | Description |
|---|---|---|
| `id` | oui | Identifiant stable, en minuscules et tirets, unique dans la base. Ne change jamais une fois publié (le widget et les tests s'y réfèrent). |
| `date` | oui | Date de référence, `AAAA-MM-JJ`, au jour près, dans le passé : la première sortie mondiale pour une œuvre ou un produit, la date de l'événement sinon. |
| `regionalDates` | non | Dates par pays (code ISO 3166-1 à deux lettres : `FR`, `BE`, `US`, `JP`…) quand elles diffèrent de `date`. Chacune doit être sourcée (dans `source` ou `note`). |
| `title` | oui | Nom affiché, par langue. Le français (`fr`) est obligatoire ; les autres langues s'ajoutent au jalon « Traductions ». Les noms propres et les titres d'œuvre restent dans leur forme connue dans la langue affichée. |
| `kind` | oui | Nature de l'événement, qui donne le libellé affiché sous le titre (voir « Natures »). |
| `type` | oui | Type qui sert à apparier les équivalences (voir « Types »). |
| `importance` | oui | 1 (anecdotique), 2 (connu), 3 (que tout le monde connaît). Départage les anniversaires d'un même jour. |
| `source` | oui | Lien qui confirme la date. Jamais affiché dans l'application, sauf dans « À propos » sous forme de crédit global. |
| `note` | non | Précision pour les relecteurs (pays de la première sortie, sources des dates régionales). |

## Dates par région (décision du 13/11/2026)

- La **région** de l'utilisateur est déduite du système (région du téléphone ou de Windows) et se change dans Réglages.
- Pour chaque événement, l'application utilise `regionalDates[région]` si elle existe, sinon `date`. Cette date sert partout : anniversaire, âge, équivalences.
- Au départ, seules les dates françaises sont saisies quand elles diffèrent de la première sortie mondiale ; les autres pays s'ajoutent au fil des traductions.

## Natures (`kind`, validées le 13/11/2026)

La nature donne le libellé sous le titre de l'événement (« Sortie au cinéma · 2 novembre 2011 »). La liste peut s'allonger pendant la saisie.

| `kind` | Libellé affiché |
|---|---|
| `film-release` | Sortie au cinéma |
| `series-premiere` | Première diffusion |
| `series-finale` | Dernier épisode |
| `album-release` | Sortie de l'album |
| `single-release` | Sortie du single |
| `death` | Mort |
| `product-launch` | Sortie |
| `game-release` | Sortie du jeu |
| `sport` | Victoire |
| `history` | Événement |
| `election` | Élection |

## Types (`type`) et familles

Le **type** est ce qui rend une équivalence juste : Friends ↔ Happy Days marche parce que ce sont deux sitcoms cultes. Deux événements ne sont appariés que s'ils ont **le même type**. Les types sont **larges** (décision du 13/11/2026) : la mort de Tupac et celle d'Elvis sont toutes deux une « mort d'une icône de la musique », comme dans l'épisode. Chaque type appartient à une **famille**, qui sert aux filtres de l'écran Équivalences.

Catalogue initial, à compléter pendant la saisie de la base :

| Famille | Types |
|---|---|
| Séries & télé | `sitcom-hit` (sitcom culte), `drama-series-hit` (série dramatique culte), `french-series-hit`, `reality-tv` (téléréalité), `kids-tv-show` (émission jeunesse), `cartoon-on-tv` (dessin animé culte à la télé) |
| Cinéma | `blockbuster`, `animated-feature` (film d'animation familial), `french-comedy-hit`, `saga-episode` (épisode d'une saga culte) |
| Musique | `music-icon-death` (mort d'une icône de la musique, tous genres), `diva-hit`, `boy-band-hit`, `rap-album-hit`, `summer-hit` (tube de l'été), `pop-album-hit` |
| Sport | `football-star-peak` (sommet d'une star du foot), `world-cup-win-france`, `olympics-in-france`, `sports-record` |
| Jeux vidéo & tech | `nintendo-console`, `playstation-console`, `handheld-console`, `video-game-phenomenon`, `phone-launch`, `social-network-launch`, `music-format` (cassette, CD, MP3, streaming) |
| Politique & histoire | `french-president-elected`, `us-president-elected`, `actor-turned-politician`, `world-event`, `royal-event` |

Exemples tirés de l'épisode, qui guident la saisie : Friends ↔ Happy Days (`sitcom-hit`), la mort de Tupac ↔ la mort d'Elvis (`music-icon-death`), Zidane ↔ Platini (`football-star-peak`), Céline Dion ↔ Cher (`diva-hit`), Schwarzenegger ↔ Reagan (`actor-turned-politician`).

## Règles d'appariement des équivalences

Notations : `n_v` date de naissance du « vieux » (l'utilisateur), `n_j` date de naissance du « jeune », `écart = n_j − n_v` (en jours). Les dates de naissance sont calculées comme indiqué dans [`UISpec/Settings.template.md`](UISpec/Settings.template.md) (mois et année de l'utilisateur, âge du jeune).

- Pour un événement `E` « du jeune », l'événement « du vieux » correspondant est un événement `F` :
  - de **même type** que `E` ;
  - dont la date est la plus proche de `E.date − écart` ;
  - à moins de **2 ans** de cette date cible (décision du 13/11/2026).
- Dans l'autre sens (mode inverse, Mon équivalent « Du vieux vers le jeune »), la date cible est `F.date + écart` ; une date cible dans le futur ne donne pas d'équivalent.
- S'il existe plusieurs candidats dans la tolérance, on garde le plus proche de la date cible, puis la plus grande `importance`.
- **Tous les événements** de la base peuvent être appariés, y compris ceux d'avant la naissance des deux personnes : « je n'ai pas connu l'élection de De Gaulle, mais pour un jeune ça peut être celle de Chirac » (décision du 13/11/2026).
- L'âge de chacun au moment de son événement est affiché : `âge(n_j, E.date)` et `âge(n_v, F.date)`. Un événement antérieur à une naissance s'affiche « N ans avant sa naissance » / « N ans avant ta naissance ».
- Un événement ne s'apparie jamais avec lui-même.
- Un événement compte dès **1 an** (décision du 13/11/2026) : un événement de moins d'un an n'est ni apparié ni affiché comme anniversaire.

## Règles du coup de vieux du jour

- Un événement « fête ses N ans » le jour où `jour` et `mois` sont ceux de sa date (dans la région de l'utilisateur), avec `N ≥ 1`.
- Ordre d'affichage du jour :
  1. multiples de 10 (10, 20, 30… ans) ;
  2. multiples de 5 (5, 15, 25… ans) ;
  3. les autres ;

  puis, dans chaque groupe, `importance` décroissante, puis l'événement le plus récent d'abord (le plus récent fait le plus mal).
- Aucun anniversaire aujourd'hui : on affiche le prochain dans les 7 jours qui suivent (même ordre), annoncé « Dans N jours ».
- Un événement du 29 février est fêté le **28 février** les années non bissextiles (décision du 13/11/2026).
- Sans date de naissance (premier lancement passé), les anniversaires s'affichent quand même, sans la ligne « Tu avais N ans » : « le 13/11/2026, les événements du 13/11/2000 ont 26 ans » (décision du 13/11/2026).

## Volume visé

Environ **400 événements**, pour que presque chaque jour de l'année ait au moins un anniversaire, et que chaque type ait assez d'événements répartis des années 1950 à aujourd'hui pour que les équivalences marchent quel que soit l'écart d'âge. Une vérification automatique (test) compte les jours de l'année sans anniversaire et les types qui ont moins de 4 événements.

## Fichiers et stockage (décision du 14/11/2026)

- **Un fichier JSON par famille** (`series.json`, `cinema.json`, `music.json`, `sport.json`, `games-tech.json`, `history.json`), dans `src/ui/src/commonMain/composeResources/files/events/`. Chaque fichier est un tableau d'événements au format ci-dessus.
- Les fichiers sont lus au démarrage et gardés **en mémoire**, avec des index par jour de l'année et par type (voir [`ARCHITECTURE.md`](ARCHITECTURE.md), « Base d'événements »).
- **À 5 000 événements**, on mesure et on décide s'il faut passer à une base SQLite précompilée, construite à la compilation à partir des mêmes fichiers. Les fichiers JSON restent alors la source éditée par les humains.

## Validation automatique (tests du module `data`)

- `id` uniques, au format attendu.
- `date` et `regionalDates` valides, au format `AAAA-MM-JJ`, dans le passé ; codes de pays connus.
- `kind` et `type` connus, `importance` entre 1 et 3, `title.fr` non vide, `source` en `https://`.
- Statistiques de couverture (jours sans anniversaire, types trop pauvres) écrites dans le rapport de test, sans faire échouer la compilation.
