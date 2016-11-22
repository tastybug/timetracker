package com.tastybug.timetracker.model;

import android.content.Context;

import com.tastybug.timetracker.model.dao.DAOFactory;
import com.tastybug.timetracker.model.dao.EntityDAO;

import java.io.Serializable;

public abstract class Entity implements Serializable {

    DAOFactory daoFactory = new DAOFactory();

    void setDAOFactory(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public abstract String getUuid();

    public abstract void setUuid(String uuid);

    public EntityDAO getDAO(Context context) {
        return daoFactory.getDao(getClass(), context);
    }
}
