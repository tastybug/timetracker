package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;

import org.joda.time.Duration;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AggregatedCsvTupleRendererTest {

    private static final int PROJECT_TITLE_INDEX = 0;
    private static final int PROJECT_CONTRACT_ID_INDEX = 1;
    private static final int DATE_INDEX = 2;
    private static final int EFFECTIVE_DURATION_INDEX = 3;
    private static final int RECORD_DESCRIPTION_INDEX = 4;

    private AggregatedCsvTupleRenderer renderer = new AggregatedCsvTupleRenderer();

    @Test
    public void render_produces_all_required_columns_in_correct_order() {
        ReportableItem item = aReportableItem();
        Project project = aProject();

        String[] result = renderer.render(project, item);

        assertEquals(5, result.length);
        assertEquals(project.getTitle(), result[PROJECT_TITLE_INDEX]);
        assertEquals(project.getContractId().get(), result[PROJECT_CONTRACT_ID_INDEX]);
        assertEquals(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(item.getStartDate()), result[DATE_INDEX]);
        assertEquals(new DurationFormatter().formatDuration(item.getDuration()), result[EFFECTIVE_DURATION_INDEX]);
        assertEquals(item.getDescription().get(), result[RECORD_DESCRIPTION_INDEX]);
    }

    private Project aProject() {
        Project project = mock(Project.class);
        when(project.getTitle()).thenReturn("title");
        when(project.getContractId()).thenReturn(Optional.of("contract-id"));

        return project;
    }

    private ReportableItem aReportableItem() {
        ReportableItem item = mock(ReportableItem.class);
        when(item.getDescription()).thenReturn(Optional.of("TR-desc"));
        when(item.getStartDate()).thenReturn(new Date(1));
        when(item.getDuration()).thenReturn(new Duration(1));

        return item;
    }
}