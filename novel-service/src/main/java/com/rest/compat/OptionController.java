package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 下拉选项 - 兼容旧API
 * API路径: /option/**
 */
@RestController
@RequestMapping("/api/option")
@Tag(name = "下拉选项", description = "下拉选项API(兼容旧admin)")
public class OptionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/category")
    @Operation(summary = "获取小说类型选项")
    public Map<String, Object> categoryOptions() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "SELECT id, CATEGORY_NAME as label, CATEGORY_NAME as value FROM novel_category ORDER BY id"
        );
        return CompatResult.options(list);
    }

    @GetMapping("/author")
    @Operation(summary = "获取作者选项")
    public Map<String, Object> authorOptions() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "SELECT ID as id, AUTHOR_NAME as label, ACCOUNT as value FROM author WHERE AUDIT_STATUS = '是' ORDER BY ID"
        );
        return CompatResult.options(list);
    }

    @GetMapping("/auditStatus")
    @Operation(summary = "获取审核状态选项")
    public Map<String, Object> auditStatusOptions() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("label", "待审核", "value", "待审核"));
        list.add(Map.of("label", "是", "value", "是"));
        list.add(Map.of("label", "否", "value", "否"));
        return CompatResult.options(list);
    }

    @GetMapping("/gender")
    @Operation(summary = "获取性别选项")
    public Map<String, Object> genderOptions() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("label", "男", "value", "男"));
        list.add(Map.of("label", "女", "value", "女"));
        return CompatResult.options(list);
    }

    @GetMapping("/vip")
    @Operation(summary = "获取VIP选项")
    public Map<String, Object> vipOptions() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("label", "是", "value", "是"));
        list.add(Map.of("label", "否", "value", "否"));
        return CompatResult.options(list);
    }
}
