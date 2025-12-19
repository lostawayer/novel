package com.rest.compat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兼容旧API的响应格式
 * 格式: { code: 0, data: { list: [], total: N } }
 */
public class CompatResult {
    
    public static Map<String, Object> page(List<?> list, int total) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        result.put("data", data);
        return result;
    }
    
    public static Map<String, Object> ok() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "操作成功");
        return result;
    }
    
    public static Map<String, Object> ok(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", data);
        return result;
    }
    
    public static Map<String, Object> ok(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", msg);
        return result;
    }
    
    public static Map<String, Object> error(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", msg);
        return result;
    }
    
    public static Map<String, Object> options(List<?> list) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("data", list);
        return result;
    }
}
