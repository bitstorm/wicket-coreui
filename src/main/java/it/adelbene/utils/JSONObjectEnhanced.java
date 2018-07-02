package it.adelbene.utils;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class JSONObjectEnhanced extends JSONObject {

	public JSONObjectEnhanced putArray(String name, Object... values) {
		put(name, new JSONArray(values));
		return this;
	}

}
