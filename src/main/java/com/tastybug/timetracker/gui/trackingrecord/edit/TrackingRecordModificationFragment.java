package com.tastybug.timetracker.gui.trackingrecord.edit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.gui.dialog.DatePickerDialogFragment;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.CreateTrackingRecordTask;
import com.tastybug.timetracker.task.tracking.ModifyTrackingRecordTask;

import java.util.Date;

public class TrackingRecordModificationFragment extends Fragment {

    private static final String START_DATE = "START_DATE";
    private static final String START_TIME = "START_TIME";
    private static final String END_DATE = "END_DATE";
    private static final String END_TIME = "END_TIME";
    private static final String DESCRIPTION = "DESCRIPTION";

    private TrackingRecordModificationUI ui;

    private Optional<String> existingTrackingRecordUuidOpt = Optional.absent();
    private Optional<String> creationForProjectUuid = Optional.absent();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ui = new TrackingRecordModificationUI(getActivity());
        View rootView = ui.inflateWidgets(inflater, container, getFragmentManager());

        if (savedInstanceState != null) {
            ui.renderStartDate(Optional.fromNullable((Date)savedInstanceState.getSerializable(START_DATE)));
            ui.renderStartTime(Optional.fromNullable((Date)savedInstanceState.getSerializable(START_TIME)));
            ui.renderEndDate(Optional.fromNullable((Date)savedInstanceState.getSerializable(END_DATE)));
            ui.renderEndTime(Optional.fromNullable((Date)savedInstanceState.getSerializable(END_TIME)));
            ui.renderDescription(Optional.fromNullable(savedInstanceState.getString(DESCRIPTION)));
        }

        new OttoProvider().getSharedBus().register(this);
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
        new OttoProvider().getSharedBus().unregister(this);
    }

    public void showTrackingRecordData(TrackingRecord trackingRecord) {
        this.existingTrackingRecordUuidOpt = Optional.of(trackingRecord.getUuid());

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
        Optional<Date> startDate = ui.getAggregatedStartDate(true);
        Optional<Date> endDate = ui.getAggregatedEndDate(true);
        if(startDate.isPresent() && endDate.isPresent()) {
            return ensureEndDateIsAfterStartDate(startDate.get(), endDate.get());
        } else {
            return false;
        }
    }

    public ModifyTrackingRecordTask collectModificationsForEdit(ModifyTrackingRecordTask task) {
        return task.withTrackingRecordUuid(existingTrackingRecordUuidOpt.get())
                .withStartDate(ui.getAggregatedStartDate(true).get())
                .withEndDate(ui.getAggregatedEndDate(true).get())
            .withDescription(ui.getDescriptionFromWidget());
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

    @Subscribe
    public void handleDatePicked(DatePickerDialogFragment.DatePickedEvent event) {
        if (TrackingRecordModificationUI.START_DATE_TOPIC.equals(event.getTopic())) {
            ui.renderStartDate(Optional.of(event.getDate().get().toDate()));
        } else if (TrackingRecordModificationUI.START_TIME_TOPIC.equals(event.getTopic())) {
            ui.renderStartTime(Optional.of(event.getDate().get().toDate()));
        } else if (TrackingRecordModificationUI.END_DATE_TOPIC.equals(event.getTopic())) {
            ui.renderEndDate(Optional.of(event.getDate().get().toDate()));
        } else if (TrackingRecordModificationUI.END_TIME_TOPIC.equals(event.getTopic())) {
            ui.renderEndTime(Optional.of(event.getDate().get().toDate()));
        } else {
            throw new RuntimeException("Unexpected topic received: " + event.getTopic());
        }
    }
}
