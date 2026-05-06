# =====================================================================
# Kin — release ProGuard / R8 rules
# =====================================================================
# We keep:
#   - kotlinx-serialization companion serializers (otherwise @Serializable
#     models are silently shrunk away and json.decode crashes).
#   - Ktor / OkHttp internal dispatchers reflectively touched at runtime.
#   - androidx.security.crypto MasterKey + EncryptedSharedPreferences classes.
#   - Compose runtime hints — these are normally handled by the Compose
#     compiler plugin, but the safety net keeps optimisation surprises away.

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses, EnclosingMethod, SourceFile, LineNumberTable
-keepclassmembers class **$$serializer { *; }
-keep,includedescriptorclasses class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
    @kotlinx.serialization.Serializable <methods>;
}
-keep,includedescriptorclasses class com.rork.kin.**$$serializer { *; }
-keepclassmembers class com.rork.kin.** {
    *** Companion;
}
-keepclasseswithmembers class com.rork.kin.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor + Coroutines
-dontwarn io.ktor.**
-dontwarn kotlinx.coroutines.**
-keep class io.ktor.** { *; }

# AndroidX Security (EncryptedSharedPreferences / MasterKey use Tink reflection)
-dontwarn com.google.crypto.tink.**
-keep class com.google.crypto.tink.** { *; }
-keep class androidx.security.crypto.** { *; }

# AndroidX ExifInterface
-keep class androidx.exifinterface.** { *; }

# kotlinx.serialization.json
-keep class kotlinx.serialization.json.** { *; }

# Reflection used by Ktor's content negotiation
-keepclassmembers class kotlin.Metadata { public <methods>; }
