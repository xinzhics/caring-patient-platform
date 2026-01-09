package com.caring.sass.authority.entity.core;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.TreeEntity;
import com.caring.sass.common.constant.DictionaryType;
import com.caring.sass.injection.annonation.InjectionField;
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

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.caring.sass.common.constant.InjectionFieldConstants.DICTIONARY_ITEM_CLASS;
import static com.caring.sass.common.constant.InjectionFieldConstants.DICTIONARY_ITEM_METHOD;

/**
 * <p>
 * 实体类
 * 组织
 * </p>
 *
 * @author caring
 * @since 2019-10-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("c_core_org")
@ApiModel(value = "Org", description = "组织")
public class Org extends TreeEntity<Org, Long> {

    private static final long serialVersionUID = 1L;

    @Excel(name = "名称", width = 50)
    protected String label;

    /**
     * 简称
     */
    @ApiModelProperty(value = "简称")
    @Length(max = 255, message = "简称长度不能超过255")
    @TableField(value = "abbreviation", condition = LIKE)
    @Excel(name = "简称", width = 30)
    private String abbreviation;

    @ApiModelProperty(value = "类型")
    @Length(max = 2, message = "类型长度不能超过2")
    @InjectionField(api = DICTIONARY_ITEM_CLASS, method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.ORG_TYPE)
    @TableField(value = "type_")
    @Excel(name = "类型", width = 30, dict = "ORG_TYPE")
    private RemoteData<String, String> type;

    @ApiModelProperty(value = "机构码")
    @TableField(value = "code", condition = EQUAL)
    private String code;

    /**
     * 树结构
     */
    @ApiModelProperty(value = "树结构")
    @Length(max = 255, message = "树结构长度不能超过255")
    @TableField(value = "tree_path", condition = LIKE)
    private String treePath;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @TableField("status")
    @Excel(name = "状态", replace = {"启用_true", "禁用_false", "_null"})
    private Boolean status;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    @TableField(value = "describe_", condition = LIKE)
    @Excel(name = "描述", width = 50)
    private String describe;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    @Length(max = 255, message = "省长度不能超过255")
    @TableField(value = "province", condition = EQUAL)
    @Excel(name = "省", width = 50)
    private String province;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    @Length(max = 255, message = "市长度不能超过255")
    @TableField(value = "city", condition = EQUAL)
    @Excel(name = "市", width = 50)
    private String city;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    @Length(max = 255, message = "区长度不能超过255")
    @TableField(value = "area", condition = EQUAL)
    @Excel(name = "区", width = 50)
    private String area;

    /**
     * 负责人
     */
    @ApiModelProperty(value = "负责人")
    @Length(max = 255, message = "负责人长度不能超过255")
    @TableField(value = "head", condition = EQUAL)
    @Excel(name = "负责人", width = 50)
    private String head;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    @Length(max = 255, message = "电话长度不能超过255")
    @TableField(value = "tel", condition = EQUAL)
    @Excel(name = "电话", width = 50)
    private String tel;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    @Length(max = 255, message = "详细地址长度不能超过255")
    @TableField(value = "detailed_address", condition = EQUAL)
    @Excel(name = "详细地址", width = 50)
    private String detailedAddress;

    @ApiModelProperty(value = "内置")
    @TableField("readonly")
    @Excel(name = "内置", replace = {"是_true", "否_false", "_null"})
    private Boolean readonly;

    @Builder
    public Org(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser,
               String label, String abbreviation, RemoteData<String, String> type, Long parentId, String treePath, Integer sortValue,
               Boolean status, String describe, String province, String city, String area, String head, String tel, String detailedAddress, Boolean readonly) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.label = label;
        this.type = type;
        this.abbreviation = abbreviation;
        this.parentId = parentId;
        this.treePath = treePath;
        this.sortValue = sortValue;
        this.status = status;
        this.describe = describe;
        this.province = province;
        this.city = city;
        this.area = area;
        this.head = head;
        this.tel = tel;
        this.detailedAddress = detailedAddress;
        this.readonly = readonly;
    }
}
