# Hilt rules
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.viewmodel.ViewModel

# Retrofit & OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep @retrofit2.http.* interface * { <methods>; }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Gson DTOs — keep entire package to prevent deserialization crashes in release builds
-keep class com.bousmah.meteoapp_marouane.data.remote.dto.** { *; }

# Lottie
-keep class com.airbnb.lottie.** { *; }
