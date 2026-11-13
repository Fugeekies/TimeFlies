# TimeFlies

> « Pour un gars qui a 15 piges aujourd'hui, Friends, c'est exactement ce que Happy Days était pour nous. »
> Inspiré de l'épisode *Bloqués #108 - On est les vieux des jeunes*.

TimeFlies est une application **Android et Windows** qui donne des coups de vieux :

- **Le coup de vieux du jour** : « Ça fait 15 ans qu'*Intouchables* est sorti. Tu avais 20 ans. » Sur Android, il s'affiche dans un widget de l'écran d'accueil ; sur Windows, dans une icône de la barre des tâches avec une notification le matin.
- **Les équivalences** : selon ton âge et celui d'un jeune, les événements qui ont la même place dans vos deux vies (Friends ↔ Happy Days, la mort de Tupac ↔ la mort d'Elvis, Zidane ↔ Platini…).
- **Mon équivalent** : choisis un événement et trouve son équivalent pour l'autre génération.

Aucun compte, aucune connexion : les événements sont intégrés à l'application et tes réglages restent sur ton appareil.

## État

Projet en démarrage : aucune version n'est encore publiée. Les spécifications sont dans [`docs/`](docs/) et la suite dans la [feuille de route](docs/ROADMAP.md).

## Installation (à venir)

Chaque version publiera dans l'onglet **Releases** :

- l'**APK** Android, à télécharger depuis le téléphone ;
- l'**installeur Windows** (`.msi`).

## Technique

Kotlin et Compose Multiplatform : un seul code pour Android et Windows. Les versions sont compilées par GitHub Actions.

## Licence

[GNU GPL v3](LICENSE).
