package com.tastybug.timetracker.core.model;

import java.io.Serializable;

public abstract class Entity implements Serializable {

    public abstract String getUuid();

    public abstract void setUuid(String uuid);
}
