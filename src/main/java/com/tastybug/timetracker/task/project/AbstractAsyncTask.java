package com.tastybug.timetracker.task.project;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;

import com.tastybug.timetracker.database.dao.EntityDAO;
import com.tastybug.timetracker.task.OttoProvider;

import org.slf4j.Logger;

import java.util.ArrayList;


public abstract class AbstractAsyncTask extends AsyncTask<Bundle, Integer, Long> {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    protected OttoProvider ottoProvider = new OttoProvider();
    protected Bundle arguments = new Bundle();
    protected Context context;
    protected ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

    protected AbstractAsyncTask(Context context) {
        this.context = context;
    }

    protected abstract void validateArguments() throws NullPointerException;

    public void setOttoProvider(OttoProvider ottoProvider) {
        this.ottoProvider = ottoProvider;
    }

    public void execute() throws NullPointerException {
        validateArguments();
        execute(arguments);
    }

    protected Long doInBackground(Bundle... params) {

        logger.debug("Performing background stuff..");
        performBackgroundStuff(params[0]);
        logger.debug("Persisting to database..");
        executeBatchOperations();

        return 0L;
    }

    protected abstract void performBackgroundStuff(Bundle args);

    protected void storeBatchOperation(ContentProviderOperation operation) {
        operations.add(operation);
    }

    protected ContentProviderResult[] executeBatchOperations() {
        try {
            if (operations.isEmpty()) {
                return new ContentProviderResult[0];
            }

            return context.getContentResolver().applyBatch(EntityDAO.AUTHORITY, operations);
        } catch (RemoteException e) {
            logger.error("Problem executing sql batch operation.", e);
            throw new RuntimeException("Problem executing sql batch operation.", e);
        } catch (OperationApplicationException e) {
            logger.error("Problem executing sql batch operation.", e);
            throw new RuntimeException("Problem executing sql batch operation.", e);
        }
    }

    protected void onPostExecute(Long result) {
        // notify otto
        // and update the test
    }
}


