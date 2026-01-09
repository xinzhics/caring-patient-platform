package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.entity.CommonMsg;
import com.caring.sass.user.entity.CommonMsgTemplateContent;
import com.caring.sass.user.entity.CommonMsgType;

import java.util.List;

/**
 * @Author yangShuai
 * @Description 常用语
 * @Date 2020/10/13 10:51
 */
public interface CommonMsgService extends SuperService<CommonMsg> {

    boolean cleanAll(Long accountId, String userType);

    /**
     * 导入模版到自己的库
     * @param userId
     * @param userType
     * @param commonMsgTemplateContentList
     */
    void importCommonMsgTemplate(Long userId, String userType, List<Long> commonMsgTemplateContentList);

    /**
     * 查询我的分类
     * @param userId
     * @param userType
     * @return
     */
    List<CommonMsgType> queryMyType(Long userId, String userType);

    /**
     * 查询我的分类和 系统后台的分类
     * @param userId
     * @param userType
     * @return
     */
    List<CommonMsgType> queryAllType(Long userId, String userType);

    /**
     * 新增或者更新常用语
     * @param commonMsg
     */
    void saveOrUpdateCommonMsgAndType(CommonMsg commonMsg);


}
