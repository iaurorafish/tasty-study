package com.controller;

import cn.hutool.core.util.StrUtil;
import com.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.utils.SystemConstants.IMAGE_UPLOAD_DIR;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {
    @PostMapping("/blog")
    public Result uploadImage(@RequestParam("file") MultipartFile image) {
        try {
            // 获取原始文件名称
            String originalFilename = image.getOriginalFilename();
            // 生成新文件名
            String fileName = createNewFileName(originalFilename);
            // 保存文件
            image.transferTo(new File(IMAGE_UPLOAD_DIR, fileName));
            // 返回结果
            log.debug("文件上传成功，{}", fileName);
            return Result.ok(fileName);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }



    private String createNewFileName(String originalFilename) {
        // 获取后缀
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        // 生成目录（使用UUID保证文件不重名）
        String name = UUID.randomUUID().toString().replace("-","");
        // 按照唯一文件名的hashcode进行目录分离
        int hash = name.hashCode();
        // 获得一级目录
        int d1 = hash & 0xF;
        // 获得二级目录
        int d2 = (hash >> 4) & 0xF;
        // 判断目录是否存在
        File dir = new File(IMAGE_UPLOAD_DIR, StrUtil.format("/blogs/{}/{}", d1, d2));
        if (!dir.exists()) {
            // 若不存在，则创建目录
            dir.mkdirs();
        }
        // 生成文件名
        return StrUtil.format("/blogs/{}/{}/{}.{}", d1, d2, name, suffix);
    }
}
