package com.tastybug.timetracker.task;


import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.tastybug.timetracker.model.dao.EntityDAO;

import java.util.ArrayList;

import static com.tastybug.timetracker.util.ConditionalLog.logError;

public class BatchOperationExecutor {

    private ArrayList<ContentProviderOperation> operations = new ArrayList<>();

    private ContentResolver contentResolver;

    public BatchOperationExecutor(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void addOperation(ContentProviderOperation operation) {
        operations.add(operation);
    }

    ContentProviderResult[] executeBatch() {
        try {
            return contentResolver.applyBatch(EntityDAO.AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            logError(getClass().getSimpleName(), "Problem executing sql batch operation.", e);
            throw new RuntimeException("Problem executing sql batch operation.", e);
        }
    }
}
