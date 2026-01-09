package com.caring.sass.ai.face.service;

import com.caring.sass.ai.dto.UserSubscriptionTemplate;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.face.MegviiFusionDiagram;
import com.caring.sass.wx.entity.config.WechatOrders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 融合图管理
 * </p>
 *
 * @author 杨帅
 * @date 2024-04-30
 */
public interface MegviiFusionDiagramService extends SuperService<MegviiFusionDiagram> {


    MegviiFusionDiagram cozeSave(MultipartFile file, String templateDiagramTypeIds, Long userId, Long wechatOrderId);

    void reStartMergeImage(Long id, boolean resetImageSize);

    MegviiFusionDiagram save(MultipartFile file, String templateDiagramTypeIds, Long userId);

    void reStartMergeImage();


    void addFaceMergeCozeToken(String token, String botId);

    void removeFaceMergeCozeToken(String token);

    void addTemplateSubscription(UserSubscriptionTemplate userSubscriptionTemplate);


    WechatOrders createdWechatOrder(Long userId, List<Long> typeIds);

    Integer calculatePrice(Long userId, List<Long> typeIds);

}
