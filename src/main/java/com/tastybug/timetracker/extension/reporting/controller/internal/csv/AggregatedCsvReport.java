package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;

import java.util.Date;

public class AggregatedCsvReport extends CsvReport {

    AggregatedCsvReport(Context context, Project project, Date firstDay, Date lastDay) {
        super(context, project, firstDay, lastDay);
        this.csvTupleRenderer = new AggregatedCsvTupleRenderer();
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

}
