package com.tastybug.timetracker.report.internal.html;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.report.internal.ReportableItem;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HtmlReportBuilderTest {

    private HtmlReport htmlReport = mock(HtmlReport.class);
    private TitleGenerator titleGenerator = mock(TitleGenerator.class);
    private ReportableListRenderer reportableListRenderer = mock(ReportableListRenderer.class);

    private HtmlReportBuilder htmlReportBuilder = new HtmlReportBuilder(reportableListRenderer,
            titleGenerator,
            htmlReport);

    @Test
    public void build_plumbs_aggregated_day_list_into_html_report() {
        // given
        when(reportableListRenderer.renderReportablesList(anyList())).thenReturn("THE_LIST");
        htmlReportBuilder.withReportablesList(Collections.<ReportableItem>emptyList());

        // when
        htmlReportBuilder.build();

        // then
        verify(htmlReport, times(1)).insertReportablesList("THE_LIST");
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
        verify(htmlReport, times(1)).insertReportTitle("SomeTitle");
    }

    @Test
    public void build_plumbs_project_title_into_html_report() {
        // given
        Project someProject = new Project("", "title", Optional.of("desc"));
        htmlReportBuilder = htmlReportBuilder.withProject(someProject).withTimeFrame(new Date(), new Date());

        // when
        htmlReportBuilder.build();

        // then
        verify(htmlReport, times(1)).insertProjectTitle(someProject);
    }

    @Test
    public void build_plumbs_project_description_into_html_report() {
        // given
        Project someProject = new Project("", "title", Optional.of("desc"));
        htmlReportBuilder = htmlReportBuilder.withProject(someProject).withTimeFrame(new Date(), new Date());

        // when
        htmlReportBuilder.build();

        // then
        verify(htmlReport, times(1)).insertProjectDescription(someProject);
    }

    @Test
    public void build_returns_htmlReport() {
        // expect
        assertSame(htmlReport, htmlReportBuilder.build());
    }
}