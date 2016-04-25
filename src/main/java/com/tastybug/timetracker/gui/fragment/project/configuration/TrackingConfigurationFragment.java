package com.tastybug.timetracker.gui.fragment.project.configuration;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.rounding.RoundingFactory;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;

import java.util.Date;

public class TrackingConfigurationFragment extends Fragment {

    private static final String HOUR_LIMIT = "HOUR_LIMIT";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private static final String ROUNDING_STRATEGY = "ROUNDING_STRATEGY";

    private TrackingConfigurationUI ui;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ui = new TrackingConfigurationUI(getActivity());
        View view = ui.inflateWidgets(inflater, container, getFragmentManager());

        if (savedInstanceState != null) {
            ui.renderHourLimit((Integer)savedInstanceState.getSerializable(HOUR_LIMIT));
            ui.renderStartDate(Optional.fromNullable((Date) savedInstanceState.getSerializable(START_DATE)));
            ui.renderEndDate(Optional.fromNullable((Date) savedInstanceState.getSerializable(END_DATE)));
            ui.renderRoundingStrategy((RoundingFactory.Strategy) savedInstanceState.getSerializable(ROUNDING_STRATEGY));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(START_DATE, ui.getStartDateFromWidget().orNull());
        outState.putSerializable(END_DATE, ui.getLastEnddateInclusiveFromWidget().orNull());
        outState.putSerializable(HOUR_LIMIT, ui.getHourLimitFromWidget().orNull());
        outState.putSerializable(ROUNDING_STRATEGY, ui.getRoundingStrategyFromWidget());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ui.destroy();
    }

    public void showTrackingConfiguration(TrackingConfiguration trackingConfiguration) {
        ui.renderHourLimit(trackingConfiguration.getHourLimit().orNull());
        ui.renderStartDate(trackingConfiguration.getStart());
        ui.renderEndDate(trackingConfiguration.getEndDateAsInclusive());
        ui.renderRoundingStrategy(trackingConfiguration.getRoundingStrategy());
    }

    public boolean validateSettings() {
        Optional<Date> startDateOpt = ui.getStartDateFromWidget();
        Optional<Date> newLastDateOpt = ui.getLastEnddateInclusiveFromWidget();

        boolean startDateAfterEndDate = startDateOpt.isPresent()
                                        && newLastDateOpt.isPresent()
                                        && startDateOpt.get().after(newLastDateOpt.get());
        ui.blameStartDateInvalid(startDateAfterEndDate
                ? Optional.of(getString(R.string.error_start_after_end_date))
                : Optional.<String>absent());
        return !startDateAfterEndDate;
    }

    public void collectModifications(ConfigureProjectTask task) {
        Optional<Integer> newHourLimitOpt = ui.getHourLimitFromWidget();
        Optional<Date> startDateOpt = ui.getStartDateFromWidget();
        Optional<Date> newLastDateOpt = ui.getLastEnddateInclusiveFromWidget();
        RoundingFactory.Strategy strategy = ui.getRoundingStrategyFromWidget();

        task.withHourLimit(newHourLimitOpt.orNull());
        task.withStartDate(startDateOpt.orNull());
        task.withInclusiveEndDate(newLastDateOpt.orNull());
        task.withRoundingStrategy(strategy);
    }

    public boolean hasUnsavedModifications(TrackingConfiguration trackingConfiguration) {
        return !trackingConfiguration.getHourLimit().equals(ui.getHourLimitFromWidget())
                || !trackingConfiguration.getStart().equals(ui.getStartDateFromWidget())
                || !trackingConfiguration.getEndDateAsInclusive().equals(ui.getLastEnddateInclusiveFromWidget())
                || !trackingConfiguration.getRoundingStrategy().equals(ui.getRoundingStrategyFromWidget());
    }
}
