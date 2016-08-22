package com.tastybug.timetracker.model;

import android.content.Context;

import com.tastybug.timetracker.model.dao.DAOFactory;
import com.tastybug.timetracker.model.dao.EntityDAO;

import java.io.Serializable;

public abstract class Entity implements Serializable {

    protected DAOFactory daoFactory = new DAOFactory();

    public Entity() {
    }

    public void setDAOFactory(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public abstract String getUuid();

    public abstract void setUuid(String uuid);

    public EntityDAO getDAO(Context context) {
        return daoFactory.getDao(getClass(), context);
    }
}
