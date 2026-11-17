# Architecture technique - TimeFlies

> Document de conception écrit avant le code (13/11/2026, architecture Clean décidée le 14/11/2026). Les décisions sont datées ; un choix marqué **À valider** n'est pas encore tranché.

## Principes

- **Un seul code, deux plateformes.** Kotlin Multiplatform (cibles `androidTarget()` et `jvm("desktop")`) et Compose Multiplatform : règles, données et écrans sont écrits une fois pour Android et Windows. Seul ce qui n'existe que sur une plateforme est écrit à part : widget Android, icône de barre des tâches et notification Windows.
- **Clean Architecture** (décision du 14/11/2026) : les règles métier ne dépendent de rien ; les écrans, le widget et l'icône Windows appellent les mêmes cas d'usage ; les briques techniques (JSON, DataStore, horloge) sont branchées par chaque application.
- **Hors ligne, sans compte.** La base d'événements est livrée dans l'application. Aucune donnée personnelle ne quitte l'appareil. Seule exception réseau (décision du 16/11/2026) : une requête anonyme par jour au plus vers l'API GitHub pour connaître la dernière version, désactivable dans Réglages › À propos.
- **Testable sans interface ni appareil.** La date du jour est toujours injectée : chaque règle se teste sur une date fixe.

## Couches et modules (décision du 14/11/2026)

Les cinq modules sont rangés dans `src/` (`src/domain`, `src/data`, `src/ui`, `src/androidApp`, `src/desktopApp`), à part de la documentation et des fichiers de build. Leurs noms Gradle restent `:domain`, `:data`, `:ui`, `:androidApp` et `:desktopApp` (`settings.gradle.kts`).

```
        androidApp            desktopApp          ← assemblage (composition root), un par plateforme
        │  widget Glance      │  Tray, notification
        │  WorkManager        │  démarrage Windows
        ▼                     ▼
   ┌─────────── ui ───────────┐      ┌────────── data ──────────┐
   │ écrans Compose,          │      │ JsonEventRepository      │
   │ ViewModels, thème        │      │ DataStoreSettingsRepo    │
   └────────────┬─────────────┘      │ SystemClock              │
                │                    └────────────┬─────────────┘
                ▼                                 ▼ (implémente)
   ┌──────────────────────────── domain ────────────────────────────┐
   │ entités : Event, EventType, Settings, Generations              │
   │ cas d'usage : GetDailyShock, GetEquivalences, GetEventAges,    │
   │               SearchEvents, UpdateSettings…                     │
   │ ports (interfaces) : EventRepository, SettingsRepository, Clock │
   └─────────────────────────────────────────────────────────────────┘
```

**Règle de dépendance** : toutes les dépendances pointent vers `domain`, qui ne dépend de rien d'autre que `kotlinx-datetime` (pour `LocalDate`) et `kotlinx-coroutines-core` (pour le `Flow` du port `SettingsRepository`, décision du 15/11/2026 ; pas de `Dispatchers` dans `domain`). Il n'importe ni Compose, ni la sérialisation JSON, ni DataStore, ni Android.

