# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

### glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

### okhttp3-integration
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule

### okhttp3
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
### okio
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
### logging-interceptor
-keep class okhttp3.logging.*
### dns-interceptor
-keep class okhttp3.dnsoverhttps.*
### android-gif-drawable
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}
-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}
### MagicIndicator

### subsampling-scale-image-view
-keep class com.davemorrissey.labs.subscaleview.** { *; }
### textdrawable
-keep class com.amulyakhare.textdrawable.** {*;}

### 使用注解来指定需要保留的方法和类
-keep interface com.dev.util.Keep
-keep interface com.dev.util.KeepNameIfNecessary
-keep interface com.dev.util.KeepMemberIfNecessary
-keep interface com.dev.util.KeepClassOnMemberPresent
-keep,allowobfuscation @interface com.dev.util.Keep
-keep,allowobfuscation @interface com.dev.util.KeepNameIfNecessary
-keep,allowobfuscation @interface com.dev.util.KeepMemberIfNecessary
-keep,allowobfuscation @interface com.dev.util.KeepClassOnMemberPresent

-keep @com.dev.util.Keep class *{
   public *;
   protected *;
}
# 保存未被删除的类的名称，让其不被混淆
-keepnames @com.dev.util.KeepNameIfNecessary class *
# 保存未被删除的类的成员方法，让其不被混淆
 -keepclassmembers class * {
     @com.dev.util.KeepMemberIfNecessary <methods>;
 }
 # 保存未被删除的类的成员变量，让其不被混淆
 -keepclassmembers class * {
     @com.dev.util.KeepMemberIfNecessary <fields>;
 }
 # 当class的成员变量存在时，保留class及其指定成员变量不被混淆
 -keepclasseswithmembers class *{
     @com.dev.util.KeepClassOnMemberPresent <fields>;
 }
  # 当class的成员方法存在时，保留class及其指定成员方法不被混淆
  -keepclasseswithmembers class *{
      @com.dev.util.KeepClassOnMemberPresent <methods>;
  }

# Retrofit2
-keep class retrofit2.*{
 *;
}
#android-state
-keep class com.evernote.*{
  *;
}

#stetho
-keep class com.facebook.stetho.*{
  *;
}
#
-keep class com.squareup.leakcanary{
  *;
}
#dagger2
