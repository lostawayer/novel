package com.rest;

import com.commons.mvc.WebResult;
import com.domain.User;
import com.service.IUserManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理员登录Controller - 兼容前端API
 */
@RestController
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequestMapping("/users")
@Tag(name = "管理员登录", description = "管理员登录API")
public class AdminController {

    @Autowired
    @Qualifier(IUserManager.ID)
    private IUserManager userManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员登录验证")
    public Map<String, Object> login(
            @RequestParam(value = "username") @Parameter(description = "用户名") String username,
            @RequestParam(value = "password") @Parameter(description = "密码") String password) {
        
        Map<String, Object> result = new HashMap<>();
        
        User user = userManager.getUserByName(username);
        if (user == null) {
            result.put("code", 500);
            result.put("msg", "用户不存在");
            return result;
        }
        
        // 检查密码 - 支持明文和加密密码
        boolean passwordMatch = false;
        String storedPassword = user.getPassword();
        
        if (storedPassword != null) {
            // 尝试BCrypt匹配
            if (storedPassword.startsWith("{bcrypt}") || storedPassword.startsWith("$2a$")) {
                String encodedPassword = storedPassword.startsWith("{bcrypt}") 
                    ? storedPassword.substring(8) : storedPassword;
                passwordMatch = passwordEncoder.matches(password, encodedPassword);
            } else {
                // 明文密码比较
                passwordMatch = storedPassword.equals(password);
            }
        }
        
        if (!passwordMatch) {
            result.put("code", 500);
            result.put("msg", "密码错误");
            return result;
        }
        
        // 生成token
        String token = UUID.randomUUID().toString().replace("-", "");
        
        result.put("code", 0);
        result.put("msg", "登录成功");
        result.put("token", token);
        
        return result;
    }

    @GetMapping("/session")
    @Operation(summary = "获取会话信息", description = "获取当前登录用户信息")
    public Map<String, Object> session(@RequestHeader(value = "Token", required = false) String token) {
        Map<String, Object> result = new HashMap<>();
        
        if (token == null || token.isEmpty()) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        
        // 简化处理，返回管理员信息
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("username", "admin");
        data.put("role", "管理员");
        
        result.put("code", 0);
        result.put("data", data);
        
        return result;
    }
}
