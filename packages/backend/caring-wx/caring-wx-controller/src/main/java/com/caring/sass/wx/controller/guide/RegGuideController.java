package com.caring.sass.wx.controller.guide;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.paramtericText.ParametricTextManager;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.wx.dto.guide.RegGuidePageDTO;
import com.caring.sass.wx.dto.guide.RegGuideSaveDTO;
import com.caring.sass.wx.dto.guide.RegGuideUpdateDTO;
import com.caring.sass.wx.entity.guide.RegGuide;
import com.caring.sass.wx.service.guide.RegGuideService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 微信注册引导
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/regGuide")
@Api(value = "RegGuide", tags = "微信注册引导")
//@PreAuth(replace = "regGuide:")
public class RegGuideController extends SuperController<RegGuideService, Long, RegGuide, RegGuidePageDTO, RegGuideSaveDTO, RegGuideUpdateDTO> {

    @Autowired
    private ParametricTextManager parametricTextManager;

    @Autowired
    private TenantApi tenantApi;

    @ApiOperation(value = "查询项目注册引导通过租户")
    @GetMapping("getGuideByTenantCode")
    public R<RegGuide> getGuideByTenantCode(@RequestParam("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        return getGuide();
    }

    @ApiOperation(value = "查询项目注册引导")
    @GetMapping("getGuide")
    public R<RegGuide> getGuide() {
        RegGuide guide = baseService.getOne(Wraps.<RegGuide>lbQ().last(" limit 0,1 "));
        if (Objects.isNull(guide)) {
            return fail("该项目未配置注册引导");
        }
        R<Tenant> t = tenantApi.getByCode(BaseContextHandler.getTenant());
        if (t.getIsError() || t.getData() == null) {
            return R.fail("项目不存在");
        }

        Tenant tenant = t.getData();
        parametricTextManager.init();
        parametricTextManager.addParameter("project", tenant);
        String a = parametricTextManager.format(guide.getAgreement());
        String doctorAgreement = parametricTextManager.format(guide.getDoctorAgreement());
        ParametricTextManager.remove();
        parametricTextManager.init();
        parametricTextManager.addParameter("project", tenant);
        String intro = parametricTextManager.format(guide.getIntro());
        ParametricTextManager.remove();
        guide.setAgreement(a);
        guide.setDoctorAgreement(doctorAgreement);
        guide.setIntro(intro);
        return success(guide);
    }

    /**
     * 根据项目code,存在则更新记录，否插入一条记录
     *
     * @return 实体
     */
    @ApiOperation(value = "根据项目code,存在则更新记录，否插入一条记录")
    @PostMapping(value = "upsertByCode")
    public R<RegGuide> upsertByCode(@RequestBody RegGuideSaveDTO model) {
        String tenantCode = model.getTenantCode();
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        RegGuide regGuide = BeanUtil.toBean(model, RegGuide.class);
        baseService.saveOrUpdate(regGuide, Wraps.lbQ());
        return R.success(regGuide);
    }

    @Override
    public R<RegGuide> handlerSave(RegGuideSaveDTO model) {
        int count = baseService.count();
        if (count > 1) {
            return fail("该项目已存在注册引导");
        }
        return super.save(model);
    }

    @ApiOperation("复制注册引导")
    @PostMapping("copyRegGuide")
    public R<Boolean> copyRegGuide(@RequestParam("fromTenantCode") @NotEmpty(message = "待复制的项目编码不能为空") String fromTenantCode,
                                   @RequestParam("toTenantCode") @NotEmpty(message = "目标项目编码不能为空") String toTenantCode) {
        baseService.copyRegGuide(fromTenantCode, toTenantCode);
        return R.success();
    }

    @ApiOperation("更新医生和机构在基本信息页面显示的状态")
    @PutMapping("updateRegGuideDoctorOrganStatus")
    public R<RegGuide> getRegGuideDoctorOrganStatus(@RequestParam(value = "tenantCode", required = false) String tenantCode,
                                                    @RequestBody RegGuide regGuide) {

        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        baseService.saveOrUpdate(regGuide, Wraps.lbQ());
        return R.success(regGuide);
    }



    @ApiOperation("获取医生和机构在基本信息页面显示的开关")
    @GetMapping("getRegGuideDoctorOrganStatus")
    public R<RegGuide> getRegGuideDoctorOrganStatus(@RequestParam(value = "tenantCode", required = false) String tenantCode) {

        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        RegGuide regGuides = baseService.getOne(Wraps.<RegGuide>lbQ()
                .select(SuperEntity::getId, RegGuide::getHasShowDoctor, RegGuide::getHasShowOrgName)
                .last(" limit 0,1 "));
        return R.success(regGuides);
    }


    @ApiOperation("获取设置47小时未注册提醒的code")
    @GetMapping("getOpenUnregisteredReminder")
    public R<List<RegGuide>> getOpenUnregisteredReminder() {

        List<RegGuide> guideList = baseService.getOpenUnregisteredReminder();
        return R.success(guideList);

    }

    @ApiOperation("表单历史记录开关")
    @GetMapping("formHistoryRecord")
    public R<Boolean> formHistoryRecord() {
        String tenant = BaseContextHandler.getTenant();
        return openFormHistoryRecord(tenant);
    }

    @ApiOperation("更新表单历史记录开关")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "租户"),
            @ApiImplicitParam(name = "formHistoryRecord", value = "开关 0不记录， 1记录")
    })
    @GetMapping("updateFormHistoryRecord/{tenantCode}")
    public R<Boolean> updateFormHistoryRecord(@PathVariable("tenantCode") String tenantCode, Integer formHistoryRecord) {

        BaseContextHandler.setTenant(tenantCode);
        if (formHistoryRecord == 1 || formHistoryRecord == 0) {
            RegGuide regGuide = new RegGuide();
            regGuide.setFormHistoryRecord(formHistoryRecord);
            baseService.saveOrUpdate(regGuide, Wraps.lbQ());
        }
        return R.success(false);
    }

    @ApiOperation("表单历史记录是否开启")
    @GetMapping("openFormHistoryRecord/{tenantCode}")
    public R<Boolean> openFormHistoryRecord(@PathVariable("tenantCode") String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        RegGuide regGuide = baseService.getOne(Wraps.<RegGuide>lbQ()
                .select(SuperEntity::getId, RegGuide::getFormHistoryRecord)
                .last(" limit 0,1 "));
        if (Objects.nonNull(regGuide)) {
            Integer formHistoryRecord = regGuide.getFormHistoryRecord();
            if (formHistoryRecord != null) {
                if (formHistoryRecord.equals(1)) {
                    return R.success(true);
                }
            }
        }
        return R.success(false);
    }

    @ApiOperation("微信用户关注后默认角色")
    @GetMapping("wxUserDefaultRole/{tenantCode}")
    public R<String> getWxUserDefaultRole(@PathVariable("tenantCode") String tenantCode) {

        String defaultRole = baseService.getWxUserDefaultRole(tenantCode);
        return R.success(defaultRole);
    }

    @ApiOperation("更新微信用户关注后默认角色")
    @PutMapping("wxUserDefaultRole/{tenantCode}")
    public R<String> wxUserDefaultRole(@PathVariable("tenantCode") String tenantCode,
                                       @RequestParam("userType") String userType) {

        BaseContextHandler.setTenant(tenantCode);
        if (UserType.UCENTER_DOCTOR.equals(userType) || UserType.UCENTER_PATIENT.equals(userType) || UserType.TOURISTS.equals(userType)) {
            return R.success(baseService.setWxUserDefaultRole(tenantCode, userType));
        } else {
            return R.fail("参数不正确，角色参数为" + UserType.UCENTER_DOCTOR + "," + UserType.UCENTER_PATIENT + "," + UserType.TOURISTS);
        }
    }

    @ApiOperation("获取系统是否开启入组时添加用药")
    @GetMapping("hasFillDrugs")
    public R<Integer> hasFillDrugs(@RequestParam(required = false) String tenantCode) {
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        List<RegGuide> guideList = baseService.list(Wraps.<RegGuide>lbQ()
                .select(SuperEntity::getId, RegGuide::getHasFillDrugs)
                .last(" limit 0,1 "));
        if (CollUtil.isNotEmpty(guideList)) {
            RegGuide guide = guideList.get(0);
            return R.success(guide.getHasFillDrugs());
        } else {
            return R.success(0);
        }

    }

    @ApiOperation("创建项目时初始化引导")
    @PutMapping("initGuide")
    public R<String> initGuide(@RequestParam("tenantCode") String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        baseService.initGuide();
        return R.success("success");
    }

}