| Module | Contient | Dépend de |
|---|---|---|
| `domain` | Entités (`Event`, `EventType`, `Family`, `Settings`, `Generations`), **ports** (`EventRepository`, `SettingsRepository`, `Clock`, `DebugTools` pour le panneau de débogage), **cas d'usage** (`GetDailyShock`, `GetEquivalences`, `GetEventAges`, `SearchEvents`, `UpdateSettings`, `ObserveSettings`, `CompleteOnboarding`, `CountEvents`, `SendMorningNotification` pour Windows) et fonctions pures des règles (`Ages`, `Anniversaries`, `Equivalences`, `MorningNotification`, `WidgetSheets`, `Midnight`). | `kotlinx-datetime`, `kotlinx-coroutines-core` |
| `data` | Implémentations des ports : `JsonEventRepository` (lecture, validation, index), `DataStoreSettingsRepository`, `SystemClock`, et pour les versions de débogage seulement `AdjustableClock` (fausse date) et `DataDebugTools`. Tout en `commonMain`. | `domain`, `kotlinx-serialization-json`, DataStore |
| `ui` | Écrans Compose, ViewModels, thème (jetons par style), ressources (`composeResources/` : textes, polices, fichiers d'événements). Les ViewModels n'appellent **que des cas d'usage**. | `domain`, Compose Multiplatform |
| `androidApp` | `AppGraph` (assemblage), `MainActivity`, widget Glance, Workers, `BroadcastReceiver` de date et de démarrage. | `domain`, `data`, `ui` |
| `desktopApp` | `AppGraph`, `main()`, fenêtre, `Tray`, notification, clé de démarrage Windows. | `domain`, `data`, `ui` |

Points de conception :
- **Le widget Android et l'icône Windows sont des interfaces comme les écrans** : ils appellent le même `GetDailyShock` que l'écran Aujourd'hui, avec la même instance créée par `AppGraph`. Ils affichent donc toujours le même coup de vieux.
- **Les cas d'usage restent fins** : même quand l'un ne fait que relayer un appel, il est gardé, pour que `ui` ne touche jamais `data`.
- **Pas de framework d'injection** (ni Koin ni Hilt) : un `AppGraph` écrit à la main par plateforme suffit pour une dizaine de classes.
- **Différences entre plateformes par injection, pas par `expect/actual`** : le chemin du fichier de réglages et la lecture des ressources sont passés aux constructeurs de `data` par l'application. `data` reçoit une fonction `suspend (chemin) -> ByteArray` pour lire les fichiers d'événements, ce qui le garde indépendant de Compose.
- Le repli éventuel vers moins de modules se décide au jalon 1 « Création de la solution », sans changer les couches (paquets au lieu de modules).

## Stack (proposition du 14/11/2026, versions figées au jalon 1 « Création de la solution »)

Les versions sont celles de la dernière stable au moment du jalon, figées dans `gradle/libs.versions.toml`.

| Besoin | Bibliothèque |
|---|---|
| Langage, multiplateforme | Kotlin, Kotlin Multiplatform |
| Interface | Compose Multiplatform (JetBrains), Material 3 |
| Dates | `kotlinx-datetime` |
| JSON | `kotlinx-serialization-json` |
| Textes, polices, fichiers embarqués | Compose Multiplatform Resources |
| État des écrans | `androidx.lifecycle` ViewModel (multiplateforme) + `StateFlow` |
| Navigation | un état simple pour les quatre destinations (jalon 3) ; `navigation-compose` multiplateforme si des écrans empilés arrivent |
| Barre en bas ou rail à gauche | `material3-adaptive-navigation-suite` |
| Réglages | DataStore multiplateforme (`datastore-core-okio`) |
| Logs | Kermit (Touchlab) |
| Widget Android | Jetpack Glance, WorkManager |
| Tests | `kotlin.test` (`commonTest`, exécutés sur la JVM) |

## Flux de données

1. Au premier usage, `JsonEventRepository` lit les fichiers d'événements sur `Dispatchers.Default` (jamais sur le thread de l'interface, test `JsonEventRepositoryThreadTest`), valide chaque entrée et construit les index en mémoire (voir « Base d'événements »). Une entrée invalide fait échouer les tests ; en production, elle est écartée et journalisée.
2. `SettingsRepository.settings: Flow<Settings>` fournit les réglages.
3. `Clock.today()` donne la date du jour, dans le **fuseau horaire de l'appareil** ; chaque ViewModel qui dépend de la date (Aujourd'hui, Équivalences, « Quel âge ? », Réglages) recalcule une seconde après chaque minuit local de l'horloge système (`dayChanges()` dans `ui`, règle `Midnight` de `domain`), et `Clock.manualChanges()` signale une fausse date changée à la main dans le panneau de débogage (jalon 6, 15/11/2026).
4. Chaque ViewModel combine réglages, date du jour et **âges à la volée** (un `MutableStateFlow` gardé par `AppGraph`, jamais écrit sur disque) et appelle les cas d'usage.

Exemple, le coup de vieux du jour :

