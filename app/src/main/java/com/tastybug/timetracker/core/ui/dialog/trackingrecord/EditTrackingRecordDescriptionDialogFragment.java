package com.tastybug.timetracker.core.ui.dialog.trackingrecord;

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
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordTask;
import com.tastybug.timetracker.core.ui.trackingrecordmodification.TrackingRecordModificationActivity;

public class EditTrackingRecordDescriptionDialogFragment extends DialogFragment {

    private static String TRACKING_RECORD_UUID = "TRACKING_RECORD_UUID";
    private static String TRACKING_RECORD_DESCRIPTION = "TRACKING_RECORD_DESCRIPTION";

    private EditText descriptionEditText;

    private String trackingRecordUuid;
    private Optional<String> descriptionOptional;

    public static EditTrackingRecordDescriptionDialogFragment aDialog() {
        return new EditTrackingRecordDescriptionDialogFragment();
    }

    public EditTrackingRecordDescriptionDialogFragment forTrackingRecord(TrackingRecord trackingRecord) {
        this.trackingRecordUuid = trackingRecord.getUuid();
        this.descriptionOptional = trackingRecord.getDescription();
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TRACKING_RECORD_UUID, trackingRecordUuid);
        if (getDescriptionFromWidget().isPresent()) {
            outState.putString(TRACKING_RECORD_DESCRIPTION, getDescriptionFromWidget().get());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.trackingRecordUuid = savedInstanceState.getString(TRACKING_RECORD_UUID);
            this.descriptionOptional = Optional.fromNullable(savedInstanceState.getString(TRACKING_RECORD_DESCRIPTION));
        }

        if (trackingRecordUuid == null) {
            throw new IllegalStateException("No tracking record data given for modification.");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(prepareView(descriptionOptional))
                .setPositiveButton(R.string.common_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new UpdateTrackingRecordTask(getActivity())
                                .withTrackingRecordUuid(trackingRecordUuid)
                                .withDescription(getDescriptionFromWidget())
                                .run();
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.button_show_tracking_record_editing_activity, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showTrackingRecordEditingActivity(trackingRecordUuid);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.common_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);
        return dialog;
    }

    private void showTrackingRecordEditingActivity(String trackingRecordUuid) {
        Intent intent = new Intent(getActivity(), TrackingRecordModificationActivity.class);
        intent.putExtra(TrackingRecordModificationActivity.TRACKING_RECORD_UUID, trackingRecordUuid);
        startActivity(intent);
    }

    private View prepareView(Optional<String> descriptionOptional) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_tracking_record_description_editing, null);
        descriptionEditText = (EditText) rootView.findViewById(R.id.tracking_record_description);

        renderExistingDescription(descriptionOptional);

        return rootView;
    }

    private void renderExistingDescription(Optional<String> descriptionOptional) {
        if (descriptionOptional.isPresent()) {
            descriptionEditText.setText(descriptionOptional.get());
        }
    }

    private Optional<String> getDescriptionFromWidget() {
        return TextUtils.isEmpty(descriptionEditText.getText())
                ? Optional.<String>absent()
                : Optional.of(descriptionEditText.getText().toString());
    }
}