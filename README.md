# Pocket Log: simple time tracking for Freelancers
 
Pocket Log is a pretty simple Android app that allows time tracking for a number of projects with
constraints, like hour limits, due dates and such. The app is free and open source, for 
screenshots have a look at the 
[Playstore Entry](https://play.google.com/store/apps/details?id=com.tastybug.timetracker).

## Initial setup

Like most Android projects these days Pocket Log uses gradle. In `app/build.gradle` you find a 
section handling the signing process for release versions:

    release {
        storeFile file("../timetracker.keystore")
        storePassword TIMETRACKER_RELEASE_STORE_PASSWORD // ~/.gradle/gradle.properties
        keyAlias "timetracker"
        keyPassword TIMETRACKER_RELEASE_KEY_PASSWORD // ~/.gradle/gradle.properties
    }
    
`timetracker.keystore` contains the signing key which you will have to replace with your own key
Once that is done, create a file at `~/.gradle/gradle.properties` in which you can store the
passwords for your key and keystore like this:

    TIMETRACKER_RELEASE_STORE_PASSWORD: aPasswordHere
    TIMETRACKER_RELEASE_KEY_PASSWORD: anotherPasswordHere
    
Using this approach you can have your key in the open without exposing the passwords.

### Building
Pocket Log is build using gradle. The usual tasks are available, like:

* `./gradlew assembleDebug` produces an APK file that you can manually install using ADB
* `./gradlew test` runs the contained unit tests

## Releasing

Before running your first release, you'll have to replace the release signing key, which is located
in /timetracker.keystore. After that, the following steps will have to be repeated for each
release:

1) Ensure new version and version code is set in app/build.gradle
1) run `./gradlew clean assembleRelease`
1) Upload the artifact `app/build/outputs/apk/app-release.apk` using the Store Webclient
1) Once the release is done, update `versionCode` and `versionString` in `app/build.gradle` and 
commit the changes 