package com.tastybug.timetracker.report.internal.html;

import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class HtmlReportTest {

    private Context context = mock(Context.class);
    private TemplateAssetProvider templateAssetProvider = mock(TemplateAssetProvider.class);

    private HtmlReport htmlReport;

    @Before
    public void setup() {
        when(context.getString(R.string.report_project_no_description_placeholder)).thenReturn("");
        when(context.getString(R.string.report_date_header)).thenReturn("");
        when(context.getString(R.string.report_amount_header)).thenReturn("");
        when(context.getString(R.string.report_description_header)).thenReturn("");
    }

    @Test(expected = IOException.class)
    public void constructor_passes_IO_exceptions_received_from_HtmlTemplateProvider() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenThrow(new IOException("AAAAH"));

        // expect
        new HtmlReport(context, templateAssetProvider);
    }

    @Test
    public void constructor_gets_template_from_assets_via_provider() throws IOException {
        // given
        String expectedTemplate = "expectedTemplate";
        when(templateAssetProvider.getReportTemplate()).thenReturn(expectedTemplate);

        // when
        HtmlReport htmlReport = new HtmlReport(context, templateAssetProvider);

        // then
        assertEquals(expectedTemplate, htmlReport.toHtml());
    }

    @Test
    public void localizeHeaders_localizes_all_headers() throws IOException {
        // given
        when(context.getString(R.string.report_date_header)).thenReturn("DATEHEADER");
        when(context.getString(R.string.report_amount_header)).thenReturn("AMOUNTHEADER");
        when(context.getString(R.string.report_description_header)).thenReturn("DESCRIPTIONHEADER");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${date_header} --> <!-- ${amount_header} --> <!-- ${description_header} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // when
        htmlReport.localizeHeaders();

        // then
        assertEquals("DATEHEADER AMOUNTHEADER DESCRIPTIONHEADER", htmlReport.toHtml());
    }

    @Test
    public void insertReportables_alters_template_correctly() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("bla <!-- ${reportables_list} --> bla");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // when
        htmlReport.insertReportablesList("testHtml");

        // then
        assertEquals("bla testHtml bla", htmlReport.toHtml());
    }

    @Test(expected = IllegalStateException.class)
    public void insertReportablesList_throws_IllegalState_when_marker_is_not_found_in_template() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("nothing here");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // expect
        htmlReport.insertReportablesList("testHtml");
    }

    @Test
    public void insertTitle_alters_template_correctly() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("bla <!-- ${title} --> bla");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // when
        htmlReport.insertReportTitle("testTitle");

        // then
        assertEquals("bla testTitle bla", htmlReport.toHtml());
    }

    @Test(expected = IllegalStateException.class)
    public void insertTitle_throws_IllegalState_when_marker_is_not_found_in_template() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("nothing here");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // expect
        htmlReport.insertReportTitle("testHtml");
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectTitle_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_title_labelXX} --> <!-- ${project_title} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // expect
        htmlReport.insertProjectTitle(new Project("", "A Title", Optional.<String>absent()));
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectTitle_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_project_title_label)).thenReturn("ProjectLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_title_label} --> <!-- ${project_titleXX} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // expect
        htmlReport.insertProjectTitle(new Project("", "A Title", Optional.<String>absent()));
    }

    @Test(expected = IllegalStateException.class)
    public void insertTotalDuration_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_total_duration_label)).thenReturn("Laaaabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${total_duration_labelXX} --> <!-- ${total_duration} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // expect
        htmlReport.insertTotalDuration("someValue");
    }

    @Test(expected = IllegalStateException.class)
    public void insertTotalDuration_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_total_duration_label)).thenReturn("Laabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${total_duration_label} --> <!-- ${total_durationXX} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // expect
        htmlReport.insertTotalDuration("someValue");
    }

    @Test
    public void insertProjectTitle_sets_project_title_and_a_localized_label() throws IOException {
        // given
        when(context.getString(R.string.report_project_title_label)).thenReturn("ProjectLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_title_label} --> <!-- ${project_title} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // when
        htmlReport.insertProjectTitle(new Project("", "MyTitle", Optional.<String>absent()));

        // then
        assertEquals("ProjectLabel MyTitle", htmlReport.toHtml());
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectDescription_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_description_labelXX} --> <!-- ${project_description} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // expect
        htmlReport.insertProjectDescription(new Project("", "A Title", Optional.of("desc")));
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectDescription_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_project_description_label)).thenReturn("ProjectDescLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_description_label} --> <!-- ${project_descriptionXX} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // expect
        htmlReport.insertProjectDescription(new Project("", "A Title", Optional.of("desc")));
    }

    @Test
    public void insertProjectDescription_sets_project_description_and_a_localized_label() throws IOException {
        // given
        when(context.getString(R.string.report_project_description_label)).thenReturn("ProjectDescLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_description_label} --> <!-- ${project_description} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // when
        htmlReport.insertProjectDescription(new Project("", "MyTitle", Optional.of("desc")));

        // then
        assertEquals("ProjectDescLabel desc", htmlReport.toHtml());
    }

    @Test
    public void insertProjectDescription_inserts_localized_placeholder_when_no_description_is_set_at_project() throws IOException {
        // given
        when(context.getString(R.string.report_project_description_label)).thenReturn("ProjectDescLabel");
        when(context.getString(R.string.report_project_no_description_placeholder)).thenReturn("NoDescriptionPlaceholder");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_description_label} --> <!-- ${project_description} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // when: reporting a project *without* a description
        htmlReport.insertProjectDescription(new Project("", "MyTitle", Optional.<String>absent()));

        // then
        assertEquals("ProjectDescLabel NoDescriptionPlaceholder", htmlReport.toHtml());
    }

    public void insertTotalDuration_inserts_localized_label_and_total_duration_value() throws IOException {
        // given
        when(context.getString(R.string.report_total_duration_label)).thenReturn("TotalDurationLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${total_duration_label} --> <!-- ${total_duration} -->");
        htmlReport = new HtmlReport(context, templateAssetProvider);

        // when
        htmlReport.insertTotalDuration("SomeDurationValue");

        // then
        assertEquals("TotalDurationLabel SomeDurationValue", htmlReport.toHtml());
    }
}