package com.tastybug.timetracker.ui.manualbackup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.task.dataexport.ExportDataTask;

public class ConfirmManualBackupCreationFragment extends DialogFragment {

    public static ConfirmManualBackupCreationFragment aDialog() {
        return new ConfirmManualBackupCreationFragment();
    }

    private String getDialogMessage() {
        return getString(R.string.manual_backup_creation_description_text);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.manual_backup_creation_dialog_title)
                .setMessage(getDialogMessage())
                .setPositiveButton(R.string.manual_backup_submit_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new ExportDataTask(getActivity()).run();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
