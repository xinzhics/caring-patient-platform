package com.caring.sass.user.merck;

import lombok.Data;

/**
 * @className: SyncMerckDto
 * @author: 杨帅
 * @date: 2023/12/22
 */
@Data
public class SyncMerckDto {

    // 表示将 未同步的敏识患者数据拉取的哪个项目
    String tenantCode;

    // 拉到项目下的哪个医生下。
    Long doctorId;
    String merckToken;
    String merckDomain;

}
