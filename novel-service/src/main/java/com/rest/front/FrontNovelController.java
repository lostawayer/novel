package com.rest.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 前台小说接口 - 兼容旧前端API
 */
@RestController
@RequestMapping("/xiaoshuoxinxi")
@Tag(name = "前台-小说", description = "前台小说接口")
public class FrontNovelController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    @Operation(summary = "小说列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String xiaoshuomingcheng,
            @RequestParam(required = false) String xiaoshuoleixing) {
        
        StringBuilder sql = new StringBuilder("SELECT id, NOVEL_NAME as xiaoshuomingcheng, CATEGORY_NAME as xiaoshuoleixing, ");
        sql.append("PICTURE as fengmian, DESCRIPTION as jianjie, AUTHOR_NAME as zuozhe, ");
        sql.append("CLICK_TIME as clicknum, PUBLISH_TIME as addtime, ACCOUNT as jiage ");
        sql.append("FROM novel_info WHERE 1=1");
        
        List<Object> params = new ArrayList<>();
        
        if (xiaoshuomingcheng != null && !xiaoshuomingcheng.isEmpty()) {
            sql.append(" AND NOVEL_NAME LIKE ?");
            params.add("%" + xiaoshuomingcheng + "%");
        }
        if (xiaoshuoleixing != null && !xiaoshuoleixing.isEmpty()) {
            sql.append(" AND CATEGORY_NAME = ?");
            params.add(xiaoshuoleixing);
        }
        
        // 处理排序字段映射
        String sortField = mapSortField(sort);
        
        String countSql = "SELECT COUNT(*) FROM novel_info WHERE 1=1";
        if (xiaoshuomingcheng != null && !xiaoshuomingcheng.isEmpty()) {
            countSql += " AND NOVEL_NAME LIKE ?";
        }
        if (xiaoshuoleixing != null && !xiaoshuoleixing.isEmpty()) {
            countSql += " AND CATEGORY_NAME = ?";
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(sortField).append(" ").append(order);
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        
        // 添加收藏数
        for (Map<String, Object> item : list) {
            try {
                Integer storeupnum = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM storeup WHERE REF_ID = ? AND TABLE_NAME = 'xiaoshuoxinxi'",
                    Integer.class, item.get("id"));
                item.put("storeupnum", storeupnum != null ? storeupnum : 0);
            } catch (Exception e) {
                item.put("storeupnum", 0);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total != null ? total : 0);
        result.put("data", data);
        return result;
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "小说详情")
    public Map<String, Object> detail(@PathVariable Long id) {
        try {
            String sql = "SELECT id, NOVEL_NAME as xiaoshuomingcheng, CATEGORY_NAME as xiaoshuoleixing, " +
                    "PICTURE as fengmian, DESCRIPTION as jianjie, AUTHOR_NAME as zuozhe, " +
                    "CLICK_TIME as clicknum, PUBLISH_TIME as addtime, ACCOUNT as jiage " +
                    "FROM novel_info WHERE id = ?";
            Map<String, Object> data = jdbcTemplate.queryForMap(sql, id);
            
            // 增加点击量
            jdbcTemplate.update("UPDATE novel_info SET CLICK_TIME = IFNULL(CLICK_TIME, 0) + 1 WHERE id = ?", id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("data", data);
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 1);
            result.put("msg", "小说不存在");
            return result;
        }
    }

    private String mapSortField(String sort) {
        switch (sort) {
            case "addtime": return "PUBLISH_TIME";
            case "clicknum": return "CLICK_TIME";
            case "xiaoshuomingcheng": return "NOVEL_NAME";
            default: return "id";
        }
    }
}
