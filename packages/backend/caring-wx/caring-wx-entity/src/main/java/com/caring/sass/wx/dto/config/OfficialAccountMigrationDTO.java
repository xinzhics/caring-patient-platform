package com.caring.sass.wx.dto.config;

import lombok.Data;

import java.util.List;

/**
 * @className: OfficialAccountMigrationDTO
 * @author: 杨帅
 * @date: 2023/10/8
 */
@Data
public class OfficialAccountMigrationDTO {

    private String oldAppId;

    private List<String> openIds;


}
