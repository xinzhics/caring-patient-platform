package com.caring.sass.tenant.controller.router;


import java.sql.Wrapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.dto.router.H5CoreFunctionsPageDTO;
import com.caring.sass.tenant.dto.router.H5CoreFunctionsSaveDTO;
import com.caring.sass.tenant.dto.router.H5CoreFunctionsUpdateDTO;
import com.caring.sass.tenant.entity.router.H5CoreFunctions;
import com.caring.sass.tenant.service.router.H5CoreFunctionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 患者个人中心核心功能
 * </p>
 *
 * @author 杨帅
 * @date 2023-06-27
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/h5CoreFunctions")
@Api(value = "H5CoreFunctions", tags = "患者个人中心核心功能")
@PreAuth(replace = "h5CoreFunctions:")
public class H5CoreFunctionsController extends SuperController<H5CoreFunctionsService, Long, H5CoreFunctions, H5CoreFunctionsPageDTO, H5CoreFunctionsSaveDTO, H5CoreFunctionsUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<H5CoreFunctions> h5CoreFunctionsList = list.stream().map((map) -> {
            H5CoreFunctions h5CoreFunctions = H5CoreFunctions.builder().build();
            //TODO 请在这里完成转换
            return h5CoreFunctions;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(h5CoreFunctionsList));
    }


    @GetMapping("findOneByCode")
    @ApiOperation("查询项目患者核心功能配置")
    public R<H5CoreFunctions> findOneByCode() {

        H5CoreFunctions h5CoreFunctions = baseService.getOne(Wraps.<H5CoreFunctions>lbQ().last("limit 0, 1"));
        return R.success(h5CoreFunctions);

    }


}
