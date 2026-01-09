package com.caring.sass.nursing.api;

import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.nursing.dto.drugs.CustomDrugsCategoryPageDTO;
import com.caring.sass.nursing.dto.drugs.CustomDrugsCategorySaveDTO;
import com.caring.sass.nursing.dto.drugs.CustomDrugsCategoryUpdateDTO;
import com.caring.sass.nursing.entity.drugs.CustomDrugsCategory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 推荐用药api
 *
 * @author xinzh
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", path = "/customDrugsCategory", qualifier = "CustomDrugsCategory")
public interface CustomDrugsCategoryApi extends SuperApi<Long, CustomDrugsCategory, CustomDrugsCategoryPageDTO, CustomDrugsCategorySaveDTO, CustomDrugsCategoryUpdateDTO> {

    /**
     * 修改项目推荐用药
     *
     * @param ids 新的推荐用药
     */
    @PutMapping("updateTenantCustomDrugsCategory")
    R<Boolean> updateTenantCustomDrugsCategory(@RequestParam("ids[]") List<Long> ids);

    /**
     * 复制项目药品类别
     *
     * @param fromTenantCode 待复制的项目编码
     * @param toTenantCode   目标项目编码
     */
    @PostMapping("copyDrugsCategory")
    R<Boolean> copyDrugsCategory(@RequestParam("fromTenantCode") @NotEmpty(message = "待复制的项目编码不能为空") String fromTenantCode,
                                 @RequestParam("toTenantCode") @NotEmpty(message = "目标项目编码不能为空") String toTenantCode);
}
