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
 * dify知识库关联表
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
@ApiModel(value = "KnowledgeUpdateDTO", description = "dify知识库关联表")
public class KnowledgeUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 知识库名称
     */
    @ApiModelProperty(value = "知识库名称")
    @Length(max = 20, message = "知识库名称长度不能超过20")
    private String knowName;
    /**
     * 知识库dify的Id
     */
    @ApiModelProperty(value = "知识库dify的Id")
    @Length(max = 50, message = "知识库dify的Id长度不能超过50")
    private String knowDifyId;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long knowUserId;
    /**
     * 知识库类型(专业学术资料，个人成果，病例库，日常收集)
     */
    @ApiModelProperty(value = "知识库类型(专业学术资料，个人成果，病例库，日常收集)")
    @Length(max = 50, message = "知识库类型(专业学术资料，个人成果，病例库，日常收集)长度不能超过50")
    private String knowType;
}
