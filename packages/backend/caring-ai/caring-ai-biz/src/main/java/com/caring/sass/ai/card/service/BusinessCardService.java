package com.caring.sass.ai.card.service;

import com.caring.sass.ai.entity.card.BusinessCard;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * AI名片
 * </p>
 *
 * @author 杨帅
 * @date 2024-08-22
 */
public interface BusinessCardService extends SuperService<BusinessCard> {

    void initQrCode();

    void checkVideoInfo(BusinessCard businessCard);

    void initQrCode(Long id);

    void setDoctorStudio(BusinessCard businessCard);

}
