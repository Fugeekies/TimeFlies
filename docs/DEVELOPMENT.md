# Guide développeur - TimeFlies

> Mis à jour au jalon 1 « Création de la solution » (14/11/2026).

## Outils

- **JDK 21** : les modules demandent une toolchain Java 21 (`jvmToolchain(21)`). Gradle cherche ce JDK aux emplacements habituels ; s'il est installé ailleurs, le déclarer dans le `gradle.properties` **de l'utilisateur** (`%USERPROFILE%\.gradle\gradle.properties`, jamais dans le dépôt), sinon un éditeur qui lance Gradle avec son propre Java (extension Kotlin de VS Code, IDE) échoue avec « Cannot find a Java installation… languageVersion=21 » :

  ```properties
  org.gradle.java.installations.paths=C:/chemin/vers/jdk-21
  ```
- **SDK Android** avec la plateforme **37** (`platforms;android-37.0`), exigée par Compose Multiplatform 1.12. Son chemin va dans `local.properties`, à la racine, jamais commité :

  ```properties
  sdk.dir=C\:/chemin/vers/android-sdk
  ```
- Gradle n'est pas à installer : le wrapper (`gradlew`) télécharge la bonne version.
- **Pendant le développement** : tout se compile et se lance en local, en débogage, depuis l'IDE ou Gradle.
- **CI** : chaque PR prête (pas en brouillon) lance les tests, la compilation des deux applications et les benchmarks sur GitHub Actions (`.github/workflows/ci.yml`).
- **Publier une version** : workflow « Release » lancé à la main, qui produit l'APK et l'installeur Windows (voir « Releases » plus bas et [`ARCHITECTURE.md`](ARCHITECTURE.md), « Distribution »).
- **Développer et déboguer** : un IDE en local. **Android Studio** (gratuit) est recommandé : il gère Kotlin Multiplatform, l'émulateur, Logcat et la cible PC. IntelliJ IDEA convient aussi.

## Commandes

```bash
./gradlew :desktopApp:run            # lance la version PC
./gradlew :androidApp:assembleDebug  # compile l'APK de débogage
./gradlew :androidApp:installDebug   # installe la version de débogage sur le téléphone ou l'émulateur
./gradlew check                      # tous les tests, dont la validation des fichiers d'événements
./gradlew :data:desktopTest          # tests de la couche data seulement (affiche le rapport de couverture)
./gradlew :benchmarks:benchmark      # benchmarks (environ 1 min, hors de check)
```

Les benchmarks mesurent le chargement de la base et le calcul de chaque page, sur les fichiers réels et sur des jeux synthétiques de 500 à 10 000 événements. Le rapport s'écrit dans `src/benchmarks/build/reports/benchmarks/performance-report.md` ; une PR qui touche au chargement des données ou au calcul d'une page en committe une copie dans `docs/benchmarks/`. Les durées dépendent de la machine : comparer des rapports faits sur le même PC (ou sur la CI).

Le test `RealEventFilesTest` lit les vrais fichiers de `src/ui/src/commonMain/composeResources/files/events/` : une entrée invalide le fait échouer, avec la liste des raisons ; la couverture (événements par thème, jours sans anniversaire, types trop pauvres) s'affiche dans la sortie du test.

Au lancement de la version PC, Skiko (le moteur de rendu de Compose) peut afficher « Failed to choose DirectX12 adapter » puis « Fallback to next API » : il bascule simplement vers un autre moteur graphique, sans conséquence.

## Déboguer

### 1. La boucle rapide : la version PC
- `./gradlew :desktopApp:run`, ou Run/Debug dans l'IDE : application JVM classique, points d'arrêt et pas à pas, sans émulateur.
- Écrans et règles étant partagés, la plupart des bugs se reproduisent et se corrigent sur PC.
- **Compose Hot Reload** (JetBrains, version 1.2.0) pour voir un écran modifié sans relancer, sur la cible PC : `./gradlew :desktopApp:hotRun --auto`, ou le bouton dédié de l'IDE. Il demande le **JetBrains Runtime 21** (un JDK modifié par JetBrains), fourni avec Android Studio et IntelliJ : sans lui, la tâche échoue avec « Failed to find suitable JetBrains Runtime 21 installation ».

