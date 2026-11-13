## 5. 🧩 Widget Android (`TimeFliesWidget`)

> Spécification de conception (13/11/2026), avant le code.

### Description
Widget de l'écran d'accueil qui affiche le **coup de vieux du jour**, le même que la feuille de l'écran Aujourd'hui, en version miniature de la feuille d'éphéméride. Construit avec Jetpack Glance.

### Spécifications fonctionnelles

#### Contenu
* Même événement que la feuille d'Aujourd'hui (même calcul `Anniversaries`) : nombre d'années en grand (`primary`), titre, année et nature, « Tu avais N ans ».
* Aucun anniversaire aujourd'hui : le premier des 7 prochains jours, précédé de « Dans 3 jours ».
* Premier lancement pas encore fait : « Ouvre TimeFlies pour commencer. »
* Sans mois et année de naissance (premier lancement passé) : l'anniversaire s'affiche sans « Tu avais N ans ».
* Le widget suit le style d'affichage de l'application (Polaroïd en V1 : âge écrit à la main sur un petit Polaroïd ; Éphéméride ensuite). Les maquettes ci-dessous sont dessinées en Éphéméride.

#### Tailles (décision du 13/11/2026)
| Taille | Contenu |
|---|---|
| **2 × 2** (petit) | nombre d'années + titre (2 lignes au plus, coupé par « … ») |
| **4 × 2** (par défaut) | eyebrow de la date, nombre d'années, titre, nature · année, « Tu avais N ans » |
| **4 × 3 et plus** | idem, plus les deux anniversaires suivants du jour ou des prochains jours en lignes compactes |

* Le widget se redimensionne librement ; la mise en page la plus riche qui tient est choisie (`SizeMode.Responsive`).
* Couleurs du thème de l'application, clair ou sombre selon le système. Sur Android 12 et plus, coins arrondis du système.

#### Mise à jour
* Recalcul **chaque jour juste après minuit** (heure locale), par une tâche WorkManager périodique, plus un recalcul au démarrage du téléphone, au changement de date ou de fuseau horaire, et quand les réglages changent dans l'application.
* Aucun accès réseau : la base est dans l'application.
* Si Android retarde la tâche (économie de batterie), le widget se met à jour au plus tard à l'ouverture suivante de l'application.

#### Interaction
* Toucher le widget ouvre l'application sur **Aujourd'hui**.
* **Bouton « suivant »** (›, décision du 13/11/2026), en bas à droite : affiche l'anniversaire suivant du jour, dans l'ordre habituel, puis revient au premier après le dernier. Masqué s'il n'y a qu'un anniversaire. Le widget revient au coup de vieux principal à chaque nouveau jour. En 4 × 3 et plus, les lignes compactes suivent le défilement.

### Wireframe ASCII
```text
4 × 2 (par défaut)                          2 × 2
┌──────────────────────────────────────┐    ┌───────────────────┐
│ LUNDI 2 NOVEMBRE                     │    │  15 ans           │
│                                      │    │                   │
│  15 ans    Intouchables              │    │  Intouchables     │
│            Sortie au cinéma · 2011   │    │                   │
│            Tu avais 20 ans.        › │    │                 › │
└──────────────────────────────────────┘    └───────────────────┘
```

Exemple pour une naissance en mai 1991, le 2 novembre 2026.

