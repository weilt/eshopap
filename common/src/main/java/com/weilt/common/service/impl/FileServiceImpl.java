package com.weilt.common.service.impl;

import com.google.common.collect.Lists;
import com.weilt.common.service.IFileService;
import com.weilt.common.utils.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author weilt
 * @com.weilt.common.service.impl
 * @date 2018/8/23 == 14:59
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    @Override
    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件名：{}，上传文件路径：{}，新文件名：{}",fileName,path,uploadFileName);
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);

        try {
            file.transferTo(targetFile);
            //TODO 文件上传成功之后，需要上传至图片服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已经上传至ftp文件服务器
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return null;
    }
}
