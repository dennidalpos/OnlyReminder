# ProGuard rules for OnlyReminder

# Support for POI
-dontwarn aQute.bnd.annotation.baseline.BaselineIgnore
-dontwarn aQute.bnd.annotation.spi.ServiceConsumer
-dontwarn aQute.bnd.annotation.spi.ServiceProvider
-dontwarn org.apache.batik.**
-dontwarn org.apache.poi.**
-dontwarn org.osgi.framework.**
-dontwarn com.github.luben.zstd.**
-dontwarn javax.xml.stream.**
-dontwarn net.sf.saxon.**
-dontwarn org.apache.xmlbeans.**

# Support for Tink / Security-Crypto
-dontwarn com.google.errorprone.annotations.**
-dontwarn edu.umd.cs.findbugs.annotations.**

# SQLCipher
-keep class net.zetetic.database.sqlcipher.** { *; }
-dontwarn net.zetetic.database.sqlcipher.**

# General
-dontwarn javax.annotation.**
