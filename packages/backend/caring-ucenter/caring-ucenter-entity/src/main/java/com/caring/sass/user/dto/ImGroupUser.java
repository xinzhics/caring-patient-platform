package com.caring.sass.user.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.user.entity.Patient;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ImGroupUser
 * @Description 聊天小组内成员的信息
 * @Author yangShuai
 * @Date 2020/11/13 10:49
 * @Version 1.0
 */
@Data
public class ImGroupUser {


    private Long id;

    private String name;

    private String type;

    private String avatar;

    private String imAccount;

    @ApiModelProperty("医助对患者的备注")
    private String remark;

    @ApiModelProperty("医生对患者的备注")
    private String doctorRemark;

    private Patient patient;

    @ApiModelProperty("是否是AI托管")
    private Boolean aiHosted;

}
