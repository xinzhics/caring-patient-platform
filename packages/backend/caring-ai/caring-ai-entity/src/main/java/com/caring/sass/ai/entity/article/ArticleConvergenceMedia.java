package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
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
 * 知识库融媒体
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-12
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_article_convergence_media", autoResultMap = true)
@ApiModel(value = "ArticleConvergenceMedia", description = "知识库融媒体")
@AllArgsConstructor
public class ArticleConvergenceMedia extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 博主ID
     */
    @ApiModelProperty(value = "博主ID")
    @TableField("user_id")
    @Excel(name = "博主ID")
    private Long userId;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 100, message = "用户手机号长度不能超过100")
    @TableField(value = "user_mobile", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "用户手机号")
    private String userMobile;

    /**
     * 文件标题
     */
    @ApiModelProperty(value = "文件标题")
    @Length(max = 200, message = "文件标题长度不能超过200")
    @TableField(value = "file_title", condition = LIKE)
    @Excel(name = "文件标题")
    private String fileTitle;

    /**
     * 链接路径
     */
    @ApiModelProperty(value = "链接路径")
    @Length(max = 400, message = "链接路径长度不能超过400")
    @Excel(name = "链接路径")
    private String fileUrl;


    @Builder
    public ArticleConvergenceMedia(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long userId, String userMobile, String fileTitle, String fileUrl) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userId = userId;
        this.userMobile = userMobile;
        this.fileTitle = fileTitle;
        this.fileUrl = fileUrl;
    }

}
