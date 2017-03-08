package com.tastybug.timetracker.extension.reporting.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.extension.reporting.controller.Report;
import com.tastybug.timetracker.extension.reporting.ui.share.ShareReportIntentFactory;
import com.tastybug.timetracker.infrastructure.util.ConditionalLog;

import java.io.IOException;

public class HtmlReportViewerDialogFragment extends DialogFragment {

    private static final String REPORT_MODEL = "REPORT_MODEL";

    private Report report;

    public static HtmlReportViewerDialogFragment aDialog(Report report) {
        Bundle b = new Bundle();
        b.putSerializable(REPORT_MODEL, report);

        HtmlReportViewerDialogFragment zeDialog = new HtmlReportViewerDialogFragment();
        zeDialog.setArguments(b);

        return zeDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            report = (Report) savedInstanceState.getSerializable(REPORT_MODEL);
        } else {
            report = (Report) getArguments().getSerializable(REPORT_MODEL);
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_view_report, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rootView);
        builder.setPositiveButton(R.string.share_report_via, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                showSendOptions();
            }
        });
        builder.setNegativeButton(R.string.common_close, null);

        WebView webview = (WebView) rootView.findViewById(R.id.webView);
        webview.requestFocus(View.FOCUS_DOWN);
        webview.getSettings().setJavaScriptEnabled(false);
        webview.loadDataWithBaseURL("", report.getHtml(), "text/html", "UTF-8", "");

        return builder.create();
    }

    private void showSendOptions() {
        try {
            ShareReportIntentFactory shareReportIntentFactory = new ShareReportIntentFactory(getActivity());
            startActivity(
                    Intent.createChooser(shareReportIntentFactory.create(report),
                            getString(R.string.chooser_title_share_report_via)));
        } catch (IOException ioe) {
            ConditionalLog.logError(getClass().getSimpleName(), ioe.getMessage(), ioe);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(REPORT_MODEL, report);
    }
}
