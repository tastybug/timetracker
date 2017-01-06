package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Build;

import com.squareup.otto.Bus;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.dao.ProjectDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class DeleteProjectTaskTest {

    private Context context = mock(Context.class);
    private ProjectDAO projectDAO = mock(ProjectDAO.class);

    private DeleteProjectTask subject = new DeleteProjectTask(context, projectDAO);

    @Before
    public void setup() {
        when(projectDAO.delete(anyString())).thenReturn(true);
    }

    @Test
    public void providing_existing_project_uuid_leads_to_deletion() throws Exception {
        // given
        String projectUuid = "123";
        subject.withProjectUuid(projectUuid);

        // when
        subject.execute();

        // then
        verify(projectDAO).delete(projectUuid);
    }

    @Test(expected = RuntimeException.class)
    public void unsuccessful_deletion_yields_RuntimeException() throws Exception {
        // given
        String projectUuid = "this-does-not-exist";
        when(projectDAO.delete(projectUuid)).thenReturn(false);
        subject.withProjectUuid(projectUuid);

        // when
        subject.execute();
    }

    @Test
    public void successful_deletion_is_announced_via_otto() throws Exception {
        // given
        Bus ottoBus = mock(Bus.class);
        OttoProvider ottoProvider = mock(OttoProvider.class);
        when(ottoProvider.getSharedBus()).thenReturn(ottoBus);
        subject.withProjectUuid("123");
        subject.setOttoProvider(ottoProvider);

        // when
        subject.execute();

        // then
        verify(ottoBus).post(isA(ProjectDeletedEvent.class));
    }

    @Test(expected = NullPointerException.class)
    public void lack_of_project_uuid_to_delete_yields_NPE() {
        // expect
        new DeleteProjectTask(context).execute();
    }

}
