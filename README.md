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

Glossar: Fachliche Terminologie
-------------------------------

* Projekt
* Konkrete Zeiterfassung / TrackingRecord: eine in den Projektumfang einzahlende Zeiterfassung mit Start und Endedatum
* Zeiterfassungskonfiguration / TrackingConfiguration
* Projektzeitraum: Start- und Enddatum eines Projekts (optional) 
* effektiver Projektumfang: die Dauer eines Projektes auf Basis der bisher erfassten TrackingRecords
* maximaler Projektumfang: eine Hoechstdauer fuer ein Projekt ('es werden nicht mehr als 80h bezahlt')