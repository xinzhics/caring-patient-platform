package com.caring.sass.ai.entity.face;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 融合图管理
 * </p>
 *
 * @author 杨帅
 * @since 2024-04-30
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_megvii_fusion_diagram")
@ApiModel(value = "MegviiFusionDiagram", description = "融合图管理")
@AllArgsConstructor
public class MegviiFusionDiagram extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 人脸融合模版图IDS
     */
    @ApiModelProperty(value = "人脸融合模版图分类IDS")
    @TableField(value = "template_diagram_type_ids", condition = LIKE)
    private String templateDiagramTypeIds;

    /**
     * 人脸融合图base64
     */
    @ApiModelProperty(value = "人脸融合图base64")
    @TableField("image_base64")
    @Excel(name = "人脸融合图base64")
    private String imageBase64;

    /**
     * 失败的异常信息
     */
    @ApiModelProperty(value = "失败的异常信息")
    @Length(max = 300, message = "失败的异常信息长度不能超过300")
    @TableField(value = "error_message", condition = LIKE)
    @Excel(name = "失败的异常信息")
    private String errorMessage;


    @ApiModelProperty(value = "创建图片总数")
    @TableField(value = "create_image_total", condition = EQUAL)
    private Integer createImageTotal;

    @ApiModelProperty(value = "已经完成数量")
    @TableField(value = "finish_image_total", condition = EQUAL)
    private Integer finishImageTotal;

    @ApiModelProperty(value = " 商户系统内部订单号")
    @Length(max = 100, message = " 商户系统内部订单号")
    @TableField(value = "out_trade_no", condition = EQUAL)
    private String outTradeNo;

    @ApiModelProperty(value = "任务状态0 等待中， 1 生产中， 2生产完成")
    @TableField(value = "task_status", condition = EQUAL)
    private Integer taskStatus;


    @TableField(value = "user_id", condition = EQUAL)
    private Long userId;

    @ApiModelProperty(value = "删除状态 0 未删除， 1 已删除")
    @TableField(value = "delete_status", condition = EQUAL)
    private Integer deleteStatus;

    @ApiModelProperty(value = "融合图结果")
    @TableField(exist = false)
    private List<MegviiFusionDiagramResult> resultList;


}
