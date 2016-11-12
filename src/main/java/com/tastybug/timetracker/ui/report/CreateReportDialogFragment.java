package com.tastybug.timetracker.ui.report;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.report.Report;
import com.tastybug.timetracker.report.ReportService;
import com.tastybug.timetracker.ui.dialog.picker.DatePickerDialogFragment;
import com.tastybug.timetracker.util.ConditionalLog;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.IOException;
import java.util.Date;

public class CreateReportDialogFragment extends DialogFragment {

    private static final String FIRST_DAY = "FIRST_DAY";
    private static final String LAST_DAY = "LAST_DAY";
    private static final String TRACKING_CONFIGURATION = "TRACKING_CONFIGURATION";

    private DefaultReportTimeFrameProvider defaultReportTimeFrameProvider = new DefaultReportTimeFrameProvider();
    private TextView firstDayTextView, lastDayTextView;
    private CheckBox aggregateDaysCheckBox;

    private TrackingConfiguration trackingConfiguration;

    public CreateReportDialogFragment() {
        new OttoProvider().getSharedBus().register(this);
    }

    public static CreateReportDialogFragment aDialog(TrackingConfiguration trackingConfiguration) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TRACKING_CONFIGURATION, trackingConfiguration);

        CreateReportDialogFragment dialogFragment = new CreateReportDialogFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Interval interval = getReportIntervalFromBundles(getArguments(), savedInstanceState);
        this.trackingConfiguration = getTrackingConfigurationFromBundles(getArguments(), savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(prepareView(interval))
//                    .setTitle(R.string.dialog_create_report_title)
                .setPositiveButton(R.string.common_create, null)
                .setNegativeButton(R.string.common_close, null);
        final AlertDialog alertDialog = builder.create();
        fixDialogButtonsDontDismiss(alertDialog);
        return alertDialog;
    }

    private Interval getReportIntervalFromBundles(Bundle initialBundle, Bundle stateBundle) {
        if (stateBundle != null) {
            Date firstDay = (Date) stateBundle.getSerializable(FIRST_DAY);
            Date lastDay = (Date) stateBundle.getSerializable(LAST_DAY);
            return new Interval(new DateTime(firstDay), new DateTime(lastDay));
        } else {
            TrackingConfiguration trackingConfiguration = (TrackingConfiguration) initialBundle.getSerializable(TRACKING_CONFIGURATION);
            return defaultReportTimeFrameProvider.forTrackingConfiguration(trackingConfiguration).getTimeFrame();
        }
    }

    private TrackingConfiguration getTrackingConfigurationFromBundles(Bundle initialBundle, Bundle stateBundle) {
        if (stateBundle != null) {
            return (TrackingConfiguration) stateBundle.getSerializable(TRACKING_CONFIGURATION);
        } else {
            return (TrackingConfiguration) initialBundle.getSerializable(TRACKING_CONFIGURATION);
        }
    }

    private View prepareView(Interval reportInterval) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_create_report, null);
        firstDayTextView = (TextView) rootView.findViewById(R.id.first_day_value);
        lastDayTextView = (TextView) rootView.findViewById(R.id.last_day_value);
        aggregateDaysCheckBox = (CheckBox) rootView.findViewById(R.id.report_aggregate_days_checkbox);

        firstDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.setTopic(FIRST_DAY);
                datePickerDialogFragment.setCanNotReturnNone();
                datePickerDialogFragment.setPresetDate(getFirstDayFromWidget());
                datePickerDialogFragment.show(getFragmentManager(), DatePickerDialogFragment.class.getSimpleName());
            }
        });

        lastDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.setTopic(LAST_DAY);
                datePickerDialogFragment.setCanNotReturnNone();
                datePickerDialogFragment.setPresetDate(getLastDayFromWidget());
                datePickerDialogFragment.show(getFragmentManager(), DatePickerDialogFragment.class.getSimpleName());
            }
        });

        setFirstDay(reportInterval.getStart().toDate());
        setLastDay(reportInterval.getEnd().toDate());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(FIRST_DAY, getFirstDayFromWidget());
        outState.putSerializable(LAST_DAY, getLastDayFromWidget());
        outState.putSerializable(TRACKING_CONFIGURATION, trackingConfiguration);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        new OttoProvider().getSharedBus().unregister(this);
    }

    private void fixDialogButtonsDontDismiss(final AlertDialog alertDialog) {
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            public void onShow(final DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                startReportCreation();
                                dismiss();
                            }
                        });
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                dismiss();
                            }
                        });
            }
        });
    }

    private Date getFirstDayFromWidget() {
        return (Date) firstDayTextView.getTag();
    }

    private Date getLastDayFromWidget() {
        return (Date) lastDayTextView.getTag();
    }

    private boolean isAggregateDaysChecked() {
        return aggregateDaysCheckBox.isChecked();
    }

    void setFirstDay(Date firstDay) {
        firstDayTextView.setText(getString(R.string.dialog_create_report_first_day_X, DefaultLocaleDateFormatter.date().format(firstDay)));
        firstDayTextView.setTag(firstDay);
    }

    void setLastDay(Date lastDay) {
        lastDayTextView.setText(getString(R.string.dialog_create_report_last_day_X, DefaultLocaleDateFormatter.date().format(lastDay)));
        lastDayTextView.setTag(lastDay);
    }

    private boolean hasValidData(Date firstDay, Date lastDay) {
        return !firstDay.after(lastDay);
    }

    private void startReportCreation() {
        Date firstDay = getFirstDayFromWidget();
        Date lastDay = getLastDayFromWidget();
        boolean aggregateDays = isAggregateDaysChecked();

        try {
            Report report = new ReportService(getActivity()).createReport(trackingConfiguration.getProjectUuid(), firstDay, lastDay, aggregateDays);
            HtmlReportViewerDialogFragment
                    .aDialog(report)
                    .show(getFragmentManager(), HtmlReportViewerDialogFragment.class.getSimpleName());
        } catch (IOException ioe) {
            ConditionalLog.logError(getClass().getSimpleName(), "Failed to create report!", ioe);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleDatePicked(DatePickerDialogFragment.DatePickedEvent event) {
        if (FIRST_DAY.equals(event.getTopic())) {
            if (hasValidData(event.getDate().get().toDate(), getLastDayFromWidget())) {
                setFirstDay(event.getDate().get().toDate());
            } else {
                Toast.makeText(getActivity(), R.string.dialog_create_report_error_end_before_start, Toast.LENGTH_LONG).show();
            }
        } else if (LAST_DAY.equals(event.getTopic())) {
            if (hasValidData(getFirstDayFromWidget(), event.getDate().get().toDate())) {
                setLastDay(event.getDate().get().toDate());
            } else {
                Toast.makeText(getActivity(), R.string.dialog_create_report_error_end_before_start, Toast.LENGTH_LONG).show();
            }
        } else {
            throw new RuntimeException("Unexpected topic received: " + event.getTopic());
        }
    }
}
