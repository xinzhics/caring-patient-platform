package com.caring.sass.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Collection;
import java.util.List;

public final class JsonUtils {
  public JsonUtils() {
  }

  public static final JSONObject bean2JSONObject(Object obj) {
    return (JSONObject)JSON.toJSON(obj);
  }

  public static final JSONArray bean2JSONArray(Collection<?> collection) {
    return (JSONArray)JSON.toJSON(collection);
  }

  public static final String bean2String(Object obj) {
    return JSON.toJSONString(obj);
  }

  public static final JSONArray String2JSONArray(String jsonStr) {
    return (JSONArray)JSONArray.parse(jsonStr);
  }

  public static final JSONObject String2JSONObject(String jsonStr) {
    return (JSONObject)JSONObject.parse(jsonStr);
  }

  public static final <T> T String2Bean(String jsonStr, Class<T> clazz) {
    return JSON.parseObject(jsonStr, clazz);
  }

  public static final <T> T JSONObject2Bean(JSONObject jsonObject, Class<T> clazz) {
    return JSON.parseObject(jsonObject.toString(), clazz);
  }

  public static final <T> Collection<T> String2Collection(String jsonStr, Class<T> clazz) {
    Collection<T> t = JSON.parseArray(jsonStr, clazz);
    return t;
  }

  public static final <T> List<T> String2List(String jsonStr, Class<T> clazz) {
    List<T> t = JSON.parseArray(jsonStr, clazz);
    return t;
  }

  public static final <T> List<T> JSONArray2List(JSONArray jsonArray, Class<T> clazz) {
    List<T> t = JSON.parseArray(jsonArray.toString(), clazz);
    return t;
  }

  public static final <T> Collection<T> JSONArray2Collection(JSONArray jsonArray, Class<T> clazz) {
    Collection<T> t = JSON.parseArray(jsonArray.toString(), clazz);
    return t;
  }
}
