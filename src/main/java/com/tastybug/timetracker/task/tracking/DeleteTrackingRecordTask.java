package com.tastybug.timetracker.task.tracking;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import java.util.Collections;
import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class DeleteTrackingRecordTask extends AbstractAsyncTask {

    private static final String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";

    private DeleteTrackingRecordTask(Context context) {
        super(context);
    }

    public static DeleteTrackingRecordTask aTask(Context context) {
        return new DeleteTrackingRecordTask(context);
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
    protected List<ContentProviderOperation> performBackgroundStuff(Bundle args) {
        String uuid = arguments.getString(TRACKING_RECORD_UUID);
        new TrackingRecordDAO(context).delete(uuid);

        return Collections.emptyList();
    }

    protected void onPostExecute(Long result) {
        String uuid = arguments.getString(TRACKING_RECORD_UUID);
        logInfo(getClass().getSimpleName(), "Deleted tracking record " + uuid);
        ottoProvider.getSharedBus().post(new DeletedTrackingRecordEvent(uuid));
    }
}
