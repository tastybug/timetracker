package com.tastybug.timetracker.extension.reporting.controller.internal.html;

import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class HtmlReportTest {

    private Context context;
    private HtmlReport htmlReport;

    @Before
    public void setup() {
        context = mock(Context.class);
        when(context.getString(R.string.report_project_no_description_placeholder)).thenReturn("");
        when(context.getString(R.string.report_date_header)).thenReturn("");
        when(context.getString(R.string.report_amount_header)).thenReturn("");
        when(context.getString(R.string.report_description_header)).thenReturn("");
    }

    @Test
    public void localizeHeaders_localizes_all_headers() throws IOException {
        // given
        when(context.getString(R.string.report_date_header)).thenReturn("DATEHEADER");
        when(context.getString(R.string.report_amount_header)).thenReturn("AMOUNTHEADER");
        when(context.getString(R.string.report_description_header)).thenReturn("DESCRIPTIONHEADER");
        String template = "<!-- ${date_header} --> <!-- ${amount_header} --> <!-- ${description_header} -->";
        htmlReport = new HtmlReport(context, template, mock(Project.class), new Date(), new Date());

        // when
        htmlReport.localizeHeaders();

        // then
        assertEquals("DATEHEADER AMOUNTHEADER DESCRIPTIONHEADER", htmlReport.getContent());
    }

    @Test
    public void insertReportables_alters_template_correctly() throws IOException {
        // given
        String template = "bla <!-- ${reportables_list} --> bla";
        htmlReport = new HtmlReport(context, template, mock(Project.class), new Date(), new Date());

        // when
        htmlReport.insertReportablesList("testHtml");

        // then
        assertEquals("bla testHtml bla", htmlReport.getContent());
    }

    @Test(expected = IllegalStateException.class)
    public void insertReportablesList_throws_IllegalState_when_marker_is_not_found_in_template() throws IOException {
        // given
        htmlReport = new HtmlReport(context, "nothing here", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertReportablesList("testHtml");
    }

    @Test
    public void insertTitle_alters_template_correctly() throws IOException {
        // given
        htmlReport = new HtmlReport(context, "bla <!-- ${title} --> bla", mock(Project.class), new Date(), new Date());

        // when
        htmlReport.insertReportTitle("testTitle");

        // then
        assertEquals("bla testTitle bla", htmlReport.getContent());
    }

    @Test(expected = IllegalStateException.class)
    public void insertTitle_throws_IllegalState_when_marker_is_not_found_in_template() throws IOException {
        // given
        htmlReport = new HtmlReport(context, "nothing here", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertReportTitle("testHtml");
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectTitle_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        htmlReport = new HtmlReport(context, "<!-- ${project_title_labelXX} --> <!-- ${project_title} -->", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertProjectTitle(new Project("", "A Title", Optional.<String>absent(), Optional.<String>absent(), false));
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectTitle_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_project_title_label)).thenReturn("ProjectLabel");
        htmlReport = new HtmlReport(context, "<!-- ${project_title_label} --> <!-- ${project_titleXX} -->", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertProjectTitle(new Project("", "A Title", Optional.<String>absent(), Optional.<String>absent(), false));
    }

    @Test(expected = IllegalStateException.class)
    public void insertTotalDuration_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_total_duration_label)).thenReturn("Laaaabel");
        htmlReport = new HtmlReport(context, "<!-- ${total_duration_labelXX} --> <!-- ${total_duration} -->", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertTotalDuration("someValue");
    }

    @Test(expected = IllegalStateException.class)
    public void insertTotalDuration_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_total_duration_label)).thenReturn("Laabel");
        htmlReport = new HtmlReport(context, "<!-- ${total_duration_label} --> <!-- ${total_durationXX} -->", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertTotalDuration("someValue");
    }

    @Test
    public void insertProjectTitle_sets_project_title_and_a_localized_label() throws IOException {
        // given
        when(context.getString(R.string.report_project_title_label)).thenReturn("ProjectLabel");
        htmlReport = new HtmlReport(context, "<!-- ${project_title_label} --> <!-- ${project_title} -->", mock(Project.class), new Date(), new Date());

        // when
        htmlReport.insertProjectTitle(new Project("", "MyTitle", Optional.<String>absent(), Optional.<String>absent(), false));

        // then
        assertEquals("ProjectLabel MyTitle", htmlReport.getContent());
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectDescription_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        htmlReport = new HtmlReport(context, "<!-- ${project_description_labelXX} --> <!-- ${project_description} -->", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertProjectDescription(new Project("", "A Title", Optional.of("desc"), Optional.<String>absent(), false));
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectDescription_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_project_description_label)).thenReturn("ProjectDescLabel");
        htmlReport = new HtmlReport(context, "<!-- ${project_description_label} --> <!-- ${project_descriptionXX} -->", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertProjectDescription(new Project("", "A Title", Optional.of("desc"), Optional.<String>absent(), false));
    }

    @Test
    public void insertProjectDescription_sets_project_description_and_a_localized_label() throws IOException {
        // given
        when(context.getString(R.string.report_project_description_label)).thenReturn("ProjectDescLabel");
        htmlReport = new HtmlReport(context, "<!-- ${project_description_label} --> <!-- ${project_description} -->", mock(Project.class), new Date(), new Date());

        // when
        htmlReport.insertProjectDescription(new Project("", "MyTitle", Optional.of("desc"), Optional.<String>absent(), false));

        // then
        assertEquals("ProjectDescLabel desc", htmlReport.getContent());
    }

    @Test
    public void insertProjectDescription_inserts_localized_placeholder_when_no_description_is_set_at_project() throws IOException {
        // given
        when(context.getString(R.string.report_project_description_label)).thenReturn("ProjectDescLabel");
        when(context.getString(R.string.report_project_no_description_placeholder)).thenReturn("NoDescriptionPlaceholder");
        htmlReport = new HtmlReport(context, "<!-- ${project_description_label} --> <!-- ${project_description} -->", mock(Project.class), new Date(), new Date());

        // when: reporting a project *without* a description
        htmlReport.insertProjectDescription(new Project("", "MyTitle", Optional.<String>absent(), Optional.<String>absent(), false));

        // then
        assertEquals("ProjectDescLabel NoDescriptionPlaceholder", htmlReport.getContent());
    }

    @Test(expected = IllegalStateException.class)
    public void insertContractId_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        htmlReport = new HtmlReport(context, "<!-- ${project_contract_id_labelXX} --> <!-- ${project_contract_id} -->", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertContractId(new Project("", "A Title", Optional.of("desc"), Optional.of("contract"), false));
    }

    @Test(expected = IllegalStateException.class)
    public void insertContractId_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_project_contract_id_label)).thenReturn("ContractIdLabel");
        when(context.getString(R.string.report_project_no_contract_id_placeholder)).thenReturn("Placeholder");
        htmlReport = new HtmlReport(context, "<!-- ${project_contract_id_label} --> <!-- ${project_contract_idXX} -->", mock(Project.class), new Date(), new Date());

        // expect
        htmlReport.insertContractId(new Project("", "A Title", Optional.of("desc"), Optional.of("contract"), false));
    }

    @Test
    public void insertContractId_sets_contract_id_and_a_localized_label() throws IOException {
        // given
        when(context.getString(R.string.report_project_contract_id_label)).thenReturn("ContractIdLabel");
        when(context.getString(R.string.report_project_no_contract_id_placeholder)).thenReturn("Placeholder");
        htmlReport = new HtmlReport(context, "<!-- ${project_contract_id_label} --> <!-- ${project_contract_id} -->", mock(Project.class), new Date(), new Date());

        // when
        htmlReport.insertContractId(new Project("", "MyTitle", Optional.<String>absent(), Optional.of("contract"), false));

        // then
        assertEquals("ContractIdLabel contract", htmlReport.getContent());
    }

    @Test
    public void insertContractId_inserts_localized_placeholder_when_no_description_is_set_at_project() throws IOException {
        // given
        when(context.getString(R.string.report_project_contract_id_label)).thenReturn("ContractIdLabel");
        when(context.getString(R.string.report_project_no_contract_id_placeholder)).thenReturn("Placeholder");
        htmlReport = new HtmlReport(context, "<!-- ${project_contract_id_label} --> <!-- ${project_contract_id} -->", mock(Project.class), new Date(), new Date());

        // when: reporting a project *without* a contract id
        htmlReport.insertContractId(new Project("", "MyTitle", Optional.<String>absent(), Optional.<String>absent(), false));

        // then
        assertEquals("ContractIdLabel Placeholder", htmlReport.getContent());
    }

    public void insertTotalDuration_inserts_localized_label_and_total_duration_value() throws IOException {
        // given
        when(context.getString(R.string.report_total_duration_label)).thenReturn("TotalDurationLabel");
        htmlReport = new HtmlReport(context, "<!-- ${total_duration_label} --> <!-- ${total_duration} -->", mock(Project.class), new Date(), new Date());

        // when
        htmlReport.insertTotalDuration("SomeDurationValue");

        // then
        assertEquals("TotalDurationLabel SomeDurationValue", htmlReport.getContent());
    }
}