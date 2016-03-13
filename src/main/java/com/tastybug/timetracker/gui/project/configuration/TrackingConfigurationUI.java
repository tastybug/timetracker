package com.tastybug.timetracker.gui.project.configuration;

import android.app.FragmentManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.dialog.DatePickerDialogFragment;
import com.tastybug.timetracker.model.rounding.RoundingFactory;
import com.tastybug.timetracker.task.OttoProvider;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TrackingConfigurationUI {

    static final String START_DATE_TOPIC = "START_DATE_TOPIC";
    static final String END_DATE_TOPIC = "END_DATE_TOPIC";

    private EditText hourLimitEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Spinner roundingStrategySpinner;
    private Context context;

    public TrackingConfigurationUI(Context context) {
        this.context = context;
        new OttoProvider().getSharedBus().register(this);
    }

    public View inflateWidgets(LayoutInflater inflater,
                               ViewGroup container,
                               final FragmentManager fragmentManager) {
        View view = inflater.inflate(R.layout.fragment_tracking_configuration, container);

        hourLimitEditText = (EditText) view.findViewById(R.id.hour_limit);
        startDateEditText = (EditText) view.findViewById(R.id.start_date);
        endDateEditText = (EditText) view.findViewById(R.id.end_date_inclusive);
        roundingStrategySpinner = (Spinner) view.findViewById(R.id.rounding_strategy_spinner);

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
                newFragment.setTopic(START_DATE_TOPIC);
                newFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
                newFragment.setTopic(END_DATE_TOPIC);
                newFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        return view;
    }

    public void destroy() {
        new OttoProvider().getSharedBus().unregister(this);
    }

    public void renderHourLimit(Integer hourLimit) {
        if(hourLimit != null) {
            hourLimitEditText.setText(hourLimit + "");
        } else {
            hourLimitEditText.setText("");
        }
    }

    public void renderStartDate(Optional<Date> date) {
        if (date.isPresent()) {
            startDateEditText.setText(context.getString(R.string.project_starts_at_X,
                    SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(date.get())));
            startDateEditText.setTag(date.get());
        } else {
            startDateEditText.setText("");
            startDateEditText.setTag(null);
        }
    }

    public void renderEndDate(Optional<Date> date) {
        if (date.isPresent()) {
            endDateEditText.setText(context.getString(R.string.project_ends_at_X,
                    SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(date.get())));
            endDateEditText.setTag(date.get());
        } else {
            endDateEditText.setText("");
            endDateEditText.setTag(null);
        }
    }

    public void renderRoundingStrategy(RoundingFactory.Strategy strategy) {
        List<String> strategyList = Arrays.asList(context.getResources().getStringArray(R.array.rouding_strategy_values));
        roundingStrategySpinner.setSelection(strategyList.indexOf(strategy.name()));
    }


    public Optional<Integer> getHourLimitFromWidget() {
        String text = hourLimitEditText.getText().toString();
        return Optional.fromNullable(!TextUtils.isEmpty(text) ? Integer.valueOf(text) : null);
    }

    public Optional<Date> getStartDateFromWidget() {
        Date startDate = (Date) startDateEditText.getTag();
        return Optional.fromNullable(startDate);
    }

    public Optional<Date> getLastEnddateInclusiveFromWidget() {
        Date lastDay = (Date) endDateEditText.getTag();
        return Optional.fromNullable(lastDay);
    }

    public void blameStartDateInvalid(Optional<String> errorMessageOpt) {
        endDateEditText.setError(errorMessageOpt.orNull());
    }

    public RoundingFactory.Strategy getRoundingStrategyFromWidget() {
        List<String> strategyList = Arrays.asList(context.getResources().getStringArray(R.array.rouding_strategy_values));
        String strategyName = strategyList.get(roundingStrategySpinner.getSelectedItemPosition());
        return RoundingFactory.Strategy.valueOf(strategyName);
    }

    @Subscribe
    public void handleDatePicked(DatePickerDialogFragment.DatePickedEvent event) {
        if (TrackingConfigurationUI.START_DATE_TOPIC.equals(event.getTopic())) {
            renderStartDate(event.getDate().isPresent() ? Optional.of(event.getDate().get().toDate()) : Optional.<Date>absent());
        } else if (TrackingConfigurationUI.END_DATE_TOPIC.equals(event.getTopic())) {
            renderEndDate(event.getDate().isPresent() ? Optional.of(event.getDate().get().toDate()) : Optional.<Date>absent());
        } else {
            throw new RuntimeException("Unexpected topic received: " + event.getTopic());
        }
    }
}
