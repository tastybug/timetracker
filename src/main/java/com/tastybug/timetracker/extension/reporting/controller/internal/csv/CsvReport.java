package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import android.content.Context;
import android.support.annotation.NonNull;

import com.opencsv.CSVWriter;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.extension.reporting.controller.internal.model.Report;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

public class CsvReport extends Report {

    protected Context context;
    private StringWriter stringWriter = new StringWriter();
    private CSVWriter writer;
    CsvTupleRenderer csvTupleRenderer = new CsvTupleRenderer();

    CsvReport(Context context, Project project, Date firstDay, Date lastDay) {
        super(project, firstDay, lastDay);
        this.context = context;
        this.writer = new CSVWriter(stringWriter);
    }

    void writeHeaders() {
        writer.writeNext(getHeaderArray());
    }

    @NonNull
    String[] getHeaderArray() {
        return new String[]{
                context.getString(R.string.csv_header_project_title),
                context.getString(R.string.csv_header_project_contract_id),
                context.getString(R.string.csv_header_project_start_date),
                context.getString(R.string.csv_header_project_end_date),
                context.getString(R.string.csv_header_project_effective_duration),
                context.getString(R.string.csv_header_project_description)};
    }

    void addReportableItem(Project project, ReportableItem item) {
        writer.writeNext(csvTupleRenderer.render(project, item));
    }

    void close() throws IOException {
        writer.close();
    }

    public String getContent() {
        return stringWriter.toString();
    }

    public String getMimeType() {
        return "text/csv";
    }

    public String getFileExtension() {
        return "csv";
    }

}
