package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 小说信息管理 - 兼容旧API
 * 数据库表: novel_info
 * API路径: /xiaoshuoxinxi/**
 */
@RestController
@RequestMapping("/api/xiaoshuoxinxi")
@Tag(name = "小说信息管理", description = "小说信息管理API(兼容旧admin)")
public class NovelInfoController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询小说")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String novelName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String authorName) {
        
        StringBuilder sql = new StringBuilder("SELECT * FROM novel_info WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (novelName != null && !novelName.isEmpty()) {
            sql.append(" AND NOVEL_NAME LIKE ?");
            params.add("%" + novelName + "%");
        }
        if (categoryName != null && !categoryName.isEmpty()) {
            sql.append(" AND CATEGORY_NAME = ?");
            params.add(categoryName);
        }
        if (authorName != null && !authorName.isEmpty()) {
            sql.append(" AND AUTHOR_NAME LIKE ?");
            params.add("%" + authorName + "%");
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
    @Operation(summary = "获取小说详情")
    public Map<String, Object> info(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM novel_info WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("小说不存在");
        }
    }

    @PostMapping("/save")
    @Operation(summary = "新增小说")
    public Map<String, Object> save(@RequestBody Map<String, Object> novel) {
        try {
            jdbcTemplate.update(
                "INSERT INTO novel_info (NOVEL_NAME, CATEGORY_NAME, PICTURE, DESCRIPTION, ACCOUNT, AUTHOR_NAME, PUBLISH_TIME, CLICK_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                novel.get("novelName"), novel.get("categoryName"), novel.get("picture"), novel.get("description"),
                novel.get("account"), novel.get("authorName"), LocalDateTime.now(), LocalDateTime.now()
            );
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新小说")
    public Map<String, Object> update(@RequestBody Map<String, Object> novel) {
        try {
            jdbcTemplate.update(
                "UPDATE novel_info SET NOVEL_NAME=?, CATEGORY_NAME=?, PICTURE=?, DESCRIPTION=? WHERE id=?",
                novel.get("novelName"), novel.get("categoryName"), novel.get("picture"), 
                novel.get("description"), novel.get("id")
            );
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除小说")
    public Map<String, Object> delete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            // 同时删除相关章节和评论
            jdbcTemplate.update("DELETE FROM novel_chapter WHERE REF_ID IN (" + placeholders + ")", ids.toArray());
            jdbcTemplate.update("DELETE FROM novel_comment WHERE REF_ID IN (" + placeholders + ")", ids.toArray());
            jdbcTemplate.update("DELETE FROM novel_info WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }
}
