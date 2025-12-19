package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 小说类型管理 - 兼容旧API
 * 数据库表: novel_category
 * API路径: /xiaoshuoleixing/**
 */
@RestController
@RequestMapping("/api/xiaoshuoleixing")
@Tag(name = "小说类型管理", description = "小说类型管理API(兼容旧admin)")
public class NovelCategoryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询小说类型")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String categoryName) {
        
        StringBuilder sql = new StringBuilder("SELECT * FROM novel_category WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (categoryName != null && !categoryName.isEmpty()) {
            sql.append(" AND CATEGORY_NAME LIKE ?");
            params.add("%" + categoryName + "%");
        }
        
        String countSql = sql.toString().replace("SELECT *", "SELECT COUNT(*)");
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(sort).append(" ").append(order);
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "获取类型详情")
    public Map<String, Object> info(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM novel_category WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("类型不存在");
        }
    }

    @PostMapping("/save")
    @Operation(summary = "新增类型")
    public Map<String, Object> save(@RequestBody Map<String, Object> category) {
        try {
            jdbcTemplate.update(
                "INSERT INTO novel_category (CATEGORY_NAME) VALUES (?)",
                category.get("categoryName")
            );
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate")) {
                return CompatResult.error("类型名称已存在");
            }
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新类型")
    public Map<String, Object> update(@RequestBody Map<String, Object> category) {
        try {
            jdbcTemplate.update(
                "UPDATE novel_category SET CATEGORY_NAME=? WHERE id=?",
                category.get("categoryName"), category.get("id")
            );
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除类型")
    public Map<String, Object> delete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM novel_category WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }
}
