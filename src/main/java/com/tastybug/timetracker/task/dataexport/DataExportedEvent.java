package com.tastybug.timetracker.task.dataexport;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class DataExportedEvent implements OttoEvent {

    private byte[] data;

    DataExportedEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
