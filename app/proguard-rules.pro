# ── Hilt ────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class dagger.hilt.android.internal.** { *; }
-keep,allowobfuscation @interface dagger.hilt.android.lifecycle.HiltViewModel
-keep,allowobfuscation @interface dagger.hilt.android.AndroidEntryPoint
-keep,allowobfuscation @interface dagger.hilt.android.HiltAndroidApp
-keep,allowobfuscation @interface javax.inject.Inject
-keep,allowobfuscation @interface dagger.Module
-keep,allowobfuscation @interface dagger.Provides
-keep @dagger.hilt.android.HiltAndroidApp class * { <init>(...); }
-keep @dagger.hilt.android.AndroidEntryPoint class * { <init>(...); }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep class **_HiltModules { *; }
-keep class **_HiltModules$* { *; }
-keep class **_HiltComponents { *; }
-keep class **_HiltComponents$* { *; }
-keep class **_GeneratedInjector { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
-keep class hilt_aggregated_deps.** { *; }
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}
-keep class * extends androidx.lifecycle.ViewModel { <init>(...); }

# ── Room ────────────────────────────────────────────────────────────
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class **_Impl { *; }
-keepclassmembers class **_Impl { *; }

# ── ML Kit (language id JNI) ────────────────────────────────────────
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**

# ── Kotlin / coroutines reflection bits ─────────────────────────────
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-keepclassmembers class kotlin.coroutines.SafeContinuation { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# ── Compose ─────────────────────────────────────────────────────────
-dontwarn androidx.compose.**

# ── Bolo data + analysis (Room entities + ML Kit/dict classifiers) ──
-keep class co.bolo.app.data.model.** { *; }
-keep class co.bolo.app.data.db.** { *; }
-keep class co.bolo.app.analysis.** { *; }

# ── kotlinx coroutines play-services bridge (Tasks.await) ───────────
-keep class kotlinx.coroutines.tasks.** { *; }
-dontwarn kotlinx.coroutines.tasks.**

# ── Readable stack traces in release crashes ────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Strip verbose logs only
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}
