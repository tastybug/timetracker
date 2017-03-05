package com.tastybug.timetracker.extensions.backup.controller.dataimport;

import android.content.ContentProviderOperation;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DbWipeBatchOpsProviderTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private DbWipeBatchOpsProvider subject = new DbWipeBatchOpsProvider(projectDAO);

    @Test
    public void getOperations_returns_deletion_operation_for_every_existing_project() {
        // given
        ContentProviderOperation deletion1 = mock(ContentProviderOperation.class);
        ContentProviderOperation deletion2 = mock(ContentProviderOperation.class);
        ArrayList<Project> list = aListOfTwoProjects();
        when(projectDAO.getAll()).thenReturn(list);
        when(projectDAO.getBatchDeleteAll(list.get(0))).thenReturn(deletion1);
        when(projectDAO.getBatchDeleteAll(list.get(1))).thenReturn(deletion2);

        // when
        List<ContentProviderOperation> operations = subject.getOperations();

        // then
        assertTrue(operations.contains(deletion1));
        assertTrue(operations.contains(deletion2));
        assertEquals(2, operations.size());
    }

    private ArrayList<Project> aListOfTwoProjects() {
        Project project1 = new Project("123", "proj1", Optional.<String>absent(), false);
        Project project2 = new Project("456", "proj2", Optional.<String>absent(), false);
        ArrayList<Project> list = new ArrayList<>();
        list.add(project1);
        list.add(project2);

        return list;
    }
}