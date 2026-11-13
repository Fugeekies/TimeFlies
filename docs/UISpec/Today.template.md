## 1. 📅 Aujourd'hui (`TodayScreen`)

> Spécification de conception (13/11/2026), avant le code. Les règles de calcul sont celles de [`EVENTS_DATA.md`](../EVENTS_DATA.md) ; les points marqués **À valider** ne sont pas tranchés.

### Description
Écran d'ouverture : le **coup de vieux du jour**, présenté comme une feuille d'éphéméride (style de la V1 ; un Polaroïd viendra dans un jalon suivant, voir [`maquettes/`](../maquettes/README.md)). En dessous, les autres anniversaires du jour et ceux des 7 prochains jours.

### Spécifications fonctionnelles

#### Feuille du jour (coup de vieux principal)
* Événement choisi : le premier anniversaire du jour selon l'ordre de `EVENTS_DATA.md` (multiples de 10, puis de 5, puis les autres ; puis `importance` ; puis le plus récent).
* Contenu, de haut en bas :
  * eyebrow : date du jour en toutes lettres, majuscules (« LUNDI 2 NOVEMBRE 2026 ») ;
  * **grand chiffre** : nombre d'années (`15`), en `primary`, police `Big Shoulders Display`, suivi de « ans » (« an » pour 1) ;
  * « Il y a » n'est pas écrit : le grand chiffre suffit (décision du 13/11/2026) ;
  * **titre** de l'événement (« Intouchables ») ;
  * **nature et date** : « Sortie au cinéma · 2 novembre 2011 » (libellé du `kind`, voir `EVENTS_DATA.md`) ;
  * **âge de l'utilisateur** : « Tu avais 20 ans. » ; si l'événement est antérieur à sa naissance : « C'était 3 ans avant ta naissance. » ; l'année de sa naissance : « C'était l'année de ta naissance. » ; **sans mois et année de naissance** (premier lancement passé), la ligne n'est pas affichée (décision du 13/11/2026) ;
  * puce **« Chiffre rond »** si le nombre d'années est un multiple de 5 ;
  * pastille de la famille (couleur, voir `UI_DESIGN_SPEC.md` § 3).
* **Aucun anniversaire aujourd'hui** : la feuille affiche le premier anniversaire des 7 jours suivants, avec l'eyebrow « DANS 3 JOURS · JEUDI 5 NOVEMBRE » et « fêtera ses 25 ans » sous le titre au lieu de la nature seule.
* **Aucun anniversaire dans les 7 jours** (base trop pauvre) : la feuille affiche « Rien aujourd'hui. Profite, tu ne vieillis pas. » Ce cas ne devrait pas arriver avec la base complète.

#### « Aussi aujourd'hui »
* Liste des autres anniversaires du jour, dans le même ordre, en cartes compactes : nombre d'années (`15 ans`) en `primary`, titre, nature · année, « Tu avais N ans ».
* Masquée s'il n'y en a pas d'autre.

#### « Dans les prochains jours »
* Anniversaires des 7 jours suivants (sans ceux du jour), groupés par jour : en-tête « Mercredi 4 novembre · dans 2 jours », puis les cartes compactes avec « fêtera ses 18 ans ».
* 10 cartes au maximum, triées par jour puis dans l'ordre habituel.

#### Interactions
* Toucher une carte (ou la feuille) ouvre **Mon équivalent** sur cet événement, pour voir son équivalent dans l'autre génération (décision du 13/11/2026).
* Glisser vers le bas (Android) ou `F5` (Windows) recalcule l'écran ; il se recalcule aussi seul quand le jour change pendant que l'application est ouverte.

### Wireframe ASCII (téléphone)
```text
┌────────────────────────────────────────┐
│ TimeFlies                              │
├────────────────────────────────────────┤
│  ┌──────────────────────────────────┐  │
│  │      ◯                  ◯        │  │
│  │ - - - - - - - - - - - - - - - - -│  │
│  │  LUNDI 2 NOVEMBRE 2026           │  │
│  │                                  │  │
│  │   ██ ████                        │  │
│  │   ██ ██     ans                  │  │
│  │   ██ ████                        │  │
│  │                                  │  │
│  │  Intouchables              ● Ciné│  │
│  │  Sortie au cinéma · 2 novembre   │  │
│  │  2011                            │  │
│  │  Tu avais 20 ans.  [Chiffre rond]│  │
│  └──────────────────────────────────┘  │
│                                        │
│  DANS LES PROCHAINS JOURS              │
│  Mercredi 4 novembre · dans 2 jours    │
│  ┌──────────────────────────────────┐  │
│  │ 18 ans  L'élection de Barack     │  │
│  │         Obama · 2008             │  │
│  │         Tu avais 17 ans.         │  │
│  └──────────────────────────────────┘  │
│  Lundi 9 novembre · dans 7 jours       │
│  ┌──────────────────────────────────┐  │
│  │ 37 ans  La chute du mur de       │  │
│  │         Berlin · 1989            │  │
│  │         C'était 1 an avant ta    │  │
│  │         naissance.               │  │
│  └──────────────────────────────────┘  │
├────────────────────────────────────────┤
│  📅 Aujourd'hui  ⇄ Équiv.  🔍  ⚙        │
└────────────────────────────────────────┘
```

### Wireframe ASCII (fenêtre Windows)
```text
┌──────┬──────────────────────────────────────────────────────────────────────────┐
│  📅  │  ┌──────────────────────────────────┐   DANS LES PROCHAINS JOURS         │
│ Auj. │  │     ◯                    ◯       │   Mercredi 4 novembre · dans 2 j.  │
│      │  │ - - - - - - - - - - - - - - - - -│   18 ans  L'élection de Barack     │
│  ⇄   │  │ LUNDI 2 NOVEMBRE 2026            │           Obama · 2008             │
│ Équ. │  │                                  │           Tu avais 17 ans.         │
│      │  │   15 ans                         │                                    │
│  🔍  │  │                                  │   Lundi 9 novembre · dans 7 jours  │
│ Mon  │  │ Intouchables              ● Ciné │   37 ans  La chute du mur de       │
│      │  │ Sortie au cinéma · 2 nov. 2011   │           Berlin · 1989            │
│  ⚙   │  │ Tu avais 20 ans.  [Chiffre rond] │           1 an avant ta naissance. │
│ Rég. │  └──────────────────────────────────┘                                    │
└──────┴──────────────────────────────────────────────────────────────────────────┘
```
Au-dessus de 840 dp de large, la feuille et les listes passent sur deux colonnes.

Exemple calculé pour une naissance en mai 1991, le 2 novembre 2026.
