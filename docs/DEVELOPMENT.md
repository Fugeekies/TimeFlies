# Guide développeur - TimeFlies

> Mis à jour au jalon 1 « Création de la solution » (14/11/2026).

## Outils

- **JDK 17 ou plus** (le projet compile avec un JDK 21) : pointer `JAVA_HOME` dessus, ou laisser l'IDE fournir le sien.
- **SDK Android** avec la plateforme **37** (`platforms;android-37.0`), exigée par Compose Multiplatform 1.12. Son chemin va dans `local.properties`, à la racine, jamais commité :

  ```properties
  sdk.dir=C\:/chemin/vers/android-sdk
  ```
- Gradle n'est pas à installer : le wrapper (`gradlew`) télécharge la bonne version.
- **Pendant le développement** : tout se compile et se lance en local, en débogage, depuis l'IDE ou Gradle.
- **Publier une version** : à la fin du projet (jalon 13), GitHub Actions produira l'APK et le `.msi` (voir [`ARCHITECTURE.md`](ARCHITECTURE.md), « Distribution »).
- **Développer et déboguer** : un IDE en local. **Android Studio** (gratuit) est recommandé : il gère Kotlin Multiplatform, l'émulateur, Logcat et la cible PC. IntelliJ IDEA convient aussi.

## Commandes

```bash
./gradlew :desktopApp:run            # lance la version PC
./gradlew :androidApp:assembleDebug  # compile l'APK de débogage
./gradlew :androidApp:installDebug   # installe la version de débogage sur le téléphone ou l'émulateur
./gradlew check                      # tests (à partir du jalon 2)
```

Au lancement de la version PC, Skiko (le moteur de rendu de Compose) peut afficher « Failed to choose DirectX12 adapter » puis « Fallback to next API » : il bascule simplement vers un autre moteur graphique, sans conséquence.

## Déboguer

### 1. La boucle rapide : la version PC
- `./gradlew :desktopApp:run`, ou Run/Debug dans l'IDE : application JVM classique, points d'arrêt et pas à pas, sans émulateur.
- Écrans et règles étant partagés, la plupart des bugs se reproduisent et se corrigent sur PC.
- **Compose Hot Reload** (JetBrains) pour voir un écran modifié sans relancer, sur la cible PC : version compatible à vérifier au jalon 1 « Création de la solution ».

### 2. Les règles métier : les tests
- `domain` est en Kotlin pur et la date du jour est injectée (`Clock`) : un bug de calcul (âge, chiffre rond, 29 février, appariement) se reproduit dans un **test unitaire sur une date fixe** et se débogue avec des points d'arrêt dans le test.
- Les tests tournent sur la JVM en quelques secondes.

### 3. Android : téléphone ou émulateur
- Téléphone en USB ou en Wi-Fi, ou émulateur d'Android Studio : débogueur, **Logcat**, **App Inspection** (tâches WorkManager du widget : état et prochaine exécution).
- La version de débogage a son propre identifiant (suffixe `.debug`) : elle s'installe à côté de la version publiée sans l'écraser.
- Widget : aperçu dans l'IDE avec des données fictives, et mise à jour forcée depuis le panneau de débogage.

### 4. Aperçus d'écrans
Aperçus `@Preview` nourris de données fictives, pour voir chaque écran dans tous ses états sans lancer l'application : vide, sans mois et année de naissance, pas d'anniversaire aujourd'hui, texte agrandi à 200 %.

### 5. Panneau de débogage (versions de débogage seulement)
Une destination « Débogage », présente uniquement dans les versions de débogage (Android) ou avec l'option de lancement `-Dtimeflies.debug=true` (Windows), jamais dans une release :
- **changer la date du jour** (fausse horloge à la place de `SystemClock`) : 29 février, jour sans anniversaire, passage de minuit ;
- forcer le mois et l'année de naissance, l'âge du jeune et la région ;
- déclencher tout de suite la notification Windows ou la mise à jour du widget ;
- statistiques de la base : nombre d'événements, jours de l'année sans anniversaire, types trop pauvres, entrées écartées par la validation.

### 6. Logs et plantages
- Kermit : Logcat sur Android ; console et `%APPDATA%\TimeFlies\logs\` sur Windows.
- **Aucune donnée personnelle** dans les logs (ni mois ni année de naissance).
- Pas d'outil de rapport de plantage en ligne (l'application n'a aucune connexion) : la trace est écrite dans les logs, à joindre à la main à une issue GitHub.
