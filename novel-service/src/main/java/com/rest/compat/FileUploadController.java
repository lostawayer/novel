package com.rest.compat;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传Controller
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        if (file.isEmpty()) {
            result.put("code", 1);
            result.put("msg", "文件不能为空");
            return result;
        }

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            // 生成新文件名
            String newFilename = System.currentTimeMillis() + suffix;
            
            // 使用系统属性获取项目根目录
            String projectDir = System.getProperty("user.dir");
            Path uploadPath = Paths.get(projectDir, "novel-service", "src", "main", "resources", "static", "upload");
            
            // 确保上传目录存在
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 保存文件
            Path destPath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), destPath);
            
            // 返回文件路径 (数据库存储格式)
            String filePath = "upload/" + newFilename;
            
            result.put("code", 0);
            result.put("msg", "上传成功");
            result.put("url", filePath);
            result.put("file", newFilename);
            
        } catch (IOException e) {
            result.put("code", 1);
            result.put("msg", "上传失败: " + e.getMessage());
        }
        
        return result;
    }
}
