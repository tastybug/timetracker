package com.tastybug.timetracker.model;

import android.content.Context;

import com.tastybug.timetracker.database.dao.DAOFactory;
import com.tastybug.timetracker.database.dao.EntityDAO;

import org.slf4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

public abstract class Entity implements Serializable, PropertyChangeListener {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

	protected Context   context;
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

    public void propertyChange(PropertyChangeEvent event) {
		if (hasContext()) { // access available to the DB? if not, simply ignore it
            Object oldValue = event.getOldValue(), newValue = event.getNewValue();
            logger.debug("Propertychange: property= " + event.getPropertyName()
                    + ",sourceId=" + ((Entity) event.getSource()).getUuid()
                    + ", oldValue=" + event.getOldValue()
                    + ", newValue=" + event.getNewValue());
			EntityDAO dao;
			if (oldValue instanceof Entity || newValue instanceof Entity) { // must be an association
				if (oldValue == null) { // add
					dao = daoFactory.getDao(newValue.getClass(), context);
					dao.create((Entity)newValue);
				} else { // remove
                    dao = daoFactory.getDao(oldValue.getClass(), context);
					dao.delete((Entity)oldValue);
				}
			} else {
                dao = getDAO(context);
				if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
                    logger.debug(getClass().getSimpleName(), "Property unchanged, skipping update.");
				} else {
                    dao.update((Entity)event.getSource());
				}
			}
		}
	}
}
