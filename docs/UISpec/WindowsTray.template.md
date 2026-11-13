## 6. 🖥️ Barre des tâches et notification Windows (`TimeFliesTray`)

> Spécification de conception (13/11/2026), avant le code. Windows ne permet pas facilement à une application d'ajouter un widget : l'icône de la zone de notification et la notification du matin en tiennent lieu (décision du 13/11/2026). Les points marqués **À valider** ne sont pas tranchés.

### Description
Sur Windows, TimeFlies peut rester ouvert en arrière-plan, avec une icône dans la **zone de notification** (à côté de l'horloge). Elle affiche le coup de vieux du jour au survol et envoie une **notification** chaque matin.

### Spécifications fonctionnelles

#### Icône de la zone de notification (`Tray` de Compose Desktop)
* Présente tant que l'application tourne, fenêtre ouverte ou cachée.
* **Infobulle au survol** : « TimeFlies — Intouchables : 15 ans aujourd'hui ». Sans anniversaire du jour : « TimeFlies — Dans 3 jours : … fêtera ses 25 ans ». Mise à jour quand le jour change.
* **Clic gauche** : ouvre la fenêtre sur Aujourd'hui (ou la ramène au premier plan).
* **Menu du clic droit** :
  * « Ouvrir TimeFlies » ;
  * « Coup de vieux du jour » : affiche la notification du jour tout de suite ;
  * « Réglages » : ouvre la fenêtre sur Réglages ;
  * « Quitter » : ferme la fenêtre et l'application.

#### Fermeture de la fenêtre
* Avec « Garder TimeFlies dans la barre des tâches quand je ferme la fenêtre » coché (par défaut), fermer la fenêtre la **cache** : l'application continue dans la zone de notification. La première fois, une notification l'explique : « TimeFlies reste dans la barre des tâches. Clic droit sur l'icône › Quitter pour le fermer. »
* Décoché : fermer la fenêtre quitte l'application.

#### Lancement au démarrage de Windows
* Avec la case cochée (Réglages), TimeFlies démarre avec Windows **fenêtre cachée**, seulement dans la zone de notification.

#### Notification du matin
* Chaque jour à l'heure choisie (9:00 par défaut), une notification Windows : titre « 15 ans aujourd'hui », texte « Intouchables · Sortie au cinéma, 2 novembre 2011. Tu avais 20 ans. »
* Sans anniversaire du jour : titre « Dans 3 jours », texte avec le prochain anniversaire.
* PC éteint ou en veille à l'heure prévue : la notification part au premier réveil de la journée, une seule fois par jour.
* Cliquer sur la notification ouvre la fenêtre sur Aujourd'hui.
* Implémentation : notification de la zone de notification (`TrayState.sendNotification`), sans service externe (**À valider** : une notification Windows native plus riche demanderait une bibliothèque en plus).

### Wireframe ASCII
```text
Infobulle au survol de l'icône             Menu du clic droit
                ┌──────────────────────┐   ┌──────────────────────────┐
                │ TimeFlies —          │   │ Ouvrir TimeFlies         │
                │ Intouchables :       │   │ Coup de vieux du jour    │
                │ 15 ans aujourd'hui   │   │ Réglages                 │
                └──────────────────────┘   ├──────────────────────────┤
   ^  🕒 ⏳ 🔊  09:00                       │ Quitter                  │
                                           └──────────────────────────┘

Notification du matin (coin inférieur droit de l'écran)
┌─────────────────────────────────────────────┐
│ ⏳ TimeFlies                                 │
│ 15 ans aujourd'hui                           │
│ Intouchables · Sortie au cinéma,             │
│ 2 novembre 2011. Tu avais 20 ans.            │
└─────────────────────────────────────────────┘
```

Exemple pour une naissance en mai 1991, le 2 novembre 2026.

### Questions ouvertes
1. Une notification simple de la zone de notification suffit-elle, ou veux-tu une notification Windows native plus riche (image, bouton « Voir l'équivalent ») ?
