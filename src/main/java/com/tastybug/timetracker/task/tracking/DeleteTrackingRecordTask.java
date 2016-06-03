package com.tastybug.timetracker.task.tracking;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.BuildConfig;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.AbstractAsyncTask;

public class DeleteTrackingRecordTask extends AbstractAsyncTask {

    private static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";

    public static DeleteTrackingRecordTask aTask(Context context) {
        return new DeleteTrackingRecordTask(context);
    }

    protected DeleteTrackingRecordTask(Context context) {
        super(context);
    }

    public DeleteTrackingRecordTask withTrackingRecordUuid(String uuid) {
        arguments.putString(TRACKING_RECORD_UUID, uuid);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getString(TRACKING_RECORD_UUID));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        String uuid = arguments.getString(TRACKING_RECORD_UUID);
        new TrackingRecordDAO(context).delete(uuid);
    }

    protected void onPostExecute(Long result) {
        String uuid = arguments.getString(TRACKING_RECORD_UUID);
        if (BuildConfig.DEBUG) {
            Log.i(getClass().getSimpleName(), "Deleted tracking record " + uuid);
        }
        ottoProvider.getSharedBus().post(new DeletedTrackingRecordEvent(uuid));
    }
}
