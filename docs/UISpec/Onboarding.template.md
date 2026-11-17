## 0. 👋 Premier lancement (`OnboardingScreen`)

> Spécification de conception (13/11/2026), avant le code. Écran validé le 13/11/2026 : il demande le mois et l'année de naissance, et peut être passé.

### Description
Écran unique affiché au premier lancement, avant Aujourd'hui. Il explique le principe en une phrase et demande le **mois et l'année** de naissance de l'utilisateur, et rien d'autre : l'âge de l'autre génération ne sert qu'à Équivalences et se règle là-bas (décision du 15/11/2026). Il ne revient plus ensuite : le mois et l'année se modifient dans Réglages.

### Spécifications fonctionnelles

* **Titre :** « Ça ne nous rajeunit pas » (décision du 17/11/2026, qui remplace « Le jeu le plus triste du monde »).
* **Texte d'introduction :** « Chaque jour, TimeFlies te rappelle depuis combien de temps tes films, séries, chansons et grands moments sont arrivés, et l'âge que tu avais. Pour commencer, il nous faut ton mois et ton année de naissance. » (décision du 17/11/2026)
* **Champ « Ton mois et ton année de naissance »** : deux listes, mois (janvier à décembre) et année (de 1920 à l'année en cours moins 5), vides par défaut. **Ni jour ni date complète** (décision du 13/11/2026) : moins intrusif, et le jour ne change presque rien aux âges affichés.
* **« Ce qui t'intéresse »** (décisions du 16/11/2026 et du 17/11/2026) : « Décoche ce qui ne te parle pas. Tu pourras affiner plus tard dans Réglages › Événements. », puis **une ligne par thème** présent dans la base, toutes cochées : la pastille de couleur de la famille, son nom, ce qu'il contient en quelques mots (« Blockbusters, films d'animation, comédies françaises, sagas ») et son nombre d'événements, avec une case à cocher ; toute la ligne se touche. Décocher un thème l'écarte entièrement. **Pas de choix par pays** sur cet écran (décision du 17/11/2026, qui remplace les pastilles d'origine) : il se fait dans Réglages › Événements. Enregistré avec « C'est parti » comme avec « Plus tard ».
* **Bouton « C'est parti »** (principal) : enregistre le mois et l'année de naissance, marque le premier lancement comme terminé et ouvre Aujourd'hui. Désactivé tant que le mois ou l'année manque.
* **Bouton « Plus tard »** (secondaire, décision du 13/11/2026) : marque le premier lancement comme terminé sans date de naissance et ouvre Aujourd'hui. L'application fonctionne alors en mode réduit :
  * Aujourd'hui et le widget affichent les anniversaires sans la ligne « Tu avais N ans » ;
  * Équivalences affiche « Indique ton mois et ton année de naissance pour voir tes équivalences. » avec un bouton **« Ouvrir les réglages »** ; « Quel âge ? » garde sa recherche et affiche « Indique ton mois et ton année de naissance pour voir ton âge à cette date. » à la place des âges. Les deux proposent aussi « Choisir des âges ».
* **Mention de confidentialité** en bas, en petit : « Ton mois et ton année de naissance restent sur cet appareil. »
* Sur Android, le retour système quitte l'application sans rien enregistrer ; l'écran revient au lancement suivant.

### Wireframe ASCII (téléphone)
```text
┌────────────────────────────────────────┐
│                                        │
│  Ça ne nous rajeunit                   │
│  pas                                   │
│                                        │
│  Chaque jour, TimeFlies te rappelle    │
│  depuis combien de temps tes films,    │
│  séries, chansons et grands moments    │
│  sont arrivés, et l'âge que tu avais.  │
│  Pour commencer, il nous faut ton mois │
│  et ton année de naissance.            │
│                                        │
│  TON MOIS ET TON ANNÉE DE NAISSANCE    │
│  [ Mai            ▼ ] [ 1991       ▼ ] │
│                                        │
│  CE QUI T'INTÉRESSE                    │
│  Décoche ce qui ne te parle pas. Tu    │
│  pourras affiner plus tard dans        │
│  Réglages › Événements.                │
│  ● Séries & télé                  [x]  │
│    Sitcoms, séries cultes,             │
│    téléréalité… · N événements         │
│  ● Cinéma                         [x]  │
│    Blockbusters, films d'animation,    │
│    comédies françaises, sagas          │
│    · 76 événements                     │
│  ● Musique                        [ ]  │
│    …                                   │
│                                        │
│  [          C'est parti           ]    │
│               Plus tard                │
│                                        │
│  Ton mois et ton année de naissance    │
│  restent sur cet appareil.             │
└────────────────────────────────────────┘
```

Sur Windows, le même contenu est centré dans une colonne de 480 dp de large.
