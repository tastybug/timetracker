package com.tastybug.timetracker.extension.autoclosure.controller;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AutoClosureServiceTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private ClosabilityIndicator closabilityIndicator = mock(ClosabilityIndicator.class);
    private ProjectCloser projectCloser = mock(ProjectCloser.class);
    private AutoClosureService autoClosureService = new AutoClosureService(projectDAO, closabilityIndicator, projectCloser);

    @Test
    public void performGlobalAutoClose_checks_all_projecs_and_closes_when_necessary() {
        Project closable1 = mock(Project.class);
        Project nonClosable = mock(Project.class);
        Project closable2 = mock(Project.class);
        List<Project> projects = Arrays.asList(closable1, nonClosable, closable2);
        when(projectDAO.getAll()).thenReturn(projects);
        when(closabilityIndicator.isProjectClosable(any(Project.class))).thenReturn(true, false, true);

        autoClosureService.performGlobalAutoClose();

        // all projects have been checked
        verify(closabilityIndicator).isProjectClosable(closable1);
        verify(closabilityIndicator).isProjectClosable(nonClosable);
        verify(closabilityIndicator).isProjectClosable(closable2);
        verifyNoMoreInteractions(closabilityIndicator);
        // the correct projects have been closed
        verify(projectCloser).closeProject(closable1);
        verify(projectCloser).closeProject(closable2);
        verifyNoMoreInteractions(projectCloser);
    }

}