# R8 rules of the TimeFlies release APK. The libraries (Compose, kotlinx-serialization, DataStore, Glance,
# WorkManager, Kermit) ship their own rules; only what they cannot know is here.

# Stack traces of the logs keep their line numbers (docs/DEVELOPMENT.md, "Logs et plantages").
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
