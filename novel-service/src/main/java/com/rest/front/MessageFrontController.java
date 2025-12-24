package com.rest.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 留言板前台接口
 */
@RestController
@RequestMapping("/messages")
@Tag(name = "留言板接口", description = "留言板功能")
public class MessageFrontController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    @Operation(summary = "留言列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "SELECT id, USER_ID as userid, USERNAME as username, AVATAR_URL as avatarurl, CONTENT as content, " +
            "REPLY as reply, ADD_TIME as addtime FROM message_board ORDER BY id DESC LIMIT ? OFFSET ?",
            limit, (page - 1) * limit);
        
        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM message_board", Integer.class);
        
        return FrontResult.page(list, total != null ? total : 0);
    }

    @PostMapping("/add")
    @Operation(summary = "添加留言")
    public Map<String, Object> add(@RequestBody Map<String, Object> message) {
        String content = (String) message.get("content");
        if (content == null || content.trim().isEmpty()) {
            return FrontResult.error("留言内容不能为空");
        }
        
        try {
            jdbcTemplate.update(
                "INSERT INTO message_board (USER_ID, USERNAME, AVATAR_URL, CONTENT) VALUES (?, ?, ?, ?)",
                message.get("userid"), message.get("username"), message.get("avatarurl"),
                content.trim());
            
            return FrontResult.ok("留言成功");
        } catch (Exception e) {
            return FrontResult.error("留言失败");
        }
    }
}
