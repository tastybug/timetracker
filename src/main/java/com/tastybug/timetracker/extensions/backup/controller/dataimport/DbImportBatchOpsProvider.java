package com.tastybug.timetracker.extensions.backup.controller.dataimport;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import java.util.ArrayList;
import java.util.List;

class DbImportBatchOpsProvider {

    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;
    private TrackingRecordDAO trackingRecordDAO;

    DbImportBatchOpsProvider(Context context) {
        this(new ProjectDAO(context),
                new TrackingConfigurationDAO(context),
                new TrackingRecordDAO(context));
    }

    DbImportBatchOpsProvider(ProjectDAO projectDAO, TrackingConfigurationDAO trackingConfigurationDAO, TrackingRecordDAO trackingRecordDAO) {
        this.projectDAO = projectDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
        this.trackingRecordDAO = trackingRecordDAO;
    }

    List<ContentProviderOperation> getOperations(List<Project> importableProjectList) {
        ArrayList<ContentProviderOperation> operationArrayList = new ArrayList<>();

        for (Project importableProject : importableProjectList) {
            operationArrayList.add(projectDAO.getBatchCreate(importableProject));
            operationArrayList.add(trackingConfigurationDAO.getBatchCreate(importableProject.getTrackingConfiguration()));
            for (TrackingRecord importableTrackingRecord : importableProject.getTrackingRecords()) {
                operationArrayList.add(trackingRecordDAO.getBatchCreate(importableTrackingRecord));
            }
        }

        return operationArrayList;
    }
}
