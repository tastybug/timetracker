package com.tastybug.timetracker.task.project.update;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.rounding.Rounding;
import com.tastybug.timetracker.task.TaskPayload;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UpdateProjectTask extends TaskPayload {

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String PROJECT_TITLE = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";
    private static final String HOUR_LIMIT = "HOUR_LIMIT";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE_INCLUSIVE = "END_DATE_INCLUSIVE";
    private static final String PROMPT_FOR_DESCRIPTION = "PROMPT_FOR_DESCRIPTION";
    private static final String ROUNDING_STRATEGY = "ROUNDING_STRATEGY";
    private static final String CLOSED = "CLOSED";

    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;

    public UpdateProjectTask(Context context) {
        this(context, new ProjectDAO(context), new TrackingConfigurationDAO(context));
    }

    UpdateProjectTask(Context context, ProjectDAO projectDAO, TrackingConfigurationDAO trackingConfigurationDAO) {
        super(context);
        this.projectDAO = projectDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
    }

    public UpdateProjectTask withProjectUuid(String uuid) {
        arguments.putString(PROJECT_UUID, uuid);
        return this;
    }

    public UpdateProjectTask withProjectTitle(String title) {
        arguments.putString(PROJECT_TITLE, title);
        return this;
    }

    public UpdateProjectTask withProjectDescription(String description) {
        arguments.putString(PROJECT_DESCRIPTION, description);
        return this;
    }

    public UpdateProjectTask withoutProjectDescription() {
        return withProjectDescription(null);
    }

    public UpdateProjectTask withHourLimit(Integer hourLimit) {
        arguments.putSerializable(HOUR_LIMIT, hourLimit);
        return this;
    }

    public UpdateProjectTask withoutHourLimit() {
        return withHourLimit(null);
    }

    public UpdateProjectTask withStartDate(Date date) {
        arguments.putSerializable(START_DATE, date);
        return this;
    }

    public UpdateProjectTask withInclusiveEndDate(Date date) {
        arguments.putSerializable(END_DATE_INCLUSIVE, date);
        return this;
    }

    public UpdateProjectTask withPromptForDescription(Boolean promptForDescription) {
        arguments.putBoolean(PROMPT_FOR_DESCRIPTION, promptForDescription);
        return this;
    }

    public UpdateProjectTask withRoundingStrategy(Rounding.Strategy strategy) {
        arguments.putSerializable(ROUNDING_STRATEGY, strategy);
        return this;
    }

    public UpdateProjectTask withClosureState(boolean toBeClosed) {
        arguments.putBoolean(CLOSED, toBeClosed);
        return this;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_UUID));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        Project project = projectDAO.get(arguments.getString(PROJECT_UUID)).get();
        TrackingConfiguration trackingConfiguration = trackingConfigurationDAO.getByProjectUuid(project.getUuid()).get();

        if (arguments.containsKey(PROJECT_TITLE)) {
            project.setTitle(arguments.getString(PROJECT_TITLE));
        }

        if (arguments.containsKey(PROJECT_DESCRIPTION)) {
            project.setDescription(Optional.fromNullable(arguments.getString(PROJECT_DESCRIPTION)));
        }

        if (arguments.containsKey(CLOSED)) {
            project.setClosed(arguments.getBoolean(CLOSED));
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

        return Arrays.asList(projectDAO.getBatchUpdate(project),
                trackingConfigurationDAO.getBatchUpdate(trackingConfiguration));
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new UpdateProjectEvent(arguments.getString(PROJECT_UUID),
                (arguments.containsKey(CLOSED) && arguments.getBoolean(CLOSED)));
    }
}


