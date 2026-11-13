# Base d'événements - TimeFlies

> Document de conception écrit avant le code (13/11/2026). Les choix marqués **À valider** sont des propositions : ils ne sont pas tranchés.

La base d'événements est le cœur de l'application : le coup de vieux du jour, les équivalences et « Mon équivalent » en dépendent tous. Elle est livrée dans l'application (`events.json`), sans connexion.

## Règle d'or : zéro invention

- Chaque événement a une **source** vérifiable (page Wikipédia ou source officielle) qui confirme sa date **au jour près**.
- Un événement dont on ne trouve pas la date exacte n'entre pas dans la base.
- Quand les sources se contredisent ou que la date dépend du pays (sortie d'un film, d'une console), on garde la **date française** et on le note dans le champ `note`. À défaut de sortie française, la première sortie mondiale.

## Format d'un événement (proposition, **À valider**)

```json
{
  "id": "film-intouchables",
  "date": "2011-11-02",
  "title": { "fr": "Intouchables" },
  "kind": "film-release",
  "type": "french-comedy-hit",
  "importance": 3,
  "source": "https://fr.wikipedia.org/wiki/Intouchables",
  "note": "Sortie en France."
}
```

| Champ | Obligatoire | Description |
|---|---|---|
| `id` | oui | Identifiant stable, en minuscules et tirets, unique dans la base. Ne change jamais une fois publié (le widget et les tests s'y réfèrent). |
| `date` | oui | Date de l'événement, `AAAA-MM-JJ`, au jour près, dans le passé. |
| `title` | oui | Nom affiché, par langue. Le français (`fr`) est obligatoire ; les autres langues s'ajoutent au jalon « Traductions ». Les noms propres et les titres d'œuvre restent dans leur forme connue en France. |
| `kind` | oui | Nature de l'événement, qui donne le libellé affiché sous le titre (voir « Natures »). |
| `type` | oui | Type précis qui sert à apparier les équivalences (voir « Types »). |
| `importance` | oui | 1 (anecdotique), 2 (connu), 3 (que tout le monde connaît). Départage les anniversaires d'un même jour. |
| `source` | oui | Lien qui confirme la date. Jamais affiché dans l'application, sauf dans « À propos » sous forme de crédit global. |
| `note` | non | Précision pour les relecteurs (pays de la date, ambiguïté). |

## Natures (`kind`)

La nature donne le libellé sous le titre de l'événement (« Sortie au cinéma · 2 novembre 2011 »). Liste initiale (**À valider**) :

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

Le **type** est ce qui rend une équivalence juste : Friends ↔ Happy Days marche parce que ce sont deux sitcoms cultes. Deux événements ne sont appariés que s'ils ont **exactement le même type**. Chaque type appartient à une **famille**, qui sert aux filtres de l'écran Équivalences.

Catalogue initial (**À valider**, à compléter pendant la saisie de la base) :

| Famille | Types |
|---|---|
| Séries & télé | `us-sitcom-hit` (sitcom américaine culte), `us-drama-hit` (série dramatique culte), `french-series-hit`, `reality-tv` (téléréalité), `kids-tv-show` (émission jeunesse), `anime-on-french-tv` (dessin animé japonais à la télé française) |
| Cinéma | `blockbuster`, `animated-feature` (film d'animation familial), `french-comedy-hit`, `saga-episode` (épisode d'une saga culte) |
| Musique | `pop-star-death`, `rock-star-death`, `rapper-death`, `french-singer-death`, `diva-hit`, `boy-band-hit`, `french-rap-album`, `summer-hit` (tube de l'été), `pop-album-hit` |
| Sport | `french-football-star-peak` (sommet d'une star du foot français), `world-cup-win-france`, `olympics-in-france`, `sports-record` |
| Jeux vidéo & tech | `nintendo-console`, `playstation-console`, `handheld-console`, `video-game-phenomenon`, `phone-launch`, `social-network-launch`, `music-format` (cassette, CD, MP3, streaming) |
| Politique & histoire | `french-president-elected`, `us-president-elected`, `actor-turned-politician`, `world-event`, `royal-event` |

Exemples tirés de l'épisode, qui guident la saisie : Friends ↔ Happy Days (`us-sitcom-hit`), la mort de Tupac ↔ la mort d'Elvis (`rapper-death` et `rock-star-death` sont deux types : l'épisode les rapproche, la base aussi doit le permettre, voir question ouverte), Zidane ↔ Platini (`french-football-star-peak`), Céline Dion ↔ Cher (`diva-hit`), Schwarzenegger ↔ Reagan (`actor-turned-politician`).

## Règles d'appariement des équivalences

Notations : `n_u` date de naissance de l'utilisateur, `n_j` date de naissance du jeune, `écart = n_j − n_u` (en jours).

- Pour un événement `E` « du jeune », l'événement « de l'utilisateur » correspondant est un événement `F` :
  - de **même type** que `E` ;
  - dont la date est la plus proche de `E.date − écart` ;
  - à moins de la **tolérance** de cette date cible (proposition : 2 ans, **À valider**).
- S'il existe plusieurs candidats dans la tolérance, on garde le plus proche de la date cible, puis la plus grande `importance`.
- L'âge de chacun au moment de son événement est affiché : `âge(n_j, E.date)` et `âge(n_u, F.date)`. Un événement antérieur à une naissance s'affiche « N ans avant sa naissance » / « N ans avant ta naissance ».
- Un événement ne s'apparie jamais avec lui-même.

## Règles du coup de vieux du jour

- Un événement « fête ses N ans » le jour où `jour` et `mois` sont ceux de sa date, avec `N ≥ 1`.
- Ordre d'affichage du jour :
  1. multiples de 10 (10, 20, 30… ans) ;
  2. multiples de 5 (5, 15, 25… ans) ;
  3. les autres ;
  
  puis, dans chaque groupe, `importance` décroissante, puis l'événement le plus récent d'abord (le plus récent fait le plus mal).
- Aucun anniversaire aujourd'hui : on affiche le prochain dans les 7 jours qui suivent (même ordre), annoncé « Dans N jours ».
- Événement du 29 février : fêté le 28 février les années non bissextiles (**À valider**).

## Volume visé

Environ **400 événements**, pour que presque chaque jour de l'année ait au moins un anniversaire, et que chaque type ait assez d'événements répartis des années 1950 à aujourd'hui pour que les équivalences marchent quel que soit l'écart d'âge. Une vérification automatique (test) compte les jours de l'année sans anniversaire et les types qui ont moins de 4 événements.

## Validation automatique (tests du module `core`)

- `id` uniques, au format attendu.
- `date` valide, au format `AAAA-MM-JJ`, dans le passé.
- `kind` et `type` connus, `importance` entre 1 et 3, `title.fr` non vide, `source` en `https://`.
- Statistiques de couverture (jours sans anniversaire, types trop pauvres) écrites dans le rapport de test, sans faire échouer la compilation.

## Questions ouvertes

1. **Types voisins :** l'épisode rapproche Tupac (rap) et Elvis (rock). Faut-il un type plus large `music-icon-death`, ou des types précis avec une liste de types « compatibles » ?
2. **Tolérance :** 2 ans conviennent-ils, ou faut-il une tolérance proportionnelle à l'écart (par exemple 10 % de l'écart) ?
3. **Événements sans date au jour près** (« l'été 1969 ») : les exclure (règle actuelle) ou les garder pour les équivalences seulement, sans anniversaire ?
4. **Événements récents :** faut-il un âge minimum (par exemple 1 an) pour qu'un événement entre dans les équivalences ?
