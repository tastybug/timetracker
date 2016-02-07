package com.tastybug.timetracker.gui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.TimeFrame;

public class TimeFrameView extends LinearLayout {

    private TextView someTextview;


    public TimeFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_timeframe, this, true);

        someTextview = (TextView) findViewById(R.id.someTextview);
    }

    public void showTimeFrame(TimeFrame timeFrame) {
        someTextview.setText(timeFrame.toString());
    }
}
