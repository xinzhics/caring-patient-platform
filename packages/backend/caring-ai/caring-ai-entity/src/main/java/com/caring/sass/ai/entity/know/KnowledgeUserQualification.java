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
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 知识库博主资质
 * </p>
 *
 * @author 杨帅
 * @since 2025-12-29
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_user_qualification")
@ApiModel(value = "KnowledgeUserQualification", description = "知识库博主资质")
@AllArgsConstructor
public class KnowledgeUserQualification extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    @Length(max = 300, message = "文件名称长度不能超过300")
    @TableField(value = "file_name", condition = LIKE)
    @Excel(name = "文件名称")
    private String fileName;

    /**
     * 文件链接
     */
    @ApiModelProperty(value = "文件链接")
    @Length(max = 500, message = "文件链接长度不能超过500")
    @TableField(value = "file_url", condition = LIKE)
    @Excel(name = "文件链接")
    private String fileUrl;

    /**
     * 博主ID
     */
    @ApiModelProperty(value = "博主ID")
    @TableField("know_user_id")
    @Excel(name = "博主ID")
    private Long knowUserId;


    @Builder
    public KnowledgeUserQualification(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String fileName, String fileUrl, Long knowUserId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.knowUserId = knowUserId;
    }

}
