package com.caring.sass.wx.service.config;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.entity.config.ConfigAdditional;

/**
 * <p>
 * 业务接口
 * 微信附加信息配置
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface ConfigAdditionalService extends SuperService<ConfigAdditional> {

    /**
     * @Author yangShuai
     * @Description 获取微信公众号的其他配置
     * @Date 2020/9/15 15:58
     *
     * @return com.caring.sass.wx.entity.config.ConfigAdditional
     */
    ConfigAdditional getConfigAdditionalByWxAppId(String appId);

    /**
     * @Author yangShuai
     * @Description 使用 租户信息 查询
     * @Date 2020/9/15 17:37
     *
     * @return com.caring.sass.wx.entity.config.ConfigAdditional
     */
    ConfigAdditional getConfigAdditionalByProjectId();


    /**
     * @Author yangShuai
     * @Description 获取关键字回复 开启状态
     * @Date 2020/9/17 13:39
     *
     * @return java.lang.Integer
     */
    Integer getKeyWordStatus();


    /**
     * @Author yangShuai
     * @Description 获取自动回复 开启状态
     * @Date 2020/9/17 13:41
     *
     * @return java.lang.Integer
     */
    Integer getAutomaticReply();


    /**
     * @Author yangShuai
     * @Description 更新关键字回复 开启状态
     * @Date 2020/9/17 13:45
     *
     * @return java.lang.Integer
     */
    Integer updateKeyWordStatus(Integer keyWordStatus);


    /**
     * @Author yangShuai
     * @Description 更新自动回复 开启状态
     * @Date 2020/9/17 13:46
     *
     * @return java.lang.Integer
     */
    Integer updateAutomaticReply(Integer automaticReply);


}
