package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.task.AbstractAsyncTask;


public class DeleteProjectTask extends AbstractAsyncTask {

    private static final String PROJECT_UUID = "PROJECT_UUID";

    public static DeleteProjectTask aTask(Context context) {
        return new DeleteProjectTask(context);
    }

    protected DeleteProjectTask(Context context) {
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
        new ProjectDAO(context).delete(args.getString(PROJECT_UUID));
    }

    protected void onPostExecute(Long result) {
        Log.i(getClass().getSimpleName(), "Deleted project " + arguments.getString(PROJECT_UUID));
        ottoProvider.getSharedBus().post(new ProjectDeletedEvent(arguments.getString(PROJECT_UUID)));
    }

}


