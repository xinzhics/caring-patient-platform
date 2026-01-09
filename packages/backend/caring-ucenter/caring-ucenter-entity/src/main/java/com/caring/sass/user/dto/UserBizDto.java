package com.caring.sass.user.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @ClassName UserBizDto
 * @Description
 * @Author yangShuai
 * @Date 2022/4/13 9:43
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class UserBizDto {


    private Map<String, List<UserBizInfo>> map;


}
