package com.tastybug.timetracker.task.project;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;

import com.squareup.otto.Bus;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class CreateProjectTaskTest {

    Context context = mock(Context.class);
    ContentResolver resolver = mock(ContentResolver.class);
    OttoProvider ottoProvider = mock(OttoProvider.class);
    Bus ottoBus = mock(Bus.class);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(resolver);
        when(ottoProvider.getSharedBus()).thenReturn(ottoBus);
    }

    // TODO der Test prueft nicht den Inhalt der Batchoperation
    @Test
    public void happyPath() throws Exception {
        // given
        CreateProjectTask task = CreateProjectTask.aTask(context).withProjectTitle("a title").withProjectDescription("a desc");
        task.setOttoProvider(ottoProvider);

        // when
        task.execute();

        // then: a sql batch operation is executed
        verify(resolver, times(1)).applyBatch(any(String.class), any(ArrayList.class));
        verify(ottoBus, times(1)).post(isA(ProjectCreatedEvent.class));
    }

    @Test(expected = NullPointerException.class)
    public void lackOfProjectTitleFailsTheTask() {
        // expect
        CreateProjectTask.aTask(context).execute();
    }

}