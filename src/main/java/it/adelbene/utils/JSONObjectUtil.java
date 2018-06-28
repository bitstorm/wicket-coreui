package it.adelbene.utils;

import java.util.Arrays;

import com.github.openjson.JSONArray;

public class JSONObjectUtil {
	public static JSONArray asJSONArray(Object... objects) {
		return new JSONArray(Arrays.asList(objects));
	}
}
