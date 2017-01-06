package com.tastybug.timetracker.model;

import com.google.common.base.Optional;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

public class ProjectTest {

    @Test(expected = NullPointerException.class)
    public void setting_null_UUID_yields_NPE() {
        // given
        Project project = new Project("project title");

        // when
        project.setUuid(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setting_null_title_yields_IAE() {
        // given
        Project project = new Project("project title");

        // when
        project.setTitle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setting_empty_title_yields_IAE() {
        // given
        Project project = new Project("project title");

        // when
        project.setTitle("");
    }

    @Test
    public void can_serialize() {
        // given
        Project project = new Project("1234", "some title", Optional.of("a desc"));

        // when: this is supposed to cause no exception
        SerializationUtils.serialize(project);
    }
}