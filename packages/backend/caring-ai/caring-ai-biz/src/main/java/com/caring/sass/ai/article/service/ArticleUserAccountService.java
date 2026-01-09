package com.caring.sass.ai.article.service;

import com.caring.sass.ai.entity.article.ArticleUserAccount;
import com.caring.sass.base.service.SuperService;
import org.hibernate.validator.constraints.Length;

/**
 * <p>
 * 业务接口
 * Ai用户能量豆账户
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-12
 */
public interface ArticleUserAccountService extends SuperService<ArticleUserAccount> {

    ArticleUserAccount initAccount(@Length(max = 20, message = "用户手机号长度不能超过20") String userMobile, Integer consumptionAmount);


    void addEnergyBeans(@Length(max = 100, message = "用户手机号长度不能超过100") String userMobile, Integer energyBeansNumber);

    int useEnergyBeans(@Length(max = 100, message = "用户手机号长度不能超过100") String userMobile, int i);
}
