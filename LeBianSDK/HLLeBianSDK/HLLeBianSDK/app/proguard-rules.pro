# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
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


#lebian sdk begin
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}
-keep class com.excelliance.** {*;}
-keepclassmembers class * implements java.lang.reflect.InvocationHandler {
	private java.lang.Object *(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]);
}
#lebian sdk end
