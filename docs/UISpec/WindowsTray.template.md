## 6. 🖥️ Barre des tâches et notification Windows (`TimeFliesTray`)

> Spécification de conception (13/11/2026), avant le code. Windows ne permet pas facilement à une application d'ajouter un widget : l'icône de la zone de notification et la notification en tiennent lieu (décision du 13/11/2026).

### Description
Sur Windows, TimeFlies peut rester ouvert en arrière-plan, avec une icône dans la **zone de notification** (à côté de l'horloge). Elle affiche le coup de vieux du jour au survol et envoie une **notification** au lancement de TimeFlies, une fois par jour.

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

#### Une seule instance (décision du 16/11/2026)
* TimeFlies déjà lancé (au démarrage de Windows, par exemple), l'ouvrir de nouveau depuis le menu Démarrer ou un raccourci ne lance pas une seconde application : la fenêtre de la première revient au premier plan, sur Aujourd'hui si elle était cachée. Un lancement au démarrage de Windows alors que TimeFlies tourne déjà s'arrête sans rien afficher.
* Implémentation : un verrou `instance.lock` dans `%APPDATA%\TimeFlies` et un port local (boucle locale seulement, jamais le réseau) noté dans `instance.port`, par lequel le second lancement demande la fenêtre.

#### Notification au lancement (décision du 20/11/2026)
* Au **lancement de TimeFlies**, au démarrage de Windows ou à la main, une notification Windows : titre « 15 ans aujourd'hui », texte « Intouchables · Sortie au cinéma, 2 novembre 2011. Tu avais 20 ans. »
* **Une fois par jour au plus** : un second lancement le même jour n'envoie rien. Une sortie de veille n'est pas un lancement : rien n'est envoyé.
* Plus d'heure à choisir ni de vérification chaque minute (elle remplace la notification du matin à 9:00) : plus simple, et TimeFlies ne fait rien tant qu'il attend dans la barre des tâches. L'infobulle se met à jour à minuit et quand les réglages changent.
* Sans anniversaire du jour : titre « Dans 3 jours », texte avec le prochain anniversaire.
* Cliquer sur la notification ouvre la fenêtre sur Aujourd'hui.
* Implémentation : notification de la zone de notification, sans bibliothèque en plus : une notification simple, titre et texte (décision du 13/11/2026). Au jalon 10, l'icône est écrite directement sur le `TrayIcon` d'AWT (`displayMessage`), que le `Tray` de Compose Desktop enveloppe : celui de Compose n'ouvre la fenêtre qu'au double clic, alors que cette spec demande un clic gauche simple. AWT signale de la même façon un clic sur la notification et un double clic sur l'icône : les deux ouvrent Aujourd'hui.
* Sans anniversaire dans les 7 jours : titre « TimeFlies », texte « Rien aujourd'hui. Profite, tu ne vieillis pas. » (la phrase d'Aujourd'hui).

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

Notification au lancement (coin inférieur droit de l'écran)
┌─────────────────────────────────────────────┐
│ ⏳ TimeFlies                                 │
│ 15 ans aujourd'hui                           │
│ Intouchables · Sortie au cinéma,             │
│ 2 novembre 2011. Tu avais 20 ans.            │
└─────────────────────────────────────────────┘
```

Exemple pour une naissance en mai 1991, le 2 novembre 2026.

