package com.caring.sass.ai.dto.humanVideo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.ai.entity.humanVideo.HumanVideoMakeWay;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 数字人视频制作任务
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BusinessDigitalHumanVideoTaskUpdateDTO", description = "数字人视频制作任务")
public class BusinessDigitalHumanVideoTaskUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 名片ID
     */
    @ApiModelProperty(value = "名片ID")
    private Long businessCardId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @Length(max = 255, message = "任务名称长度不能超过255")
    private String taskName;
    /**
     * 制作方式(photo：照片, video: 视频)
     */
    @ApiModelProperty(value = "制作方式")
    private HumanVideoMakeWay makeWay;
    /**
     * 照片数字人url
     */
    @ApiModelProperty(value = "照片数字人url")
    @Length(max = 255, message = "照片数字人url长度不能超过255")
    private String photoHumanUrl;


    @ApiModelProperty(value = "是否需要制作音色")
    private Boolean createTimbre;

    /**
     * 录音文件url
     */
    @ApiModelProperty(value = "用户的录音文件url")
    @Length(max = 255, message = "录音文件url长度不能超过255")
    private String audioUrl;
    /**
     * 视频内容文案
     */
    @ApiModelProperty(value = "问候视频的内容文案")
    @Length(max = 500, message = "视频内容文案长度不能超过500")
    private String videoTextContent;



    /**
     * 模版视频url
     */
    @ApiModelProperty(value = "模版视频url")
    @Length(max = 255, message = "模版视频url长度不能超过255")
    private String templateVideoUrl;

    @ApiModelProperty(value = "底板视频素材宽度")
    private Integer videoWidth;

    @ApiModelProperty(value = "底板视频素材高度")
    private Integer videoHeight;

    /**
     * 授权视频url
     */
    @ApiModelProperty(value = "授权视频url")
    @Length(max = 255, message = "授权视频url长度不能超过255")
    private String authVideoUrl;
}
