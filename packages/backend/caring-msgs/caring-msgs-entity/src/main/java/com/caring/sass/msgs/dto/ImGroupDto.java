package com.caring.sass.msgs.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @ClassName ImGroupDto
 * @Description
 * @Author yangShuai
 * @Date 2021/6/2 16:31
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class ImGroupDto {

    private String groupname = null;


    private String desc = "群组描述";


    private Boolean _public;

    /**
     * 成员上限
     */
    private Integer maxusers;


    private Boolean approval;

    /**
     * 群主的环信 ID
     */
    private String owner = null;

    /**
     * 群组成员
     */
    private List<String> imAccount;

    public ImGroupDto() {
        this._public = false;
        this.approval = true;
        this.maxusers = 50;
    }
}
