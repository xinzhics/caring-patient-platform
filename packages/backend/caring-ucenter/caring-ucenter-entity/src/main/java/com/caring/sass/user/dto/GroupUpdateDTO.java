package com.caring.sass.user.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 小组
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "GroupUpdateDTO", description = "小组")
public class GroupUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 医助ID
     */
    @ApiModelProperty(value = "医助ID")
    private Long nurseId;
    /**
     * 医助名字
     */
    @ApiModelProperty(value = "医助名字")
    @Length(max = 20, message = "医助名字长度不能超过20")
    private String nurseName;
    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Length(max = 500, message = "图标长度不能超过500")
    private String icon;
    /**
     * 小组名字
     */
    @ApiModelProperty(value = "小组名字")
    @Length(max = 32, message = "小组名字长度不能超过32")
    private String name;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer order;
    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID")
    private Long organId;
    /**
     * 机构名字
     */
    @ApiModelProperty(value = "机构名字")
    @Length(max = 50, message = "机构名字长度不能超过50")
    private String organName;
    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    @Length(max = 32, message = "联系人长度不能超过32")
    private String contactName;
    /**
     * 联系人电话
     */
    @ApiModelProperty(value = "联系人电话")
    @Length(max = 12, message = "联系人电话长度不能超过12")
    private String contactMobile;
    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    @Length(max = 500, message = "地址长度不能超过500")
    private String address;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Length(max = 500, message = "备注长度不能超过500")
    private String remarks;
    /**
     * 机构CLASSCODE
     */
    @ApiModelProperty(value = "机构CLASSCODE")
    @Length(max = 65535, message = "机构CLASSCODE长度不能超过65,535")
    private String classCode;
}
