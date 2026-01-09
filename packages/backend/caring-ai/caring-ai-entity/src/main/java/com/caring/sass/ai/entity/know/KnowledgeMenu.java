package com.caring.sass.ai.entity.know;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 知识库菜单
 * </p>
 *
 * @author 杨帅
 * @since 2025-07-21
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_menu")
@ApiModel(value = "KnowledgeMenu", description = "知识库菜单")
@AllArgsConstructor
public class KnowledgeMenu extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称")
    @Length(max = 50, message = "菜单名称长度不能超过50")
    @TableField(value = "menu_name", condition = LIKE)
    @Excel(name = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "父级菜单")
    @Length(max = 50, message = "父级菜单长度不能超过50")
    @TableField(value = "parent_id", condition = EQUAL)
    @Excel(name = "父级菜单")
    private String parentId;


    @ApiModelProperty(value = "排序")
    @TableField(value = "sort_", condition = EQUAL)
    private Integer sort;

    @ApiModelProperty(value = "排序")
    @TableField(value = "domain_id", condition = EQUAL)
    private Long domainId;


    @Builder
    public KnowledgeMenu(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String menuName) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.menuName = menuName;
    }

}
