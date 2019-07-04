# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class nameRes to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file nameRes.
#-renamesourcefileattribute SourceFile
### general proguard
#-keep public class * extends android.app.Application # application
#-keep public class * extends androidx.appcompat.app.AppCompatActivity
#-keep public class * extends androidx.fragment.app.Fragment
#-keep public class * extends android.app.Service
#-keep public class * extends android.view.View
#-keep public class * extends android.app.IntentService
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgentHelper
#-keep public class * extends android.preference.Preference
#-keep public class * extends android.hardware.display.DisplayManager
#-keep public class * extends android.os.UserManager
#-keep public class com.android.vending.licensing.ILicensingService
#
#-keep class android.os.**{*;}
#
#-keep class **.R$* { *; }
#-keep class **.R{ *; }
#
##实现了android.os.Parcelable接口类的任何类，以及其内部定义的Creator内部类类型的public final静态成员变量，都不能被混淆和删除
#-keep class * implements android.os.Parcelable {    # 保持Parcelable不被混淆
#  public static final android.os.Parcelable$Creator *;
#}
##-keepclasseswithmembernames class * {     # 保持 native 方法不被混淆
##    native ;
##}
##-keepclasseswithmembers class * {         # 保持自定义控件类不被混淆
##    public **(android.content.Context, android.util.AttributeSet);
##}
##
##-keepclasseswithmembers class * {         # 保持自定义控件类不被混淆
##    public **(android.content.Context, android.util.AttributeSet, int);
##}
##
##-keepclasseswithmembers class * {
##  public **(android.content.Context, android.util.AttributeSet, int, int);
##}
#
## 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
## 使javascript的调用接口保留
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
-keepclassmembers class com.dev.browser.adblock.webview.AdblockWebView {
   public java.lang.String getElemhideSelectors();
   public java.lang.String getElemhideEmulationSelectors();
}
## 泛型
-keepattributes Signature
### 保留第三方libaray不被混淆
-keep,allowshrinking class !com.dev.** {*;}
-keep class org.adblockplus.libadblockplus.** {*;}
-dontwarn **
-keep class * extends android.view.View
-keep class com.dev.orangebrowser.**{
  *;
}
#-keep class com.dev.base.**{
#  *;
#}
-keep class com.dev.browser.**{
  *;
}
#-keep class com.dev.util.**{
#  *;
#}
#-keep class com.dev.view.**{
#  *;
#}
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
#-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings