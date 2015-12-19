package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.ProjectDAO;


public class DeleteProjectTask extends AbstractAsyncTask {

    private static final String PROJECT_UUID = "PROJECT_UUID";

    public static DeleteProjectTask aTask(Context context) {
        return new DeleteProjectTask(context);
    }

    private DeleteProjectTask(Context context) {
        super(context);
    }

    public DeleteProjectTask withProjectUuid(String uuid) {
        arguments.putString(PROJECT_UUID, uuid);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_UUID));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        boolean success = new ProjectDAO(context).delete(args.getString(PROJECT_UUID));

        // TODO: dem event die betroffene UUID mit auf den Weg geben
    }
}


