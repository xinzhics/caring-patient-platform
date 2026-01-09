package com.caring.sass.authority.dto.core;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.model.RemoteData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 组织
 * </p>
 *
 * @author caring
 * @since 2019-07-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "OrgSaveDTO", description = "组织")
public class OrgSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Length(max = 255, message = "名称长度不能超过255")
    private String label;
    /**
     * 简称
     */
    @ApiModelProperty(value = "简称")
    @Length(max = 255, message = "简称长度不能超过255")
    private String abbreviation;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @Length(max = 2, message = "类型长度不能超过2")
    private RemoteData<String, String> type;

    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    private Long parentId;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortValue;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Boolean status;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    private String describe;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    @Length(max = 255, message = "省长度不能超过255")
    private String province;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    @Length(max = 255, message = "市长度不能超过255")
    private String city;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    @Length(max = 255, message = "区长度不能超过255")
    private String area;

    /**
     * 负责人
     */
    @ApiModelProperty(value = "负责人")
    @Length(max = 255, message = "负责人长度不能超过255")
    private String head;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    @Length(max = 255, message = "电话长度不能超过255")
    private String tel;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    @Length(max = 255, message = "详细地址长度不能超过255")
    private String detailedAddress;

    @ApiModelProperty(value = "内置")
    private Boolean readonly;
}
