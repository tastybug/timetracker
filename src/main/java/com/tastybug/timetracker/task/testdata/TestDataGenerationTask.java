package com.tastybug.timetracker.task.testdata;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.rounding.RoundingFactory;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

public class TestDataGenerationTask extends AbstractAsyncTask {

    public TestDataGenerationTask(Context context) {
        super(context);
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        createProjectWithLoooongTitle();
        createProjectWithTimeFrame();
        createProjectWithTimeFrameAndLimit();
        createProjectWith200Records();
    }

    protected void onPostExecute(Long result) {
        Log.i(getClass().getSimpleName(), "Created test data");
        ottoProvider.getSharedBus().post(new TestdataGeneratedEvent());
    }

    @Override
    protected void validateArguments() throws NullPointerException {}

    private void createProjectWithLoooongTitle() {
        Project project = new Project("Ueberlanger Projekttitel mit vielen Zeichen___________XXXX");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), RoundingFactory.Strategy.NO_ROUNDING);

        storeBatchOperation(project.getDAO(context).getBatchCreate(project));
        storeBatchOperation(trackingConfiguration.getDAO(context).getBatchCreate(trackingConfiguration));
    }

    private void createProjectWithTimeFrame() {
        Project project = new Project("With Timeframe");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), RoundingFactory.Strategy.NO_ROUNDING);
        trackingConfiguration.setStart(Optional.of(new LocalDate(2016, 12, 24).toDate()));
        trackingConfiguration.setEnd(Optional.of(new LocalDate(2016, 12, 30).toDate()));

        storeBatchOperation(project.getDAO(context).getBatchCreate(project));
        storeBatchOperation(trackingConfiguration.getDAO(context).getBatchCreate(trackingConfiguration));
    }

    private void createProjectWithTimeFrameAndLimit() {
        Project project = new Project("Timeframe+Limit");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), RoundingFactory.Strategy.NO_ROUNDING);
        trackingConfiguration.setStart(Optional.of(new LocalDate(2016, 12, 24).toDate()));
        trackingConfiguration.setEnd(Optional.of(new LocalDate(2016, 12, 30).toDate()));
        trackingConfiguration.setHourLimit(Optional.of(100));

        storeBatchOperation(project.getDAO(context).getBatchCreate(project));
        storeBatchOperation(trackingConfiguration.getDAO(context).getBatchCreate(trackingConfiguration));
    }

    private void createProjectWith200Records() {
        Project project = new Project("200 Records, Limit 100h");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), RoundingFactory.Strategy.NO_ROUNDING);
        trackingConfiguration.setHourLimit(Optional.of(100));
        storeBatchOperation(project.getDAO(context).getBatchCreate(project));
        storeBatchOperation(trackingConfiguration.getDAO(context).getBatchCreate(trackingConfiguration));

        TrackingRecord record;
        LocalDateTime time = new LocalDateTime(2016, 11, 24, 9, 0);
        for (int i=0; i<200; i++) {
            record = new TrackingRecord(project.getUuid());
            record.setStart(time.toDate());
            time = time.plusHours(1);
            record.setEnd(time.toDate());
            time = time.plusHours(1);
            record.setDescription(Optional.of("Eintrag #" + i));

            storeBatchOperation(record.getDAO(context).getBatchCreate(record));
        }

    }

}
