package com.tastybug.timetracker.ui.util;

import android.content.Context;

import com.tastybug.timetracker.R;

public class LocalizedDurationFormatter extends DurationFormatter {

    public LocalizedDurationFormatter(Context context) {
        super(context.getString(R.string.hours_short),
                context.getString(R.string.minutes_short),
                context.getString(R.string.seconds_short));
    }

    public static LocalizedDurationFormatter a(Context context) {
        return new LocalizedDurationFormatter(context);
    }
}
