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
 * 知识库专科域名
 * </p>
 *
 * @author 杨帅
 * @since 2025-07-23
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_menu_domain")
@ApiModel(value = "KnowledgeMenuDomain", description = "知识库专科域名")
@AllArgsConstructor
public class KnowledgeMenuDomain extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称 (只能用parentId是0的)")
    @Length(max = 50, message = "菜单名称长度不能超过50")
    @TableField(value = "menu_id", condition = LIKE)
    @Excel(name = "菜单名称")
    private String menuId;

    /**
     * 专科域名
     */
    @ApiModelProperty(value = "专科域名")
    @Length(max = 255, message = "专科域名长度不能超过255")
    @TableField(value = "menu_domain", condition = LIKE)
    @Excel(name = "专科域名")
    private String menuDomain;


    @Builder
    public KnowledgeMenuDomain(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String menuId, String menuDomain) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.menuId = menuId;
        this.menuDomain = menuDomain;
    }

}
