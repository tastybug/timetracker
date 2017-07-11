package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

class AggregatedCsvTupleRenderer extends CsvTupleRenderer {

    @Override
    protected void populateTuple(Project project, ReportableItem reportableItem, List<String> list) {
        addProjectTitle(list, project);
        addContractId(list, project);
        addDate(list, reportableItem);
        addDuration(list, reportableItem);
        addDescription(list, reportableItem);
    }

    private void addDate(List<String> list, ReportableItem reportableItem) {
        String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(reportableItem.getStartDate());
        list.add(dateString);
    }
}
