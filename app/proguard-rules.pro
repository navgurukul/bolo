# Keep Hilt generated
-keep class dagger.hilt.** { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Compose
-dontwarn androidx.compose.**

# Forbid raw audio APIs in release (Phase 1+ enforces lint; ProGuard only strips logging)
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}
