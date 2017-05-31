package com.tastybug.timetracker.extension.backup.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.ui.dashboard.ProjectsActivity;
import com.tastybug.timetracker.extension.backup.controller.dataexport.DataExportedEvent;
import com.tastybug.timetracker.extension.backup.controller.dataimport.ImportDataTask;
import com.tastybug.timetracker.extension.backup.controller.dataimport.ImportedDataEvent;
import com.tastybug.timetracker.extension.backup.controller.localbackup.LocalBackupService;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class BackupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.backup_activity_title);

        if (isRestorationFromLocal(getIntent())) {
            showConfirmImportFromLastBackupDialog();
        } else if (isCreateManualBackup(getIntent())) {
            showBackupDialog();
        } else {
            logInfo(getClass().getSimpleName(), "Received intent for data: " + getIntent().getData() + ", scheme: " + getIntent().getScheme() + ", type: " + getIntent().getType());
            showConfirmImportFromExternalSourceDialog();
        }
    }

    private boolean isRestorationFromLocal(Intent intent) {
        return intent.getAction().equals(getString(R.string.internal_action_restore_last_backup));
    }

    private boolean isCreateManualBackup(Intent intent) {
        return intent.getAction().equals(getString(R.string.internal_action_create_and_share_backup));
    }

    private LocalBackupService getLocalBackupService() {
        return new LocalBackupService(getApplicationContext());
    }

    private Date getLastBackupDate() {
        File backupFile = getLocalBackupService().getBackupFile();
        return new Date(backupFile.lastModified());
    }

    private void showNoLocalBackupAvailableMessage() {
        Toast.makeText(getApplicationContext(), "Sorry, no backup found.", Toast.LENGTH_LONG).show();
    }

    private void showBackupDialog() {
        ConfirmManualBackupCreationFragment.aDialog().show(getFragmentManager(), getClass().getSimpleName());
    }

    private void showConfirmImportFromLastBackupDialog() {
        final byte[] data = getLocalBackupService().getBackupData();
        if (data.length > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Dialog dialog = builder.setTitle(R.string.restore_backup_dialog_title)
                    .setMessage(getString(R.string.restore_backup_from_X, DefaultLocaleDateFormatter.dateTime().format(getLastBackupDate())))
                    .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplication(), R.string.data_import_starting, Toast.LENGTH_LONG).show();
                            new ImportDataTask(getApplication()).withData(data).run();
                        }
                    })
                    .setNegativeButton(R.string.common_discard, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            showProjectsActivity();
                        }
                    }).create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            showNoLocalBackupAvailableMessage();
            showProjectsActivity();
        }
    }

    private void showConfirmImportFromExternalSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Dialog dialog = builder.setTitle(R.string.import_data_dialog_title)
                .setMessage(R.string.import_data_dialog_message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplication(), R.string.data_import_starting, Toast.LENGTH_LONG).show();
                        new ImportDataTask(getApplication()).withDataUri(getIntent().getData()).run();
                    }
                })
                .setNegativeButton(R.string.common_discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showProjectsActivity();
                    }
                }).create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleDataImported(ImportedDataEvent event) {
        Toast.makeText(getApplication(), R.string.data_import_done, Toast.LENGTH_SHORT).show();
        showProjectsActivity();
    }

    protected void showProjectsActivity() {
        Intent intent = new Intent(getApplicationContext(), ProjectsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleDataExported(final DataExportedEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            try {
                Intent intent = new ShareManualBackupIntentFactory(BackupActivity.this).create(event.getData());
                startActivity(intent);
            } catch (IOException e) {
                throw new RuntimeException("Failed to share exported data.", e);
            }

            }
        });
    }
}
