# SoniFlac ProGuard Rules
# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep model classes for serialization
-keep,includedescriptorclasses class com.particlesector.soniflac.core.model.**$$serializer { *; }
-keepclassmembers class com.particlesector.soniflac.core.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.particlesector.soniflac.core.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}
