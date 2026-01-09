package com.caring.sass.nursing.dto.follow;

import cn.hutool.core.collection.CollUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @className: PatientMenuFollow
 * @author: 杨帅
 * @date: 2023/7/2
 */
@Data
@ApiModel("患者随访日历的信息")
public class PatientMenuFollow {

    @ApiModelProperty("今日待完成项")
    private Integer followCount;

    @ApiModelProperty("随访的条目")
    private List<PatientMenuFollowItem> followItems;

    @ApiModelProperty("医生的头像")
    private String doctorAvatar;

    @ApiModelProperty("菜单数据统计")
    private List<MenuDataCount> menuDataCountList;



    public void addMenuDataCountList(Long menuId, Integer dataCount) {
        MenuDataCount count = new MenuDataCount();
        count.setMenuId(menuId);
        count.setDataCount(dataCount);
        if (CollUtil.isEmpty(menuDataCountList)) {
            menuDataCountList = new ArrayList<>();
        }
        menuDataCountList.add(count);
    }

    @Data
    @ApiModel("菜单数据统计")
    class MenuDataCount{

        @ApiModelProperty("菜单的ID")
        private Long menuId;

        @ApiModelProperty("菜单的业务数据统计")
        private Integer dataCount;

    }

}
