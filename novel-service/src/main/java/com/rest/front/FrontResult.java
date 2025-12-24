package com.rest.front;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台接口统一响应
 */
public class FrontResult {
    
    public static Map<String, Object> ok() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        return result;
    }
    
    public static Map<String, Object> ok(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", msg);
        return result;
    }
    
    public static Map<String, Object> ok(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", data);
        return result;
    }
    
    public static Map<String, Object> ok(String msg, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }
    
    public static Map<String, Object> error(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", msg);
        return result;
    }
    
    public static Map<String, Object> page(List<?> list, int total) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        result.put("data", data);
        return result;
    }
    
    public static Map<String, Object> login(Object data, String token) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("token", token);
        result.put("data", data);
        return result;
    }
}
