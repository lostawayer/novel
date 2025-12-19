package com.rest.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 前台留言板接口
 */
@RestController
@RequestMapping("/messages")
@Tag(name = "前台-留言板", description = "前台留言板接口")
public class FrontMessageController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    @Operation(summary = "留言列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "SELECT id, USER_ID as userid, USERNAME as username, AVATAR as avatarurl, CONTENT as content, " +
            "REPLY as reply, CREATE_TIME as addtime FROM message_board ORDER BY id DESC LIMIT ? OFFSET ?",
            limit, (page - 1) * limit);
        
        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM message_board", Integer.class);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total != null ? total : 0);
        result.put("data", data);
        return result;
    }

    @PostMapping("/add")
    @Operation(summary = "添加留言")
    public Map<String, Object> add(@RequestBody Map<String, Object> message) {
        try {
            jdbcTemplate.update(
                "INSERT INTO message_board (USER_ID, USERNAME, AVATAR, CONTENT, CREATE_TIME) VALUES (?, ?, ?, ?, ?)",
                message.get("userid"), message.get("username"), message.get("avatarurl"),
                message.get("content"), LocalDateTime.now());
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("msg", "留言成功");
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 1);
            result.put("msg", e.getMessage());
            return result;
        }
    }
}
