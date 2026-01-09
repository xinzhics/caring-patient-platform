package com.caring.sass.tenant.dto.router;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.tenant.entity.router.H5CoreFunctions;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.entity.router.H5Ui;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @className: H5RouterPatientDto
 * @author: 杨帅
 * @date: 2023/6/27
 */
@Data
@ApiModel("超管端配置患者个人中心")
public class H5RouterPatientDto {

    @ApiModelProperty("患者基本信息部分")
    private H5Router patientBaseInfo;

    @ApiModelProperty("核心功能配置")
    private H5CoreFunctions h5CoreFunctions;

    @ApiModelProperty("患者我的功能菜单")
    private List<H5Router> patientMyFeatures;

    @ApiModelProperty("患者我的档案菜单")
    private List<H5Router> patientMyFile;

    @ApiModelProperty("个人中心背景图")
    private H5Ui h5UiImage;


    public void addPatientMyFeatures(H5Router h5Router) {
        if (patientMyFeatures == null) {
            patientMyFeatures = new ArrayList<>();
        }
        patientMyFeatures.add(h5Router);
    }

    public void addPatientMyFile(H5Router h5Router) {
        if (patientMyFile == null) {
            patientMyFile = new ArrayList<>();
        }
        patientMyFile.add(h5Router);
    }

    public void sortRouter() {
        sortRouter(patientMyFeatures);
        sortRouter(patientMyFile);
    }

    private void sortRouter(List<H5Router> routers) {
        if (CollUtil.isNotEmpty(routers)) {
            routers.sort((o1, o2) -> {
                if (o1.getSortValue() == null || o2.getSortValue() == null) {
                    return -1;
                }
                if (o1.getSortValue().equals(o2.getSortValue())) {
                    return 0;
                }
                return o1.getSortValue() > o2.getSortValue() ? 1 : -1;
            });
        }
    }

}