```kotlin
// domain
class GetDailyShock(
    private val events: EventRepository,
    private val settings: SettingsRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(): DailyShock {
        val today = clock.today()
        val s = settings.current()
        val candidates = events.onMonthDay(today.monthDay(), s.region)
        return Anniversaries.pick(today, candidates, s.birth)   // fonction pure
    }
}
```

## Base d'événements (décision du 14/11/2026)

- **Fichiers JSON chargés en mémoire**, pas de base SQL : la base est en lecture seule et petite au départ. Format dans [`EVENTS_DATA.md`](EVENTS_DATA.md).
- **Un fichier par groupe** (décision du 16/11/2026, qui remplace « un fichier par famille ») : `assets/events/<famille>/<type>/<origine>[/<sous-catégorie>].json`, à part du code, copié dans les ressources de `ui` à la compilation avec un `index.txt` (tâche `gatherResources`). Rangement et règles dans [`EVENTS_DATA.md`](EVENTS_DATA.md), « Fichiers et stockage ». Chaque fichier est un groupe du **filtre** : les groupes décochés (`Settings.excludedEventGroups`) sont écartés de toutes les requêtes par `JsonEventRepository`, qui lit les réglages à chaque requête.
- **Port** `EventRepository` dans `domain` :

  ```kotlin
  interface EventRepository {
      suspend fun groups(): List<EventGroupCount>   // tous les groupes, décochés compris, pour le filtre
      suspend fun onMonthDay(monthDay: MonthDay, region: Region): List<Event>
      suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region): List<Event>
      suspend fun search(query: String, limit: Int): List<Event>
      suspend fun count(): Int
  }
  ```
- **Index en mémoire** construits au chargement : `byMonthDay` (coup de vieux du jour) et `byType` trié par date (équivalences : recherche binaire de la date cible, puis parcours à ±2 ans). Ils sont calculés avec les dates de la région choisie, sans les groupes décochés, et reconstruits quand l'une ou l'autre change.
- **Seuil de 5 000 événements** : arrivé là, on mesure et on décide s'il faut passer à une **base SQLite précompilée** (SQLDelight ; base construite à la compilation à partir des mêmes fichiers sources, index `(mois, jour)` et `(type, date)`, recherche FTS5). Seule la classe `JsonEventRepository` serait remplacée : `domain` et `ui` ne changent pas.

## Règles de calcul

Détaillées dans [`EVENTS_DATA.md`](EVENTS_DATA.md) (format et types) et dans les spécifications d'écran (`UISpec/`). En résumé :

