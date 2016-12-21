package com.tastybug.timetracker.task.testdata;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.rounding.Rounding;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class TestDataGenerationTask extends AbstractAsyncTask {

    public TestDataGenerationTask(Context context) {
        super(context);
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        createProjectWithLoooongTitle();
        createProjectWithTimeFrame();
        createProjectWithTimeFrameAndLimit();
        createProjectWith200Records(false);
        createProjectWith200Records(true);
        createProjectWithRecordsWithOverlongDescriptions();
        createProjectWithLongRunningRecord();
        createProjectWithEarlyRecord();
        createProjectWithLateRecord();
    }

    protected void onPostExecute(Long result) {
        logInfo(getClass().getSimpleName(), "Created test data");
        ottoProvider.getSharedBus().post(new TestdataGeneratedEvent());
    }

    @Override
    protected void validateArguments() throws NullPointerException {
    }

    private void createProjectWithLoooongTitle() {
        Project project = new Project("Donaudampfschifffahrtsgesellschaftskapitaen Heinz Kaluppke");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);

        storeBatchOperation(new ProjectDAO(context).getBatchCreate(project));
        storeBatchOperation(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));
    }

    private void createProjectWithTimeFrame() {
        Project project = new Project("With Timeframe");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setStart(Optional.of(new LocalDate(2016, 12, 24).toDate()));
        trackingConfiguration.setEnd(Optional.of(new LocalDate(2016, 12, 30).toDate()));

        storeBatchOperation(new ProjectDAO(context).getBatchCreate(project));
        storeBatchOperation(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));
    }

    private void createProjectWithTimeFrameAndLimit() {
        Project project = new Project("Timeframe+Limit");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setStart(Optional.of(new LocalDate(2016, 12, 24).toDate()));
        trackingConfiguration.setEnd(Optional.of(new LocalDate(2016, 12, 30).toDate()));
        trackingConfiguration.setHourLimit(Optional.of(100));

        storeBatchOperation(new ProjectDAO(context).getBatchCreate(project));
        storeBatchOperation(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));
    }

    private void createProjectWith200Records(boolean overbooked) {
        String title = overbooked ? "200 Records, Limit 100h overbooked" : "200 Records, Limit 500h";
        Project project = new Project(title);
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setHourLimit(Optional.of(overbooked ? 100 : 500));
        storeBatchOperation(new ProjectDAO(context).getBatchCreate(project));
        storeBatchOperation(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));

        TrackingRecord record;
        LocalDateTime time = new LocalDateTime(2016, 11, 24, 9, 0);
        for (int i = 0; i < 200; i++) {
            record = new TrackingRecord(project.getUuid());
            record.setStart(Optional.of(time.toDate()));
            time = time.plusHours(1);
            record.setEnd(Optional.of(time.toDate()));
            time = time.plusHours(1);
            record.setDescription(Optional.of("Eintrag #" + i));

            storeBatchOperation(new TrackingRecordDAO(context).getBatchCreate(record));
        }
    }

    private void createProjectWithRecordsWithOverlongDescriptions() {
        Project project = new Project("Overlong Record Descriptions");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);

        storeBatchOperation(new ProjectDAO(context).getBatchCreate(project));
        storeBatchOperation(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));

        TrackingRecord record = new TrackingRecord(project.getUuid());
        record.setStart(Optional.of(new LocalDateTime(2016, 11, 24, 9, 0).toDate()));
        record.setEnd(Optional.of(new LocalDateTime(2016, 11, 24, 10, 0).toDate()));
        record.setDescription(Optional.of(aVeryLongRecordDescription()));

        storeBatchOperation(new TrackingRecordDAO(context).getBatchCreate(record));
    }

    private String aVeryLongRecordDescription() {
        return "Dies ist die erste Zeile eines sehr langen Kommentars.\nEr streckt sich ueber 5 Zeilen, dies ist die 2. Zeile.\nNun kommt die 3. Zeile.\n" +
                "In der 4.Zeile steht wiederum ein recht langer Satz drin, der nicht so recht enden will, vielmehr geht er einfach immer weiter.\n" +
                "Am Ende ist dann die 5. Zeile, die diesen Kommentar abschliesst." +
                "Hier kommt noch eine einfache Liste:\n" +
                "* eins\n" +
                "* zwei\n" +
                "* drei";
    }

    private void createProjectWithLongRunningRecord() {
        Project project = new Project("Ongoing Overlong Record");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);

        storeBatchOperation(new ProjectDAO(context).getBatchCreate(project));
        storeBatchOperation(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));

        TrackingRecord record = new TrackingRecord(project.getUuid());
        record.setStart(Optional.of(new LocalDateTime().minusDays(2).toDate()));

        storeBatchOperation(new TrackingRecordDAO(context).getBatchCreate(record));
    }

    private void createProjectWithEarlyRecord() {
        Project project = new Project("Early Record");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setStart(Optional.of(new LocalDate(2016, 12, 24).toDate()));

        storeBatchOperation(new ProjectDAO(context).getBatchCreate(project));
        storeBatchOperation(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));

        TrackingRecord record = new TrackingRecord(project.getUuid());
        record.setStart(Optional.of(new LocalDate(2016, 12, 24).minusDays(2).toDate()));
        record.setEnd(Optional.of(new LocalDate(2016, 12, 24).minusDays(1).toDate()));

        storeBatchOperation(new TrackingRecordDAO(context).getBatchCreate(record));
    }

    private void createProjectWithLateRecord() {
        Project project = new Project("Late Record");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setEnd(Optional.of(new LocalDate(2016, 12, 24).toDate()));

        storeBatchOperation(new ProjectDAO(context).getBatchCreate(project));
        storeBatchOperation(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));

        TrackingRecord record = new TrackingRecord(project.getUuid());
        record.setStart(Optional.of(new LocalDate(2016, 12, 24).plusDays(1).toDate()));
        record.setEnd(Optional.of(new LocalDate(2016, 12, 24).plusDays(2).toDate()));

        storeBatchOperation(new TrackingRecordDAO(context).getBatchCreate(record));
    }
}
