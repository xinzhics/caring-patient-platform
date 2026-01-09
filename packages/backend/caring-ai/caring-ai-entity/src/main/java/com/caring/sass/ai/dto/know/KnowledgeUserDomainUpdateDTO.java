package com.caring.sass.ai.dto.know;

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
 * 知识库博主子平台分类
 * </p>
 *
 * @author 杨帅
 * @since 2025-12-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KnowledgeUserDomainUpdateDTO", description = "知识库博主子平台分类")
public class KnowledgeUserDomainUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 二级域名名称
     */
    @ApiModelProperty(value = "二级域名名称")
    @Length(max = 50, message = "二级域名名称长度不能超过50")
    private String domainName;
    /**
     * 二级域名菜单分类
     */
    @ApiModelProperty(value = "二级域名菜单分类")
    @Length(max = 50, message = "二级域名菜单分类长度不能超过50")
    private String domainMenuId;
    /**
     * 博主ID
     */
    @ApiModelProperty(value = "博主ID")
    private Long knowUserId;
    /**
     * 从小到大排序
     */
    @ApiModelProperty(value = "从大到小排序")
    private Integer sort;
}
