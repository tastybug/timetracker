package com.tastybug.timetracker.extension.feedback.controller;

import android.content.Context;
import android.content.Intent;

class FeedbackMailService {

    private Context context;
    private FeedbackMailIntentFactory intentFactory;

    FeedbackMailService(Context context) {
        this(context, new FeedbackMailIntentFactory());
    }

    private FeedbackMailService(Context context,
                                FeedbackMailIntentFactory intentFactory) {
        this.context = context;
        this.intentFactory = intentFactory;
    }

    void sendFeedback() {
        Intent intent = intentFactory.createIntent(context);
        context.startActivity(intent);
    }

}
