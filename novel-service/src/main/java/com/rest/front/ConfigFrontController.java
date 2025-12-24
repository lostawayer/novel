package com.rest.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 配置前台接口
 */
@RestController
@RequestMapping("/config")
@Tag(name = "配置接口", description = "轮播图等配置")
public class ConfigFrontController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    @Operation(summary = "配置列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "SELECT id, NAME as name, VALUE as value FROM system_config ORDER BY id LIMIT ? OFFSET ?",
            limit, (page - 1) * limit);
        
        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM system_config", Integer.class);
        
        return FrontResult.page(list, total != null ? total : 0);
    }
}
