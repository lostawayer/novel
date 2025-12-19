package com.rest.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 前台配置接口 - 轮播图等
 */
@RestController
@RequestMapping("/config")
@Tag(name = "前台-配置", description = "前台配置接口")
public class FrontConfigController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    @Operation(summary = "配置列表(轮播图)")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort) {
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "SELECT id, NAME as name, VALUE as value FROM system_config ORDER BY id LIMIT ? OFFSET ?",
            limit, (page - 1) * limit);
        
        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM system_config", Integer.class);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total != null ? total : 0);
        result.put("data", data);
        return result;
    }
}
