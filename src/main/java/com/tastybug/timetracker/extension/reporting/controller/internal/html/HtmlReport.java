package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.Report;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class HtmlReport extends Report implements Serializable {

    private transient Context context;
    private String template = "";

    HtmlReport(Context context,
               Project project,
               Date firstDay,
               Date lastDay) {
        this(context, new TemplateAssetProvider(context.getAssets()), project, firstDay, lastDay);
    }

    HtmlReport(Context context,
               TemplateAssetProvider templateAssetProvider,
               Project project,
               Date firstDay,
               Date lastDay) {
        super(project, firstDay, lastDay);
        try {
            this.template = templateAssetProvider.getReportTemplate();
            this.context = context;
        } catch(IOException ioe) {
            throw new RuntimeException("Problem accessing report template.", ioe);
        }
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

    void insertContractId(Project project) {
        String labelMarker = "<!-- ${project_contract_id_label} -->";
        insertStringAtMarker(labelMarker, context.getString(R.string.report_project_contract_id_label));
        String valueMarker = "<!-- ${project_contract_id} -->";
        insertStringAtMarker(valueMarker, project.getContractId().or(context.getString(R.string.report_project_no_contract_id_placeholder)));
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

    public String getContent() {
        return template;
    }

    public String getMimeType() {
        return "text/html";
    }

    public String getFileExtension() {
        return "html";
    }

    private void insertStringAtMarker(String marker, String value) throws IllegalStateException {
        if (!template.contains(marker)) {
            throw new IllegalStateException(String.format("Missing marker %s.", marker));
        }
        this.template = template.replace(marker, value);
    }

}
