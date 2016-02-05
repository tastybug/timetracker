package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.task.OttoEvent;

public class TimeFrameModifiedEvent implements OttoEvent {

    private TimeFrame timeFrame;

    public TimeFrameModifiedEvent(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }
}