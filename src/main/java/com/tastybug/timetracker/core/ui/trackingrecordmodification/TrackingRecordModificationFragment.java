package com.tastybug.timetracker.core.ui.trackingrecordmodification;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.task.tracking.create.CreateTrackingRecordTask;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordTask;

import java.util.Date;

public class TrackingRecordModificationFragment extends Fragment {

    private static final String START_DATE = "START_DATE";
    private static final String START_TIME = "START_TIME";
    private static final String END_DATE = "END_DATE";
    private static final String END_TIME = "END_TIME";
    private static final String DESCRIPTION = "DESCRIPTION";

    private TrackingRecordModificationUI ui;

    private Optional<String> existingTrackingRecordUuidOpt = Optional.absent();
    private Optional<Boolean> isExistingTrackingRecordThatIsRunning = Optional.absent();
    private Optional<String> creationForProjectUuid = Optional.absent();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ui = new TrackingRecordModificationUI(getActivity());
        View rootView = ui.inflateWidgets(inflater, container, getFragmentManager());

        if (savedInstanceState != null) {
            ui.renderStartDate(Optional.fromNullable((Date) savedInstanceState.getSerializable(START_DATE)));
            ui.renderStartTime(Optional.fromNullable((Date) savedInstanceState.getSerializable(START_TIME)));
            ui.renderEndDate(Optional.fromNullable((Date) savedInstanceState.getSerializable(END_DATE)));
            ui.renderEndTime(Optional.fromNullable((Date) savedInstanceState.getSerializable(END_TIME)));
            ui.renderDescription(Optional.fromNullable(savedInstanceState.getString(DESCRIPTION)));
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(START_DATE, ui.getStartDateFromWidget(false).orNull());
        outState.putSerializable(START_TIME, ui.getStartTimeFromWidget(false).orNull());
        outState.putSerializable(END_DATE, ui.getEndDateFromWidget(false).orNull());
        outState.putSerializable(END_TIME, ui.getEndTimeFromWidget(false).orNull());
        outState.putString(DESCRIPTION, ui.getDescriptionFromWidget().orNull());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ui.destroy();
    }

    public void showTrackingRecordData(TrackingRecord trackingRecord) {
        this.existingTrackingRecordUuidOpt = Optional.of(trackingRecord.getUuid());
        this.isExistingTrackingRecordThatIsRunning = Optional.of(trackingRecord.isRunning());

        ui.renderStartDate(trackingRecord.getStart());
        ui.renderStartTime(trackingRecord.getStart());
        ui.renderEndDate(trackingRecord.getEnd());
        ui.renderEndTime(trackingRecord.getEnd());
        ui.renderDescription(trackingRecord.getDescription());
    }

    public void showCreationForProject(String projectUuid) {
        this.creationForProjectUuid = Optional.of(projectUuid);
    }

    public boolean validateData() {
        if (existingTrackingRecordUuidOpt.isPresent()
                && isExistingTrackingRecordThatIsRunning.isPresent()) {
            return validateForRunningTrackingRecord();
        } else {
            return validateForCompletedTrackingRecord();
        }
    }

    private boolean validateForRunningTrackingRecord() {
        Optional<Date> startDate = ui.getAggregatedStartDate(true);
        Optional<Date> endDate = ui.getAggregatedEndDate(false);
        if (startDate.isPresent() && endDate.isPresent()) {
            return ensureEndDateIsAfterStartDate(startDate.get(), endDate.get());
        } else {
            return startDate.isPresent();
        }
    }

    private boolean validateForCompletedTrackingRecord() {
        Optional<Date> startDate = ui.getAggregatedStartDate(true);
        Optional<Date> endDate = ui.getAggregatedEndDate(true);
        return startDate.isPresent()
                && endDate.isPresent()
                && ensureEndDateIsAfterStartDate(startDate.get(), endDate.get());
    }

    public UpdateTrackingRecordTask collectModificationsForEdit(UpdateTrackingRecordTask task) {
        task.withTrackingRecordUuid(existingTrackingRecordUuidOpt.get())
                .withStartDate(ui.getAggregatedStartDate(true).get())
                .withDescription(ui.getDescriptionFromWidget());

        if (isExistingTrackingRecordThatIsRunning.or(false)) {
            if (ui.getAggregatedEndDate(false).isPresent()) {
                task.withEndDate(ui.getAggregatedEndDate(false).get());
            }
        } else {
            task.withEndDate(ui.getAggregatedEndDate(true).get());
        }
        return task;
    }

    public CreateTrackingRecordTask collectModificationsForCreate(CreateTrackingRecordTask task) {
        return task.withProjectUuid(creationForProjectUuid.get())
                .withStartDate(ui.getAggregatedStartDate(true).get())
                .withEndDate(ui.getAggregatedEndDate(true).get())
                .withDescription(ui.getDescriptionFromWidget());
    }

    public boolean hasUnsavedModifications(Optional<TrackingRecord> recordBeingEdited) {
        if (recordBeingEdited.isPresent()) {
            // there is an existing record that we are checking against
            return !recordBeingEdited.get().getStart().equals(ui.getAggregatedStartDate(false))
                    || !recordBeingEdited.get().getEnd().equals(ui.getAggregatedEndDate(false))
                    || !recordBeingEdited.get().getDescription().equals(ui.getDescriptionFromWidget());

        } else {
            // we are creating a record, so nothing to check against
            return ui.getStartTimeFromWidget(false).isPresent()
                    || ui.getEndDateFromWidget(false).isPresent()
                    || ui.getDescriptionFromWidget().isPresent();
        }
    }

    private boolean ensureEndDateIsAfterStartDate(Date startDate, Date endDate) {
        boolean erroneous = endDate.before(startDate);
        ui.blameEndDateBeforeStartDate(erroneous);
        return !erroneous;
    }
}
