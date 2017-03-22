package eu.randomobile.payolle.apppayolle.utils;

import org.json.JSONObject;

public abstract class JSONManager {
	
	/**
	 * Devuelve el valor de un parametro del JSON.
	 * @param json JSON a parsear.
	 * @param parametro Parametro a recuperar.
	 * @return
	 */
	public static String getString(final JSONObject json, final String parametro) {
		String valor = "";
		try {
			valor = json.getString(parametro);
			if (valor.equals("null")) {
				valor = "";
			}
		} catch(Exception ex) {}
		return valor;
	}
	
	public static int getInt(final JSONObject json, final String parametro) {
		int valor = -1;
		try {
			valor = json.getInt(parametro);
		} catch(Exception ex) {}
		return valor;
	}

}
