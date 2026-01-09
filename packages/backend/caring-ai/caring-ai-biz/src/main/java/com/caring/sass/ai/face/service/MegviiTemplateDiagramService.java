package com.caring.sass.ai.face.service;

import com.caring.sass.ai.entity.face.MegviiTemplateDiagram;
import com.caring.sass.base.service.SuperService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 业务接口
 * 模版图管理
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-30
 */
public interface MegviiTemplateDiagramService extends SuperService<MegviiTemplateDiagram> {

    /**
     * 上传一个模版图
     *
     * @param file                模版图文件
     * @param templateDiagramType 模版图类型
     * @param order               顺序
     * @param gender
     * @return 模版图
     */
    MegviiTemplateDiagram save(MultipartFile file, Long templateDiagramType, Integer order, String gender);


}
