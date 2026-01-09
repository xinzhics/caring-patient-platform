package com.caring.sass.cms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "ChannelContentCopyOrMoveDto", description = "文章复制或移动")
public class ChannelContentCopyOrMoveDto {

    @ApiModelProperty(value = "文章ID")
    private Long id;

    @ApiModelProperty(value = "文章ID")
    private List<Long> ids;

    @ApiModelProperty(value = "栏目组ID (患者库时这个是null)")
    private Long channelGroupId;

    @ApiModelProperty(value = "栏目Id")
    private Long channelId;

    @ApiModelProperty(value = "是否是医生学院，true是，false否")
    private Boolean doctorOwner;

    @ApiModelProperty(value = "复制或移动， 复制： 1， 移动： 2")
    @NotNull
    private Integer copyOrMove;
}
