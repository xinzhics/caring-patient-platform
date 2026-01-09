package com.caring.sass.nursing.api;

import com.caring.sass.base.api.SuperApi;
import com.caring.sass.nursing.dto.drugs.SysDrugsCategoryPageDTO;
import com.caring.sass.nursing.dto.drugs.SysDrugsCategorySaveDTO;
import com.caring.sass.nursing.dto.drugs.SysDrugsUpdateDTO;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author leizhi
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", path = "/sysDrugsCategory", qualifier = "SysDrugCategory")
public interface SysDrugCategoryApi extends SuperApi<Long, SysDrugsCategory, SysDrugsCategoryPageDTO, SysDrugsCategorySaveDTO, SysDrugsUpdateDTO> {

}
