package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;

import java.util.ArrayList;
import java.util.List;

import static com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter.iso8601;

class CsvTupleRenderer {

    String[] render(Project project, ReportableItem reportableItem) {
        ArrayList<String> list = new ArrayList<>();
        populateTuple(project, reportableItem, list);

        return list.toArray(new String[0]);
    }

    protected void populateTuple(Project project, ReportableItem reportableItem, List<String> list) {
        addProjectTitle(list, project);
        addContractId(list, project);
        addStartDate(list, reportableItem);
        addEndDate(list, reportableItem);
        addDuration(list, reportableItem);
        addDescription(list, reportableItem);
    }

    void addDescription(List<String> list, ReportableItem reportableItem) {
        list.add(reportableItem.getDescription().or(""));
    }

    void addDuration(List<String> list, ReportableItem reportableItem) {
        list.add(new DurationFormatter().formatDuration(reportableItem.getDuration()));
    }

    private void addEndDate(List<String> list, ReportableItem reportableItem) {
        list.add(iso8601().format(reportableItem.getEndDate()));
    }

    private void addStartDate(List<String> list, ReportableItem reportableItem) {
        list.add(iso8601().format(reportableItem.getStartDate()));
    }

    void addContractId(List<String> list, Project project) {
        list.add(project.getContractId().or(""));
    }

    void addProjectTitle(List<String> list, Project project) {
        list.add(project.getTitle());
    }

}
