package com.caring.sass.ai.article.service;

import com.caring.sass.ai.dto.know.KnowsUserDataCountModel;

public interface AiUserService {


    /**
     * 获取用户数据统计
     * @param domain
     * @return
     */
    KnowsUserDataCountModel countUserAllData(String domain);


}
