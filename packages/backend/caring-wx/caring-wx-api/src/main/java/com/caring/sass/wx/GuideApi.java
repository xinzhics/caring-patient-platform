package com.caring.sass.wx;

import com.caring.sass.base.R;
import com.caring.sass.wx.dto.guide.RegGuideSaveDTO;
import com.caring.sass.wx.entity.guide.RegGuide;
import com.caring.sass.wx.hystrix.GuideApiFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName GuideApi
 * @Description
 * @Author yangShuai
 * @Date 2020/9/27 15:08
 * @Version 1.0
 */
@Component
@FeignClient(name = "${caring.feign.oauth-server:caring-wx-server}", path = "/regGuide",
        qualifier = "GuideApi", fallback = GuideApiFallback.class)
public interface GuideApi {

    @ApiOperation(value = "查询项目注册引导通过租户")
    @GetMapping("getGuideByTenantCode")
    R<RegGuide> getGuideByTenantCode(@RequestParam("tenantCode") String tenantCode);

    @GetMapping("getGuide")
    R<RegGuide> getGuide();

    /**
     * 保存微信注册引导信息
     */
    @PostMapping()
    R<RegGuide> save(@RequestBody RegGuideSaveDTO model);

    /**
     * 存在更新记录，否插入一条记录
     *
     * @return 实体
     */
    @PostMapping(value = "upsertByCode")
    R<RegGuide> upsertByCode(@RequestBody RegGuideSaveDTO model);

    /**
     * 复制注册引导
     */
    @ApiOperation("复制注册引导")
    @PostMapping("copyRegGuide")
    R<Boolean> copyRegGuide(@RequestParam("fromTenantCode") String fromTenantCode,
                            @RequestParam("toTenantCode") String toTenantCode);

    @ApiOperation("获取设置47小时未注册提醒的code")
    @GetMapping("getOpenUnregisteredReminder")
    R<List<RegGuide>> getOpenUnregisteredReminder();


    @ApiOperation("表单历史记录是否开启")
    @GetMapping("openFormHistoryRecord/{tenantCode}")
    R<Boolean> openFormHistoryRecord(@PathVariable("tenantCode") String tenantCode);


    @ApiOperation("获取系统是否开启入组时添加用药")
    @GetMapping("hasFillDrugs")
    R<Integer> hasFillDrugs(@RequestParam(required = false, name = "tenantCode") String tenantCode);

    @ApiOperation("获取系统是否开启入组时添加用药")
    @PutMapping("initGuide")
    R<String> initGuide(@RequestParam("tenantCode") String tenantCode);


}
