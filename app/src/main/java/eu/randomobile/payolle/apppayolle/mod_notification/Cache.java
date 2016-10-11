package eu.randomobile.payolle.apppayolle.mod_notification;

import java.util.ArrayList;
import java.util.HashMap;

import eu.randomobile.payolle.apppayolle.mod_global.model.Poi;

public class Cache {
	public static ArrayList<Poi> arrayPois = null;
	public static HashMap<String, Integer> hashMapPois = null;
	public static ArrayList<Poi> filteredPois = null;
	public static Boolean notificationEnabled = false;
	
	public static void iniHashMapPois () {
		if (Cache.hashMapPois != null){
			Cache.hashMapPois.clear();
			Cache.hashMapPois = null;
		}
		Cache.hashMapPois = new HashMap<String, Integer>();
		for (int i=0; i < Cache.arrayPois.size(); i++) {
			Cache.hashMapPois.put(Cache.arrayPois.get(i).getNid(), i);
		}
	}
}
