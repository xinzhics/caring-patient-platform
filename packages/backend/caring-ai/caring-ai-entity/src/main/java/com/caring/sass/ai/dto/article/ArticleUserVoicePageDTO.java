package com.caring.sass.ai.dto.article;

import java.time.LocalDateTime;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 声音管理
 * </p>
 *
 * @author leizhi
 * @since 2025-02-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleUserVoicePageDTO", description = "声音管理")
public class ArticleUserVoicePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @NotNull
    @ApiModelProperty(value = "用户id")
    private Long userId;


    @ApiModelProperty(value = "音频名称")
    private String name;

    @ApiModelProperty(value = "克隆状态")
    private VoiceCloneStatus cloneStatus;

    @ApiModelProperty(value = "删除状态")
    private Boolean deleteStatus = false;
}
