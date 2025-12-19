package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 作者管理 - 兼容旧API
 * 数据库表: author
 * API路径: /zuozhe/**
 */
@RestController
@RequestMapping("/api/zuozhe")
@Tag(name = "作者管理", description = "作者管理API(兼容旧admin)")
public class AuthorAdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询作者")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "ID") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String auditStatus) {
        
        StringBuilder sql = new StringBuilder("SELECT * FROM author WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (account != null && !account.isEmpty()) {
            sql.append(" AND ACCOUNT LIKE ?");
            params.add("%" + account + "%");
        }
        if (authorName != null && !authorName.isEmpty()) {
            sql.append(" AND AUTHOR_NAME LIKE ?");
            params.add("%" + authorName + "%");
        }
        if (auditStatus != null && !auditStatus.isEmpty()) {
            sql.append(" AND AUDIT_STATUS = ?");
            params.add(auditStatus);
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
    @Operation(summary = "获取作者详情")
    public Map<String, Object> info(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM author WHERE ID = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("作者不存在");
        }
    }

    @PostMapping("/save")
    @Operation(summary = "新增作者")
    public Map<String, Object> save(@RequestBody Map<String, Object> author) {
        try {
            jdbcTemplate.update(
                "INSERT INTO author (ACCOUNT, PASSWORD, AUTHOR_NAME, GENDER, AVATAR, AGE, ID_CARD, PHONE, EMAIL, AUDIT_STATUS, AUDIT_REPLY) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                author.get("account"), author.get("password"), author.get("authorName"), author.get("gender"),
                author.get("avatar"), author.get("age"), author.get("idCard"), author.get("phone"), 
                author.get("email"), author.getOrDefault("auditStatus", "待审核"), author.get("auditReply")
            );
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新作者")
    public Map<String, Object> update(@RequestBody Map<String, Object> author) {
        try {
            jdbcTemplate.update(
                "UPDATE author SET AUTHOR_NAME=?, GENDER=?, AVATAR=?, AGE=?, ID_CARD=?, PHONE=?, EMAIL=?, AUDIT_STATUS=?, AUDIT_REPLY=? WHERE ID=?",
                author.get("authorName"), author.get("gender"), author.get("avatar"), author.get("age"),
                author.get("idCard"), author.get("phone"), author.get("email"), 
                author.get("auditStatus"), author.get("auditReply"), author.get("id")
            );
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/audit")
    @Operation(summary = "审核作者")
    public Map<String, Object> audit(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "UPDATE author SET AUDIT_STATUS=?, AUDIT_REPLY=? WHERE ID=?",
                data.get("auditStatus"), data.get("auditReply"), data.get("id")
            );
            return CompatResult.ok("审核成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除作者")
    public Map<String, Object> delete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM author WHERE ID IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }
}
