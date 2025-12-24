package com.rest.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 作者前台接口
 */
@RestController
@RequestMapping("/zuozhe")
@Tag(name = "作者前台接口", description = "作者注册审核等")
public class AuthorFrontController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/register")
    @Operation(summary = "作者注册")
    public Map<String, Object> register(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        
        if (username == null || username.trim().isEmpty()) {
            return FrontResult.error("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            return FrontResult.error("密码不能少于6位");
        }
        
        String authorName = (String) params.getOrDefault("authorName", username);
        String phone = (String) params.get("phone");
        String email = (String) params.get("email");
        
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM author WHERE ACCOUNT = ?", Integer.class, username);
            if (count != null && count > 0) {
                return FrontResult.error("用户名已存在");
            }
            
            jdbcTemplate.update(
                "INSERT INTO author (ACCOUNT, PASSWORD, AUTHOR_NAME, PHONE, EMAIL, AUDIT_STATUS, CREATE_TIME) VALUES (?, ?, ?, ?, ?, '待审核', ?)",
                username.trim(), password, authorName, phone, email, LocalDateTime.now());
            
            return FrontResult.ok("注册成功！您的账号正在等待管理员审核。");
        } catch (Exception e) {
            return FrontResult.error("注册失败");
        }
    }

    @GetMapping("/auditStatus")
    @Operation(summary = "查询审核状态")
    public Map<String, Object> auditStatus(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) {
            return FrontResult.error("用户名不能为空");
        }
        
        try {
            Map<String, Object> author = jdbcTemplate.queryForMap(
                "SELECT ACCOUNT as username, AUTHOR_NAME as authorName, AUDIT_STATUS as auditStatus, " +
                "AUDIT_REPLY as auditReply, CREATE_TIME as createTime FROM author WHERE ACCOUNT = ?", username);
            return FrontResult.ok(author);
        } catch (Exception e) {
            return FrontResult.error("账号不存在");
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新作者信息")
    public Map<String, Object> update(@RequestBody Map<String, Object> params) {
        if (params.get("id") == null) {
            return FrontResult.error("作者ID不能为空");
        }
        
        try {
            Long id = Long.valueOf(params.get("id").toString());
            
            if (params.containsKey("mima") && params.get("mima") != null) {
                String newPassword = params.get("mima").toString();
                if (newPassword.length() < 6) {
                    return FrontResult.error("密码不能少于6位");
                }
                jdbcTemplate.update("UPDATE author SET PASSWORD = ? WHERE id = ?", newPassword, id);
            } else {
                jdbcTemplate.update(
                    "UPDATE author SET AUTHOR_NAME = ?, GENDER = ?, AVATAR = ?, AGE = ?, ID_CARD = ? WHERE id = ?",
                    params.get("zuozhemingcheng"), params.get("xingbie"), params.get("touxiang"),
                    params.get("nianling"), params.get("shenfenzheng"), id);
            }
            return FrontResult.ok("更新成功");
        } catch (Exception e) {
            return FrontResult.error("更新失败");
        }
    }
}
