package com.caring.sass.ai.face.service.impl;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.caring.sass.ai.dto.FaceDto;
import com.caring.sass.ai.dto.FaceV3DeteactDTO;
import com.caring.sass.ai.dto.faceRectangleDTO;
import com.caring.sass.ai.entity.face.MegviiTemplateDiagram;
import com.caring.sass.ai.face.dao.MegviiTemplateDiagramMapper;
import com.caring.sass.ai.face.service.MegviiTemplateDiagramService;
import com.caring.sass.ai.utils.FaceApi;
import com.caring.sass.ai.utils.ImageThumbnails;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.file.entity.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 模版图管理
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-30
 */
@Slf4j
@Service

public class MegviiTemplateDiagramServiceImpl extends SuperServiceImpl<MegviiTemplateDiagramMapper, MegviiTemplateDiagram> implements MegviiTemplateDiagramService {

    @Autowired
    FileUploadApi fileUploadApi;

    /**
     * 保存模板图示例
     *
     * @param file                需要保存的多媒体文件
     * @param templateDiagramType 模板图类型
     * @param order               图片的排序顺序
     * @param gender
     * @return MegviiTemplateDiagram 保存后的模板图对象，包含人脸检测信息
     * @throws BizException 业务异常，可能由于图片上传、压缩、人脸检测失败等原因抛出
     */
    @Override
    public MegviiTemplateDiagram save(MultipartFile file, Long templateDiagramType, Integer order, String gender) {

        // 压缩图片，并转换为base64编码
        String base64Image = ImageThumbnails.getImageBase64(file);

        // 调用旷视Face++ API进行人脸检测
        MegviiTemplateDiagram diagram = new MegviiTemplateDiagram();
        diagram.setTemplateDiagramType(templateDiagramType);
        diagram.setOrder(order);
//        diagram.setImageBase64(base64Image);

        diagram.setGender(gender);

        // 将base64转成MultipartFile 上传到文件服务。
        MockMultipartFile multipartFile = FileUtils.imageBase64ToMultipartFile(base64Image);
        if (Objects.nonNull(multipartFile)) {
            R<File> uploaded = fileUploadApi.upload(2L, multipartFile);
            if (uploaded.getIsSuccess()) {
                File fileInfo = uploaded.getData();
                diagram.setImageObsUrl(fileInfo.getUrl());
                save(diagram); // 保存模板图示例至数据库等
                return diagram;
            }
        }
        return null;
    }


}