- **Naissance** : l'utilisateur ne donne que son **mois et son année** (jamais le jour, décision du 13/11/2026) ; la date retenue est le 1er du mois. Celle de chaque âge ajouté dans « Quel âge ? » et Équivalences (jusqu'à 3, plus jeunes ou plus âgés, décisions du 15/11/2026 et du 17/11/2026) est aujourd'hui moins cet âge.
- **Âge à une date** : années civiles révolues entre la date de naissance et la date, comme un âge réel (on ne prend pas d'année de 365,25 jours).
- **Date d'un événement** : `regionalDates[région] ?: date` (date de référence : la date française, décision du 14/11/2026).
- **Anniversaire** : même jour et même mois qu'une date d'événement, au moins un an après. Un événement du 29 février est fêté le 28 février les années non bissextiles (décision du 13/11/2026).
- **Écart d'âge** : différence au jour près entre la date de naissance de l'utilisateur et celle de chaque âge ajouté, dans un sens ou dans l'autre (voir `UISpec/Settings.template.md`).
- **Équivalence** : événement de **même type** (types larges), décalé de l'écart d'âge, à ±2 ans près (décision du 13/11/2026).

## Réglages (décision du 14/11/2026)

Une seule implémentation pour les deux plateformes : DataStore (`datastore-core-okio`) avec un sérialiseur JSON sur une `data class Settings` typée. Écriture atomique, exposée en `Flow`, valeurs par défaut portées par la `data class`, champ `version` pour les évolutions (un champ ajouté prend sa valeur par défaut).

| Plateforme | Fichier | Contenu |
|---|---|---|
| Android | `settings.json` dans le dossier privé de l'application | mois et année de naissance, région, style, thèmes de l'écran Équivalences, premier lancement terminé (les âges ajoutés ne sont pas enregistrés, décision du 17/11/2026) |
| Windows | `%APPDATA%\TimeFlies\settings.json` | les mêmes, plus le panneau Windows (objet `windows`, version 3) : lancement au démarrage, notification du matin et son heure, garder dans la barre des tâches, date de la dernière notification. La taille et la position de la fenêtre ne sont pas encore gardées |

Une désinstallation Android efface les réglages. Sur Windows, le dossier `%APPDATA%\TimeFlies` reste après une désinstallation : une réinstallation retrouve les réglages (décision du 13/11/2026).

## Langues de l'interface

- **Textes** : ressources Compose Multiplatform, français de référence dans `composeResources/values/strings.xml`, traductions dans `values-<langue>/strings.xml`. Accords avec `<plurals>` (« 1 an », « 15 ans ») et `pluralStringResource`. Aucun texte écrit en dur dans un écran.
- **Titres des événements** : traduits dans le JSON (`title: {"fr": …, "en": …}`), avec repli sur `fr`.
- **Dates** (« 2 novembre 2011 », « 2. November 2011 », « 2 de noviembre de 2011 », « 02/11/2011 ») : petit formateur propre à l'application (`DateTexts`), avec les noms des jours et des mois et l'ordre des éléments par langue (`date_long`, `date_day_and_month`, `date_with_day_name`, `date_short`) dans les `strings.xml`. Même rendu sur Android et Windows, et testable (`kotlinx-datetime` ne formate pas selon la langue).
- **Langues** (jalon 17, décision du 17/11/2026) : français de référence, anglais, allemand, italien et espagnol (`AppLanguage` dans `domain`). `TranslationTest` vérifie que chaque langue a tous les textes, avec les mêmes paramètres.
- **Changer de langue dans l'application** (jalon 17) : Réglages › Affichage › Langue, gardé dans `settings.json` (`language`, version 8) ; sans choix, la langue du système si elle est proposée, sinon le français. Les ressources Compose lisent la langue par défaut du processus, à l'écran comme dans le widget, l'icône et la notification : `AppLocale` (une version par plateforme) la règle avec `Locale.setDefault` (et `LocaleList.setDefault` sur Android) avant toute lecture de texte, puis la racine de l'application relit tous les textes (`key(language)`) en gardant l'écran affiché et les âges ajoutés. Le changement s'applique aussitôt, sans redémarrer.

## Parties propres à chaque plateforme

- **Widget Android (Glance, jalon 11)** : une tâche WorkManager unique (`widget-midnight`), planifiée une minute après le minuit local et replanifiée à chaque exécution ; un `BroadcastReceiver` (`BOOT_COMPLETED`, `TIME_SET`, `TIMEZONE_CHANGED`) qui recalcule le widget et replanifie minuit ; un recalcul quand les réglages ou la fausse date changent, et à chaque démarrage de l'application ; le bouton « suivant » est un `ActionCallback` qui garde la position dans l'état du widget.
- **Windows (jalon 10)** : icône écrite sur le `TrayIcon` d'AWT (`WindowsTray`), que le `Tray` de Compose enveloppe, pour ouvrir la fenêtre d'un clic gauche simple ; notifications par `displayMessage`. `TrayUpdater` vérifie chaque minute si la notification du matin est due : après une veille, elle part dans la minute du réveil, et la date du dernier envoi (dans les réglages) garantit un seul envoi par jour. Lancement au démarrage par la valeur `TimeFlies` de `HKCU\Software\Microsoft\Windows\CurrentVersion\Run`, écrite avec `reg.exe` par l'application installée, sans droits administrateur.

## Logs

Kermit : Logcat sur Android ; console et fichier `%APPDATA%\TimeFlies\logs\` sur Windows. **Aucune donnée personnelle** dans les logs (ni mois ni année de naissance) : seulement des identifiants d'événements, des durées et des erreurs. Pas d'outil de rapport de plantage en ligne : la trace est écrite dans les logs. Débogage : [`DEVELOPMENT.md`](DEVELOPMENT.md).

## Distribution

- **`ci.yml`** (en place depuis le 14/11/2026), sur chaque PR prête (pas en brouillon) et sur `main`, deux jobs sur Ubuntu avec un JDK 21 :
  - « Tests et compilation » : `./gradlew check` (tests de `domain` et `data`, dont la validation de la base), puis compilation de `desktopApp` et de l'APK de débogage (installe la plateforme Android 37) ;
  - « Benchmarks » : `./gradlew :benchmarks:benchmark`, rapport dans le résumé du run et en artefact.
- **Benchmarks** (`src/benchmarks`, module JVM, hors de `check`) : chargement complet de la base, requêtes de l'`EventRepository`, calcul de chaque page (Aujourd'hui pour l'instant), sur les fichiers réels et sur des jeux synthétiques de 500 à 10 000 événements. Purement informatifs : aucun seuil ne fait échouer la CI. Ils serviront à la décision des 5 000 événements.
- **`release.yml`** (jalon 13, décision du 16/11/2026 : même méthode que LMU-DeltaRacing, sans WiX), **lancé à la main** depuis l'onglet Actions, sur `main`, avec le type de version (Majeur, Mineur, Patch) :
  - la version suivante est calculée à partir du dernier tag `vX.Y.Z` ;
  - exécuteur Ubuntu : `./gradlew check`, puis **APK** signée (`assembleRelease`), clé stable dans les secrets du dépôt (`ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`), signature vérifiée par `apksigner` ;
  - exécuteur Windows : dossier de l'application par `createDistributable` de Compose Desktop (jpackage, avec son propre Java, sans WiX), empaqueté par **Inno Setup** (`scripts/installer.iss`) en `TimeFlies-X.Y.Z-Setup.exe`. Installation par utilisateur dans `%LOCALAPPDATA%\Programs\TimeFlies`, sans droits administrateur ; la désinstallation retire la valeur de démarrage Windows et garde `%APPDATA%\TimeFlies` ; installeur en anglais seulement (décision du 17/11/2026), l'application démarrant ensuite dans la langue de Windows si elle la propose ;
  - publication : tag, release et notes tirées des PR fusionnées depuis la version précédente. Décocher « Publier » ne garde que les fichiers en artefacts, pour essayer.
- **Numéro de version** : passé à Gradle par `-Ptimeflies.version=X.Y.Z`. Android : `versionName` X.Y.Z et `versionCode` X × 10 000 + Y × 100 + Z (toujours croissant, Android installe par-dessus et garde les réglages). Windows : version du lanceur et de l'installeur ; l'`AppId` fixe d'Inno Setup fait qu'une nouvelle version remplace l'ancienne. Une compilation locale n'a pas de version : Réglages affiche « version de développement ».
- **Mises à jour** (décision du 16/11/2026) : installées à la main depuis les Releases GitHub ; l'application le signale. Port `ReleaseSource` (`GitHubReleases` dans `data`, lecture HTTP fournie par chaque application), cas d'usage `ReleaseUpdates` : au démarrage (Android : à l'ouverture, jamais depuis le widget ; Windows : boucle de l'icône, donc chaque jour même si l'application reste ouverte), GitHub est interrogé une fois par jour au plus ; la dernière version connue est gardée dans `settings.json` (version 4, objet `releaseCheck`) ; un bandeau « Nouvelle version : TimeFlies X.Y.Z » au-dessus d'Aujourd'hui ouvre la page de la release (seules les pages de ce dépôt sont acceptées) ; « Plus tard » le masque jusqu'à la version suivante. Une compilation locale n'a pas de version et ne cherche jamais.
- L'installeur Windows n'est pas signé par un éditeur reconnu : SmartScreen affiche « Windows a protégé votre ordinateur » au premier lancement. Le README et les notes de version l'expliquent.

## Versions minimales (décision du 13/11/2026)

- Android 8.0 (API 26) et plus : WorkManager et Glance y fonctionnent, et cela couvre la quasi-totalité des téléphones en service.
- Windows 10 et 11, 64 bits.
