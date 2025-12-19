package com.rest.compat;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传Controller
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {

    // 上传目录 - resources/static/upload (开发时) 或运行目录下的 static/upload (生产时)
    private static final String UPLOAD_DIR = "src/main/resources/static/upload/";

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
            
            // 确保上传目录存在
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            File destFile = new File(uploadDir, newFilename);
            file.transferTo(destFile);
            
            // 返回文件路径 (数据库存储格式)
            String filePath = "upload/" + newFilename;
            
            result.put("code", 0);
            result.put("msg", "上传成功");
            result.put("url", filePath);
            
        } catch (IOException e) {
            result.put("code", 1);
            result.put("msg", "上传失败: " + e.getMessage());
        }
        
        return result;
    }
}
