package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author 周超
 * @version 1.0
 * 通用接口
 *
 */

/*
为什么前端能够看到图片：
前端会访问到后端给我们返回回来的阿里云的图片的绝对访问路径
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController
{
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    //图片上传到阿里云之后，图片绝对的网址，绝对的请求路径
    public Result<String> upload(MultipartFile file)
    {
        log.info("文件上传：{}", file);
        //接收到文件之后，需要把文件上传到阿里云服务器,通过uuid对原始文件重新命名，
        //生成唯一的文件名，不会重复，防止覆盖
        String originalFilename = file.getOriginalFilename(); //原始文件名
        //截取原始文件名的后缀
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        //拼接,构造新的文件名称
        String objectName = UUID.randomUUID().toString() + extension;
        try {
            //图片文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            //上传成功
            return Result.success(filePath);
        } catch (IOException e) {
            //上传失败，输出失败日志
            log.error("文件上传失败：{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }


}





















