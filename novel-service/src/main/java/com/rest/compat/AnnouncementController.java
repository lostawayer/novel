package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 公告管理
 */
@RestController
@RequestMapping("/api/news")
@Tag(name = "公告管理", description = "公告管理API")
public class AnnouncementController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询公告")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String title) {
        
        String safeSort = SqlSafeUtil.safeSortField(sort, "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT id, TITLE as title, INTRODUCTION as introduction, PICTURE as picture, CONTENT as content, ADD_TIME as addtime FROM announcement WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (title != null && !title.isEmpty()) {
            sql.append(" AND TITLE LIKE ?");
            params.add("%" + title + "%");
        }
        
        String countSql = "SELECT COUNT(*) FROM announcement WHERE 1=1" + (title != null && !title.isEmpty() ? " AND TITLE LIKE ?" : "");
        List<Object> countParams = title != null && !title.isEmpty() ? List.of("%" + title + "%") : List.of();
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, countParams.toArray());
        
        sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "获取公告详情")
    public Map<String, Object> info(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT id, TITLE as title, INTRODUCTION as introduction, PICTURE as picture, CONTENT as content, ADD_TIME as addtime FROM announcement WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("公告不存在");
        }
    }

    @PostMapping("/save")
    @Operation(summary = "新增公告")
    public Map<String, Object> save(@RequestBody Map<String, Object> news) {
        try {
            jdbcTemplate.update(
                "INSERT INTO announcement (TITLE, INTRODUCTION, PICTURE, CONTENT) VALUES (?, ?, ?, ?)",
                news.get("title"), news.get("introduction"), news.get("picture"), news.get("content")
            );
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新公告")
    public Map<String, Object> update(@RequestBody Map<String, Object> news) {
        Long id = Long.valueOf(news.get("id").toString());
        try {
            jdbcTemplate.update(
                "UPDATE announcement SET TITLE=?, INTRODUCTION=?, PICTURE=?, CONTENT=? WHERE id=?",
                news.get("title"), news.get("introduction"), news.get("picture"), news.get("content"), id
            );
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "批量删除公告")
    public Map<String, Object> delete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM announcement WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }
}