#### Avec VS Code
- Extension **Kotlin by JetBrains** (`JetBrains.kotlin-server`, officielle, en alpha) : édition Kotlin à jour et débogueur `intellij_jvm`. Le support complet de Kotlin Multiplatform n'y est pas encore : on lance donc l'app avec Gradle, puis on attache le débogueur. L'ancienne extension `fwcd.kotlin` n'est plus maintenue et ne gère pas Kotlin 2.
- `./gradlew :desktopApp:run --debug-jvm` démarre l'app et attend un débogueur sur le port 5005 (« Listening for transport dt_socket at address: 5005 »).
- `.vscode/` n'est pas commité (chemin du JDK propre à chaque PC). Configuration type :

  ```jsonc
  // .vscode/launch.json
  { "name": "Déboguer TimeFlies PC", "type": "intellij_jvm", "request": "attach",
    "hostName": "localhost", "port": 5005, "timeout": 30000,
    "preLaunchTask": "TimeFlies PC : lancer en attente du débogueur" }
  ```

  avec, dans `.vscode/tasks.json`, une tâche de fond `./gradlew :desktopApp:run --debug-jvm '-Dtimeflies.debug=true'` (`isBackground`, fin détectée sur « Listening for transport ») et `JAVA_HOME` dans `options.env`. L'option `-Dtimeflies.debug=true` active le panneau de débogage : F5 ouvre ainsi l'application avec la destination « Débogage ». Les apostrophes sont nécessaires : VS Code lance les tâches dans PowerShell, qui coupe sinon l'option au point (`Task '.debug=true' not found`).
- Le débogage Android avec points d'arrêt et Compose Hot Reload passent par Android Studio.

### 2. Les règles métier : les tests
- `domain` est en Kotlin pur et la date du jour est injectée (`Clock`) : un bug de calcul (âge, chiffre rond, 29 février, appariement) se reproduit dans un **test unitaire sur une date fixe** et se débogue avec des points d'arrêt dans le test.
- Les tests tournent sur la JVM en quelques secondes.

### 3. Android : téléphone ou émulateur
- Téléphone en USB ou en Wi-Fi, ou émulateur : débogueur, **Logcat**, **App Inspection** d'Android Studio (tâches WorkManager du widget : état et prochaine exécution).
- **Émulateur sans Android Studio** (mis en place le 16/11/2026) : le SDK de compilation (`sdk.dir`) n'est souvent pas modifiable sans droits administrateur ; l'émulateur vit dans un SDK utilisateur à part, `%LOCALAPPDATA%\Android\Sdk`.
  1. Une fois, en administrateur : `Enable-WindowsOptionalFeature -Online -FeatureName HypervisorPlatform -All`, puis redémarrer (accélération WHPX ; `emulator -accel-check` doit répondre « WHPX … is installed and usable »).
  2. Une fois : `sdkmanager --sdk_root=%LOCALAPPDATA%\Android\Sdk "emulator" "platform-tools" "cmdline-tools;latest" "system-images;android-36;google_apis;x86_64"`, puis, avec l'`avdmanager` de ce SDK (`ANDROID_SDK_ROOT` pointé dessus) : `avdmanager create avd --name TimeFlies_Pixel --package "system-images;android-36;google_apis;x86_64" --device pixel_7`.
  3. À chaque fois : `& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd TimeFlies_Pixel`, puis `./gradlew :androidApp:installDebug`.
- La version de débogage a son propre identifiant (suffixe `.debug`) : elle s'installe à côté de la version publiée sans l'écraser.
- Widget : mise à jour forcée depuis le panneau de débogage. La tâche de minuit (`widget-midnight`) se voit dans **App Inspection › Background Task Inspector** d'Android Studio, ou avec `adb shell dumpsys jobscheduler | grep timeflies`.

