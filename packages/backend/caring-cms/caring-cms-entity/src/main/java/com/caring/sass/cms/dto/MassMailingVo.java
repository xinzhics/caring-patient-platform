package com.caring.sass.cms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.cms.constant.MassMailingMessageStatus;
import com.caring.sass.cms.constant.MediaTypeEnum;
import com.caring.sass.cms.constant.SendTarget;
import com.caring.sass.cms.entity.ArticleNews;
import com.caring.sass.cms.entity.ArticleOther;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MassMailingVo
 * @Description
 * @Author yangShuai
 * @Date 2021/11/24 10:04
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ApiModel(value = "MassMailingVo", description = "群发列表的vo")
@AllArgsConstructor
public class MassMailingVo {

    private Long id;
    /**
     * 定时发送： timing
     * 手动发送： manual
     */
    @ApiModelProperty(value = "发送类型 定时发送： timing、手动发送： manual")
    private String sendType;

    /**
     * 定时发送的时间
     */
    @ApiModelProperty(value = "定时发送的时间")
    private LocalDateTime timingSendTime;

    /**
     * 发送的时间
     */
    @ApiModelProperty(value = "实际发送的时间")
    private LocalDateTime sendTime;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    private MediaTypeEnum mediaTypeEnum;

    /**
     * 图文文章的 集合 toJsonString
     */
    @ApiModelProperty(value = "图文素材")
    private List<ArticleNews> articleNewsList = new ArrayList<>(8);

    /**
     * 判定转载时，是否继续群发
     */
    @ApiModelProperty(value = "转载时是否继续群发")
    private Boolean sendIgnoreReprint;

    /**
     * 图片素材的 id 集合 toJOSNString
     */
    @ApiModelProperty(value = "图片素材的集合")
    private List<ArticleOther> articleImageList;

    @ApiModelProperty(value = "推荐语，不填则默认为“分享图片”")
    private String recommend;

    /**
     * 其他 视频，语音 素材的 素材id
     */
    @ApiModelProperty(value = "素材（视频、音频素材的id）")
    private ArticleOther articleOther;

    /**
     * 文字内容
     */
    @ApiModelProperty(value = "文字内容(消息类型为文本时)")
    private String textContent;

    /**
     * 发送的目标
     */
    @ApiModelProperty(value = "发送的目标")
    private SendTarget sendTarget;


    @ApiModelProperty(value = "群发消息状态(等待发送, 提交中, 已提交, 发送成功, 发送失败)")
    @TableField(value = "message_status")
    private MassMailingMessageStatus messageStatus;


    @ApiModelProperty(value = "微信推送的结果")
    @TableField(value = "sent_result")
    private String sentResult;

    @ApiModelProperty(value = "异常说明")
    private String errorMessage;

    public void addArticleNewsList(ArticleNews articleNews) {
        articleNewsList.add(articleNews);
    }

    public void addArticleImageList(ArticleOther articleOther) {
        articleImageList.add(articleOther);
    }
}
