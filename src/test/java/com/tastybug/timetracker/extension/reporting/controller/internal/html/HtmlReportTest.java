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
    private TemplateAssetProvider templateAssetProvider;

    private HtmlReport htmlReport;

    @Before
    public void setup() {
        context = mock(Context.class);
        templateAssetProvider = mock(TemplateAssetProvider.class);
        htmlReport = new HtmlReport(context, templateAssetProvider, mock(Project.class), new Date(), new Date());
        when(context.getString(R.string.report_project_no_description_placeholder)).thenReturn("");
        when(context.getString(R.string.report_date_header)).thenReturn("");
        when(context.getString(R.string.report_amount_header)).thenReturn("");
        when(context.getString(R.string.report_description_header)).thenReturn("");
    }

    @Test
    public void constructor_gets_template_from_assets_via_provider() throws IOException {
        // given
        String expectedTemplate = "expectedTemplate";
        when(templateAssetProvider.getReportTemplate()).thenReturn(expectedTemplate);

        // when
        HtmlReport htmlReport = new HtmlReport(context, templateAssetProvider, mock(Project.class), new Date(), new Date());

        // then
        assertEquals(expectedTemplate, htmlReport.getContent());
    }

    @Test
    public void localizeHeaders_localizes_all_headers() throws IOException {
        // given
        when(context.getString(R.string.report_date_header)).thenReturn("DATEHEADER");
        when(context.getString(R.string.report_amount_header)).thenReturn("AMOUNTHEADER");
        when(context.getString(R.string.report_description_header)).thenReturn("DESCRIPTIONHEADER");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${date_header} --> <!-- ${amount_header} --> <!-- ${description_header} -->");

        // when
        htmlReport.localizeHeaders();

        // then
        assertEquals("DATEHEADER AMOUNTHEADER DESCRIPTIONHEADER", htmlReport.getContent());
    }

    @Test
    public void insertReportables_alters_template_correctly() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("bla <!-- ${reportables_list} --> bla");

        // when
        htmlReport.insertReportablesList("testHtml");

        // then
        assertEquals("bla testHtml bla", htmlReport.getContent());
    }

    @Test(expected = IllegalStateException.class)
    public void insertReportablesList_throws_IllegalState_when_marker_is_not_found_in_template() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("nothing here");

        // expect
        htmlReport.insertReportablesList("testHtml");
    }

    @Test
    public void insertTitle_alters_template_correctly() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("bla <!-- ${title} --> bla");

        // when
        htmlReport.insertReportTitle("testTitle");

        // then
        assertEquals("bla testTitle bla", htmlReport.getContent());
    }

    @Test(expected = IllegalStateException.class)
    public void insertTitle_throws_IllegalState_when_marker_is_not_found_in_template() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("nothing here");

        // expect
        htmlReport.insertReportTitle("testHtml");
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectTitle_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_title_labelXX} --> <!-- ${project_title} -->");

        // expect
        htmlReport.insertProjectTitle(new Project("", "A Title", Optional.<String>absent(), Optional.<String>absent(), false));
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectTitle_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_project_title_label)).thenReturn("ProjectLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_title_label} --> <!-- ${project_titleXX} -->");

        // expect
        htmlReport.insertProjectTitle(new Project("", "A Title", Optional.<String>absent(), Optional.<String>absent(), false));
    }

    @Test(expected = IllegalStateException.class)
    public void insertTotalDuration_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_total_duration_label)).thenReturn("Laaaabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${total_duration_labelXX} --> <!-- ${total_duration} -->");

        // expect
        htmlReport.insertTotalDuration("someValue");
    }

    @Test(expected = IllegalStateException.class)
    public void insertTotalDuration_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_total_duration_label)).thenReturn("Laabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${total_duration_label} --> <!-- ${total_durationXX} -->");

        // expect
        htmlReport.insertTotalDuration("someValue");
    }

    @Test
    public void insertProjectTitle_sets_project_title_and_a_localized_label() throws IOException {
        // given
        when(context.getString(R.string.report_project_title_label)).thenReturn("ProjectLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_title_label} --> <!-- ${project_title} -->");

        // when
        htmlReport.insertProjectTitle(new Project("", "MyTitle", Optional.<String>absent(), Optional.<String>absent(), false));

        // then
        assertEquals("ProjectLabel MyTitle", htmlReport.getContent());
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectDescription_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_description_labelXX} --> <!-- ${project_description} -->");

        // expect
        htmlReport.insertProjectDescription(new Project("", "A Title", Optional.of("desc"), Optional.<String>absent(), false));
    }

    @Test(expected = IllegalStateException.class)
    public void insertProjectDescription_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_project_description_label)).thenReturn("ProjectDescLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_description_label} --> <!-- ${project_descriptionXX} -->");

        // expect
        htmlReport.insertProjectDescription(new Project("", "A Title", Optional.of("desc"), Optional.<String>absent(), false));
    }

    @Test
    public void insertProjectDescription_sets_project_description_and_a_localized_label() throws IOException {
        // given
        when(context.getString(R.string.report_project_description_label)).thenReturn("ProjectDescLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_description_label} --> <!-- ${project_description} -->");

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
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_description_label} --> <!-- ${project_description} -->");

        // when: reporting a project *without* a description
        htmlReport.insertProjectDescription(new Project("", "MyTitle", Optional.<String>absent(), Optional.<String>absent(), false));

        // then
        assertEquals("ProjectDescLabel NoDescriptionPlaceholder", htmlReport.getContent());
    }

    @Test(expected = IllegalStateException.class)
    public void insertContractId_throws_IllegalState_when_label_marker_is_not_found_in_template() throws IOException {
        // given
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_contract_id_labelXX} --> <!-- ${project_contract_id} -->");

        // expect
        htmlReport.insertContractId(new Project("", "A Title", Optional.of("desc"), Optional.of("contract"), false));
    }

    @Test(expected = IllegalStateException.class)
    public void insertContractId_throws_IllegalState_when_value_marker_is_not_found_in_template() throws IOException {
        // given
        when(context.getString(R.string.report_project_contract_id_label)).thenReturn("ContractIdLabel");
        when(context.getString(R.string.report_project_no_contract_id_placeholder)).thenReturn("Placeholder");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_contract_id_label} --> <!-- ${project_contract_idXX} -->");

        // expect
        htmlReport.insertContractId(new Project("", "A Title", Optional.of("desc"), Optional.of("contract"), false));
    }

    @Test
    public void insertContractId_sets_contract_id_and_a_localized_label() throws IOException {
        // given
        when(context.getString(R.string.report_project_contract_id_label)).thenReturn("ContractIdLabel");
        when(context.getString(R.string.report_project_no_contract_id_placeholder)).thenReturn("Placeholder");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_contract_id_label} --> <!-- ${project_contract_id} -->");

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
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${project_contract_id_label} --> <!-- ${project_contract_id} -->");

        // when: reporting a project *without* a contract id
        htmlReport.insertContractId(new Project("", "MyTitle", Optional.<String>absent(), Optional.<String>absent(), false));

        // then
        assertEquals("ContractIdLabel Placeholder", htmlReport.getContent());
    }

    public void insertTotalDuration_inserts_localized_label_and_total_duration_value() throws IOException {
        // given
        when(context.getString(R.string.report_total_duration_label)).thenReturn("TotalDurationLabel");
        when(templateAssetProvider.getReportTemplate()).thenReturn("<!-- ${total_duration_label} --> <!-- ${total_duration} -->");

        // when
        htmlReport.insertTotalDuration("SomeDurationValue");

        // then
        assertEquals("TotalDurationLabel SomeDurationValue", htmlReport.getContent());
    }
}