package com.caring.sass.ai.entity.know;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_user_domain")
@ApiModel(value = "KnowledgeUserDomain", description = "知识库博主子平台分类")
@AllArgsConstructor
public class KnowledgeUserDomain extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 二级域名名称
     */
    @ApiModelProperty(value = "二级域名名称")
    @Length(max = 50, message = "二级域名名称长度不能超过50")
    @TableField(value = "domain_name", condition = LIKE)
    @Excel(name = "二级域名名称")
    private String domainName;

    /**
     * 二级域名菜单分类
     */
    @ApiModelProperty(value = "二级域名菜单分类")
    @Length(max = 50, message = "二级域名菜单分类长度不能超过50")
    @TableField(value = "domain_menu_id", condition = LIKE)
    @Excel(name = "二级域名菜单分类")
    private String domainMenuId;

    /**
     * 博主ID
     */
    @ApiModelProperty(value = "博主ID")
    @TableField("know_user_id")
    @Excel(name = "博主ID")
    private Long knowUserId;

    /**
     * 从小到大排序
     */
    @ApiModelProperty(value = "从大到小排序")
    @TableField("sort_")
    @Excel(name = "从大到小排序")
    private Integer sort;


    @Builder
    public KnowledgeUserDomain(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String domainName, String domainMenuId, Long knowUserId, Integer sort) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.domainName = domainName;
        this.domainMenuId = domainMenuId;
        this.knowUserId = knowUserId;
        this.sort = sort;
    }

}
