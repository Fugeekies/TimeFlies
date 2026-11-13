## 0. 👋 Premier lancement (`OnboardingScreen`)

> Spécification de conception (13/11/2026), avant le code. Écran **proposé** : l'application a besoin d'une date de naissance pour calculer les âges, elle doit donc la demander au premier lancement. **À valider.**

### Description
Écran unique affiché au premier lancement, avant Aujourd'hui. Il explique le principe en une phrase et demande la date de naissance de l'utilisateur et l'âge du jeune. Il ne revient plus ensuite : tout se modifie dans Réglages.

### Spécifications fonctionnelles

* **Titre :** « Le jeu le plus triste du monde ».
* **Texte d'introduction :** « Pour un jeune de 15 ans, Friends, c'est ce que Happy Days était pour toi. TimeFlies te le rappelle chaque jour. »
* **Champ « Ta date de naissance »** : sélecteur de date (Material 3 `DatePicker`), date par défaut vide. Dates acceptées : du 1er janvier 1920 à aujourd'hui moins 5 ans.
  * Lien secondaire « Je préfère donner mon année seulement » : remplace le sélecteur par une liste d'années. La date retenue est alors le **1er juillet** de cette année, milieu de l'année, pour que l'âge affiché ne se trompe au plus que d'un an (**À valider**).
* **Champ « Âge du jeune »** : curseur de 5 à 60 ans, **15 ans** par défaut, valeur affichée à côté. Doit rester inférieur à l'âge de l'utilisateur d'au moins 5 ans ; sinon le message « Le jeune doit avoir au moins 5 ans de moins que toi. » s'affiche et le bouton est désactivé.
* **Résumé en direct** sous les champs : « Tu as 35 ans, le jeune en a 15 : 20 ans d'écart. »
* **Bouton « C'est parti »** (principal) : enregistre les réglages, marque le premier lancement comme terminé et ouvre Aujourd'hui. Désactivé tant que la date de naissance manque.
* **Mention de confidentialité** en bas, en petit : « Ta date de naissance reste sur cet appareil. »
* Sur Android, le retour système quitte l'application sans rien enregistrer.

### Wireframe ASCII (téléphone)
```text
┌────────────────────────────────────────┐
│                                        │
│  Le jeu le plus triste                 │
│  du monde                              │
│                                        │
│  Pour un jeune de 15 ans, Friends,     │
│  c'est ce que Happy Days était pour    │
│  toi. TimeFlies te le rappelle chaque  │
│  jour.                                 │
│                                        │
│  TA DATE DE NAISSANCE                  │
│  [ 14/05/1991                     📅 ] │
│  Je préfère donner mon année seulement │
│                                        │
│  ÂGE DU JEUNE                  15 ans  │
│  ●━━━━━━━━○──────────────────────────  │
│                                        │
│  Tu as 35 ans, le jeune en a 15 :      │
│  20 ans d'écart.                       │
│                                        │
│  [          C'est parti           ]    │
│                                        │
│  Ta date de naissance reste sur cet    │
│  appareil.                             │
└────────────────────────────────────────┘
```

Sur Windows, le même contenu est centré dans une colonne de 480 dp de large.

### Questions ouvertes
1. Faut-il pouvoir passer cet écran (bouton « Plus tard ») ? Sans date de naissance, l'écran Aujourd'hui pourrait afficher les anniversaires sans « Tu avais N ans » et les équivalences resteraient vides.
2. Le 1er juillet pour « année seulement » convient-il ?
