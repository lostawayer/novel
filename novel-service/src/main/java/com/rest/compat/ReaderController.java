package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户(读者)管理 - 兼容旧API
 * 数据库表: reader
 * API路径: /yonghu/**
 */
@RestController
@RequestMapping("/api/yonghu")
@Tag(name = "用户管理", description = "用户管理API(兼容旧admin)")
public class ReaderController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询用户")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName) {
        
        StringBuilder sql = new StringBuilder("SELECT * FROM reader WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (username != null && !username.isEmpty()) {
            sql.append(" AND USERNAME LIKE ?");
            params.add("%" + username + "%");
        }
        if (realName != null && !realName.isEmpty()) {
            sql.append(" AND REAL_NAME LIKE ?");
            params.add("%" + realName + "%");
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
    @Operation(summary = "获取用户详情")
    public Map<String, Object> info(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM reader WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("用户不存在");
        }
    }

    @PostMapping("/save")
    @Operation(summary = "新增用户")
    public Map<String, Object> save(@RequestBody Map<String, Object> user) {
        try {
            jdbcTemplate.update(
                "INSERT INTO reader (USERNAME, PASSWORD, NICKNAME, REAL_NAME, GENDER, AVATAR, EMAIL, PHONE, VIP) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                user.get("username"), user.get("password"), user.get("nickname"), user.get("realName"),
                user.get("gender"), user.get("avatar"), user.get("email"), user.get("phone"), 
                user.getOrDefault("vip", "否")
            );
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新用户")
    public Map<String, Object> update(@RequestBody Map<String, Object> user) {
        try {
            jdbcTemplate.update(
                "UPDATE reader SET NICKNAME=?, REAL_NAME=?, GENDER=?, AVATAR=?, EMAIL=?, PHONE=?, VIP=? WHERE id=?",
                user.get("nickname"), user.get("realName"), user.get("gender"), user.get("avatar"),
                user.get("email"), user.get("phone"), user.get("vip"), user.get("id")
            );
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除用户")
    public Map<String, Object> delete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM reader WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }
}
