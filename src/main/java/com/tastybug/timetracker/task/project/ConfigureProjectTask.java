package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.rounding.Rounding;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import java.util.Date;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class ConfigureProjectTask extends AbstractAsyncTask {

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String PROJECT_TITLE = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";
    private static final String HOUR_LIMIT = "HOUR_LIMIT";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE_INCLUSIVE = "END_DATE_INCLUSIVE";
    private static final String PROMPT_FOR_DESCRIPTION = "PROMPT_FOR_DESCRIPTION";
    private static final String ROUNDING_STRATEGY = "ROUNDING_STRATEGY";

    private ConfigureProjectTask(Context context) {
        super(context);
    }

    public static ConfigureProjectTask aTask(Context context) {
        return new ConfigureProjectTask(context);
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

    public ConfigureProjectTask withPromptForDescription(Boolean promptForDescription) {
        arguments.putBoolean(PROMPT_FOR_DESCRIPTION, promptForDescription);
        return this;
    }

    public ConfigureProjectTask withRoundingStrategy(Rounding.Strategy strategy) {
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

        if (arguments.containsKey(PROJECT_TITLE)) {
            project.setTitle(arguments.getString(PROJECT_TITLE));
        }

        if (arguments.containsKey(PROJECT_DESCRIPTION)) {
            project.setDescription(Optional.fromNullable(arguments.getString(PROJECT_DESCRIPTION)));
        }

        if (arguments.containsKey(HOUR_LIMIT)) {
            trackingConfiguration.setHourLimit(Optional.fromNullable((Integer) arguments.getSerializable(HOUR_LIMIT)));
        }

        if (arguments.containsKey(START_DATE)) {
            if (arguments.containsKey(END_DATE_INCLUSIVE)) {
                // prevent date validation false alarms while setting
                // a new start date that might be after the OLD end date
                trackingConfiguration.setEnd(Optional.<Date>absent());
            }
            trackingConfiguration.setStart(Optional.fromNullable((Date) arguments.getSerializable(START_DATE)));
        }

        if (arguments.containsKey(END_DATE_INCLUSIVE)) {
            trackingConfiguration.setEndAsInclusive(Optional.fromNullable((Date) arguments.getSerializable(END_DATE_INCLUSIVE)));
        }

        if (arguments.containsKey(PROMPT_FOR_DESCRIPTION)) {
            trackingConfiguration.setPromptForDescription(arguments.getBoolean(PROMPT_FOR_DESCRIPTION, false));
        }

        if (arguments.containsKey(ROUNDING_STRATEGY)) {
            trackingConfiguration.setRoundingStrategy((Rounding.Strategy) arguments.getSerializable(ROUNDING_STRATEGY));
        }

        storeBatchOperation(projectDAO.getBatchUpdate(project));
        storeBatchOperation(trackingConfigurationDAO.getBatchUpdate(trackingConfiguration));
    }

    protected void onPostExecute(Long result) {
        logInfo(getClass().getSimpleName(), "Configured project with UUID " + arguments.getString(PROJECT_UUID) + " with arguments: " + arguments);
        ottoProvider.getSharedBus().post(new ProjectConfiguredEvent(arguments.getString(PROJECT_UUID)));
    }

}


