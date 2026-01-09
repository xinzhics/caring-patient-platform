package com.caring.sass.ai.dto.know;

import com.caring.sass.ai.entity.know.KnowledgeFileAcademicMaterialsLabel;
import com.caring.sass.ai.entity.know.KnowledgeFileCaseDatabaseLabel;
import com.caring.sass.ai.entity.know.KnowledgeFilePersonalAchievementsLabel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * dify知识库文档关联表
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KnowledgeFileUpdateDTO", description = "dify知识库文档关联表")
public class KnowledgeFileUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    @Length(max = 300, message = "文件名称长度不能超过300")
    private String fileName;

    @ApiModelProperty(value = "查询的等级要求")
    private Integer viewPermissions;

    @ApiModelProperty(value = "下载的等级要求")
    private Integer downloadPermission;

    @ApiModelProperty(value = "病例库的标签")
    @TableField(exist = false)
    KnowledgeFileCaseDatabaseLabel caseDatabaseLabel;

    @ApiModelProperty(value = "个人成长的标签")
    @TableField(exist = false)
    KnowledgeFilePersonalAchievementsLabel personalAchievementsLabel;

    @ApiModelProperty(value = "专业学术资料标签")
    @TableField(exist = false)
    KnowledgeFileAcademicMaterialsLabel academicMaterialsLabel;

}
