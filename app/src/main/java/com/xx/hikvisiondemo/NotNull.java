package com.xx.hikvisiondemo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class NotNull {
    public static boolean isNotNull(Integer i) {
        return null != i && 0 != i;
    }

    public static boolean isNotNull(Double d) {
        return null != d && 0 != d;
    }

    public static boolean isNotNull(Object object) {
        return null != object && !"".equals(object);
    }

    public static boolean isNotNull(List<?> t) {
        return null != t && t.size() > 0;
    }

    public static boolean isNotNull(Map<?, ?> t) {
        return null != t && t.size() > 0;
    }

    public static boolean isNotNull(Object[] objects) {
        return null != objects && objects.length > 0;
    }

    public static boolean isNotNull(JSONArray jsonArray) {
        return null != jsonArray && jsonArray.length() > 0;
    }

    public static boolean isNotNull(JSONObject jsonObject) {
        return null != jsonObject;
    }

    public static boolean isNotNullAndNaN(Object object) {
        return isNotNull(object) && !object.toString().equals("NaN");
    }
}
