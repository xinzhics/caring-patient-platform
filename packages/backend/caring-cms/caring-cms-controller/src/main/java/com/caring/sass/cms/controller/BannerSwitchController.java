package com.caring.sass.cms.controller;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.cms.entity.BannerSwitch;
import com.caring.sass.cms.dto.BannerSwitchSaveDTO;
import com.caring.sass.cms.dto.BannerSwitchUpdateDTO;
import com.caring.sass.cms.dto.BannerSwitchPageDTO;
import com.caring.sass.cms.service.BannerSwitchService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.context.BaseContextHandler;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * banner
 * </p>
 *
 * @author 杨帅
 * @date 2023-12-08
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/bannerSwitch")
@Api(value = "BannerSwitch", tags = "bannerSwitch")
public class BannerSwitchController extends SuperController<BannerSwitchService, Long, BannerSwitch, BannerSwitchPageDTO, BannerSwitchSaveDTO, BannerSwitchUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<BannerSwitch> bannerSwitchList = list.stream().map((map) -> {
            BannerSwitch bannerSwitch = BannerSwitch.builder().build();
            //TODO 请在这里完成转换
            return bannerSwitch;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(bannerSwitchList));
    }


    @Override
    public R<List<BannerSwitch>> query(BannerSwitch data) {
        String tenantCode = data.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        return super.query(data);
    }
}
