package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.model.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ConfigureProjectTask extends AbstractAsyncTask {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigureProjectTask.class);

    private static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String PROJECT_TITLE = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";

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

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_UUID));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        ProjectDAO dao = new ProjectDAO(context);
        Project project = dao.get(args.getString(PROJECT_UUID)).get();

        if(arguments.containsKey(PROJECT_TITLE)) {
            project.setTitle(arguments.getString(PROJECT_TITLE));
        }

        if(arguments.containsKey(PROJECT_DESCRIPTION)) {
            project.setDescription(Optional.of(arguments.getString(PROJECT_DESCRIPTION)));
        }

        storeBatchOperation(dao.getBatchUpdate(project));
    }

    protected void onPostExecute(Long result) {
        LOG.info("Configured project with UUID " + arguments.getString(PROJECT_UUID));
        ottoProvider.getSharedBus().post(new ProjectConfiguredEvent(arguments.getString(PROJECT_UUID)));
    }

}


