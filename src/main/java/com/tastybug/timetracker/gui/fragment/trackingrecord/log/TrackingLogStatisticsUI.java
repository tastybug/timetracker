package com.tastybug.timetracker.gui.fragment.trackingrecord.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tastybug.timetracker.R;

public class TrackingLogStatisticsUI {

    private Context context;

    public TrackingLogStatisticsUI(Context context) {
        this.context = context;
    }

    public View inflateWidgets(LayoutInflater inflater,
                               ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_tracking_log_statistics, container);

        return rootView;
    }
}
