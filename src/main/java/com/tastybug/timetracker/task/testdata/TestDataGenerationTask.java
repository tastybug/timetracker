package com.tastybug.timetracker.task.testdata;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Optional;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.rounding.Rounding;
import com.tastybug.timetracker.task.TaskPayload;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDataGenerationTask extends TaskPayload {

    public TestDataGenerationTask(Context context) {
        super(context, new OttoProvider());
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.addAll(createProjectWithLoooongTitle());
        operations.addAll(createProjectWithTimeFrame());
        operations.addAll(createProjectWithTimeFrameAndLimit());
        operations.addAll(createProjectWith200Records(false));
        operations.addAll(createProjectWith200Records(true));
        operations.addAll(createProjectWithRecordsWithOverlongDescriptions());
        operations.addAll(createProjectWithLongRunningRecord());
        operations.addAll(createProjectWithEarlyRecord());
        operations.addAll(createProjectWithLateRecord());

        return operations;
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new TestDataGeneratedEvent();
    }

    private List<ContentProviderOperation> createProjectWithLoooongTitle() {
        Project project = new Project("Donaudampfschifffahrtsgesellschaftskapitaen Heinz Kaluppke");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);

        return Arrays.asList(new ProjectDAO(context).getBatchCreate(project),
                new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));
    }

    private List<ContentProviderOperation> createProjectWithTimeFrame() {
        Project project = new Project("With Timeframe");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setStart(Optional.of(new LocalDate(2016, 12, 24).toDate()));
        trackingConfiguration.setEnd(Optional.of(new LocalDate(2016, 12, 30).toDate()));

        return Arrays.asList(new ProjectDAO(context).getBatchCreate(project),
                new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));
    }

    private List<ContentProviderOperation> createProjectWithTimeFrameAndLimit() {
        Project project = new Project("Timeframe+Limit");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setStart(Optional.of(new LocalDate(2016, 12, 24).toDate()));
        trackingConfiguration.setEnd(Optional.of(new LocalDate(2016, 12, 30).toDate()));
        trackingConfiguration.setHourLimit(Optional.of(100));

        return Arrays.asList(new ProjectDAO(context).getBatchCreate(project),
                new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));
    }

    private List<ContentProviderOperation> createProjectWith200Records(boolean overbooked) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        String title = overbooked ? "200 Records, Limit 100h overbooked" : "200 Records, Limit 500h";
        Project project = new Project(title);
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setHourLimit(Optional.of(overbooked ? 100 : 500));
        operations.add(new ProjectDAO(context).getBatchCreate(project));
        operations.add(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));

        TrackingRecord record;
        LocalDateTime time = new LocalDateTime(2016, 11, 24, 9, 0);
        for (int i = 0; i < 200; i++) {
            record = new TrackingRecord(project.getUuid());
            record.setStart(time.toDate());
            time = time.plusHours(1);
            record.setEnd(time.toDate());
            time = time.plusHours(1);
            record.setDescription(Optional.of("Eintrag #" + i));

            operations.add(new TrackingRecordDAO(context).getBatchCreate(record));
        }
        return operations;
    }

    private List<ContentProviderOperation> createProjectWithRecordsWithOverlongDescriptions() {
        Project project = new Project("Overlong Record Descriptions");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);

        TrackingRecord record = new TrackingRecord(project.getUuid());
        record.setStart(new LocalDateTime(2016, 11, 24, 9, 0).toDate());
        record.setEnd(new LocalDateTime(2016, 11, 24, 10, 0).toDate());
        record.setDescription(Optional.of(aVeryLongRecordDescription()));

        return Arrays.asList(new ProjectDAO(context).getBatchCreate(project),
                new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration),
                new TrackingRecordDAO(context).getBatchCreate(record));
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

    private List<ContentProviderOperation> createProjectWithLongRunningRecord() {
        Project project = new Project("Ongoing Overlong Record");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);

        TrackingRecord record = new TrackingRecord(project.getUuid());
        record.setStart(new LocalDateTime().minusDays(2).toDate());

        return Arrays.asList(new ProjectDAO(context).getBatchCreate(project),
                new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration),
                new TrackingRecordDAO(context).getBatchCreate(record));
    }

    private List<ContentProviderOperation> createProjectWithEarlyRecord() {
        Project project = new Project("Early Record");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setStart(Optional.of(new LocalDate(2016, 12, 24).toDate()));

        TrackingRecord record = new TrackingRecord(project.getUuid());
        record.setStart(new LocalDate(2016, 12, 24).minusDays(2).toDate());
        record.setEnd(new LocalDate(2016, 12, 24).minusDays(1).toDate());

        return Arrays.asList(new ProjectDAO(context).getBatchCreate(project),
                new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration),
                new TrackingRecordDAO(context).getBatchCreate(record));
    }

    private List<ContentProviderOperation> createProjectWithLateRecord() {
        Project project = new Project("Late Record");
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        trackingConfiguration.setEnd(Optional.of(new LocalDate(2016, 12, 24).toDate()));

        TrackingRecord record = new TrackingRecord(project.getUuid());
        record.setStart(new LocalDate(2016, 12, 24).plusDays(1).toDate());
        record.setEnd(new LocalDate(2016, 12, 24).plusDays(2).toDate());

        return Arrays.asList(new ProjectDAO(context).getBatchCreate(project),
                new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration),
                new TrackingRecordDAO(context).getBatchCreate(record));
    }

}
