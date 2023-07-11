-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively
-repackageclasses

# https://developer.android.com/build/shrink-code#retracing
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Remove @ retrofit > 2.9.0 which will ship this rule in the library jar metadata
# https://github.com/square/retrofit/blob/6cd6f7d8287f73909614cb7300fcde05f5719750/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro#L38-L41
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
