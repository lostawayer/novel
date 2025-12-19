package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 留言板管理 - 兼容旧API
 * 数据库表: message_board
 * API路径: /messages/**
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "留言板管理", description = "留言板管理API(兼容旧admin)")
public class MessageBoardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询留言")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String username) {
        
        StringBuilder sql = new StringBuilder("SELECT * FROM message_board WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (username != null && !username.isEmpty()) {
            sql.append(" AND USERNAME LIKE ?");
            params.add("%" + username + "%");
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
    @Operation(summary = "获取留言详情")
    public Map<String, Object> info(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM message_board WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("留言不存在");
        }
    }

    @PostMapping("/reply")
    @Operation(summary = "回复留言")
    public Map<String, Object> reply(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "UPDATE message_board SET REPLY=?, R_PICTURE=? WHERE id=?",
                data.get("reply"), data.get("rPicture"), data.get("id")
            );
            return CompatResult.ok("回复成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除留言")
    public Map<String, Object> delete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM message_board WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }
}
