package com.tastybug.timetracker.task.project;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;

import com.squareup.otto.Bus;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.task.OttoProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class DeleteProjectTaskTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);
    OttoProvider ottoProvider = mock(OttoProvider.class);
    Bus ottoBus = mock(Bus.class);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
        when(ottoProvider.getSharedBus()).thenReturn(ottoBus);
    }

    @Test
    public void happyPath() throws Exception {
        // given
        DeleteProjectTask task = DeleteProjectTask.aTask(context).withProjectUuid("123");
        task.setOttoProvider(ottoProvider);

        // when
        task.execute();

        // then
        verify(resolver, times(1)).delete(eq(new ProjectDAO(context).getQueryUri()), isA(String.class), eq(new String[] {"123"}));
        verify(ottoBus, times(1)).post(isA(ProjectDeletedEvent.class));
    }

    @Test(expected = NullPointerException.class)
    public void lackOfProjectTitleFailsTheTask() {
        // expect
        DeleteProjectTask.aTask(context).execute();
    }

}