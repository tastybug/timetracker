package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.ProjectTimeConstraintsDAO;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.ProjectTimeConstraints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ConfigureProjectTask extends AbstractAsyncTask {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigureProjectTask.class);

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String PROJECT_TITLE = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";
    private static final String HOUR_LIMIT = "HOUR_LIMIT";

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

        storeBatchOperation(projectDAO.getBatchUpdate(project));
        storeBatchOperation(constraintsDAO.getBatchUpdate(timeConstraints));
    }

    protected void onPostExecute(Long result) {
        LOG.info("Configured project with UUID " + arguments.getString(PROJECT_UUID));
        ottoProvider.getSharedBus().post(new ProjectConfiguredEvent(arguments.getString(PROJECT_UUID)));
    }

}


