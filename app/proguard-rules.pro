# Security & Optimization Rules for BTEB Result App

# 1. Preserve Data Models & JSON Serialization (Moshi & Room)
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keep class com.example.data.model.** { *; }
-keepclassmembers class com.example.data.model.** { *; }

-keep class com.example.data.local.** { *; }
-keepclassmembers class com.example.data.local.** { *; }

# Moshi rules
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

# Retrofit & OkHttp rules
-keepattributes Exceptions
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn okio.**

# Room Database rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# 2. Security Hardening: Strip Debug Logs from Release APK
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# 3. Obfuscation & Source Protection
-repackageclasses 'com.example.bteb.obf'
-allowaccessmodification

# Hide source file names and line numbers in stack traces for reverse-engineering prevention
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
