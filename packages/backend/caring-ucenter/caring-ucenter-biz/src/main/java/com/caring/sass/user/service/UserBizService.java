package com.caring.sass.user.service;

import com.caring.sass.authority.entity.core.Org;
import org.springframework.stereotype.Service;

/**
 * @ClassName userBizService
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 9:54
 * @Version 1.0
 */
@Service
public interface UserBizService {


    boolean createDefaultUser(Org org, String domain, String projectName);

    /**
     * 按项目删除用户信息
     */
    boolean deleteByTenant();

}
