package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 系统配置管理 - 兼容旧API
 * 数据库表: system_config
 * API路径: /config/**
 */
@RestController
@RequestMapping("/api/config")
@Tag(name = "系统配置", description = "系统配置API(兼容旧admin)")
public class SystemConfigController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询配置")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String name) {
        
        StringBuilder sql = new StringBuilder("SELECT * FROM system_config WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (name != null && !name.isEmpty()) {
            sql.append(" AND NAME LIKE ?");
            params.add("%" + name + "%");
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
    @Operation(summary = "获取配置详情")
    public Map<String, Object> info(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap("SELECT * FROM system_config WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("配置不存在");
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新配置")
    public Map<String, Object> update(@RequestBody Map<String, Object> config) {
        try {
            jdbcTemplate.update(
                "UPDATE system_config SET VALUE=? WHERE id=?",
                config.get("value"), config.get("id")
            );
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }
}
