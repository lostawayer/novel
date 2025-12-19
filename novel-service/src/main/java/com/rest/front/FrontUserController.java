package com.rest.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 前台用户接口 - 登录注册
 */
@RestController
@Tag(name = "前台-用户", description = "前台用户接口")
public class FrontUserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ========== 读者 ==========
    @PostMapping("/yonghu/login")
    @Operation(summary = "读者登录")
    public Map<String, Object> yonghuLogin(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(
                "SELECT id, USERNAME as username, PASSWORD as password, REAL_NAME as xingming, " +
                "GENDER as xingbie, AVATAR as touxiang, EMAIL as youxiang, PHONE as shouji " +
                "FROM reader WHERE USERNAME = ?", username);
            
            if (!password.equals(user.get("password"))) {
                return errorResult("密码错误");
            }
            
            user.remove("password");
            user.put("tableName", "yonghu");
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("token", "token_" + user.get("id") + "_" + System.currentTimeMillis());
            result.put("data", user);
            return result;
        } catch (Exception e) {
            return errorResult("用户不存在");
        }
    }

    @PostMapping("/yonghu/register")
    @Operation(summary = "读者注册")
    public Map<String, Object> yonghuRegister(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reader WHERE USERNAME = ?", Integer.class, username);
            if (count != null && count > 0) {
                return errorResult("用户名已存在");
            }
            
            jdbcTemplate.update(
                "INSERT INTO reader (USERNAME, PASSWORD, CREATE_TIME) VALUES (?, ?, ?)",
                username, password, LocalDateTime.now());
            
            return successResult("注册成功");
        } catch (Exception e) {
            return errorResult(e.getMessage());
        }
    }

    @PostMapping("/yonghu/update")
    @Operation(summary = "更新读者信息")
    public Map<String, Object> yonghuUpdate(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.valueOf(params.get("id").toString());
            
            // 如果有密码字段，更新密码
            if (params.containsKey("mima") && params.get("mima") != null) {
                jdbcTemplate.update("UPDATE reader SET PASSWORD = ? WHERE id = ?", params.get("mima"), id);
            } else {
                jdbcTemplate.update(
                    "UPDATE reader SET REAL_NAME = ?, GENDER = ?, AVATAR = ?, EMAIL = ?, PHONE = ? WHERE id = ?",
                    params.get("xingming"), params.get("xingbie"), params.get("touxiang"),
                    params.get("youxiang"), params.get("shouji"), id);
            }
            
            return successResult("更新成功");
        } catch (Exception e) {
            return errorResult(e.getMessage());
        }
    }

    // ========== 作者 ==========
    @PostMapping("/zuozhe/login")
    @Operation(summary = "作者登录")
    public Map<String, Object> zuozheLogin(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(
                "SELECT id, USERNAME as username, PASSWORD as password, AUTHOR_NAME as zuozhemingcheng, " +
                "GENDER as xingbie, AVATAR as touxiang, AGE as nianling, ID_CARD as shenfenzheng, " +
                "STATUS as zhanghaoztai FROM author WHERE USERNAME = ?", username);
            
            if (!password.equals(user.get("password"))) {
                return errorResult("密码错误");
            }
            
            // 检查账号状态
            String status = (String) user.get("zhanghaoztai");
            if (!"已通过".equals(status)) {
                return errorResult("账号未审核通过");
            }
            
            user.remove("password");
            user.put("tableName", "zuozhe");
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("token", "token_" + user.get("id") + "_" + System.currentTimeMillis());
            result.put("data", user);
            return result;
        } catch (Exception e) {
            return errorResult("用户不存在");
        }
    }

    @PostMapping("/zuozhe/register")
    @Operation(summary = "作者注册")
    public Map<String, Object> zuozheRegister(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM author WHERE USERNAME = ?", Integer.class, username);
            if (count != null && count > 0) {
                return errorResult("用户名已存在");
            }
            
            jdbcTemplate.update(
                "INSERT INTO author (USERNAME, PASSWORD, STATUS, CREATE_TIME) VALUES (?, ?, '待审核', ?)",
                username, password, LocalDateTime.now());
            
            return successResult("注册成功，请等待审核");
        } catch (Exception e) {
            return errorResult(e.getMessage());
        }
    }

    @PostMapping("/zuozhe/update")
    @Operation(summary = "更新作者信息")
    public Map<String, Object> zuozheUpdate(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.valueOf(params.get("id").toString());
            
            if (params.containsKey("mima") && params.get("mima") != null) {
                jdbcTemplate.update("UPDATE author SET PASSWORD = ? WHERE id = ?", params.get("mima"), id);
            } else {
                jdbcTemplate.update(
                    "UPDATE author SET AUTHOR_NAME = ?, GENDER = ?, AVATAR = ?, AGE = ?, ID_CARD = ? WHERE id = ?",
                    params.get("zuozhemingcheng"), params.get("xingbie"), params.get("touxiang"),
                    params.get("nianling"), params.get("shenfenzheng"), id);
            }
            
            return successResult("更新成功");
        } catch (Exception e) {
            return errorResult(e.getMessage());
        }
    }

    private Map<String, Object> successResult(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", msg);
        return result;
    }

    private Map<String, Object> errorResult(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", msg);
        return result;
    }
}
