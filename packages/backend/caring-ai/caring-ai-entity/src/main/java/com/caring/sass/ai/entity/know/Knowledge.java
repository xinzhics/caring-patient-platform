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
 * dify知识库关联表
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-20
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge")
@ApiModel(value = "Knowledge", description = "dify知识库关联表")
@AllArgsConstructor
public class Knowledge extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 知识库名称
     */
    @ApiModelProperty(value = "知识库名称")
    @Length(max = 20, message = "知识库名称长度不能超过20")
    @TableField(value = "know_name", condition = LIKE)
    @Excel(name = "知识库名称")
    private String knowName;

    /**
     * 知识库dify的Id
     */
    @ApiModelProperty(value = "知识库dify的Id")
    @Length(max = 50, message = "知识库dify的Id长度不能超过50")
    @TableField(value = "know_dify_id", condition = LIKE)
    @Excel(name = "知识库dify的Id")
    private String knowDifyId;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @TableField("know_user_id")
    @Excel(name = "用户ID")
    private Long knowUserId;

    /**
     * 知识库类型(专业学术资料，个人成果，病例库，日常收集)
     */
    @ApiModelProperty(value = "知识库类型(专业学术资料，个人成果，病例库，日常收集)")
    @Length(max = 50, message = "知识库类型(专业学术资料，个人成果，病例库，日常收集)长度不能超过50")
    @TableField(value = "know_type", condition = LIKE)
    @Excel(name = "知识库类型(专业学术资料，个人成果，病例库，日常收集)")
    private KnowledgeType knowType;


    @ApiModelProperty(value = "查询的等级要求")
    @TableField("view_permissions")
    private Integer viewPermissions;



    @ApiModelProperty(value = "下载的等级要求")
    @TableField("download_permission")
    private Integer downloadPermission;


    @ApiModelProperty(value = "知识库所属元数据Id")
    @TableField("metadata_ownership_id")
    private String metadataOwnershipId;

    @ApiModelProperty(value = "知识库类型元数据ID")
    @TableField("metadata_category_id")
    private String metadataCategoryId;



    @Builder
    public Knowledge(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String knowName, String knowDifyId, Long knowUserId, KnowledgeType knowType) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.knowName = knowName;
        this.knowDifyId = knowDifyId;
        this.knowUserId = knowUserId;
        this.knowType = knowType;
    }

}
