package com.caring.sass.ai.article.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caring.sass.ai.article.dao.ArticleUserAccountMapper;
import com.caring.sass.ai.entity.article.ArticleUserAccount;
import com.caring.sass.ai.article.service.ArticleUserAccountService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * Ai用户能量豆账户
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-12
 */
@Slf4j
@Service

public class ArticleUserAccountServiceImpl extends SuperServiceImpl<ArticleUserAccountMapper, ArticleUserAccount> implements ArticleUserAccountService {


    @Override
    public ArticleUserAccount initAccount(String userMobile, Integer consumptionAmount) {

        ArticleUserAccount account = null;
        try {
            account = baseMapper.selectOne(Wraps.<ArticleUserAccount>lbQ()
                    .eq(ArticleUserAccount::getUserMobile, EncryptionUtil.encrypt(userMobile)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (account == null) {
            account = new ArticleUserAccount();
            try {
                account.setUserMobile(userMobile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            account.setEnergyBeans(0L);
            account.setFreeEnergyBeans(Long.parseLong(consumptionAmount.toString()));
            baseMapper.insert(account);
        } else {
            account.setFreeEnergyBeans(account.getFreeEnergyBeans() +Long.parseLong(consumptionAmount.toString()));
            baseMapper.updateById(account);
        }
        return account;
    }


    /**
     * 增加能量豆
     * @param userMobile
     * @param energyBeansNumber
     */
    @Override
    public void addEnergyBeans(String userMobile, Integer energyBeansNumber) {
        UpdateWrapper<ArticleUserAccount> wrapper = new UpdateWrapper<ArticleUserAccount>();
        wrapper.setSql("energy_beans = energy_beans +" + energyBeansNumber);
        try {
            String encrypt = EncryptionUtil.encrypt(userMobile);
            wrapper.eq("user_mobile", encrypt);
            baseMapper.update(new ArticleUserAccount(), wrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





    /**
     * 扣除能量豆
     * @param userMobile
     * @param i
     * @return
     */
    @Override
    public int useEnergyBeans(String userMobile, int i) {

        UpdateWrapper<ArticleUserAccount> wrapper = new UpdateWrapper<ArticleUserAccount>();
        wrapper.setSql("free_energy_beans = 0");
        wrapper.setSql("energy_beans = energy_beans -" + i);
        wrapper.ge("energy_beans", i);

        try {
            String encrypt = EncryptionUtil.encrypt(userMobile);
            wrapper.eq("user_mobile", encrypt);
            return baseMapper.update(new ArticleUserAccount(), wrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
