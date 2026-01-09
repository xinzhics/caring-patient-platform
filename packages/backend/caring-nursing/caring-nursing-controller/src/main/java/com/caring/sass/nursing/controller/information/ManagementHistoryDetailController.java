package com.caring.sass.nursing.controller.information;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.nursing.dto.information.ManagementHistoryDetailPageDTO;
import com.caring.sass.nursing.dto.information.ManagementHistoryDetailSaveDTO;
import com.caring.sass.nursing.dto.information.ManagementHistoryDetailUpdateDTO;
import com.caring.sass.nursing.entity.information.ManagementHistoryDetail;
import com.caring.sass.nursing.service.information.ManagementHistoryDetailService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 管理历史详细记录
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/managementHistoryDetail")
@Api(value = "ManagementHistoryDetail", tags = "管理历史详细记录")
//@PreAuth(replace = "managementHistoryDetail:")
public class ManagementHistoryDetailController extends SuperController<ManagementHistoryDetailService, Long, ManagementHistoryDetail, ManagementHistoryDetailPageDTO, ManagementHistoryDetailSaveDTO, ManagementHistoryDetailUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<ManagementHistoryDetail> managementHistoryDetailList = list.stream().map((map) -> {
            ManagementHistoryDetail managementHistoryDetail = ManagementHistoryDetail.builder().build();
            //TODO 请在这里完成转换
            return managementHistoryDetail;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(managementHistoryDetailList));
    }

    @Override
    public R<IPage<ManagementHistoryDetail>> page(@RequestBody PageParams<ManagementHistoryDetailPageDTO> params) {
        @SuppressWarnings("unchecked")
        IPage<ManagementHistoryDetail> page = params.buildPage();
        query(params,page,null);
        IPage<ManagementHistoryDetail> build = baseService.build(page);
        return success(build);
    }
}
