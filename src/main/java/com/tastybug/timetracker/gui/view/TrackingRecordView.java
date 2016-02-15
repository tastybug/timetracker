package com.tastybug.timetracker.gui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.TrackingRecord;

public class TrackingRecordView extends LinearLayout {

    private TextView someTextview;


    public TrackingRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_tracking_record, this, true);

        someTextview = (TextView) findViewById(R.id.someTextview);
    }

    public void showTrackingRecord(TrackingRecord trackingRecord) {
        someTextview.setText(trackingRecord.toString());
    }
}
