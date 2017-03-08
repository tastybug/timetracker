package com.tastybug.timetracker.core.task;


import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.tastybug.timetracker.core.model.dao.EntityDAO;

import java.util.ArrayList;
import java.util.List;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logError;

public class BatchOperationExecutor {

    private ContentResolver contentResolver;

    public BatchOperationExecutor(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    ContentProviderResult[] executeBatch(List<ContentProviderOperation> batchOperations) {
        try {
            return contentResolver.applyBatch(EntityDAO.AUTHORITY, new ArrayList<>(batchOperations));
        } catch (RemoteException | OperationApplicationException e) {
            logError(getClass().getSimpleName(), "Problem executing sql batch operation.", e);
            throw new RuntimeException("Problem executing sql batch operation.", e);
        }
    }
}
