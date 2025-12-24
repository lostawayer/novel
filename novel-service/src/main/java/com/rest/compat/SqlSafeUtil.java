package com.rest.compat;

import java.util.Set;

/**
 * SQL安全工具类 - 防止SQL注入
 */
public class SqlSafeUtil {
    
    // 允许的排序字段白名单
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id", "ID", "addtime", "ADD_TIME", "CREATE_TIME", "PUBLISH_TIME", "CLICK_TIME",
        "CHAPTER_NUM", "title", "TITLE", "name", "NAME", "NOVEL_NAME", "CATEGORY_NAME",
        "AUTHOR_NAME", "USERNAME", "ACCOUNT", "REAL_NAME", "NICKNAME"
    );
    
    // 允许的排序方向
    private static final Set<String> ALLOWED_ORDER = Set.of("asc", "ASC", "desc", "DESC");
    
    /**
     * 验证并返回安全的排序字段
     */
    public static String safeSortField(String sort, String defaultField) {
        if (sort == null || sort.isEmpty() || !ALLOWED_SORT_FIELDS.contains(sort)) {
            return defaultField;
        }
        return sort;
    }
    
    /**
     * 验证并返回安全的排序方向
     */
    public static String safeOrder(String order) {
        if (order == null || !ALLOWED_ORDER.contains(order)) {
            return "DESC";
        }
        return order.toUpperCase();
    }
}
