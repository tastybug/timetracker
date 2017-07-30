package com.tastybug.timetracker.extension.feedback.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.tastybug.timetracker.BuildConfig;
import com.tastybug.timetracker.R;

class FeedbackMailIntentFactory {

    private static final String TO = "feedback@pocket-log.com";

    FeedbackMailIntentFactory() {}

    Intent createIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822") ;
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {TO});
        intent.putExtra(Intent.EXTRA_SUBJECT, getSubject(context));
        intent.putExtra(Intent.EXTRA_TEXT, getBody(context));
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this

        return intent;
    }

    private String getBody(Context context) {
        return context.getString(R.string.feedback_mail_body_debug_data_X, getDebugData());
    }

    private String getSubject(Context context) {
        return context.getString(R.string.feedback_mail_subject_for_app_name_X_version_Y_hash_Z,
                context.getString(R.string.app_name),
                BuildConfig.VERSION_NAME,
                BuildConfig.GIT_HASH);
    }

    private String getDebugData() {
        return BuildConfig.VERSION_NAME + "-" + BuildConfig.GIT_HASH
                + "/"
                + Build.MANUFACTURER + "-" + Build.MODEL
                + "/"
                + Build.VERSION.RELEASE;
    }
}
