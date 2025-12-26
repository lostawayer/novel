package com.rest.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 读者前台接口
 */
@RestController
@RequestMapping("/yonghu")
@Tag(name = "读者接口", description = "读者登录注册等")
public class ReaderFrontController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    @Operation(summary = "读者登录")
    public Map<String, Object> login(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        
        if (username == null || password == null) {
            return FrontResult.error("用户名和密码不能为空");
        }
        
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(
                "SELECT id, USERNAME as yonghuming, PASSWORD as password, REAL_NAME as xingming, " +
                "GENDER as xingbie, AVATAR as touxiang, EMAIL as youxiang, PHONE as shouji, VIP as vip " +
                "FROM reader WHERE USERNAME = ?", username);
            
            if (!password.equals(user.get("password"))) {
                return FrontResult.error("密码错误");
            }
            
            user.remove("password");
            user.put("tableName", "yonghu");
            
            String token = "token_" + user.get("id") + "_" + System.currentTimeMillis();
            return FrontResult.login(user, token);
        } catch (Exception e) {
            return FrontResult.error("用户不存在");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "读者注册")
    public Map<String, Object> register(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        
        if (username == null || username.trim().isEmpty()) {
            return FrontResult.error("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            return FrontResult.error("密码不能少于6位");
        }
        
        String nickname = (String) params.getOrDefault("yonghuxingming", username);
        String phone = (String) params.getOrDefault("shouji", "");
        
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reader WHERE USERNAME = ?", Integer.class, username);
            if (count != null && count > 0) {
                return FrontResult.error("用户名已存在");
            }
            
            jdbcTemplate.update(
                "INSERT INTO reader (USERNAME, PASSWORD, NICKNAME, REAL_NAME, EMAIL, PHONE, ADD_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)",
                username.trim(), password, nickname, nickname, "", phone, LocalDateTime.now());
            
            return FrontResult.ok("注册成功");
        } catch (Exception e) {
            return FrontResult.error("注册失败");
        }
    }

    @PostMapping("/update")
    @Operation(summary = "更新读者信息")
    public Map<String, Object> update(@RequestBody Map<String, Object> params) {
        if (params.get("id") == null) {
            return FrontResult.error("用户ID不能为空");
        }
        
        try {
            Long id = Long.valueOf(params.get("id").toString());
            
            if (params.containsKey("mima") && params.get("mima") != null) {
                String newPassword = params.get("mima").toString();
                if (newPassword.length() < 6) {
                    return FrontResult.error("密码不能少于6位");
                }
                jdbcTemplate.update("UPDATE reader SET PASSWORD = ? WHERE id = ?", newPassword, id);
            } else {
                jdbcTemplate.update(
                    "UPDATE reader SET REAL_NAME = ?, GENDER = ?, AVATAR = ?, EMAIL = ?, PHONE = ? WHERE id = ?",
                    params.get("xingming"), params.get("xingbie"), params.get("touxiang"),
                    params.get("youxiang"), params.get("shouji"), id);
            }
            return FrontResult.ok("更新成功");
        } catch (Exception e) {
            return FrontResult.error("更新失败");
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public Map<String, Object> info(@RequestParam Long userId) {
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(
                "SELECT id, USERNAME as yonghuming, NICKNAME as nicheng, REAL_NAME as xingming, " +
                "GENDER as xingbie, AVATAR as touxiang, EMAIL as youxiang, PHONE as shouji, " +
                "VIP as vip, VIP_EXPIRE_TIME as vipExpireTime " +
                "FROM reader WHERE id = ?", userId);
            
            // 检查VIP是否过期
            Object expireTime = user.get("vipExpireTime");
            if ("是".equals(user.get("vip")) && expireTime != null) {
                LocalDateTime expire;
                if (expireTime instanceof java.sql.Timestamp) {
                    expire = ((java.sql.Timestamp) expireTime).toLocalDateTime();
                } else {
                    expire = (LocalDateTime) expireTime;
                }
                
                if (expire.isBefore(LocalDateTime.now())) {
                    // VIP已过期，更新状态
                    jdbcTemplate.update("UPDATE reader SET VIP = '否' WHERE id = ?", userId);
                    user.put("vip", "否");
                }
            }
            
            return FrontResult.ok(user);
        } catch (Exception e) {
            return FrontResult.error("用户不存在");
        }
    }

    @PostMapping("/buyVip")
    @Operation(summary = "购买VIP")
    public Map<String, Object> buyVip(@RequestBody Map<String, Object> params) {
        if (params.get("userId") == null) {
            return FrontResult.error("用户ID不能为空");
        }
        
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String vipType = (String) params.getOrDefault("vipType", "month");
            
            jdbcTemplate.update("UPDATE reader SET VIP = '是' WHERE id = ?", userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("vip", "是");
            data.put("vipType", vipType);
            return FrontResult.ok("VIP购买成功！", data);
        } catch (Exception e) {
            return FrontResult.error("购买失败");
        }
    }
}
