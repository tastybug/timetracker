package com.tastybug.timetracker.task.dataimport;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;

import java.util.ArrayList;
import java.util.List;

class DbWipeBatchOpsProvider {

    private ProjectDAO projectDAO;

    DbWipeBatchOpsProvider(Context context) {
        this(new ProjectDAO(context));
    }

    DbWipeBatchOpsProvider(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    List<ContentProviderOperation> getOperations() {
        ArrayList<ContentProviderOperation> operationArrayList = new ArrayList<>();
        for (Project project : projectDAO.getAll()) {
            operationArrayList.add(projectDAO.getBatchDeleteAll(project));
        }
        return operationArrayList;
    }
}
