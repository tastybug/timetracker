package com.tastybug.timetracker.report.internal.html;

import android.content.Context;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;

import java.io.IOException;
import java.io.Serializable;

public class HtmlReport implements Serializable {

    private transient Context context;
    private String template = "";

    HtmlReport(Context context) throws IOException {
        this(context, new TemplateAssetProvider(context.getAssets()));
    }

    HtmlReport(Context context,
               TemplateAssetProvider templateAssetProvider) throws IOException {
        this.template = templateAssetProvider.getReportTemplate();
        this.context = context;
    }

    void insertReportablesList(String html) {
        String reportablesListMarker = "<!-- ${reportables_list} -->";
        insertStringAtMarker(reportablesListMarker, html);
    }

    void insertReportTitle(String title) {
        String titleMarker = "<!-- ${title} -->";
        insertStringAtMarker(titleMarker, title);
    }

    void insertProjectTitle(Project project) {
        String labelMarker = "<!-- ${project_title_label} -->";
        insertStringAtMarker(labelMarker, context.getString(R.string.report_project_title_label));
        String valueMarker = "<!-- ${project_title} -->";
        insertStringAtMarker(valueMarker, project.getTitle());
    }

    void insertProjectDescription(Project project) {
        String labelMarker = "<!-- ${project_description_label} -->";
        insertStringAtMarker(labelMarker, context.getString(R.string.report_project_description_label));
        String valueMarker = "<!-- ${project_description} -->";
        insertStringAtMarker(valueMarker, project.getDescription().or(context.getString(R.string.report_project_no_description_placeholder)));
    }

    void insertTotalDuration(String totalDuration) {
        String labelMarker = "<!-- ${total_duration_label} -->";
        insertStringAtMarker(labelMarker, context.getString(R.string.report_total_duration_label));
        String valueMarker = "<!-- ${total_duration} -->";
        insertStringAtMarker(valueMarker, totalDuration);
    }

    void localizeHeaders() {
        insertStringAtMarker("<!-- ${date_header} -->", context.getString(R.string.report_date_header));
        insertStringAtMarker("<!-- ${amount_header} -->", context.getString(R.string.report_amount_header));
        insertStringAtMarker("<!-- ${description_header} -->", context.getString(R.string.report_description_header));
    }

    public String toHtml() {
        return template;
    }

    private void insertStringAtMarker(String marker, String value) throws IllegalStateException {
        if (!template.contains(marker)) {
            throw new IllegalStateException(String.format("Missing marker %s.", marker));
        }
        this.template = template.replace(marker, value);
    }

}
