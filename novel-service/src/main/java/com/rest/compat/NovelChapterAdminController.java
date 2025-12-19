package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 小说章节管理 - 兼容旧API
 * 数据库表: novel_chapter
 * API路径: /chapterxiaoshuoxinxi/**
 */
@RestController
@RequestMapping("/api/chapterxiaoshuoxinxi")
@Tag(name = "章节管理", description = "章节管理API(兼容旧admin)")
public class NovelChapterAdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询章节")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "CHAPTER_NUM") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) Long refId,
            @RequestParam(required = false) String chapterTitle) {
        
        StringBuilder sql = new StringBuilder("SELECT * FROM novel_chapter WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (refId != null) {
            sql.append(" AND REF_ID = ?");
            params.add(refId);
        }
        if (chapterTitle != null && !chapterTitle.isEmpty()) {
            sql.append(" AND CHAPTER_TITLE LIKE ?");
            params.add("%" + chapterTitle + "%");
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
    @Operation(summary = "获取章节详情")
    public Map<String, Object> info(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM novel_chapter WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("章节不存在");
        }
    }

    @PostMapping("/save")
    @Operation(summary = "新增章节")
    public Map<String, Object> save(@RequestBody Map<String, Object> chapter) {
        try {
            jdbcTemplate.update(
                "INSERT INTO novel_chapter (REF_ID, CHAPTER_NUM, CHAPTER_TITLE, CONTENT, VIP_READ) VALUES (?, ?, ?, ?, ?)",
                chapter.get("refId"), chapter.get("chapterNum"), chapter.get("chapterTitle"), 
                chapter.get("content"), chapter.getOrDefault("vipRead", "否")
            );
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新章节")
    public Map<String, Object> update(@RequestBody Map<String, Object> chapter) {
        try {
            jdbcTemplate.update(
                "UPDATE novel_chapter SET CHAPTER_NUM=?, CHAPTER_TITLE=?, CONTENT=?, VIP_READ=? WHERE id=?",
                chapter.get("chapterNum"), chapter.get("chapterTitle"), chapter.get("content"),
                chapter.get("vipRead"), chapter.get("id")
            );
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除章节")
    public Map<String, Object> delete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM novel_chapter WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }
}
