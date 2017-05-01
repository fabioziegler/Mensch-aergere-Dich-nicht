package com.vintagetechnologies.menschaergeredichnicht;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fabio on 05.04.17.
 *
 * Used for information passing between activities (uses singleton pattern).
 * Recommended way by android google engineers.
 */
public class DataHolder {

    private static DataHolder instance = null;

    private Map<String, WeakReference<Object>> data = new HashMap<String, WeakReference<Object>>();

    /**
     * Only to prevent initialisation
     */
    private DataHolder(){

    }

    /**
     * Save an object to access it later from another activity.
     * @param id A String to retrieve the object later
     * @param object The object to be saved
     */
    public void save(String id, Object object) {
        data.put(id, new WeakReference<>(object));
    }

    /**
     * Retrieve an object from the global data holder.
     * @param id A String referencing the object
     * @return The object saved for that String or null if the object does not exist.
     */
    public Object retrieve(String id) {
        WeakReference<Object> objectWeakReference = data.get(id);

		if(objectWeakReference != null)
        	return objectWeakReference.get();
		else
			return null;
    }

    /**
     * Get the instance of the global data holder
     * @return
     */
    public static DataHolder getInstance(){
        if(instance == null){
            instance = new DataHolder();
        }
        return instance;
    }

}
