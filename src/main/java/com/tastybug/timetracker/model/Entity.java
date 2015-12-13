package com.tastybug.timetracker.model;

import android.content.Context;

import com.tastybug.timetracker.util.database.EntityDAO;

import org.slf4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

public abstract class Entity implements Serializable, PropertyChangeListener {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

	protected Context   context;
	protected EntityDAO dao;

    public Entity() {}

	public Entity(Context c) {
		this.context = c;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context c) {
		this.context = c;
	}

	public boolean hasContext () {
		return context != null;
	}

	public abstract Integer getId();

	public abstract void setId(Integer id);

	protected void setDAO(EntityDAO dao) {
        this.dao = dao;
	}

	protected EntityDAO getDAO(Context context) {
        return dao != null ? dao : getDefaultDAOInstance(context);
    }

    protected abstract EntityDAO getDefaultDAOInstance(Context context);

    public void propertyChange(PropertyChangeEvent event) {
		if (hasContext()) { // access available to the DB? if not, simply ignore it
            Object oldValue = event.getOldValue(), newValue = event.getNewValue();
            logger.debug("Propertychange: property= " + event.getPropertyName()
                    + ",sourceId=" + ((Entity) event.getSource()).getId()
                    + ", oldValue=" + event.getOldValue()
                    + ", newValue=" + event.getNewValue());
			EntityDAO dao;
			if (oldValue instanceof Entity || newValue instanceof Entity) { // must be an association
				if (oldValue == null) { // add
					dao = ((Entity)newValue).getDAO(context);
					dao.create((Entity)newValue);
				} else { // remove
					dao = ((Entity)oldValue).getDAO(context);
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
