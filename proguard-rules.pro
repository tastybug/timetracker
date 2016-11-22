-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# this class contains testibility constructors that break due to obfuscation, which is fine
-dontnote com.tastybug.timetracker.infrastructure.backup.OSFacingBackupAgentHandler

-keep class com.tastybug.timetracker.infrastructure.runtime.Application

-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}