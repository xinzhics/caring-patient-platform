package com.caring.sass.nursing.api;

import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.nursing.dto.drugs.CustomCommendDrugsPageDTO;
import com.caring.sass.nursing.dto.drugs.CustomCommendDrugsSaveDTO;
import com.caring.sass.nursing.dto.drugs.CustomCommendDrugsUpdateDTO;
import com.caring.sass.nursing.entity.drugs.CustomCommendDrugs;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;

/**
 * 推荐用药api
 *
 * @author xinzh
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", path = "/customCommendDrugs", qualifier = "CustomCommendDrugs")
public interface CustomCommendDrugsApi extends SuperApi<Long, CustomCommendDrugs, CustomCommendDrugsPageDTO, CustomCommendDrugsSaveDTO, CustomCommendDrugsUpdateDTO> {


    /**
     * 添加推荐用药
     *
     * @param drugsId 药品id
     */
    @PostMapping(value = "addRecommendDrugs")
    R<Boolean> addRecommendDrugs(@RequestParam(value = "drugsId") Long drugsId);

    /**
     * 删除推荐用药
     *
     * @param drugsId 药品id
     */
    @DeleteMapping(value = "delRecommendDrugs")
    R<Boolean> delRecommendDrugs(@RequestParam(value = "drugsId") Long drugsId);

    /**
     * 复制推荐用药
     *
     * @param fromTenantCode 待复制的项目编码
     * @param toTenantCode   目标项目编码
     */
    @PostMapping("copyRecommendDrugs")
    R<Boolean> copyRecommendDrugs(@RequestParam("fromTenantCode") String fromTenantCode,
                                  @RequestParam("toTenantCode") String toTenantCode);
}
