package com.caring.sass.cms.dto;

import com.caring.sass.cms.constant.MediaTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @ClassName MassMailingBaseDto
 * @Description
 * @Author yangShuai
 * @Date 2021/11/19 14:19
 * @Version 1.0
 */
@Data
public class MassMailingBaseDto {

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    private MediaTypeEnum mediaTypeEnum;

    /**
     * 图文文章的 集合 toJsonString
     */
    @ApiModelProperty(value = "图文素材id的集合")
    private List<Long> articleNewsList;

    /**
     * 判定转载时，是否继续群发
     */
    @ApiModelProperty(value = "转载时是否继续群发")
    private Boolean sendIgnoreReprint;

    /**
     * 图片素材的 id 集合 toJOSNString
     */
    @ApiModelProperty(value = "素材id的集合")
    private List<Long> articleImageList;


    @ApiModelProperty(value = "推荐语，不填则默认为“分享图片”")
    @Length(max = 200, message = "群发描述不能超过200")
    private String recommend;
    /**
     * 其他 视频，语音 素材的 素材id
     */
    @ApiModelProperty(value = "素材id（视频、音频素材的id）")
    private Long articleOtherId;

    /**
     * 文字内容
     */
    @ApiModelProperty(value = "文字内容(消息类型为文本时)")
    private String textContent;


}
