package com.starsgroupchina.web.server.controller.management;

import com.starsgroupchina.common.file.FileUploadService;
import com.starsgroupchina.common.objects.If;
import com.starsgroupchina.common.response.SimpleResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zhangfeng on 2018/11/2
 */
@Slf4j
@RestController
@Api(description = "文件")
@RequestMapping(value = "/file")
public class FileController {


    @Value("${image-url}")
    private String imgURL;

    @Value("${spring.application.name:}")
    private String appKey;

    @Autowired
    private FileUploadService fileUploadService;

    @ApiOperation("上传图片")
    @PostMapping
    public SimpleResponse<String> upload(@RequestParam("file") MultipartFile file) {
        String fileKey = fileUploadService.upload(file, appKey);
        return SimpleResponse.success(imgURL + "/" + fileKey);
    }


}
