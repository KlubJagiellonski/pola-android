# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/paweljaneczek/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepclasseswithmembernames class pl.pola_app.** { *; }
-keepclasseswithmembernames class androidx.** { *; }
-keepclasseswithmembernames class com.google.** { *; }
-keepclasseswithmembernames class com.facebook.** { *; }
-keepclasseswithmembernames class com.github.** { *; }
-keepclasseswithmembernames class com.squareup.** { *; }
-keepclasseswithmembernames class io.reactivex.** { *; }
-keepclasseswithmembernames class icom.journeyapps.** { *; }
-keepclasseswithmembernames class com.jakewharton.** { *; }
-keepclasseswithmembernames class org.parceler.** { *; }
-keepattributes SourceFile,LineNumberTable

# Parceler library
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
