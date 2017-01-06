* TrackingConfiguration sollte ProjectConstraints heissen
    * WarningEventListenerService sollte ProjectConstraintsListenerService heissen
    * in DB umbenennen
* Naming allgemein ueberdenken: 
    * Project/Projekt zu Account/Zeitkonto
    * TrackingRecord/Zeiterfassung zu Record/Zeiterfassung, 
    * TrackingConfiguration/Zeiterfassungskonfiguration zu Constraints/Regelsatz
* Task.performBackgroundStuff sollte die ContentProviderOperations als Liste zurueckgeben. Die Parentklasse kann die Operations dann einheitlich verarbeiten.