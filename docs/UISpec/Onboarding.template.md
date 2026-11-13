## 0. 👋 Premier lancement (`OnboardingScreen`)

> Spécification de conception (13/11/2026), avant le code. Écran validé le 13/11/2026 : il demande le mois et l'année de naissance, et peut être passé.

### Description
Écran unique affiché au premier lancement, avant Aujourd'hui. Il explique le principe en une phrase et demande le **mois et l'année** de naissance de l'utilisateur et l'âge du jeune. Il ne revient plus ensuite : tout se modifie dans Réglages.

### Spécifications fonctionnelles

* **Titre :** « Le jeu le plus triste du monde ».
* **Texte d'introduction :** « Pour un jeune de 15 ans, Friends, c'est ce que Happy Days était pour toi. TimeFlies te le rappelle chaque jour. »
* **Champ « Ton mois et ton année de naissance »** : deux listes, mois (janvier à décembre) et année (de 1920 à l'année en cours moins 5), vides par défaut. **Ni jour ni date complète** (décision du 13/11/2026) : moins intrusif, et le jour ne change presque rien aux âges affichés.
* **Champ « Âge du jeune »** : curseur de 5 à 60 ans, **15 ans** par défaut, valeur affichée à côté. Doit rester inférieur à l'âge de l'utilisateur d'au moins 5 ans ; sinon le message « Le jeune doit avoir au moins 5 ans de moins que toi. » s'affiche et le bouton est désactivé.
* **Résumé en direct** sous les champs, dès que le mois et l'année sont choisis : « Tu as 35 ans, le jeune en a 15 : 20 ans d'écart. »
* **Bouton « C'est parti »** (principal) : enregistre les réglages, marque le premier lancement comme terminé et ouvre Aujourd'hui. Désactivé tant que le mois ou l'année manque.
* **Bouton « Plus tard »** (secondaire, décision du 13/11/2026) : marque le premier lancement comme terminé sans date de naissance et ouvre Aujourd'hui. L'application fonctionne alors en mode réduit :
  * Aujourd'hui et le widget affichent les anniversaires sans la ligne « Tu avais N ans » ;
  * Équivalences et Mon équivalent affichent « Indique ton mois et ton année de naissance pour voir tes équivalences. » avec un bouton **« Ouvrir les réglages »**.
* **Mention de confidentialité** en bas, en petit : « Ton mois et ton année de naissance restent sur cet appareil. »
* Sur Android, le retour système quitte l'application sans rien enregistrer ; l'écran revient au lancement suivant.

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
│  TON MOIS ET TON ANNÉE DE NAISSANCE    │
│  [ Mai            ▼ ] [ 1991       ▼ ] │
│                                        │
│  ÂGE DU JEUNE                  15 ans  │
│  ●━━━━━━━━○──────────────────────────  │
│                                        │
│  Tu as 35 ans, le jeune en a 15 :      │
│  20 ans d'écart.                       │
│                                        │
│  [          C'est parti           ]    │
│               Plus tard                │
│                                        │
│  Ton mois et ton année de naissance    │
│  restent sur cet appareil.             │
└────────────────────────────────────────┘
```

Sur Windows, le même contenu est centré dans une colonne de 480 dp de large.
