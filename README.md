Fachliches Glossar
==================

Entitäten
----------

* Projekt
    * Projekttitel
    * Projektbeschreibung
    
* Zeiterfassungskonfiguration (Tracking Configuration)
    * Projektzeitraum (Time Frame): Start- und Enddatum eines Projekts (jeweils optional)
    * maximaler Projektumfang: eine Höchstdauer für ein Projekt
        * ein Projekt, auf welches mehr gebucht wurde als konfiguriert ist 'OVERBOOKED'
    * Rundungsstrategie: werden Zeiterfassungen nach Beendigung auf/ab gerundet?
    
* Zeiterfassung / TrackingRecord: eine in den Projektumfang einzahlende Zeiterfassung mit Start und Endedatum
    * Start-,Endedatum der -
    * Dauer der -
    * Beschreibung einer -

Statistiken
-----------

* Projektstatistiken (Project Statistics)
    * effektiver Projektumfang (Project Duration): die Dauer eines Projektes auf Basis der bisherigen Zeiterfassungen ('es wurden bisher 10h erfasst')
    * Projekterfüllung (Project Completion): prozentuale Erfüllung eines Projekts. Ist gleich effektivem Projektumfang im Verhältnis zum max. Projektumfang.
        * ein Projekt ohne max. Umfang hat keine Projekterfüllung
    * Projektfristerreichung (Project Expiration): prozentualer Wert der Fristerreichung eines Projekts. Messgenauigkeit in Stunden.
        * ein Projekt ohne Start- oder Enddatum hat kein Projektablauf
    
Warnungen
---------

* Checkout Erinnerung / Checkout Reminder: Erinnerung bei überlangen Zeiterfassung, diese zu schließen

Views
-----

* Zeiterfassungshistorie / TrackingLog: eine Liste aller TrackingRecords zu einem Projekt in chronologischer Reihenfolge

Begriffe
------

* Zeiterfassung/Tracking: das Erfassen einer Zeiteinheit mit Projektbezug
* CheckIn: Starten einer Zeiterfassung
* CheckOut: Stoppen einer Zeiterfassung
* Pausieren: alias eines Checkouts
* Schliessen/Close: Beendigung eines Projekts
* Wiedereröffnen/Re-open: Projektbeendigung rückgängig machen
* `WLAN Zeiterfassung`/`Wi-Fi Tracking`: Zeiterfassung mittels WLAN Zugang
    * `WiFi-CheckIn`
    * `WiFi-CheckOut`
    * `Toleranzzeit` / `Grace Period`: Ruhezeitraum nach dem Verlassen eines WLAN Netzes, der vergehen muss, damit das WiFi-Checkout erfolgt. Verhindert erratische Zeiterfassung bei schlechter Netzqualität.
