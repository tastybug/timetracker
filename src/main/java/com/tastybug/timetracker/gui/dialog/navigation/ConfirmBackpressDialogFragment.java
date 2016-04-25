package com.tastybug.timetracker.gui.dialog.navigation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.task.OttoEvent;
import com.tastybug.timetracker.task.OttoProvider;

public class ConfirmBackpressDialogFragment extends DialogFragment {

    protected OttoProvider ottoProvider = new OttoProvider();

    private String affectedEntityUuid;

    public static ConfirmBackpressDialogFragment aDialog() {
        return new ConfirmBackpressDialogFragment();
    }

    public ConfirmBackpressDialogFragment forEntityUuid(String affectedEntityUuid) {
        this.affectedEntityUuid = affectedEntityUuid;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.msg_confirm_data_loss_from_backpress)
                .setPositiveButton(R.string.common_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                        ottoProvider.getSharedBus().post(new SaveThenBackpressRequestedEvent(affectedEntityUuid));
                    }
                })
                .setNegativeButton(R.string.common_discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                        ottoProvider.getSharedBus().post(new DiscardThenBackpressRequestedEvent(affectedEntityUuid));
                    }
                });
        return builder.create();
    }

    public static class SaveThenBackpressRequestedEvent implements OttoEvent {

        private String affectedEntityUuid;

        public SaveThenBackpressRequestedEvent(String affectedEntityUuid) {
            this.affectedEntityUuid = affectedEntityUuid;
        }

        public String getAffectedEntityUuid() {
            return affectedEntityUuid;
        }
    }

    public static class DiscardThenBackpressRequestedEvent implements OttoEvent {

        private String affectedEntityUuid;

        public DiscardThenBackpressRequestedEvent(String affectedEntityUuid) {
            this.affectedEntityUuid = affectedEntityUuid;
        }

        public String getAffectedEntityUuid() {
            return affectedEntityUuid;
        }
    }
}
