package com.caring.sass.nursing.api;

import com.caring.sass.base.api.SuperApi;
import com.caring.sass.nursing.dto.drugs.SysDrugsPageDTO;
import com.caring.sass.nursing.dto.drugs.SysDrugsSaveDTO;
import com.caring.sass.nursing.dto.drugs.SysDrugsUpdateDTO;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author leizhi
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", path = "/sysDrugs", qualifier = "SysDrugs")
public interface SysDrugApi extends SuperApi<Long, SysDrugs, SysDrugsPageDTO, SysDrugsSaveDTO, SysDrugsUpdateDTO> {

}
