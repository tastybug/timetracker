package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TimeFrameRounding;
import com.tastybug.timetracker.model.TrackingConfiguration;

import java.util.Date;

public class ConfigureProjectTask extends AbstractAsyncTask {

    private static final String TAG = ConfigureProjectTask.class.getSimpleName();

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String PROJECT_TITLE = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";
    private static final String HOUR_LIMIT = "HOUR_LIMIT";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE_INCLUSIVE = "END_DATE_INCLUSIVE";
    private static final String ROUNDING_STRATEGY = "ROUNDING_STRATEGY";

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
        arguments.putSerializable(HOUR_LIMIT, hourLimit);
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

    public ConfigureProjectTask withRoundingStrategy(TimeFrameRounding.Strategy strategy) {
        arguments.putSerializable(ROUNDING_STRATEGY, strategy);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_UUID));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        ProjectDAO projectDAO = new ProjectDAO(context);
        TrackingConfigurationDAO trackingConfigurationDAO = new TrackingConfigurationDAO(context);
        Project project = projectDAO.get(args.getString(PROJECT_UUID)).get();
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(project.getUuid()).get();

        /*
            Note: both entities dont have a context as we want to alter the entities in a transactional way!
         */

        if(arguments.containsKey(PROJECT_TITLE)) {
            project.setTitle(arguments.getString(PROJECT_TITLE));
        }

        if(arguments.containsKey(PROJECT_DESCRIPTION)) {
            project.setDescription(Optional.fromNullable(arguments.getString(PROJECT_DESCRIPTION)));
        }

        if(arguments.containsKey(HOUR_LIMIT)) {
            trackingConfiguration.setHourLimit(Optional.fromNullable((Integer) arguments.getSerializable(HOUR_LIMIT)));
        }

        if(arguments.containsKey(START_DATE)) {
            trackingConfiguration.setStart(Optional.fromNullable((Date) arguments.getSerializable(START_DATE)));
        }

        if(arguments.containsKey(END_DATE_INCLUSIVE)) {
            trackingConfiguration.setEndAsInclusive(Optional.fromNullable((Date) arguments.getSerializable(END_DATE_INCLUSIVE)));
        }

        if(arguments.containsKey(ROUNDING_STRATEGY)) {
            trackingConfiguration.setRoundingStrategy((TimeFrameRounding.Strategy) arguments.getSerializable(ROUNDING_STRATEGY));
        }

        storeBatchOperation(projectDAO.getBatchUpdate(project));
        storeBatchOperation(trackingConfigurationDAO.getBatchUpdate(trackingConfiguration));
    }

    protected void onPostExecute(Long result) {
        Log.i(TAG, "Configured project with UUID " + arguments.getString(PROJECT_UUID) + " with arguments: " + arguments);
        ottoProvider.getSharedBus().post(new ProjectConfiguredEvent(arguments.getString(PROJECT_UUID)));
    }

}


