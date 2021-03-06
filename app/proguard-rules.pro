
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
# http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
# public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-dontwarn com.github.mikephil.**
-keep class * extends android.webkit.WebChromeClient { *; }
-dontwarn im.delight.android.webview.**
-dontwarn okio.**
-keepclassmembers class com.paytm.pgsdk.PaytmWebView$PaytmJavaScriptInterface {
   public *;
}
-dontwarn javax.annotation.**
-dontwarn retrofit2.Platform$Java8

# For razorpay
-keepclassmembers class * {
  @android.webkit.JavascriptInterface <methods>;}
  -keepattributes JavascriptInterface
  -keepattributes *Annotation*
  -dontwarn com.razorpay.**
  -keep class com.razorpay.** {*;}
  -optimizations !method/inlining
