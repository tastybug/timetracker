Entitäten
----------

* `Projekt`
    * `Titel`
    * `Beschreibung`
    * `Kontrakt Id`: Vom Auftraggeber vergebener Schlüssel, der den Arbeitsauftrag identifiziert und zu Abrechnungszwecken bekannt sein kann.
* `Zeiterfassungskonfiguration` / `Tracking Configuration`
    * `Projektzeitraum` / `Time Frame`: Start- und Enddatum eines Projekts (jeweils optional)
    * `Maximaler Projektumfang`: eine Höchstdauer für ein Projekt
        * ein Projekt, auf welches mehr gebucht wurde als konfiguriert ist 'OVERBOOKED'
    * `Rundungsstrategie` / `Rounding Strategy`: werden Zeiterfassungen nach Beendigung auf/ab gerundet?
* `Zeiterfassung` / `TrackingRecord`: eine in den Projektumfang einzahlende Zeiterfassung mit Start und Endedatum
    * Start-,Endedatum der -
    * Dauer der -
    * Beschreibung einer -
    
Statistiken
-----------

* `Effektiver Projektumfang` / `Project Duration`: die Dauer eines Projektes auf Basis der bisherigen Zeiterfassungen ('es wurden bisher 10h erfasst')
* `Projekterfüllung` / `Project Completion`: prozentuale Erfüllung eines Projekts. Ist gleich effektivem Projektumfang im Verhältnis zum max. Projektumfang.
    * ein Projekt ohne max. Umfang hat keine Projekterfüllung
* `Projektfristerreichung` / `Project Expiration`: prozentualer Wert der Fristerreichung eines Projekts. Messgenauigkeit in Stunden.
    * ein Projekt ohne Start- oder Enddatum hat kein Projektablauf

Warnungen
---------

* `Checkout Erinnerung` / `Checkout Reminder`: Erinnerung bei überlangen Zeiterfassung, diese zu schließen

Begriffe und Verben
------

* `Zeiterfassung` / `Tracking`: das Erfassen einer Zeiteinheit mit Projektbezug
* `CheckIn`: Starten einer Zeiterfassung
* `CheckOut`: Stoppen einer Zeiterfassung
* `Pausieren`: alias eines Checkouts
* `Schliessen` / `Close`: Beendigung eines Projekts
* `Wiedereröffnen` / `Re-open`: Projektbeendigung rückgängig machen
* `WLAN Zeiterfassung` / `Wi-Fi Tracking`: Zeiterfassung mittels WLAN Zugang
    * `WiFi-CheckIn`
    * `WiFi-CheckOut`
    * `Toleranzzeit` / `Grace Period`: Ruhezeitraum nach dem Verlassen eines WLAN Netzes, der vergehen muss, damit das WiFi-Checkout erfolgt. Verhindert erratische Zeiterfassung bei schlechter Netzqualität.
* `Daten zurücksetzen` / `Restore Backup`: Wiederherstellung eines lokalen Backups.
* `Automatische lokale Datensicherung` / `Automatic Local Backup`: Der Mechanismus, welcher jeden Tag eine lokal gespeicherte Datensicherung erstellt.
* `Ausstehend` / `Pending`: Ein Projekt ist ausstehend, wenn der Projektzeitraum noch nicht begonnen hat (braucht ein Startdatum)
* `Im Gange` / `Ongoing`: Ein Projekt ist im Gange, wenn das Projektenddatum noch nicht erreicht ist (braucht ein Endedatum)
* `Abgelaufen` / `Expired`: Ein Projekt ist abgelaufen, wenn das Projektenddatum erreicht ist (braucht ein Endedatum)

