package com.tastybug.timetracker.extensions.backup.controller.dataexport;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class DataExportedEvent implements OttoEvent {

    private byte[] data;

    DataExportedEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dataLength", getData().length)
                .toString();
    }
}
