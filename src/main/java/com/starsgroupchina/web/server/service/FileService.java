package com.starsgroupchina.web.server.service;

import com.starsgroupchina.common.file.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by zhangfeng on 2018/11/2
 */
@Service
public class FileService {

    @Value("${image-url}")
    private String imgURL;

    @Value("${spring.application:name:}")
    private String appKey;

    @Autowired
    private FileUploadService fileUploadService;

}
