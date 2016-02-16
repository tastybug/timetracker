package com.tastybug.timetracker.model;

import android.content.Context;

import com.tastybug.timetracker.database.dao.DAOFactory;
import com.tastybug.timetracker.database.dao.EntityDAO;

import java.io.Serializable;

public abstract class Entity implements Serializable {

	protected Context context;
	protected DAOFactory daoFactory = new DAOFactory();

    public Entity() {}

	public Context getContext() {
		return context;
	}

	public void setContext(Context c) {
		this.context = c;
	}

	public boolean hasContext () {
		return context != null;
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
