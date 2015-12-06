Release
-------

_Checkliste_

* `pom.xml`: `version` und `manifest.version.code` sind korrekt?

_Release Testlauf_

* Releasecandidate erstellen: `mvn -P release-build-testrun,release-build clean package`
* Releasecandidate installieren: `adb install -r /home/philipp/repos/timetracker/target/timetracker-signed-aligned.apk`

_Release Durchfuehrung_

Noch offen.

_Release abbrechen_

* ein angelegter Tag kann mittels `git tag -d v1.06 && git push origin :refs/tags/v1.00` rueckgegaengig gemacht werden. Im POM die version zuruecksetzen, neu starten.

