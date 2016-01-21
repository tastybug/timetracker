package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.ProjectTimeConstraintsDAO;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.ProjectTimeConstraints;

import java.util.Date;

public class ConfigureProjectTask extends AbstractAsyncTask {

    private static final String TAG = ConfigureProjectTask.class.getSimpleName();

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String PROJECT_TITLE = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";
    private static final String HOUR_LIMIT = "HOUR_LIMIT";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE_INCLUSIVE = "END_DATE_INCLUSIVE";

    public static ConfigureProjectTask aTask(Context context) {
        return new ConfigureProjectTask(context);
    }

    private ConfigureProjectTask(Context context) {
        super(context);
    }

    public ConfigureProjectTask withProjectUuid(String uuid) {
        arguments.putString(PROJECT_UUID, uuid);
        return this;
    }

    public ConfigureProjectTask withProjectTitle(String title) {
        arguments.putString(PROJECT_TITLE, title);
        return this;
    }

    public ConfigureProjectTask withProjectDescription(String description) {
        arguments.putString(PROJECT_DESCRIPTION, description);
        return this;
    }

    public ConfigureProjectTask withHourLimit(Integer hourLimit) {
        arguments.putInt(HOUR_LIMIT, hourLimit);
        return this;
    }

    public ConfigureProjectTask withStartDate(Date date) {
        arguments.putSerializable(START_DATE, date);
        return this;
    }

    public ConfigureProjectTask withInclusiveEndDate(Date date) {
        arguments.putSerializable(END_DATE_INCLUSIVE, date);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_UUID));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        ProjectDAO projectDAO = new ProjectDAO(context);
        ProjectTimeConstraintsDAO constraintsDAO = new ProjectTimeConstraintsDAO(context);
        Project project = projectDAO.get(args.getString(PROJECT_UUID)).get();
        ProjectTimeConstraints timeConstraints = constraintsDAO.getByProjectUuid(project.getUuid()).get();

        /*
            Note: both entities dont have a context as we want to alter the entities in a transactional way!
         */

        if(arguments.containsKey(PROJECT_TITLE)) {
            project.setTitle(arguments.getString(PROJECT_TITLE));
        }

        if(arguments.containsKey(PROJECT_DESCRIPTION)) {
            project.setDescription(Optional.of(arguments.getString(PROJECT_DESCRIPTION)));
        }

        if(arguments.containsKey(HOUR_LIMIT)) {
            timeConstraints.setHourLimit(Optional.of(arguments.getInt(HOUR_LIMIT)));
        }

        if(arguments.containsKey(START_DATE)) {
            timeConstraints.setStart(Optional.fromNullable((Date) arguments.getSerializable(START_DATE)));
        }

        if(arguments.containsKey(END_DATE_INCLUSIVE)) {
            timeConstraints.setEndAsInclusive(Optional.fromNullable((Date)arguments.getSerializable(END_DATE_INCLUSIVE)));
        }

        storeBatchOperation(projectDAO.getBatchUpdate(project));
        storeBatchOperation(constraintsDAO.getBatchUpdate(timeConstraints));
    }

    protected void onPostExecute(Long result) {
        Log.i(TAG, "Configured project with UUID " + arguments.getString(PROJECT_UUID) + " with arguments: " + arguments);
        ottoProvider.getSharedBus().post(new ProjectConfiguredEvent(arguments.getString(PROJECT_UUID)));
    }

}


