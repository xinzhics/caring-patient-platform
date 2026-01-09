package com.caring.sass;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.common.constant.DictionaryType;
import com.caring.sass.injection.annonation.InjectionField;
import com.caring.sass.model.RemoteData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

import static com.caring.sass.common.constant.InjectionFieldConstants.STATION_ID_METHOD;

@Data
@ToString
/**
 * 测试DTO
 *
 * @author caring
 * @date 2019/07/25
 */
public class TestModel {
    private LocalDateTime date;
    private Date d2;

    @ApiModelProperty(value = "组织ID")
    @TableField("org_id")
//    @InjectionField(feign = OrgApi.class, method = ORG_ID_METHOD)
    private RemoteData<Long, Org> org;


    //    @InjectionField(api = "orgApi", method = "findOrgNameByIds")
    private RemoteData<Long, String> org2;


    @InjectionField(apiClass = Object.class, method = "findOrgNameByIds")
    private RemoteData<Long, String> error;

    @InjectionField(api = "stationServiceImpl", method = STATION_ID_METHOD)
    private RemoteData<Long, Org> station;

    // 去数据字典表 根据code 查询 name
    @InjectionField(api = "dictionaryItemServiceImpl", method = "findDictionaryItem", dictType = DictionaryType.EDUCATION)
    private RemoteData<String, String> education;

    @InjectionField(api = "dictionaryItemServiceImpl", method = "findDictionaryItem", dictType = DictionaryType.EDUCATION)
    private String education2;

}
