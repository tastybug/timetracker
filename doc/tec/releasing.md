### Release Steps

1) Ensure new version and version code is set in app/build.gradle
1) run `./gradlew clean assembleRelease`
1) Upload artifact `app/build/outputs/apk/app-release.apk` into the Play Store
1) Once the release is done, commit the changes regarding `build.gradle`
