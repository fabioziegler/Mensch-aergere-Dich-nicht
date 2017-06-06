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

	/* weak reference: GC collects an object if the last reference to it is in the weak reference map */

    private Map<String, Object> data = new HashMap<>();

    /**
     * Only to prevent initialisation.
     */
    private DataHolder(){

    }

    /**
     * Save an object to access it later from another activity.
     * @param id A String to retrieve the object later
     * @param object The object to be saved
     */
    public void save(String id, Object object) {
        data.put(id, object);
    }

    /**
     * Retrieve an object from the global data holder.
     * @param id A String referencing the object
     * @return The object saved for that String or null if the object does not exist.
     */
    public Object retrieve(String id) {
		return data.get(id);
    }


	/**
	 * Retrieve an object from the global data holder.
	 * @param id A String referencing the object
	 * @param type The class that should be casted to.
	 * @return The object saved for by the given id, casted to the given class or null if the object does not exist.
	 */
    public <T> T retrieve(String id, Class<T> type){
		Object o = retrieve(id);
		if(o != null)
			return type.cast(retrieve(id));
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
