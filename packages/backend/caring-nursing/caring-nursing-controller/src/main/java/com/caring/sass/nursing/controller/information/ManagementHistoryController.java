package com.caring.sass.nursing.controller.information;


import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.information.ManagementHistoryPageDTO;
import com.caring.sass.nursing.dto.information.ManagementHistorySaveDTO;
import com.caring.sass.nursing.dto.information.ManagementHistoryUpdateDTO;
import com.caring.sass.nursing.entity.information.ManagementHistory;
import com.caring.sass.nursing.service.information.ManagementHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 管理历史
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/managementHistory")
@Api(value = "ManagementHistory", tags = "管理历史")
//@PreAuth(replace = "managementHistory:")
public class ManagementHistoryController extends SuperController<ManagementHistoryService, Long, ManagementHistory, ManagementHistoryPageDTO, ManagementHistorySaveDTO, ManagementHistoryUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<ManagementHistory> managementHistoryList = list.stream().map((map) -> {
            ManagementHistory managementHistory = ManagementHistory.builder().build();
            //TODO 请在这里完成转换
            return managementHistory;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(managementHistoryList));
    }


    @ApiOperation(value = "通过管理历史id来删除当前数据")
    @DeleteMapping("{id}")
    public R<Boolean> deleteByType(@PathVariable("id") Long id) {
        baseService.deleteById(id);
        return R.success();
    }
}
