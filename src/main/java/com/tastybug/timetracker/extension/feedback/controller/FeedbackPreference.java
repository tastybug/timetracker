package com.tastybug.timetracker.extension.feedback.controller;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class FeedbackPreference extends Preference implements Preference.OnPreferenceClickListener {

    public FeedbackPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        new FeedbackMailService(getContext()).sendFeedback();
        return true;
    }
}