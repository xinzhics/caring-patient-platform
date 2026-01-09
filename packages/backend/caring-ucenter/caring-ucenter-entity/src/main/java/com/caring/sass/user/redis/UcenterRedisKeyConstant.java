package com.caring.sass.user.redis;

public interface UcenterRedisKeyConstant {


    /**
     * 项目关键字回复 开关和 回复形式
     * {@link KeywordProjectSettingsRedisDTO}
     * hash 存储
     * 租户为字段， 配置的属性
     */
    String KeywordProjectSettings = "keyword_project_settings";


    /**
     * 项目关注时，回复的开关，形式 内容
     * hash 存储
     * 租户为字段， 配置的属性
     */
    String KeywordProjectFollowReply = "keyword_project_follow_reply";

    /**
     * 项目配置的 规则 在redis的缓存的hash结构Key
     * hash 存储
     * field 为 tenant_%d_reply_%id
     */
    String KeywordProjectReply = "keyword_project_reply";

    /**
     * 项目配置的 规则 在redis的缓存的hash结构key下的field
     * hash 存储
     * field 为 tenant_%s_reply_%d
     */
    String KeywordProjectReplyFieldName = "tenant_%s_reply_%d";

    /**
     * 关键词存储在redis的Key
     */
    String KeywordSettingTenant = "keyword_setting_tenant_%s";

    /**
     * 最近7日触发次数
     * 按日期记录 每日 所有关键字的触发次数
     * 最近 8个日期的触发次数
     */
    String KeywordTenantDayAllKeywordTriggerFrequency = "keyword_tenant_%d:day%s_frequency";

    /**
     * 项目 所有规则 总计触发次数
     * redis 的key 永久不过期
     */
    String KeywordTenantTriggerFrequency = "keyword_tenant_total_frequency";

    /**
     * 项目 一个规则 生命周期中 总的触发次数
     */
    String KeywordTenantKeyWordReplyTriggerFrequency = "keyword_tenant_keyword_reply_total_frequency";

    /**
     * 项目 所有规则 每日触发次数
     * key 有效期最多 10天
     * %s tenantCode ,filed 是日期
     */
    String KeywordTenantEveryDayTriggerFrequency = "keyword_tenant_%s:_frequency";

    /**
     * 项目 每个规则 每日触发次数
     * key 有效期最多 10天
     * %s tenantCode %d 规则的ID
     */
    String KeywordTenantEveryReplyEveryDayTriggerFrequency = "keyword_tenant_%s:reply_%d_frequency";


    /**
     * 每个关键字的 全部触发次数
     * 关键字 全部触发排行榜
     * %s 租户。使用租户分隔
     */
    String keywordTenantAllDayOneKeywordTriggerFrequency = "keyword_tenant_%s:all_key_word_frequency";


    /**
     * 每天将 40天之前那天的 关键字触发次数记录移除
     * 按关键字 每日 触发排行榜
     * 关键字每日触发排行榜
     * %s 租户。使用租户分隔 %s 日期  field 为 关键字ID
     */
    String keywordTenantDayOneKeywordTriggerFrequency = "keyword_tenant_%s:every_day_%s_one_key_word_frequency";

    /**
     * 关键字7天排行榜 每天晚上7点 将关键字的触发次数从 当天关键字中减去
     * 关键字 7日触发排行榜
     * %s 租户。使用租户分隔  field 为 关键字ID
     */
    String keywordTenant7DayKeywordTriggerFrequency = "keyword_tenant_%s:7_dya_one_key_word_frequency";

    /**
     * 每天晚上1点。将30天前的触发次数从当前关键字中减去。
     * 关键字30天排行榜
     * %s 租户。使用租户分隔  field 为 关键字ID
     */
    String keywordTenant30DayKeywordTriggerFrequency = "keyword_tenant_%s:30_day_one_key_word_frequency";




}
