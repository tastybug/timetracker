package com.tastybug.timetracker.extension.demodata.controller;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.model.rounding.Rounding;
import com.tastybug.timetracker.core.task.TaskPayload;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DemoDataGenerationTask extends TaskPayload {

    private static final DateTime NOW = DateTime.now();

    DemoDataGenerationTask(Context context) {
        super(context);
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.addAll(prepareDemoProject1());
        operations.addAll(prepareDemoProject2());

        return operations;
    }

    @Override
    protected OttoEvent preparePostEvent() {
        context.sendBroadcast(new DemoDataCreatedIntent());
        return new DemoDataGeneratedEvent();
    }

    private List<ContentProviderOperation> prepareDemoProject1() {
        Project project = new Project(context.getString(R.string.demo_project_1_title));
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        project.setClosed(true);
        project.setDescription(Optional.of(context.getString(R.string.demo_project_1_description)));
        project.setContractId(Optional.of("Client 4 - 06/17"));
        trackingConfiguration.setStart(Optional.of(new DateTime(NOW.getYear(), 3, 1, 0, 0, 0).toDate()));
        trackingConfiguration.setEnd(Optional.of(new DateTime(NOW.getYear(), 3, 31, 0, 0, 0).toDate()));
        trackingConfiguration.setHourLimit(Optional.of(50));

        TrackingRecord trackingRecord1 = new TrackingRecord(project.getUuid(), trackingConfiguration.getRoundingStrategy());
        trackingRecord1.setDescription(Optional.of(context.getString(R.string.demo_project_1_tracking_record_1_description)));
        trackingRecord1.setStart(new DateTime(NOW.getYear(), 3, 1, 12, 0, 0).toDate());
        trackingRecord1.setEnd(new DateTime(NOW.getYear(), 3, 1, 19, 30, 0).toDate());

        TrackingRecord trackingRecord2 = new TrackingRecord(project.getUuid(), trackingConfiguration.getRoundingStrategy());
        trackingRecord2.setDescription(Optional.of(context.getString(R.string.demo_project_1_tracking_record_2_description)));
        trackingRecord2.setStart(new DateTime(NOW.getYear(), 3, 2, 9, 0, 0).toDate());
        trackingRecord2.setEnd(new DateTime(NOW.getYear(), 3, 2, 15, 30, 0).toDate());

        TrackingRecord trackingRecord3 = new TrackingRecord(project.getUuid(), trackingConfiguration.getRoundingStrategy());
        trackingRecord3.setDescription(Optional.of(context.getString(R.string.demo_project_1_tracking_record_3_description)));
        trackingRecord3.setStart(new DateTime(NOW.getYear(), 3, 3, 9, 0, 0).toDate());
        trackingRecord3.setEnd(new DateTime(NOW.getYear(), 3, 3, 17, 0, 0).toDate());

        TrackingRecord trackingRecord4 = new TrackingRecord(project.getUuid(), trackingConfiguration.getRoundingStrategy());
        trackingRecord4.setDescription(Optional.of(context.getString(R.string.demo_project_1_tracking_record_4_description)));
        trackingRecord4.setStart(new DateTime(NOW.getYear(), 3, 4, 9, 0, 0).toDate());
        trackingRecord4.setEnd(new DateTime(NOW.getYear(), 3, 4, 17, 40, 0).toDate());

        TrackingRecord trackingRecord5 = new TrackingRecord(project.getUuid(), trackingConfiguration.getRoundingStrategy());
        trackingRecord5.setDescription(Optional.of(context.getString(R.string.demo_project_1_tracking_record_5_description)));
        trackingRecord5.setStart(new DateTime(NOW.getYear(), 3, 5, 12, 0, 0).toDate());
        trackingRecord5.setEnd(new DateTime(NOW.getYear(), 3, 5, 15, 30, 0).toDate());

        return Arrays.asList(new ProjectDAO(context).getBatchCreate(project),
                new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration),
                new TrackingRecordDAO(context).getBatchCreate(trackingRecord1),
                new TrackingRecordDAO(context).getBatchCreate(trackingRecord2),
                new TrackingRecordDAO(context).getBatchCreate(trackingRecord3),
                new TrackingRecordDAO(context).getBatchCreate(trackingRecord4),
                new TrackingRecordDAO(context).getBatchCreate(trackingRecord5));
    }

    private List<ContentProviderOperation> prepareDemoProject2() {

        Project project = new Project(context.getString(R.string.demo_project_2_title));
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid(), Rounding.Strategy.NO_ROUNDING);
        project.setDescription(Optional.of(context.getString(R.string.demo_project_2_description)));
        project.setContractId(Optional.of(context.getString(R.string.demo_project_2_contract_id)));
        trackingConfiguration.setStart(Optional.of(new DateTime(NOW.getYear(), 2, 1, 0, 0, 0).toDate()));
        trackingConfiguration.setEnd(Optional.of(new DateTime(NOW.getYear(), 12, 31, 0, 0, 0).toDate()));
        trackingConfiguration.setHourLimit(Optional.of(750));

        TrackingRecord trackingRecord1 = new TrackingRecord(project.getUuid(), trackingConfiguration.getRoundingStrategy());
        trackingRecord1.setDescription(Optional.of(context.getString(R.string.demo_project_2_tracking_record_1_description)));
        trackingRecord1.setStart(new DateTime(NOW.getYear(), 2, 1, 12, 0, 0).toDate());
        trackingRecord1.setEnd(new DateTime(NOW.getYear(), 2, 1, 19, 30, 0).toDate());

        TrackingRecord trackingRecord2 = new TrackingRecord(project.getUuid(), trackingConfiguration.getRoundingStrategy());
        trackingRecord2.setDescription(Optional.of(context.getString(R.string.demo_project_2_tracking_record_2_description)));
        trackingRecord2.setStart(new DateTime(NOW.getYear(), 2, 5, 9, 0, 0).toDate());
        trackingRecord2.setEnd(new DateTime(NOW.getYear(), 2, 5, 15, 30, 0).toDate());

        TrackingRecord trackingRecord3 = new TrackingRecord(project.getUuid(), trackingConfiguration.getRoundingStrategy());
        trackingRecord3.setDescription(Optional.of(context.getString(R.string.demo_project_2_tracking_record_3_description)));
        trackingRecord3.setStart(new DateTime(NOW.getYear(), 2, 7, 9, 0, 0).toDate());
        trackingRecord3.setEnd(new DateTime(NOW.getYear(), 2, 7, 17, 0, 0).toDate());

        TrackingRecord trackingRecord4 = new TrackingRecord(project.getUuid(), trackingConfiguration.getRoundingStrategy());
        trackingRecord4.setDescription(Optional.of(context.getString(R.string.demo_project_2_tracking_record_4_description)));
        trackingRecord4.setStart(new DateTime(NOW.getYear(), 2, 10, 9, 0, 0).toDate());
        trackingRecord4.setEnd(new DateTime(NOW.getYear(), 2, 10, 17, 40, 0).toDate());

        return Arrays.asList(new ProjectDAO(context).getBatchCreate(project),
                new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration),
                new TrackingRecordDAO(context).getBatchCreate(trackingRecord1),
                new TrackingRecordDAO(context).getBatchCreate(trackingRecord2),
                new TrackingRecordDAO(context).getBatchCreate(trackingRecord3),
                new TrackingRecordDAO(context).getBatchCreate(trackingRecord4));
    }

}