### 4. Aperçus d'écrans
Prévus, pas encore écrits : des aperçus `@Preview` nourris de données fictives, pour voir chaque écran dans tous ses états sans lancer l'application (vide, sans mois et année de naissance, pas d'anniversaire aujourd'hui, texte agrandi à 200 %). En attendant, le panneau de débogage (fausse date, réglages forcés, thème) donne ces états dans l'application.

### 5. Panneau de débogage (versions de débogage seulement)
Une destination « Débogage » (icône de clé), présente uniquement dans les versions de débogage (Android, détectées par le drapeau `debuggable` de l'APK) ou avec l'option de lancement `-Dtimeflies.debug=true` (Windows : `./gradlew :desktopApp:run '-Dtimeflies.debug=true'`, avec les apostrophes dans PowerShell ; seule la tâche `run` reçoit l'option), jamais dans une release. Elle apparaît une fois le premier lancement passé.
- **changer la date du jour** (`AdjustableClock` à la place de `SystemClock`) : un jour de plus ou de moins, 29 février 2028, 28 février 2027 (année non bissextile, où les événements du 29 février sont fêtés), prochain jour sans anniversaire, une date saisie (`AAAA-MM-JJ`), retour à l'horloge système. Aujourd'hui et Réglages se recalculent aussitôt ; une fausse date ne change pas toute seule à minuit ;
- forcer les réglages sans les contrôles des écrans : mois et année de naissance (ou les effacer), région (code de pays à deux lettres), **revoir le premier lancement**, remettre les réglages par défaut. L'âge de l'autre génération n'y est pas (décision du 15/11/2026) ;
- **forcer le thème** clair ou sombre, ou revenir au thème du système, pour essayer les deux palettes du Polaroïd sans changer le réglage du système. Pas enregistré : chaque lancement repart sur le thème du système. Sur Android, la barre d'état et le fond de la fenêtre suivent toujours le système ;
- statistiques de la base : nombre d'événements, par thème, jours de l'année sans anniversaire, types trop pauvres, entrées écartées par la validation ;
- Windows : **envoyer tout de suite la notification du matin** (jalon 10), sans marquer le jour : la vraie notification du matin part quand même à son heure.
- Android : **mettre à jour le widget** tout de suite (jalon 11), avec la date du jour du panneau, vraie ou fausse. Changer la fausse date ou les réglages met aussi le widget à jour, comme en vrai.

### 5 bis. Icône de la barre des tâches et démarrage Windows (jalon 10)
- L'icône et la notification marchent aussi lancées depuis Gradle. En débogage (`-Dtimeflies.debug=true`, donc F5), **fermer la fenêtre quitte l'application** : cachée dans la barre des tâches, elle garderait le port 5005 du débogueur et le lancement suivant échouerait aussitôt. Lancée sans cette option, elle suit le réglage « Garder TimeFlies dans la barre des tâches » : clic gauche sur l'icône pour la rouvrir, clic droit › Quitter pour l'arrêter.
- Si F5 ne lance plus rien, une ancienne instance tient sans doute le port : `Get-NetTCPConnection -LocalPort 5005` donne son numéro de processus, à arrêter avec `Stop-Process -Id <numéro>`.
- **Une seule instance** (décision du 16/11/2026) : un TimeFlies installé et lancé (dans la barre des tâches) empêche le lancement de débogage, qui se contente de ramener sa fenêtre et s'arrête. Quitter TimeFlies (clic droit sur l'icône › Quitter) avant F5. Le verrou est `%APPDATA%TimeFliesinstance.lock`.
- **Lancement au démarrage** : la valeur `TimeFlies` de `HKCU\Software\Microsoft\Windows\CurrentVersion\Run` n'est écrite (ou retirée) que par l'application installée, qui connaît le chemin de son exécutable (`jpackage.app-path`) ; lancée depuis Gradle, elle l'écrit seulement dans les logs. Windows la lance avec l'argument `--background` : fenêtre cachée, icône seule. Vérifier avec `reg query HKCU\Software\Microsoft\Windows\CurrentVersion\Run /v TimeFlies`.
- Notification du matin : vérifiée chaque minute ; la date du dernier envoi est dans `settings.json` (`windows.lastNotificationDate`). La supprimer à la main, ou choisir une heure déjà passée après avoir effacé cette date, fait repartir la notification du jour.

### 6. Logs et plantages
- Kermit : Logcat sur Android ; console et `%APPDATA%\TimeFlies\logs\timeflies-AAAA-MM-JJ.log` sur Windows (un fichier par jour, les 7 derniers jours gardés). Niveau `Debug` en débogage, `Info` sinon.
- **Aucune donnée personnelle** dans les logs (ni mois ni année de naissance) : un fichier de réglages illisible est signalé par le nom de l'erreur seulement, son message pouvant citer le fichier.
- Pas d'outil de rapport de plantage en ligne (aucune donnée n'est envoyée, la seule connexion cherche les nouvelles versions) : une exception non rattrapée est écrite dans les logs (étiquette `Crash`) avant l'arrêt, à joindre à la main à une issue GitHub.
- La console Windows peut afficher les accents de travers (page de code) ; le fichier est en UTF-8.

