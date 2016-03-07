package com.tastybug.timetracker.gui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.trackingrecord.edit.TrackingRecordModificationActivity;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.tracking.ModifyTrackingRecordTask;

public class EditTrackingRecordDescriptionDialogFragment extends DialogFragment {

    private static String TRACKING_RECORD = "TRACKING_RECORD";

    private EditText descriptionEditText;

    private TrackingRecord trackingRecord;


    public static EditTrackingRecordDescriptionDialogFragment aDialog() {
        return new EditTrackingRecordDescriptionDialogFragment();
    }

    public EditTrackingRecordDescriptionDialogFragment forTrackingRecord(TrackingRecord trackingRecord) {
        this.trackingRecord = trackingRecord;
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TRACKING_RECORD, trackingRecord);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            this.trackingRecord = (TrackingRecord) savedInstanceState.getSerializable(TRACKING_RECORD);
        }

        if (trackingRecord == null) {
            throw new IllegalStateException("No tracking record given for modification.");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(prepareView())
                .setPositiveButton(R.string.common_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ModifyTrackingRecordTask
                                .aTask(getActivity())
                                .withTrackingRecordUuid(trackingRecord.getUuid())
                                .withDescription(getDescriptionFromWidget())
                                .execute();
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.button_show_tracking_record_editing_activity, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showTrackingRecordEditingActivity(trackingRecord);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.common_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    private void showTrackingRecordEditingActivity(TrackingRecord trackingRecord) {
        Intent intent = new Intent(getActivity(), TrackingRecordModificationActivity.class);
        intent.putExtra(TrackingRecordModificationActivity.TRACKING_RECORD_UUID, trackingRecord.getUuid());
        startActivity(intent);
    }

    private View prepareView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_tracking_record_description_editing, null);
        descriptionEditText = (EditText) rootView.findViewById(R.id.tracking_record_description);

        renderExistingDescription(trackingRecord);

        return rootView;
    }

    private void renderExistingDescription(TrackingRecord record) {
        if (record.getDescription().isPresent()) {
            descriptionEditText.setText(record.getDescription().get());
        }
    }

    private Optional<String> getDescriptionFromWidget() {
        return TextUtils.isEmpty(descriptionEditText.getText())
                ? Optional.<String>absent()
                : Optional.of(descriptionEditText.getText().toString());
    }
}