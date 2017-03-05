package com.tastybug.timetracker.extensions.reporting.controller.internal.aggregated;

import com.tastybug.timetracker.extensions.reporting.controller.Report;
import com.tastybug.timetracker.extensions.reporting.controller.internal.html.HtmlReport;
import com.tastybug.timetracker.model.Project;

import org.junit.Test;

import java.util.Date;

import static org.mockito.Mockito.mock;

public class ReportTest {

    private HtmlReport htmlReport = mock(HtmlReport.class);

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_on_null_project() {
        new Report(null, new Date(), new Date(), htmlReport);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_on_null_htmlReport() {
        new Report(new Project(""), new Date(), new Date(), null);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_on_null_from_date() {
        new Report(new Project(""), null, new Date(), htmlReport);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_throws_NPE_on_null_until_date() {
        new Report(new Project(""), new Date(), null, htmlReport);
    }
}