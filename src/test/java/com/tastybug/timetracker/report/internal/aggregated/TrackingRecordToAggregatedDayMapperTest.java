package com.tastybug.timetracker.report.internal.aggregated;

import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class TrackingRecordToAggregatedDayMapperTest {

    private TrackingRecordToAggregatedDayMapper trackingRecordToAggregatedDayMapper = new TrackingRecordToAggregatedDayMapper();

    @Test(expected = NullPointerException.class)
    public void mapTrackingRecordsToAggregatedDays_throws_NPE_on_null_aggregated_days_list() {
        trackingRecordToAggregatedDayMapper.mapTrackingRecordsToAggregatedDays(null, new ArrayList<TrackingRecord>(), new TrackingConfiguration(""));
    }

    @Test(expected = NullPointerException.class)
    public void mapTrackingRecordsToAggregatedDays_throws_NPE_on_null_tracking_record_list() {
        trackingRecordToAggregatedDayMapper.mapTrackingRecordsToAggregatedDays(new ArrayList<AggregatedDay>(), null, new TrackingConfiguration(""));
    }

    @Test(expected = NullPointerException.class)
    public void mapTrackingRecordsToAggregatedDays_throws_NPE_on_null_tracking_configuration() {
        trackingRecordToAggregatedDayMapper.mapTrackingRecordsToAggregatedDays(new ArrayList<AggregatedDay>(), new ArrayList<TrackingRecord>(), null);
    }

    @Test
    public void mapTrackingRecordsToAggregatedDays_can_deal_with_empty_tracking_record_list() {
        // given
        AggregatedDay aggregatedDay = mock(AggregatedDay.class);
        List<AggregatedDay> aggregatedDayList = Collections.singletonList(aggregatedDay);

        // when
        trackingRecordToAggregatedDayMapper.mapTrackingRecordsToAggregatedDays(aggregatedDayList,
                new ArrayList<TrackingRecord>(),
                new TrackingConfiguration(""));

        // then: no aggregations have been done
        verifyZeroInteractions(aggregatedDay);
    }

    @Test
    public void mapTrackingRecordsToAggregatedDays_plumbs_every_tracking_record_into_every_aggregated_day() {
        // given
        AggregatedDay aggregatedDay1 = mock(AggregatedDay.class);
        AggregatedDay aggregatedDay2 = mock(AggregatedDay.class);
        TrackingRecord trackingRecord1 = mock(TrackingRecord.class);
        TrackingRecord trackingRecord2 = mock(TrackingRecord.class);
        TrackingConfiguration trackingConfiguration = mock(TrackingConfiguration.class);
        List<AggregatedDay> aggregatedDayList = Arrays.asList(aggregatedDay1, aggregatedDay2);
        List<TrackingRecord> trackingRecords = Arrays.asList(trackingRecord1, trackingRecord2);

        // when
        trackingRecordToAggregatedDayMapper.mapTrackingRecordsToAggregatedDays(aggregatedDayList, trackingRecords, trackingConfiguration);

        // then
        verify(aggregatedDay1, times(1)).addRecord(trackingRecord1, trackingConfiguration);
        verify(aggregatedDay1, times(1)).addRecord(trackingRecord2, trackingConfiguration);
        verify(aggregatedDay2, times(1)).addRecord(trackingRecord1, trackingConfiguration);
        verify(aggregatedDay2, times(1)).addRecord(trackingRecord2, trackingConfiguration);
    }
}