## Releases

Méthode reprise de LMU-DeltaRacing (décision du 16/11/2026) : pas de WiX, un installeur **Inno Setup**.

### Publier une version
1. Sur GitHub : **Actions › Release › Run workflow**, branche `main`, type de version **Majeur**, **Mineur** ou **Patch** (la première version, sans tag, devient `v0.1.0` en Mineur).
2. Le workflow lance les tests, compile l'APK signée et l'installeur Windows, puis crée le tag `vX.Y.Z` et la release avec les deux fichiers et les notes tirées des PR fusionnées.
3. Pour essayer sans rien publier : décocher **Publier** ; les deux fichiers restent en artefacts du run (une branche autre que `main` est alors permise).

Avant de publier, passer la section `## [Unreleased]` du `CHANGELOG.md` sous le numéro de la version, dans une PR.

### Construire en local
```bash
./gradlew :androidApp:assembleRelease -Ptimeflies.version=0.1.0     # APK réduite par R8, signée avec la clé de débogage sans les variables de la clé
./gradlew :desktopApp:createDistributable -Ptimeflies.version=0.1.0 # src/desktopApp/build/compose/binaries/main/app/TimeFlies/
iscc /DAppVersion=0.1.0 scripts\installer.iss                       # output\TimeFlies-Setup.exe (Inno Setup 6 installé)
```
Sans `-Ptimeflies.version`, l'application affiche « version de développement » et ne cherche jamais de nouvelle version. Avec une version plus ancienne que la dernière publiée (`-Ptimeflies.version=0.0.9`), le bandeau « Nouvelle version » apparaît sur Aujourd'hui : c'est ainsi qu'il se teste.

L'APK de release passe par **R8** (réduction du code, décision du 16/11/2026 : 11 Mo → 2,7 Mo). Après un changement de bibliothèque, l'installer sur l'émulateur (`adb install -r src/androidApp/build/outputs/apk/release/androidApp-release.apk`) et parcourir les écrans et le widget : une classe retirée à tort ne se voit qu'à l'exécution. Les règles propres à TimeFlies sont dans `src/androidApp/proguard-rules.pro`.

### Clé de signature Android (une seule fois)
La même clé doit signer toutes les versions : sans elle, Android refuse d'installer une mise à jour par-dessus. Elle n'est jamais dans le dépôt.
1. Générer la clé (validité de 30 ans), hors du dépôt :
   ```bash
   keytool -genkeypair -v -keystore timeflies-release.keystore -alias timeflies -keyalg RSA -keysize 4096 -validity 10950
   ```
2. La ranger avec ses mots de passe dans un gestionnaire de mots de passe ou une sauvegarde hors du PC.
3. La donner aux secrets du dépôt :
   ```bash
   base64 -w0 timeflies-release.keystore | gh secret set ANDROID_KEYSTORE_BASE64
   gh secret set ANDROID_KEYSTORE_PASSWORD
   gh secret set ANDROID_KEY_ALIAS --body timeflies
   gh secret set ANDROID_KEY_PASSWORD
   ```
Sans ces secrets, le job « APK Android » s'arrête avec un message clair.
