package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 小说评论管理 - 兼容旧API
 * 数据库表: novel_comment
 * API路径: /discussxiaoshuoxinxi/**
 */
@RestController
@RequestMapping("/api/discussxiaoshuoxinxi")
@Tag(name = "评论管理", description = "评论管理API(兼容旧admin)")
public class NovelCommentController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询评论")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) Long refId,
            @RequestParam(required = false) String nickname) {
        
        StringBuilder sql = new StringBuilder("SELECT c.*, n.NOVEL_NAME FROM novel_comment c LEFT JOIN novel_info n ON c.REF_ID = n.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (refId != null) {
            sql.append(" AND c.REF_ID = ?");
            params.add(refId);
        }
        if (nickname != null && !nickname.isEmpty()) {
            sql.append(" AND c.NICKNAME LIKE ?");
            params.add("%" + nickname + "%");
        }
        
        String countSql = sql.toString().replace("SELECT c.*, n.NOVEL_NAME", "SELECT COUNT(*)");
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, params.toArray());
        
        sql.append(" ORDER BY c.").append(sort).append(" ").append(order);
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "获取评论详情")
    public Map<String, Object> info(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM novel_comment WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("评论不存在");
        }
    }

    @PostMapping("/reply")
    @Operation(summary = "回复评论")
    public Map<String, Object> reply(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "UPDATE novel_comment SET REPLY=? WHERE id=?",
                data.get("reply"), data.get("id")
            );
            return CompatResult.ok("回复成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除评论")
    public Map<String, Object> delete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM novel_comment WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }
}
