package com.tastybug.timetracker.model.filter;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TrackingRecordTimeFrameFilter {

    private List<TrackingRecord> trackingRecords = Collections.emptyList();

    private Optional<Date> timeFrameStartingAt = Optional.absent();
    private Optional<Date> timeFrameEndingAt = Optional.absent();

    public TrackingRecordTimeFrameFilter() {
    }

    public TrackingRecordTimeFrameFilter withTimeFrameStartingAt(Date startingAt) {
        Preconditions.checkNotNull(startingAt);
        Preconditions.checkArgument(!timeFrameEndingAt.isPresent() || timeFrameEndingAt.get().after(startingAt));
        timeFrameStartingAt = Optional.of(startingAt);
        return this;
    }

    public TrackingRecordTimeFrameFilter withTimeFrameEndingAt(Date endingAt) {
        Preconditions.checkNotNull(endingAt);
        Preconditions.checkArgument(!timeFrameStartingAt.isPresent() || timeFrameStartingAt.get().before(endingAt));
        timeFrameEndingAt = Optional.of(endingAt);
        return this;
    }

    public TrackingRecordTimeFrameFilter withTrackingRecordList(List<TrackingRecord> list) {
        Preconditions.checkNotNull(list);
        this.trackingRecords = list;
        return this;
    }

    public List<TrackingRecord> build() {
        ArrayList<TrackingRecord> resultList = new ArrayList<>();
        for (TrackingRecord trackingRecord : trackingRecords) {
            if (!trackingRecord.isFinished()) {
                continue;
            }
            if (isTrackingRecordEndingBeforeTimeFrame(trackingRecord)) {
                continue;
            }
            if (isTrackingRecordEndingAfterTimeFrame(trackingRecord)) {
                continue;
            }
            resultList.add(trackingRecord);
        }
        return resultList;
    }

    public List<TrackingRecord> buildEdges() {
        ArrayList<TrackingRecord> resultList = new ArrayList<>();

        for (TrackingRecord trackingRecord : trackingRecords) {
            if (!trackingRecord.isFinished()) {
                continue;
            }
            if (!isTrackingRecordEndingBeforeTimeFrame(trackingRecord)
                    && !isTrackingRecordEndingBeforeTimeFrame(trackingRecord)
                    && isTrackingRecordStartingBeforeTimeFrame(trackingRecord)) {
                resultList.add(trackingRecord);
            }
        }
        return resultList;
    }

    private boolean isTrackingRecordEndingAfterTimeFrame(TrackingRecord trackingRecord) {
        return timeFrameEndingAt.isPresent() && !trackingRecord.getEnd().get().before(timeFrameEndingAt.get());
    }

    private boolean isTrackingRecordEndingBeforeTimeFrame(TrackingRecord trackingRecord) {
        return timeFrameStartingAt.isPresent() && trackingRecord.getEnd().get().before(timeFrameStartingAt.get());
    }

    private boolean isTrackingRecordStartingBeforeTimeFrame(TrackingRecord trackingRecord) {
        return timeFrameStartingAt.isPresent() && trackingRecord.getStart().get().before(timeFrameStartingAt.get());
    }
}
