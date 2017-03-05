package com.tastybug.timetracker.extensions.reporting.controller.internal.html;

import com.tastybug.timetracker.extensions.reporting.controller.internal.ReportableItem;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportablesListRendererTest {

    private ReportableItemRenderer reportableItemRenderer = mock(ReportableItemRenderer.class);

    private ReportableListRenderer reportableListRenderer = new ReportableListRenderer(reportableItemRenderer);

    @Test
    public void renderReportablesList_plumbs_all_days_into_a_list() {
        // given
        ReportableItem day1 = mock(ReportableItem.class);
        ReportableItem day2 = mock(ReportableItem.class);
        when(reportableItemRenderer.render(any(ReportableItem.class))).thenReturn("A");
        List<ReportableItem> itemList = new ArrayList<>(Arrays.asList(day1, day2));

        // when
        String fullHtmlList = reportableListRenderer.renderReportablesList(itemList);

        // then
        assertEquals("AA", fullHtmlList);
    }

    @Test
    public void renderReportablesList_returns_empty_html_for_empty_day_list() {
        // given
        when(reportableItemRenderer.render(any(ReportableItem.class))).thenReturn("A");

        // when
        String fullHtmlList = reportableListRenderer.renderReportablesList(new ArrayList<ReportableItem>());

        // then
        assertEquals("", fullHtmlList);
    }
}