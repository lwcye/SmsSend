# common configuration
-keepattributes SourceFile, LineNumberTable
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepattributes Signature
# support v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# Serializable
-keep class * implements java.io.Serializable { *;}
-keepclassmembers class * implements java.io.Serializable { *;}

# design library
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# retrofit 2.x
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# okio
-dontwarn okio.**

# okhttp3
-dontwarn okhttp3.**

# rxjava
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

# green dao
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.greenrobot.greendao.database.**
-dontwarn rx.**
-keep class org.greenrobot.greendao.** { *; }

# event bus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
# umeng
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.gcit.smssend.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# libdev-common
-keep class com.cqmc.opsrc.blankj.utilcode.util.LogUtils { *; }
-keepclassmembers class com.gcit.smssend.db.bean.** {*;}
-keepclassmembers class * extends com.gcit.smssend.network.ApiResult {*;}


