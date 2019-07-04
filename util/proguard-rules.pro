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