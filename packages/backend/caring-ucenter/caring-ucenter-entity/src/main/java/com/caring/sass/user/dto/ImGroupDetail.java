package com.caring.sass.user.dto;

import com.caring.sass.user.entity.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ImGorupDetail
 * @Description
 * @Author yangShuai
 * @Date 2021/9/15 17:07
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImGroupDetail {

    private Patient patient;

    private List<ImGroupMember> groupMembers;

}
