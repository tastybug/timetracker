package com.tastybug.timetracker.report.internal.html;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.report.internal.ReportableItem;
import com.tastybug.timetracker.ui.util.LocalizedDurationFormatter;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HtmlReportBuilderTest {

    private HtmlReport htmlReport = mock(HtmlReport.class);
    private TitleGenerator titleGenerator = mock(TitleGenerator.class);
    private LocalizedDurationFormatter localizedDurationFormatter = mock(LocalizedDurationFormatter.class);
    private ReportableListRenderer reportableListRenderer = mock(ReportableListRenderer.class);

    private HtmlReportBuilder htmlReportBuilder = new HtmlReportBuilder(reportableListRenderer,
            titleGenerator,
            localizedDurationFormatter,
            htmlReport);

    @Test
    public void build_plumbs_reportables_list_into_html_report() {
        // given
        when(reportableListRenderer.renderReportablesList(anyListOf(ReportableItem.class))).thenReturn("THE_LIST");
        htmlReportBuilder.withReportablesList(Collections.<ReportableItem>emptyList());

        // when
        htmlReportBuilder.build();

        // then
        verify(htmlReport).insertReportablesList("THE_LIST");
    }

    @Test
    public void build_plumbs_report_title_into_html_report() {
        // given
        Project someProject = new Project("");
        Date firstDay = new DateTime(2016, 12, 24, 0, 0).toDate();
        Date lastDay = new DateTime(2016, 12, 26, 0, 0).toDate();
        htmlReportBuilder = htmlReportBuilder.withProject(someProject).withTimeFrame(firstDay, lastDay);
        when(titleGenerator.getTitle(someProject, firstDay, lastDay)).thenReturn("SomeTitle");

        // when
        htmlReportBuilder.build();

        // then
        verify(htmlReport).insertReportTitle("SomeTitle");
    }

    @Test
    public void build_plumbs_project_title_into_html_report() {
        // given
        Project someProject = new Project("", "title", Optional.of("desc"), false);
        htmlReportBuilder = htmlReportBuilder.withProject(someProject).withTimeFrame(new Date(), new Date());

        // when
        htmlReportBuilder.build();

        // then
        verify(htmlReport).insertProjectTitle(someProject);
    }

    @Test
    public void build_plumbs_project_description_into_html_report() {
        // given
        Project someProject = new Project("", "title", Optional.of("desc"), false);
        htmlReportBuilder = htmlReportBuilder.withProject(someProject).withTimeFrame(new Date(), new Date());

        // when
        htmlReportBuilder.build();

        // then
        verify(htmlReport).insertProjectDescription(someProject);
    }

    @Test
    public void build_plumbs_total_duration_into_html_report() {
        // given
        htmlReportBuilder = htmlReportBuilder.withTotalDuration(new Duration(123));
        when(localizedDurationFormatter.formatDuration(new Duration(123))).thenReturn("someTotalDuration");

        // when
        htmlReportBuilder.build();

        // then
        verify(htmlReport).insertTotalDuration("someTotalDuration");
    }

    @Test
    public void build_returns_htmlReport() {
        // expect
        assertSame(htmlReport, htmlReportBuilder.build());
    }
}