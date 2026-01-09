package com.caring.sass.cms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.cms.constant.MassMailingMessageStatus;
import com.caring.sass.cms.constant.MediaTypeEnum;
import com.caring.sass.cms.constant.SendTarget;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName MassMailing
 * @Description
 * @Author yangShuai
 * @Date 2021/11/19 9:47
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_mass_mailing")
@ApiModel(value = "MassMailing", description = "群发管理")
@AllArgsConstructor
public class MassMailing extends Entity<Long> {


    private static final long serialVersionUID = 1L;

    /**
     * 定时发送
     */
    public static final String TIMING = "timing";

    /**
     * 手动发送
     */
    public static final String MANUAL = "manual";

    /**
     * 定时发送： timing
     * 手动发送： manual
     */
    @ApiModelProperty(value = "发送类型 定时发送： timing、手动发送： manual")
    @Length(max = 50, message = "发送类型不能超过200")
    @TableField(value = "send_type", condition = EQUAL)
    private String sendType;

    /**
     * 定时发送的时间
     */
    @ApiModelProperty(value = "定时发送的时间")
    @TableField(value = "timing_send_time", condition = EQUAL)
    private LocalDateTime timingSendTime;

    /**
     * 发送的时间
     */
    @ApiModelProperty(value = "实际发送的时间")
    @TableField(value = "send_time", condition = EQUAL)
    private LocalDateTime sendTime;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    @TableField(value = "media_type_enum", condition = EQUAL)
    private MediaTypeEnum mediaTypeEnum;

    /**
     * 图文文章的 集合 toJsonString
     */
    @ApiModelProperty(value = "图文素材id的集合")
    @TableField(value = "article_news_list", condition = EQUAL)
    private String articleNewsListJson;

    /**
     * 判定转载时，是否继续群发
     */
    @ApiModelProperty(value = "转载时是否继续群发")
    @TableField(value = "send_ignore_reprint", condition = EQUAL)
    private Boolean sendIgnoreReprint;

    /**
     * 图片素材的 id 集合 toJOSNString
     */
    @ApiModelProperty(value = "素材id的集合")
    @TableField(value = "article_image_list", condition = EQUAL)
    private String articleImageListJson;

    @ApiModelProperty(value = "推荐语，不填则默认为“分享图片”")
    @TableField(value = "recommend")
    private String recommend;

    /**
     * 其他 视频，语音 素材的 素材id
     */
    @ApiModelProperty(value = "素材id（视频、音频素材的id）")
    @TableField(value = "article_other_id", condition = EQUAL)
    private Long articleOtherId;

    /**
     * 文字内容
     */
    @ApiModelProperty(value = "文字内容(消息类型为文本时)")
    @TableField(value = "text_content", condition = EQUAL)
    private String textContent;

    /**
     * 发送的目标
     */
    @ApiModelProperty(value = "发送的目标")
    @TableField(value = "send_target", condition = EQUAL)
    private SendTarget sendTarget;


    /**
     * 选择的标签 的id
     */
    @ApiModelProperty(value = "选择的标签")
    @TableField(value = "tag_ids", condition = EQUAL)
    private String tagIdsJson;

    /**
     * openIds List的 toJSONString
     */
    @ApiModelProperty(value = "发送的目标的openIds")
    @TableField(value = "openids")
    private String openIdsJson;

    @ApiModelProperty(value = "租户")
    @TableField(value = "tenant_code")
    private String tenantCode;


    @ApiModelProperty(value = "群发消息状态(等待发送, 提交中, 发送中, 发送成功, 发送失败)")
    @TableField(value = "message_status")
    private MassMailingMessageStatus messageStatus;

    @ApiModelProperty(value = "群发排序")
    @TableField(value = "mass_sort")
    private Integer massSort;

    @ApiModelProperty(value = "微信群发消息的ID")
    @TableField(value = "msg_id")
    private String msgId;

    @ApiModelProperty(value = "发送总数")
    @TableField(value = "sent_count")
    private Integer sentCount;

    @ApiModelProperty(value = "发送失败总数")
    @TableField(value = "error_count")
    private Integer errorCount;

    @ApiModelProperty(value = "微信推送的结果")
    @TableField(value = "sent_result")
    private String sentResult;

    @ApiModelProperty(value = "异常说明")
    @TableField(value = "error_message")
    private String errorMessage;

}
