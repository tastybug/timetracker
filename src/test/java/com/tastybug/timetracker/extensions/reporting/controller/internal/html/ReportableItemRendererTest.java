package com.tastybug.timetracker.extensions.reporting.controller.internal.html;

import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.extensions.reporting.controller.internal.ReportableItem;
import com.tastybug.timetracker.ui.util.LocalizedDurationFormatter;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ReportableItemRendererTest {

    private Context context = mock(Context.class);
    private LocalizedDurationFormatter durationFormatter = mock(LocalizedDurationFormatter.class);
    private DefaultLocaleDateFormatter defaultLocaleDateFormatter = mock(DefaultLocaleDateFormatter.class);

    private ReportableItemRenderer renderer = new ReportableItemRenderer(context, durationFormatter, defaultLocaleDateFormatter);

    @Before
    public void setup() {
        when(context.getString(R.string.report_reportable_item_no_description_placeholder)).thenReturn("");
        when(durationFormatter.formatDuration(any(Duration.class))).thenReturn("aDuration");
        when(defaultLocaleDateFormatter.dateFormat(any(Date.class))).thenReturn("aDate");
    }

    @Test
    public void render_returns_proper_day_html_for_same_day_not_covering_whole_day_length_item() {
        // given
        ReportableItem reportableItem = mock(ReportableItem.class);
        when(reportableItem.getStartDate()).thenReturn(mock(Date.class));
        when(reportableItem.getEndDate()).thenReturn(mock(Date.class));
        when(reportableItem.getDuration()).thenReturn(new Duration(0));
        when(reportableItem.isSameDay()).thenReturn(true);
        when(reportableItem.isWholeDay()).thenReturn(false);
        when(reportableItem.getDescription()).thenReturn(Optional.of("someDescription"));
        when(durationFormatter.formatDuration(any(Duration.class))).thenReturn("aDuration");
        when(defaultLocaleDateFormatter.dateFormat(any(Date.class))).thenReturn("aDate");
        when(defaultLocaleDateFormatter.timeFormat(any(Date.class))).thenReturn("aTime");
        String expectedHtml = "<div class=\"row\">" +
                "<div class=\"data twenty\">aDate, aTime - aTime</div>" +
                "<div class=\"data twenty\">aDuration</div>" +
                "<div class=\"data fifty\">someDescription</div>" +
                "</div>";

        // when
        String factualHtml = renderer.render(reportableItem);

        // then
        assertEquals(expectedHtml, factualHtml);
    }

    @Test
    public void render_returns_proper_day_html_for_same_day_whole_day_length_item() {
        // given
        ReportableItem reportableItem = mock(ReportableItem.class);
        when(reportableItem.getStartDate()).thenReturn(mock(Date.class));
        when(reportableItem.getEndDate()).thenReturn(mock(Date.class));
        when(reportableItem.getDuration()).thenReturn(new Duration(0));
        when(reportableItem.isSameDay()).thenReturn(true);
        when(reportableItem.isWholeDay()).thenReturn(true);
        when(reportableItem.getDescription()).thenReturn(Optional.of("someDescription"));
        when(durationFormatter.formatDuration(any(Duration.class))).thenReturn("aDuration");
        when(defaultLocaleDateFormatter.dateFormat(any(Date.class))).thenReturn("aDate");
        when(defaultLocaleDateFormatter.timeFormat(any(Date.class))).thenReturn("aTime");
        String expectedHtml = "<div class=\"row\">" +
                "<div class=\"data twenty\">aDate</div>" +
                "<div class=\"data twenty\">aDuration</div>" +
                "<div class=\"data fifty\">someDescription</div>" +
                "</div>";

        // when
        String factualHtml = renderer.render(reportableItem);

        // then
        assertEquals(expectedHtml, factualHtml);
    }

    @Test
    public void render_returns_proper_day_html_for_multiple_day_item() {
        // given
        ReportableItem reportableItem = mock(ReportableItem.class);
        when(reportableItem.getStartDate()).thenReturn(mock(Date.class));
        when(reportableItem.getEndDate()).thenReturn(mock(Date.class));
        when(reportableItem.getDuration()).thenReturn(new Duration(0));
        when(reportableItem.isSameDay()).thenReturn(false);
        when(reportableItem.isWholeDay()).thenReturn(false);
        when(reportableItem.getDescription()).thenReturn(Optional.of("someDescription"));
        when(durationFormatter.formatDuration(any(Duration.class))).thenReturn("aDuration");
        when(defaultLocaleDateFormatter.dateTimeFormat(any(Date.class))).thenReturn("aDateAndTime");
        String expectedHtml = "<div class=\"row\">" +
                "<div class=\"data twenty\">aDateAndTime - aDateAndTime</div>" +
                "<div class=\"data twenty\">aDuration</div>" +
                "<div class=\"data fifty\">someDescription</div>" +
                "</div>";

        // when
        String factualHtml = renderer.render(reportableItem);

        // then
        assertEquals(expectedHtml, factualHtml);
    }

    @Test
    public void render_sets_placeholder_when_no_description_is_available() {
        // given
        ReportableItem reportableItem = mock(ReportableItem.class);
        when(reportableItem.getStartDate()).thenReturn(mock(Date.class));
        when(reportableItem.getEndDate()).thenReturn(mock(Date.class));
        when(reportableItem.getDuration()).thenReturn(new Duration(0));
        when(reportableItem.isSameDay()).thenReturn(false);
        when(reportableItem.getDescription()).thenReturn(Optional.<String>absent());
        when(defaultLocaleDateFormatter.dateTimeFormat(any(Date.class))).thenReturn("aDateAndTime");
        when(context.getString(R.string.report_reportable_item_no_description_placeholder)).thenReturn("none");
        String expectedHtml = "<div class=\"row\">" +
                "<div class=\"data twenty\">aDateAndTime - aDateAndTime</div>" +
                "<div class=\"data twenty\">aDuration</div>" +
                "<div class=\"data fifty\">none</div>" +
                "</div>";

        // when
        String factualHtml = renderer.render(reportableItem);

        // then
        assertEquals(expectedHtml, factualHtml);
    }
}