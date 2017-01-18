package com.tastybug.timetracker.task.dataimport;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class ImportedDataEvent implements OttoEvent {

    ImportedDataEvent() {
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .toString();
    }
}
