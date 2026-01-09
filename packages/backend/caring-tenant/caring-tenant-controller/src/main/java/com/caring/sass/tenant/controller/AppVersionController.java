package com.caring.sass.tenant.controller;

import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.tenant.entity.AppVersion;
import com.caring.sass.tenant.dto.AppVersionSaveDTO;
import com.caring.sass.tenant.dto.AppVersionUpdateDTO;
import com.caring.sass.tenant.dto.AppVersionPageDTO;
import com.caring.sass.tenant.service.AppVersionService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 版本表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/appVersion")
@Api(value = "AppVersion", tags = "版本表")
//@PreAuth(replace = "appVersion:")
public class AppVersionController extends SuperController<AppVersionService, Long, AppVersion, AppVersionPageDTO, AppVersionSaveDTO, AppVersionUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<AppVersion> appVersionList = list.stream().map((map) -> {
            AppVersion appVersion = AppVersion.builder().build();
            //TODO 请在这里完成转换
            return appVersion;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(appVersionList));
    }

    @ApiOperation("获取最新的app版本")
    @GetMapping("getNewest")
    public R<AppVersion> getNewest() {
        LbqWrapper<AppVersion> wrapper = new LbqWrapper<>();
        wrapper.orderByDesc(AppVersion::getCreateTime);
        List<AppVersion> versions = baseService.list(wrapper);
        if (CollectionUtils.isEmpty(versions)) {
            return R.success(null);
        } else {
            return R.success(versions.get(0));
        }
    }


}
