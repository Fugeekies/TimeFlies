# Maquettes

Maquettes visuelles de TimeFlies, dessinées le 13/11/2026 à partir des spécifications de [`UISpec/`](../UISpec/). Elles montrent l'apparence visée ; en cas d'écart, ce sont les spécifications qui font foi.

- Chaque écran existe en **page HTML** (à ouvrir dans un navigateur ; les liens entre écrans fonctionnent) et en **image PNG**.
- Les polices viennent de Google Fonts : sans connexion, le navigateur affiche une police de secours. L'application, elle, embarquera ses polices.
- Exemple utilisé partout : naissance en mai 1991, jeune de 15 ans, nous sommes le lundi 2 novembre 2026. Les dates des exemples seront vérifiées lors de la saisie de la base.
- Version modifiable sur le canvas Claude : https://claude.ai/artifact/FA79nXm9tDxCgUj1mQMj6h (privé, à partager depuis son menu Partager).

## Styles d'affichage

| Éphéméride (V1) | Polaroïd (jalon futur) | VHS (idée) |
|---|---|---|
| ![Aujourd'hui, Éphéméride](android/Today.png) | ![Aujourd'hui, Polaroïd](android/TodayPolaroid.png) | ![Aujourd'hui, VHS](android/TodayVhs.png) |

Décision du 13/11/2026 : la V1 n'a que le style **Éphéméride**, le plus clair pour les menus. Le **Polaroïd** viendra dans un jalon suivant, le **VHS** peut-être plus tard.

## Android (style Éphéméride)

| Premier lancement | Équivalences | Mon équivalent | Réglages | Widget |
|---|---|---|---|---|
| ![Premier lancement](android/Onboarding.png) | ![Équivalences](android/Equivalences.png) | ![Mon équivalent](android/MyEquivalent.png) | ![Réglages](android/Settings.png) | ![Widget](android/Widget.png) |

La maquette Réglages montre le choix des trois styles : en V1, ce choix n'apparaît pas (voir [`Settings.template.md`](../UISpec/Settings.template.md)).

## Windows (style Éphéméride)

| Aujourd'hui | Équivalences |
|---|---|
| ![Aujourd'hui sur PC](pc/Today.png) | ![Équivalences sur PC](pc/Equivalences.png) |

| Réglages | Barre des tâches et notification |
|---|---|
| ![Réglages sur PC](pc/Settings.png) | ![Barre des tâches et notification](pc/Tray.png) |
