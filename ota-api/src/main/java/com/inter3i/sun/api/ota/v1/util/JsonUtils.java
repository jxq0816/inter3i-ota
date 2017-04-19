/*
 *
 * Copyright (c) 2016, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2016/12/19
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static Object deepCloneObj(Object cloneSource) throws JSONException, CloneNotSupportedException {
        Object innerResult = null;
        if (isSimpleType(cloneSource)) {
            innerResult = cloneSimpleData(cloneSource);
        } else if (cloneSource instanceof JSONObject) {
            innerResult = new JSONObject();
            deepClone((JSONObject) cloneSource, (JSONObject) innerResult);
        } else if (cloneSource instanceof JSONArray) {
            innerResult = new JSONArray();
            deepClone((JSONArray) cloneSource, (JSONArray) innerResult);
        } else {
            throw new CloneNotSupportedException("Data type:[" + cloneSource.getClass().getSimpleName() + "].");
        }
        return innerResult;
    }


    public static void deepClone(JSONObject sourceData, JSONObject result) throws JSONException, CloneNotSupportedException {
        String key = null;
        Object innerData = null;
        while (sourceData.keys().hasNext()) {
            key = (String) sourceData.keys().next();
            innerData = sourceData.get(key);
            Object resultData = deepCloneObj(innerData);
            result.put(key, resultData);
        }
    }

    public static void deepClone(JSONArray sourceData, JSONArray result) throws JSONException, CloneNotSupportedException {
        for (int t = 0; t < sourceData.length(); t++) {
            Object cloneSource = sourceData.get(t);
            Object cloneResult = deepCloneObj(cloneSource);
            result.put(t, cloneResult);
        }
    }

    public static boolean isSimpleType(Object innerData) {
        if (innerData instanceof String || innerData instanceof Integer || innerData instanceof Long || innerData instanceof Double
                || innerData instanceof Float || innerData instanceof Boolean) {
            return true;
        }
        return false;
    }

    private static Object cloneSimpleData(Object innerData) throws JSONException, CloneNotSupportedException {
        if (innerData instanceof String) {
            return new String((String) innerData);
        } else if (innerData instanceof Integer || innerData instanceof Long || innerData instanceof Double
                || innerData instanceof Float || innerData instanceof Boolean) {
            return innerData;
        } else {
            throw new CloneNotSupportedException("Data type:[" + innerData.getClass().getSimpleName() + "].");
        }
    }
}
