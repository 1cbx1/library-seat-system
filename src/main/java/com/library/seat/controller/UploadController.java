package com.library.seat.controller;

import com.library.seat.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传 Controller
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    // 上传目录配置
    @Value("${upload.path:D:/毕设项目/library-seat-system/uploads}")
    private String uploadPath;

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }
        
        // 检查文件大小 (最大5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("文件大小不能超过5MB");
        }
        
        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成新文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFileName = UUID.randomUUID().toString() + suffix;
            
            // 保存文件
            File destFile = new File(uploadDir, newFileName);
            file.transferTo(destFile);
            
            // 返回访问路径
            String fileUrl = "/uploads/" + newFileName;
            return Result.success(fileUrl);
            
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传轮播图
     */
    @PostMapping("/banner")
    public Result<?> uploadBanner(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        try {
            File uploadDir = new File(uploadPath + "/banners");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            String suffix = ".jpg";
            if (file.getOriginalFilename() != null) {
                suffix = file.getOriginalFilename().substring(
                    file.getOriginalFilename().lastIndexOf("."));
            }
            String newFileName = "banner_" + System.currentTimeMillis() + suffix;
            
            File destFile = new File(uploadDir, newFileName);
            file.transferTo(destFile);
            
            String fileUrl = "/uploads/banners/" + newFileName;
            return Result.success(fileUrl);
            
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }
        
        try {
            File uploadDir = new File(uploadPath + "/avatars");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            String suffix = ".jpg";
            if (file.getOriginalFilename() != null) {
                suffix = file.getOriginalFilename().substring(
                    file.getOriginalFilename().lastIndexOf("."));
            }
            String newFileName = "avatar_" + System.currentTimeMillis() + suffix;
            
            File destFile = new File(uploadDir, newFileName);
            file.transferTo(destFile);
            
            String fileUrl = "/uploads/avatars/" + newFileName;
            return Result.success(fileUrl);
            
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
}
