# Architecture technique - TimeFlies

> Document de conception écrit avant le code (13/11/2026, architecture Clean décidée le 14/11/2026). Les décisions sont datées ; un choix marqué **À valider** n'est pas encore tranché.

## Principes

- **Un seul code, deux plateformes.** Kotlin Multiplatform (cibles `androidTarget()` et `jvm("desktop")`) et Compose Multiplatform : règles, données et écrans sont écrits une fois pour Android et Windows. Seul ce qui n'existe que sur une plateforme est écrit à part : widget Android, icône de barre des tâches et notification Windows.
- **Clean Architecture** (décision du 14/11/2026) : les règles métier ne dépendent de rien ; les écrans, le widget et l'icône Windows appellent les mêmes cas d'usage ; les briques techniques (JSON, DataStore, horloge) sont branchées par chaque application.
- **Hors ligne, sans compte.** La base d'événements est livrée dans l'application. Aucune donnée ne quitte l'appareil.
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
   │ cas d'usage : GetDailyShock, GetEquivalences, FindEquivalent,  │
   │               UpdateSettings                                    │
   │ ports (interfaces) : EventRepository, SettingsRepository, Clock │
   └─────────────────────────────────────────────────────────────────┘
```

**Règle de dépendance** : toutes les dépendances pointent vers `domain`, qui ne dépend de rien d'autre que `kotlinx-datetime` (pour `LocalDate`). Il n'importe ni Compose, ni la sérialisation JSON, ni DataStore, ni Android.

| Module | Contient | Dépend de |
|---|---|---|
| `domain` | Entités (`Event`, `EventType`, `Family`, `Settings`, `Generations`), **ports** (`EventRepository`, `SettingsRepository`, `Clock`), **cas d'usage** (`GetDailyShock`, `GetEquivalences`, `FindEquivalent`, `UpdateSettings`) et fonctions pures des règles (`Ages`, `Anniversaries`, `Equivalences`). | `kotlinx-datetime` |
| `data` | Implémentations des ports : `JsonEventRepository` (lecture, validation, index), `DataStoreSettingsRepository`, `SystemClock`. Tout en `commonMain`. | `domain`, `kotlinx-serialization-json`, DataStore |
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
| Navigation | `navigation-compose` multiplateforme (JetBrains) |
| Barre en bas ou rail à gauche | `material3-adaptive-navigation-suite` |
| Réglages | DataStore multiplateforme (`datastore-core-okio`) |
| Logs | Kermit (Touchlab) |
| Widget Android | Jetpack Glance, WorkManager |
| Tests | `kotlin.test` (`commonTest`, exécutés sur la JVM) |

## Flux de données

1. Au démarrage, `JsonEventRepository` lit les fichiers d'événements sur `Dispatchers.Default`, valide chaque entrée et construit les index en mémoire (voir « Base d'événements »). Une entrée invalide fait échouer les tests ; en production, elle est écartée et journalisée.
2. `SettingsRepository.settings: Flow<Settings>` fournit les réglages.
3. `Clock.todayFlow` émet la date du jour, dans le **fuseau horaire de l'appareil**, puis la date suivante à chaque minuit local.
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
- **Un fichier par famille** (`series.json`, `cinema.json`, `music.json`, `sport.json`, `games-tech.json`, `history.json`) dans `src/ui/src/commonMain/composeResources/files/events/`, pour des PR de saisie petites et faciles à relire.
- **Port** `EventRepository` dans `domain` :

  ```kotlin
  interface EventRepository {
      suspend fun onMonthDay(monthDay: MonthDay, region: Region): List<Event>
      suspend fun ofType(type: EventType, from: LocalDate, to: LocalDate, region: Region): List<Event>
      suspend fun search(query: String, limit: Int): List<Event>
      suspend fun count(): Int
  }
  ```
- **Index en mémoire** construits au chargement : `byMonthDay` (coup de vieux du jour) et `byType` trié par date (équivalences : recherche binaire de la date cible, puis parcours à ±2 ans). Ils sont calculés avec les dates de la région choisie et reconstruits quand elle change.
- **Seuil de 5 000 événements** : arrivé là, on mesure et on décide s'il faut passer à une **base SQLite précompilée** (SQLDelight ; base construite à la compilation à partir des mêmes fichiers sources, index `(mois, jour)` et `(type, date)`, recherche FTS5). Seule la classe `JsonEventRepository` serait remplacée : `domain` et `ui` ne changent pas.

## Règles de calcul

Détaillées dans [`EVENTS_DATA.md`](EVENTS_DATA.md) (format et types) et dans les spécifications d'écran (`UISpec/`). En résumé :

- **Naissance** : l'utilisateur ne donne que son **mois et son année** (jamais le jour, décision du 13/11/2026) ; la date retenue est le 1er du mois. Celle du jeune est aujourd'hui moins son âge.
- **Âge à une date** : années civiles révolues entre la date de naissance et la date, comme un âge réel (on ne prend pas d'année de 365,25 jours).
- **Date d'un événement** : `regionalDates[région] ?: date` (date de référence : la date française, décision du 14/11/2026).
- **Anniversaire** : même jour et même mois qu'une date d'événement, au moins un an après. Un événement du 29 février est fêté le 28 février les années non bissextiles (décision du 13/11/2026).
- **Écart d'âge** : différence au jour près entre la date de naissance de l'utilisateur et celle du jeune (voir `UISpec/Settings.template.md`).
- **Équivalence** : événement de **même type** (types larges), décalé de l'écart d'âge, à ±2 ans près (décision du 13/11/2026).

## Réglages (décision du 14/11/2026)

Une seule implémentation pour les deux plateformes : DataStore (`datastore-core-okio`) avec un sérialiseur JSON sur une `data class Settings` typée. Écriture atomique, exposée en `Flow`, valeurs par défaut portées par la `data class`, champ `version` pour les évolutions (un champ ajouté prend sa valeur par défaut).

| Plateforme | Fichier | Contenu |
|---|---|---|
| Android | `settings.json` dans le dossier privé de l'application | mois et année de naissance, âge du jeune, région, style, thèmes et sens de l'écran Équivalences, premier lancement terminé |
| Windows | `%APPDATA%\TimeFlies\settings.json` | les mêmes, plus l'heure de la notification, le lancement au démarrage, la taille et la position de la fenêtre, la date de la dernière notification |

Une désinstallation Android efface les réglages. Sur Windows, le dossier `%APPDATA%\TimeFlies` reste après une désinstallation : une réinstallation retrouve les réglages (décision du 13/11/2026).

## Langues de l'interface

- **Textes** : ressources Compose Multiplatform, français de référence dans `composeResources/values/strings.xml`, traductions dans `values-<langue>/strings.xml`. Accords avec `<plurals>` (« 1 an », « 15 ans ») et `pluralStringResource`. Aucun texte écrit en dur dans un écran.
- **Titres des événements** : traduits dans le JSON (`title: {"fr": …, "en": …}`), avec repli sur `fr`.
- **Dates longues** (« 2 novembre 2011 ») : petit formateur propre à l'application, avec les noms des jours et des mois dans les `strings.xml` et l'ordre des éléments par langue. Même rendu sur Android et Windows, et testable (`kotlinx-datetime` ne formate pas selon la langue).
- **Changer de langue dans l'application** : inutile en V1 (français seulement). Au jalon « Traductions », la langue choisie s'appliquera au démarrage (API de langue par application sur Android, `Locale.setDefault` sur Windows), à détailler alors.

## Parties propres à chaque plateforme

- **Widget Android (Glance)** : une tâche WorkManager unique replanifiée chaque jour juste après minuit local ; des `BroadcastReceiver` (`BOOT_COMPLETED`, `TIME_SET`, `TIMEZONE_CHANGED`) qui appellent `updateAll()` ; le bouton « suivant » est un `ActionCallback` qui garde la position dans l'état du widget.
- **Windows** : `Tray` de Compose Desktop (icône, infobulle, menu) et `TrayState.sendNotification`. La notification du matin est planifiée par une coroutine qui attend la prochaine heure choisie ; au réveil du PC, la date de la dernière notification (dans les réglages) garantit un seul envoi par jour. Lancement au démarrage par la clé `HKCU\Software\Microsoft\Windows\CurrentVersion\Run`, écrite avec `reg.exe`, sans droits administrateur.

## Logs

Kermit : Logcat sur Android ; console et fichier `%APPDATA%\TimeFlies\logs\` sur Windows. **Aucune donnée personnelle** dans les logs (ni mois ni année de naissance) : seulement des identifiants d'événements, des durées et des erreurs. Pas d'outil de rapport de plantage en ligne : la trace est écrite dans les logs. Débogage : [`DEVELOPMENT.md`](DEVELOPMENT.md).

## Distribution

- **`ci.yml`** : sur une PR prête (pas en brouillon), `./gradlew check` (tests de `domain` et `data`, dont la validation de la base).
- **`release.yml`**, sur un tag `vX.Y.Z` :
  - exécuteur Ubuntu : **APK** signée (`assembleRelease`), clé stable dans les secrets du dépôt (`ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`) ;
  - exécuteur Windows : **installeur** `.msi` (`packageMsi` de Compose Desktop, basé sur jpackage), qui embarque son propre Java. jpackage a besoin de WiX pour un `.msi` : sa présence sur l'exécuteur GitHub est à vérifier au jalon ;
  - publication des deux fichiers dans la release.
- Le numéro de version vient du tag ; une compilation locale reste en `0.0.0-dev`.
- L'installeur Windows n'est pas signé par un éditeur reconnu : Windows affiche « éditeur inconnu » au premier lancement. Le README l'explique.

## Versions minimales (décision du 13/11/2026)

- Android 8.0 (API 26) et plus : WorkManager et Glance y fonctionnent, et cela couvre la quasi-totalité des téléphones en service.
- Windows 10 et 11, 64 bits.
