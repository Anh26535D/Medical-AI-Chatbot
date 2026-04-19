# Project specific ProGuard rules

# 1. Enable optimizations and stronger obfuscation
-optimizationpasses 5
-allowaccessmodification

# 2. Obfuscate BuildConfig class
# By default, the Android Gradle Plugin keeps BuildConfig.
# We allow ProGuard to rename the class and its fields to hide AI prompts.
-repackageclasses ''
-allowaccessmodification
-keepattributes SourceFile,LineNumberTable

# Allow obfuscating fields in BuildConfig (includes AI Prompts)
-keep class edu.hust.medicalaichatbot.BuildConfig {
    # We don't use a full -keep here so R8 can rename fields like SYSTEM_PROMPT to 'a', 'b', etc.
}

# 3. Protect Room Database or model classes if necessary
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}

# Keep important attributes
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
