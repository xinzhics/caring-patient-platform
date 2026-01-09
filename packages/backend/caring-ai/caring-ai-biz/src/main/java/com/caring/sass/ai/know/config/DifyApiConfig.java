package com.caring.sass.ai.know.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = DifyApiConfig.PREFIX)
public class DifyApiConfig {

    public static final String PREFIX = "dify.config";


    @ApiModelProperty(value = "dify的知识库的apiKey")
    private String APIKey;

    @ApiModelProperty(value = "dify的域名")
    private String apiDomain;

    @ApiModelProperty(value = "dify的知识库ID")
    private String difyKnowledgeId;

    @ApiModelProperty(value = "知识库文档分类元数据ID")
    private String metadataCategoryId;

    @ApiModelProperty(value = "知识库文档所属元数据ID")
    private String metadataOwnershipId;

    @ApiModelProperty(value = "podcastDify的域名")
    private String podcastApiDomain;

    @ApiModelProperty(value = "1分钟最大限流")
    private Integer flowControl;

    @ApiModelProperty(value = "文本的索引方式")
    private String textIndexingTechnique;

    @ApiModelProperty(value = "文本的处理规则")
    private String textProcessRule;


    @ApiModelProperty(value = "dify日常收集标题apikey")
    private String titleCreateWorkApiKey;

    @ApiModelProperty(value = "专业学术资料关键词提炼token")
    private String zhuan_ye_xue_shu_key_word_apikey;

    @ApiModelProperty(value = "病例库关键词提炼token")
    private String bing_li_key_word_apikey;


    @ApiModelProperty(value = "个人成果关键词提炼token")
    private String ge_ren_cheng_guo_key_word_apikey;


    @ApiModelProperty(value = "日常收集关键词提炼token")
    private String ri_chang_shou_ji_key_word_apikey;


    @ApiModelProperty(value = "专业学术资料条件提炼")
    private String zhuanyexueshu_condition_extract_apikey;

    @ApiModelProperty(value = "病例库条件提炼")
    private String bingliku_condition_extract_apikey;


    @ApiModelProperty(value = "个人成果条件提炼")
    private String gerenchengguo_condition_extract_apikey;

    @ApiModelProperty(value = "日常收集条件提炼")
    private String richangshouji_condition_extract_apikey;

    @ApiModelProperty(value = "docuSearch的翻译和总结")
    private String docu_search_translation_and_summary;

    @ApiModelProperty("科普名片视频文案生成")
    private String card_human_video_text_create_key;

    @ApiModelProperty(value = "case create key")
    private String case_create_key;


    @ApiModelProperty(value = "podcast bot key")
    private String podcast_bot_key;

    @ApiModelProperty(value = "textual bot key")
    private String textual_bot_key;

    @ApiModelProperty(value = "dify生成常见问题的key")
    private String general_question_key;
}
