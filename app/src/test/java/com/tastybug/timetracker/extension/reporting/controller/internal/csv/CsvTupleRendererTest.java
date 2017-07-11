package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;

import org.joda.time.Duration;
import org.junit.Test;

import java.util.Date;

import static com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter.iso8601;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CsvTupleRendererTest {

    private static final int PROJECT_TITLE_INDEX = 0;
    private static final int PROJECT_CONTRACT_ID_INDEX = 1;
    private static final int START_DATE_INDEX = 2;
    private static final int END_DATE_INDEX = 3;
    private static final int EFFECTIVE_DURATION_INDEX = 4;
    private static final int RECORD_DESCRIPTION_INDEX = 5;

    private CsvTupleRenderer renderer = new CsvTupleRenderer();

    @Test
    public void render_produces_all_required_columns_in_correct_order() {
        ReportableItem item = aReportableItem();
        Project project = aProject();

        String[] result = renderer.render(project, item);

        assertEquals(6, result.length);
        assertEquals(project.getTitle(), result[PROJECT_TITLE_INDEX]);
        assertEquals(project.getContractId().get(), result[PROJECT_CONTRACT_ID_INDEX]);
        assertEquals(iso8601().format(item.getStartDate()), result[START_DATE_INDEX]);
        assertEquals(iso8601().format(item.getEndDate()), result[END_DATE_INDEX]);
        assertEquals(new DurationFormatter().formatDuration(item.getDuration()), result[EFFECTIVE_DURATION_INDEX]);
        assertEquals(item.getDescription().get(), result[RECORD_DESCRIPTION_INDEX]);
    }

    @Test
    public void render_produces_missing_contract_id_as_empty_string() {
        ReportableItem item = aReportableItem();
        Project project = aProject();
        when(project.getContractId()).thenReturn(Optional.<String>absent());

        String[] result = renderer.render(project, item);

        assertEquals("", result[PROJECT_CONTRACT_ID_INDEX]);
    }

    @Test
    public void render_produces_missing_description_as_empty_string() {
        ReportableItem item = aReportableItem();
        Project project = aProject();
        when(item.getDescription()).thenReturn(Optional.<String>absent());

        String[] result = renderer.render(project, item);

        assertEquals("", result[RECORD_DESCRIPTION_INDEX]);
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
        when(item.getEndDate()).thenReturn(new Date(1));
        when(item.getDuration()).thenReturn(new Duration(1));

        return item;
    }
}