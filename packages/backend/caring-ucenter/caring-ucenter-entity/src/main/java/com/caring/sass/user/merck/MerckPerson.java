package com.caring.sass.user.merck;

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
 * 敏识用户
 * </p>
 *
 * @author 杨帅
 * @since 2023-12-20
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_merck_person")
@ApiModel(value = "MerckPerson", description = "敏识用户")
@AllArgsConstructor
public class MerckPerson extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "敏识的用户ID， 使用此ID。调敏识接口获取用户信息")
    @TableField("person_id")
    private Long personId;

    @Length(max = 50, message = "长度不能超过50")
    @TableField(value = "open_id", condition = LIKE)
    private String openId;

    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "union_id", condition = LIKE)
    private String unionId;

    /**
     * 同步状态 1 已同步 0 未同步
     */
    @ApiModelProperty(value = "同步状态 1 已同步 0 未同步")
    @TableField("information_sync")
    @Excel(name = "同步状态 1 已同步 0 未同步 2 同步失败")
    private Integer informationSync;
  /**
     * 同步状态 1 已同步 0 未同步
     */
    @ApiModelProperty(value = "关注状态 1 关注 0 未关注")
    @TableField("subscribe")
    @Excel(name = "关注状态 1 关注 0 未关注")
    private Integer subscribe;


    @Builder
    public MerckPerson(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    Long personId, String openId, String unionId, Integer informationSync) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.personId = personId;
        this.openId = openId;
        this.unionId = unionId;
        this.informationSync = informationSync;
    }

}
