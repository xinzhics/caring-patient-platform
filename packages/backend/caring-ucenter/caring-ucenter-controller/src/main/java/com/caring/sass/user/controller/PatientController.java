package com.caring.sass.user.controller;

import cn.afterturn.easypoi.entity.vo.MapExcelConstants;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.view.PoiBaseView;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.LetterUtil;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.common.utils.PasswordUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.exception.BizException;
import com.caring.sass.exception.code.ExceptionCode;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.msgs.api.VerificationCodeApi;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.nursing.api.FormResultApi;
import com.caring.sass.nursing.dto.form.FormWidgetType;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.sms.dto.VerificationCodeDTO;
import com.caring.sass.sms.enumeration.VerificationCodeType;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dao.DoctorCustomGroupPatientMapper;
import com.caring.sass.user.dao.NursingStaffMapper;
import com.caring.sass.user.dao.PatientMapper;
import com.caring.sass.user.dao.PatientQueryMapper;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.*;
import com.caring.sass.user.util.I18nUtils;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.utils.SpringUtils;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.SyncUserUnionIdDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <p>
 * 前端控制器
 * 患者表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patient")
@Api(value = "Patient", tags = "患者表")
//@PreAuth(replace = "patient:")
public class PatientController extends SuperController<PatientService, Long, Patient, PatientPageDTO, PatientSaveDTO, PatientUpdateDTO> {

    private final TenantApi tenantApi;

    private final UserService userService;

    private final FormResultApi formResultApi;

    private final DoctorService doctorService;

    private final WeiXinApi weiXinApi;

    private final DictionaryItemService dictionaryItemService;

    @Autowired
    DoctorCustomGroupService doctorCustomGroupService;

    @Autowired
    CustomGroupingService customGroupingService;

    public PatientController(TenantApi tenantApi, UserService userService, FormResultApi formResultApi, DoctorService doctorService, DictionaryItemService dictionaryItemService,
                             WeiXinApi weiXinApi) {
        this.tenantApi = tenantApi;
        this.userService = userService;
        this.formResultApi = formResultApi;
        this.doctorService = doctorService;
        this.weiXinApi = weiXinApi;
        this.dictionaryItemService = dictionaryItemService;
    }



    @Deprecated
    @ApiOperation(value = "会员详细数据导出")
    @RequestMapping(value = "/exportFormResult", method = RequestMethod.POST, produces = "application/octet-stream")
    public void exportFormResult(@RequestBody @Validated PageParams<PatientPageDTO> params, ModelMap modelMap, HttpServletRequest request,
                                 HttpServletResponse response) {
        params.setSize(Integer.MAX_VALUE);
        R<IPage<Patient>> r = pageWithScope(params);
        List<Patient> patients = r.getData().getRecords();

        List<ExcelExportEntity> entity = new ArrayList<>();
        entity.add(new ExcelExportEntity("创建时间", "createTime"));
        entity.add(new ExcelExportEntity("姓名", "姓名"));
        entity.add(new ExcelExportEntity("所属医生", "所属医生"));
        entity.add(new ExcelExportEntity("所属医助", "所属医助"));
        List<String> titleList = new ArrayList<>();
        List<Map<String, Object>> list = new ArrayList<>();
        List<JSONObject> j;
        for (Patient patient : patients) {
            j = new ArrayList<>();
            R<FormResult> baseInfo = formResultApi.getFormResultByCategory(patient.getId(), FormEnum.BASE_INFO);
            R<FormResult> healthInfo = formResultApi.getFormResultByCategory(patient.getId(), FormEnum.HEALTH_RECORD);
            if (baseInfo.getIsSuccess() != null && baseInfo.getIsSuccess()) {
                FormResult data = baseInfo.getData();
                if (Objects.nonNull(data) && StrUtil.isNotBlank(data.getJsonContent())) {
                    try {
                        j.addAll(JSON.parseArray(data.getJsonContent(), JSONObject.class));
                    } catch (Exception e) {
                        log.error("解析用户{}基本信息异常", patient.getId(), e);
                    }
                }
            }
            if (healthInfo.getIsSuccess() != null && healthInfo.getIsSuccess()) {
                FormResult healthInfoData = healthInfo.getData();
                if (Objects.nonNull(healthInfoData) && StrUtil.isNotBlank(healthInfoData.getJsonContent())) {
                    try {
                        j.addAll(JSON.parseArray(healthInfoData.getJsonContent(), JSONObject.class));
                    } catch (Exception e) {
                        log.error("解析用户{}疾病信息异常", patient.getId(), e);
                    }
                }
            }

            Map<String, Object> map = new HashMap<>(j.size());
            map.put("头像", patient.getAvatar());
            map.put("姓名", patient.getName());
            map.put("所属医生", patient.getDoctorName());
            map.put("所属医助", patient.getServiceAdvisorName());
            for (JSONObject o : j) {
                if (FormWidgetType.FULL_NAME.equals(o.get("widgetType"))) {
                    continue;
                }
                String label = o.getString("label");
                if (!titleList.contains(label)) {
                    ExcelExportEntity tmpEntity = new ExcelExportEntity(label, label);
                    entity.add(tmpEntity);
                    titleList.add(label);
                }
                String value = parseFormValue(o.getJSONArray("values"));
                map.put(label, value);
            }
            map.put("createTime", LocalDateTimeUtil.format(patient.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
            list.add(map);
        }

        String range = "";
        String createTime_st = params.getMap().get("createTime_st");
        String createTime_ed = params.getMap().get("createTime_ed");
        if (StrUtil.isBlank(createTime_st) || StrUtil.isBlank(createTime_ed)) {
            range = "全部";
        } else {
            range = createTime_st + "~" + createTime_st;
        }
        String title = StrUtil.concat(true, "   数据范围： ", range,
                "   导出时间：" + LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_PATTERN),
                "   操作人：" + getAccount());

        String tenantName = tenantApi.getByCode(getTenant()).getData().getName();
        ExportParams exportParams = new ExportParams(title, tenantName + "会员详细数据", ExcelType.XSSF);
        modelMap.put(MapExcelConstants.MAP_LIST, list);
        modelMap.put(MapExcelConstants.ENTITY_LIST, entity);
        modelMap.put(MapExcelConstants.PARAMS, exportParams);
        modelMap.put(MapExcelConstants.FILE_NAME, "会员详细数据");
        PoiBaseView.render(modelMap, request, response, MapExcelConstants.EASYPOI_MAP_EXCEL_VIEW);
    }

    private static String parseFormValue(JSONArray value) {
        if (CollUtil.isEmpty(value)) {
            return "";
        }
        for (Object o : value) {
            JSONObject jsonObj = (JSONObject) o;
            if (jsonObj.containsKey("valueText")) {
                return jsonObj.getString("valueText");
            }
            return jsonObj.getString("attrValue");
        }
        return "";
    }


    @PutMapping("update")
    public R<Boolean> update(@RequestBody Patient patient) {
        if (patient.getId() != null) {
            baseService.updateById(patient);
        }
        return R.success(true);
    }

    @PostMapping("updateSuccessiveCheck")
    public R<Boolean> updateSuccessiveCheck(@RequestBody List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return R.success();
        }
        UpdateWrapper<Patient> patient = new UpdateWrapper<>();
        patient.set("successive_check", 0);
        patient.in("id", ids);
        baseService.update(patient);
        return R.success(true);
    }

    /**
     * 患者发送转人工模版消息给医生医助
     * @param patientId
     * @return
     */
    @ApiOperation("患者发送转人工模版消息给医生医助")
    @GetMapping("sendManualTemplate")
    public R<Boolean> sendManualTemplate(@RequestParam("patientId") Long patientId) {
        baseService.sendManualTemplate(patientId);
        return R.success(true);
    }


    @ApiOperation("发送一条给专员和医生")
    @PostMapping("/anno/sendChat")
    public R<Chat> sendChat(@RequestBody Chat chat, @RequestParam(required = false) Integer forcedManualReply) {
        Chat chatList = baseService.sendChat(chat, forcedManualReply);
        return R.success(chatList);
    }


    @GetMapping("queryByOpenId")
    public R<Patient> queryByOpenId(@RequestParam(value = "openId") @NotEmpty(message = "openId不能为空") String openId) {
        String tenant = BaseContextHandler.getTenant();
        BizAssert.notEmpty(tenant, "项目编码不能为空");
        Patient patient = baseService.getOne(Wraps.<Patient>lbQ().eq(Patient::getOpenId, openId));
        if (patient == null) {
            return R.fail(ExceptionCode.OPENID_USER_NOT_FOUND);
        }
        return R.success(patient);
    }

    @ApiOperation("获取患者的聊天小组")
    @GetMapping("anno/getPatientImGroup")
    public R<List<ImGroupUser>> getPatientImGroup(@RequestParam("patientId") Long patientId) {

        List<ImGroupUser> imGroupUser = baseService.getImGroupUser(patientId);
        return R.success(imGroupUser);

    }

    @ApiOperation("获取聊天小组详细成员2.4")
    @GetMapping("anno/getPatientGroupDetail")
    public R<ImGroupDetail> getPatientGroupDetail(@RequestParam("patientId") Long patientId) {

        ImGroupDetail imGroupUser = baseService.getImGroupDetail(patientId, null, null);
        return R.success(imGroupUser);

    }

    @ApiOperation("统计租户下入组且未取关患者数量")
    @GetMapping("countTenantPatientNumber")
    public R<Integer> countTenantPatientNumber() {
        int count = baseService.count(Wraps.<Patient>lbQ().eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE_NORMAL));
        return R.success(count);
    }


    @ApiOperation("统计医助所属机构和子机构下的患者的数量")
    @PostMapping("countNursingOrgPatientNumber")
    public R<Integer> countNursingOrgPatientNumber(@RequestParam("nursingId") Long nursingId,  @RequestBody List<Long> tagIdList) {

        NursingStaffService service = SpringUtils.getBean(NursingStaffService.class);
        if (service != null) {
            List<Long> nursingOrgIds = service.getNursingOrgIds(nursingId);
            LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ()
                    .in(Patient::getOrganId, nursingOrgIds)
                    .eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE_NORMAL);
            if (CollUtil.isNotEmpty(tagIdList)) {
                String tagIds = StrUtil.join(",", tagIdList);
                lbqWrapper.apply(" id in (select association_id from t_tag_association where tag_id in (" + tagIds + ")) ");
            }
            int selectedPatient = baseService.count(lbqWrapper);
            return R.success(selectedPatient);
        } else {
            return R.success(null);
        }
    }


    @ApiOperation("统计统计医生下 注册未取关 患者数量")
    @PostMapping("countDoctorPatientNumber")
    public R<Integer> countDoctorPatientNumber(@RequestParam("doctorId") Long doctorId, @RequestBody List<Long> tagIdList) {

        LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ()
                .in(Patient::getDoctorId, doctorId)
                .eq(Patient::getStatus, Patient.PATIENT_SUBSCRIBE_NORMAL);
        if (CollUtil.isNotEmpty(tagIdList)) {
            String tagIds = StrUtil.join(",", tagIdList);
            lbqWrapper.apply(" id in (select association_id from t_tag_association where tag_id in (" + tagIds + ")) ");
        }
        int selectedPatient = baseService.count(lbqWrapper);
        return R.success(selectedPatient);
    }


    /**
     * 接口不具备实际作用 2.4 删除
     * @param patientId
     * @param tenantCode
     * @return
     */
    @Deprecated
    @ApiOperation("患者Im在线 ")
    @PutMapping("anno/imOnline/{patientId}")
    public R patientImOnline(@PathVariable("patientId") Long patientId,
                             @RequestParam(value = "tenantCode", required = false) String tenantCode) {

        if (!StringUtils.isEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        Patient patient = baseService.getById(patientId);
        if (Objects.nonNull(patient)) {
            baseService.updateById(patient);
        }
        return R.success();
    }

    @PostMapping("imGroupPage")
    @ApiOperation("群发消息列表")
    public R<IPage<Patient>> page1(@RequestBody PageParams<PatientPageDTO> params) {

        IPage<Patient> page = params.buildPage();
        PatientPageDTO model = params.getModel();
        LbqWrapper<Patient> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Patient::getServiceAdvisorId, model.getServiceAdvisorId());
        lbqWrapper.eq(Patient::getDoctorId, model.getDoctorId());
        lbqWrapper.ne(Patient::getStatus, Patient.PATIENT_NO_SUBSCRIBE);
        lbqWrapper.select(Patient::getId, Patient::getName, Patient::getAvatar, Patient::getImAccount, Patient::getDiagnosisId,
                Patient::getDiagnosisName, Patient::getStatus, Patient::getDoctorId, Patient::getRemark, Patient::getDoctorRemark);
        baseService.page(page, lbqWrapper);
        return R.success(page);
    }

    @ApiOperation("患者填写疾病信息完成")
    @PostMapping("/diseaseInformationStatus")
    public R<Boolean> diseaseInformationStatus(@RequestParam("patientId") @NotNull(message = "患者id不为空") Long patientId) {
        baseService.diseaseInformationStatus(patientId);
        return R.success();
    }

    @ApiOperation("患者同意入组协议")
    @PostMapping("/agreeAgreement")
    public R<Boolean> agreeAgreement(@RequestParam("patientId") @NotNull(message = "患者id不为空") Long patientId) {
        baseService.agreeAgreement(patientId);
        return R.success();
    }

    @ApiOperation("患者完成入组")
    @PostMapping("/completeEnterGroup")
    public R<Boolean> completeEnterGroup(@RequestParam("patientId") @NotNull(message = "患者id不为空") Long patientId) {
        baseService.completeEnterGroup(patientId);
        return R.success();
    }

    @ApiOperation(value = "导出任务查询带数据范围分页列表查询")
    @PostMapping(value = "/exportPageWithScope")
    public R<IPage<Patient>> exportPageWithScope(@RequestBody @Validated PageParams<PatientPageDTO> params) {
        IPage<Patient> page = params.buildPage();
        PatientPageDTO userPage = params.getModel();
        String currentUserType = userPage.getCurrentUserType();
        Patient queryParam = BeanUtil.toBean(userPage, Patient.class);
        QueryWrap<Patient> wrap = handlerWrapper(queryParam, params);
        LbqWrapper<Patient> wrapper = wrap.lambda()
                .eq(Patient::getStatus, queryParam.getStatus())
                .eq(Patient::getServiceAdvisorId, queryParam.getServiceAdvisorId())
                .eq(Patient::getDoctorId, queryParam.getDoctorId())
                .like(Patient::getName, queryParam.getName())
                .select(SuperEntity::getId, Patient::getName, SuperEntity::getCreateTime, Patient::getServiceAdvisorName, Patient::getDoctorName);
        baseService.findPage(page, wrapper);
        if (StrUtil.isNotEmpty(currentUserType)) {
            BaseContextHandler.setUserType(currentUserType);
        }
        baseService.desensitization(page.getRecords());
        return R.success(page);
    }

    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "带数据范围分页列表查询")
    @PostMapping(value = "/pageWithScope")
    @SysLog(value = "'带数据范围分页列表查询:第' + #params?.current + '页, 显示' + #params?.size + '行'", response = false)
    public R<IPage<Patient>> pageWithScope(@RequestBody @Validated PageParams<PatientPageDTO> params) {
        IPage<Patient> page = params.buildPage();
        PatientPageDTO userPage = params.getModel();
        Patient queryParam = BeanUtil.toBean(userPage, Patient.class);
        QueryWrap<Patient> wrap = handlerWrapper(queryParam, params);
        LbqWrapper<Patient> wrapper = wrap.lambda()
                .eq(Patient::getStatus, queryParam.getStatus())
                .eq(Patient::getServiceAdvisorId, queryParam.getServiceAdvisorId())
                .eq(Patient::getDoctorId, queryParam.getDoctorId())
                .like(Patient::getName, queryParam.getName());
        baseService.findPage(page, wrapper);
        baseService.desensitization(page.getRecords());
        return R.success(page);
    }


    @Deprecated
    @ApiOperation(value = "初始化正常项目的患者的微信标签， 只使用一次。禁止频繁重复使用")
    @GetMapping(value = "/anno/initPatientTag")
    public R<String> initPatientTag() {
//        R<List<Tenant>> normalTenant = tenantApi.getAllNormalTenant();
//        List<Tenant> data = normalTenant.getData();
//        LbqWrapper<Patient> lbqWrapper = new LbqWrapper<>();
//        lbqWrapper.ne(Patient::getStatus, 2);
//        if (!CollectionUtils.isEmpty(data)) {
//            for (Tenant datum : data) {
//                String code = datum.getCode();
//                BaseContextHandler.setTenant(code);
//                List<Patient> patients = baseService.list(lbqWrapper);
//                for (Patient patient : patients) {
//                    baseService.createWeiXinTag(patient, code);
//                }
//            }
//        }
        return R.success("同步完成");
    }

    @ApiOperation(value = "新增会员趋势")
    @GetMapping(value = "/trend")
    public R<Map<String, Object>> trend(@RequestParam LocalDate startTime, @RequestParam LocalDate endTime) {
        Map<String, Object> retMap = new HashMap<>();

        List<Map<String, Object>> ret = new ArrayList<>();
        boolean isBefore = endTime.isBefore(startTime);
        if (isBefore) {
            return R.success(retMap);
        }
        Period bet = Period.between(startTime, endTime);
        boolean showInYear = bet.getMonths() > 1 || bet.getYears() > 0;
        Map<String, Object> m = userService.getDataScopeById(BaseContextHandler.getUserId());
        List<Long> orgIds = (List<Long>) m.get("orgIds");
        int selectedPatient = baseService.count(Wraps.<Patient>q().in("org_id", orgIds).between("create_time", startTime, endTime));
        int totalPatient = baseService.count(Wraps.<Patient>q().in("org_id", orgIds));
        retMap.put("selectedPatient", selectedPatient);
        retMap.put("totalPatient", totalPatient);
        List<Map<String, Object>> tmp;
        if (showInYear) {
            tmp = baseService.listMaps(Wraps.<Patient>q().select("MONTH(create_time) as createTime", "count(id) countNum")
                    .in("org_id", orgIds)
                    .between("create_time", startTime, endTime)
                    .groupBy("MONTH(create_time)"));
        } else {
            tmp = baseService.listMaps(Wraps.<Patient>q().select("DATE(create_time) as createTime", "count(id) countNum")
                    .in("org_id", orgIds)
                    .between("create_time", startTime, endTime)
                    .groupBy("DATE(create_time)"));
        }
        // 整合数据为字典结构
        Map<String, Object> tmpMap = new HashMap<>();
        for (Map<String, Object> t : tmp) {
            tmpMap.put(Convert.toStr(t.get("createTime")), t.get("countNum"));
        }
        // 按年份统计
        if (showInYear) {
            // 计算相差的月份
            long distance = ChronoUnit.MONTHS.between(startTime, endTime);
            Stream.iterate(startTime, d -> d.plusMonths(1)).limit(distance + 1).forEach(f -> {
                String thisMonth = Convert.toStr(f.getMonthValue());
                Map<String, Object> tm = MapUtil.of("name", thisMonth + "月");
                tm.put("value", tmpMap.get(thisMonth) == null ? 0 : tmpMap.get(thisMonth));
                ret.add(tm);
            });
            retMap.put("trend", ret);
            return R.success(retMap);
        }
        // 按月份统计
        long distance = ChronoUnit.DAYS.between(startTime, endTime);
        Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            String thisDay = Convert.toStr(f.toString());
            Map<String, Object> tm = MapUtil.of("name", thisDay);
            tm.put("value", tmpMap.get(thisDay) == null ? 0 : tmpMap.get(thisDay));
            ret.add(tm);
        });
        retMap.put("trend", ret);
        return R.success(retMap);
    }

    @ApiOperation(value = "男女比例&年龄分布")
    @GetMapping(value = "/sexDistribution")
    public R<Map<String, Object>> sexDistribution() {
        Map<String, Object> ret = new HashMap<>();

        Map<String, Object> m = userService.getDataScopeById(BaseContextHandler.getUserId());
        List<Long> orgIds = (List<Long>) m.get("orgIds");

        // 性别分布
        List<Map<String, Object>> sexDistribution = baseService.listMaps(Wrappers.<Patient>query()
                .select("IF(sex=0, '男', '女') as name", "count(*) as value")
                .groupBy("sex")
                .in(CollUtil.isNotEmpty(orgIds), "org_id", orgIds)
                .isNotNull("sex"));

        // 年龄分布
        List<Map<String, Object>> ageDistribution = baseService.listMaps(Wrappers.<Patient>query()
                .select("CASE\n" +
                        "\tWHEN DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( birthday ) ), '%Y' ) + 0 < 18 THEN \"18岁以下\" \n" +
                        "\tWHEN DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( birthday ) ), '%Y' ) + 0 < 25 THEN \"18~24岁\" \n" +
                        "\tWHEN DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( birthday ) ), '%Y' ) + 0 < 30 THEN \"25~30岁\" \n" +
                        "  WHEN DATE_FORMAT( FROM_DAYS( TO_DAYS( NOW() ) - TO_DAYS( birthday ) ), '%Y' ) + 0 < 50 THEN \"31~50岁\" \n" +
                        "\tELSE \"51岁以上\" \n" +
                        "\tEND AS 'age' ,\n" +
                        "\tcount( 1 ) AS 'value' ")
                .groupBy("age")
                .isNotNull("birthday")
                .in(CollUtil.isNotEmpty(orgIds), "org_id", orgIds));
        for (Map<String, Object> s : ageDistribution) {
            s.put("name", s.get("age"));
        }

        // 会员总数
        int total = baseService.count();
        // 本月新增
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayInMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        Integer thisMonthPatient = baseService.count(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds)
                .between(Patient::getCreateTime, firstDayInMonth.with(LocalTime.MIN), now));

        ret.put("thisMonthPatient", thisMonthPatient);
        ret.put("total", total);
        ret.put("sexDistribution", sexDistribution);
        ret.put("ageDistribution", ageDistribution);
        return R.success(ret);
    }

    @ApiOperation(value = "给患者更换医生")
    @GetMapping("/changeDoctor")
    public R<Boolean> changeDoctor(@RequestParam("patientId") Long patientId,
                                   @RequestParam("doctorId") Long doctorId) {

        Boolean changeDoctor = baseService.changeDoctor(patientId, doctorId);
        return R.success(changeDoctor);
    }


    @ApiOperation(value = "批量给患者更换医生")
    @PostMapping("/changeDoctorMore")
    public R<Boolean> changeDoctorMore(@RequestBody @Validated ChangeDoctorMore doctorMore) {

        Boolean changeDoctor = baseService.changeDoctorMore(doctorMore);
        return R.success(changeDoctor);

    }




    @ApiOperation("查询患者基本信息(id, Name, 头像, 备注, ImAccount)")
    @PostMapping("/findPatientBaseInfo")
    public R<IPage<Patient>> findPatientBaseInfo(@RequestBody @Validated PageParams<PatientPageDTO> params) {
        IPage<Patient> buildPage = params.buildPage();
        PatientPageDTO paramsModel = params.getModel();
        LbqWrapper<Patient> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.select(Patient::getId, Patient::getName, Patient::getAvatar,
                Patient::getRemark, Patient::getDoctorRemark, Patient::getImAccount,
                Patient::getNameFirstLetter);
        if (!StringUtils.isEmpty(paramsModel.getName())) {
            lbqWrapper.like(Patient::getName, paramsModel.getName());
        }
        if (Objects.nonNull(paramsModel.getServiceAdvisorId())) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, paramsModel.getServiceAdvisorId());
        }
        if (Objects.nonNull(paramsModel.getDoctorId())) {
            lbqWrapper.eq(Patient::getDoctorId, paramsModel.getDoctorId());
        }
        lbqWrapper.orderByAsc(Patient::getNameFirstLetter);
        IPage<Patient> patientIPage = baseService.page(buildPage, lbqWrapper);
        return R.success(patientIPage);
    }


    @ApiOperation("获取患者的基本信息")
    @GetMapping("getBaseInfoForStatisticsData/{patientId}")
    public R<Patient> getBaseInfoForStatisticsData(@PathVariable("patientId") Long patientId) {

        Patient patient = baseService.getOne(Wraps.<Patient>lbQ().eq(SuperEntity::getId, patientId)
                .select(SuperEntity::getId, Patient::getDoctorId, Patient::getServiceAdvisorId, Patient::getOrganId,
                        Patient::getDoctorName, Patient::getClassCode, Patient::getNursingTime, Patient::getCompleteEnterGroupTime));
        return R.success(patient);
    }


    @ApiOperation("批量查询患者的基本信息")
    @PostMapping("getBaseInfoForNursingPlan")
    public R<List<NursingPlanPatientBaseInfoDTO>> getBaseInfoForNursingPlan(@RequestBody NursingPlanPatientDTO nursingPlanPatientDTO) {

        BaseContextHandler.setTenant(nursingPlanPatientDTO.getTenantCode());
        List<Long> longList = nursingPlanPatientDTO.getIds();
        LbqWrapper<Patient> wrapper = Wraps.<Patient>lbQ().select(SuperEntity::getId, Patient::getName, Patient::getOpenId, Patient::getExamineCount, Patient::getAvatar, Patient::getMobile,
                Patient::getClassCode, Patient::getDoctorId, Patient::getServiceAdvisorId, Patient::getOrganId, Patient::getDoctorName, Patient::getImAccount, Patient::getDefaultDoctorPatient,
                Patient::getNursingTime, Patient::getCompleteEnterGroupTime, Patient::getIsCompleteEnterGroup)
                .in(SuperEntity::getId, longList);
        List<Patient> patients = baseService.list(wrapper);
        List<NursingPlanPatientBaseInfoDTO> infoDTOList = new ArrayList<>(patients.size());
        NursingPlanPatientBaseInfoDTO baseInfoDTO;
        for (Patient patient : patients) {
            baseInfoDTO = new NursingPlanPatientBaseInfoDTO();
            BeanUtils.copyProperties(patient, baseInfoDTO);
            baseInfoDTO.setId(patient.getId());
            infoDTOList.add(baseInfoDTO);
        }
        return R.success(infoDTOList);
    }

    @ApiOperation("修改患者的入组时间")
    @PostMapping("updatePatientCompleteEnterGroupTime")
    public R<Boolean> updatePatientCompleteEnterGroupTime(@RequestBody NursingPlanUpdatePatientDTO updatePatientDTO) {
        Long id = updatePatientDTO.getId();
        UpdateWrapper<Patient> updateWrapper = new UpdateWrapper<>();
        if (updatePatientDTO.getCompleteEnterGroupTime() != null) {
            updateWrapper.set("complete_enter_group_time", updatePatientDTO.getCompleteEnterGroupTime());
        }
        if (updatePatientDTO.getNursingTime() != null) {
            updateWrapper.set("nursing_time", updatePatientDTO.getNursingTime());
        }
        updateWrapper.eq("id", id);
        baseService.update(updateWrapper);
        return R.success(true);
    }

    @ApiOperation(value = "app使用的患者分页按首字母分页排序")
    @PostMapping("appPatientPageLetter")
    public R<IPage<cn.hutool.json.JSONObject>> appPatientPageLetter(@RequestBody PageParams<AppPatientPageDTO> params) {
        IPage<Patient> buildPage = params.buildPage();
        AppPatientPageDTO patientPageDTO = params.getModel();
        LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ();
        if (patientPageDTO.getDiagnosisId() != null) {
            lbqWrapper.eq(Patient::getDiagnosisId, patientPageDTO.getDiagnosisId());
        }
        if (patientPageDTO.getStatus() != null && patientPageDTO.getStatus() != -1) {
            lbqWrapper.eq(Patient::getStatus, patientPageDTO.getStatus());
        }
        if (patientPageDTO.getGroupId() != null) {
            lbqWrapper.apply(" doctor_id in (select doctor_id from u_user_doctor_group where group_id = "+ patientPageDTO.getGroupId() +")");
        }
        if (patientPageDTO.getDoctorId() != null) {
            lbqWrapper.eq(Patient::getDoctorId, patientPageDTO.getDoctorId());
        }
        if (patientPageDTO.getServiceAdvisorId() != null) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, patientPageDTO.getServiceAdvisorId());
        }
        if (patientPageDTO.getName() != null) {
            lbqWrapper.like(Patient::getName, patientPageDTO.getName());
        }
        if (patientPageDTO.getNameFirstLetter() != null) {
            lbqWrapper.like(Patient::getNameFirstLetter, patientPageDTO.getNameFirstLetter());
        }
        lbqWrapper.orderByAsc(Patient::getNameFirstLetter);
        lbqWrapper.select(SuperEntity::getId, Patient::getName, Patient::getAvatar, Patient::getImAccount,
                Patient::getRemark, Patient::getDiagnosisName, Patient::getDiagnosisId, Patient::getNameFirstLetter);
        buildPage = baseService.page(buildPage, lbqWrapper);
        List<Patient> records = buildPage.getRecords();
        IPage<cn.hutool.json.JSONObject> resultPage = new Page<>();
        resultPage.setSize(buildPage.getSize());
        resultPage.setTotal(buildPage.getTotal());
        resultPage.setCurrent(buildPage.getCurrent());
        resultPage.setPages(buildPage.getPages());
        if (CollUtil.isEmpty(records)) {
            return R.success(resultPage);
        }
        Map<String, List<Patient>>
                stringListMap = records.stream().collect(Collectors.groupingBy(Patient::getNameFirstLetter));
        // 按 大写 英文字母 表 从map 中获取分组后的数据
        List<cn.hutool.json.JSONObject> list = new ArrayList<>();
        cn.hutool.json.JSONObject jsonObject;
        for (String s :  LetterUtil.getInstance().ENGLISH_ALPHABET) {
            List<Patient> patientModelList = stringListMap.get(s);
            if (CollUtil.isNotEmpty(patientModelList)) {
                jsonObject = new cn.hutool.json.JSONObject();
                jsonObject.set("nameFirstLetter", s);
                jsonObject.set("values", patientModelList);
                list.add(jsonObject);
            }
        }
        resultPage.setRecords(list);
        return R.success(resultPage);
    }

    @ApiOperation(value = "app使用的患者分页查询2.4")
    @PostMapping("appPatientPage")
    public R<IPage<Patient>> appPatientPage(@RequestBody PageParams<AppPatientPageDTO> params) {
        IPage buildPage = params.buildPage();
        AppPatientPageDTO patientPageDTO = params.getModel();
        LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ();
        if (patientPageDTO.getDiagnosisId() != null) {
            lbqWrapper.eq(Patient::getDiagnosisId, patientPageDTO.getDiagnosisId());
        }
        if (patientPageDTO.getStatus() != null && patientPageDTO.getStatus() != -1) {
            lbqWrapper.eq(Patient::getStatus, patientPageDTO.getStatus());
        }
        if (patientPageDTO.getGroupId() != null) {
            lbqWrapper.apply(" doctor_id in (select doctor_id from u_user_doctor_group where group_id = "+ patientPageDTO.getGroupId() +")");
        }
        if (patientPageDTO.getDoctorId() != null) {
            lbqWrapper.eq(Patient::getDoctorId, patientPageDTO.getDoctorId());
        }
        if (patientPageDTO.getServiceAdvisorId() != null) {
            lbqWrapper.eq(Patient::getServiceAdvisorId, patientPageDTO.getServiceAdvisorId());
        }
        if (patientPageDTO.getName() != null) {
            lbqWrapper.like(Patient::getName, patientPageDTO.getName());
        }
        if (patientPageDTO.getNameFirstLetter() != null) {
            lbqWrapper.like(Patient::getNameFirstLetter, patientPageDTO.getNameFirstLetter());
        }
        if (patientPageDTO.getTagId() != null) {
            lbqWrapper.apply(" id in (select association_id from t_tag_association where tag_id = "+ patientPageDTO.getTagId() +")");
        }
        lbqWrapper.orderByDesc(SuperEntity::getCreateTime);
        lbqWrapper.select(SuperEntity::getId, Patient::getName, Patient::getAvatar, Patient::getImAccount, Patient::getRemark, Patient::getDiagnosisName, Patient::getDiagnosisId);
        buildPage = baseService.page(buildPage, lbqWrapper);
        return R.success(buildPage);
    }

    @ApiOperation(value = "医生使用的患者分页查询2.4")
    @PostMapping("doctorPatientPage")
    public R<IPage<Patient>> doctorPatientPage(@RequestBody PageParams<DoctorPatientPageDTO> params) {


        IPage buildPage = params.buildPage();
        DoctorPatientPageDTO patientPageDTO = params.getModel();
        LbqWrapper<Patient> lbqWrapper = Wraps.<Patient>lbQ();
        if (patientPageDTO.getDiagnosisId() != null) {
            lbqWrapper.eq(Patient::getDiagnosisId, patientPageDTO.getDiagnosisId());
        }
        if (patientPageDTO.getStatus() != null) {
            lbqWrapper.eq(Patient::getStatus, patientPageDTO.getStatus());
        }
        Doctor doctor = doctorService.getBaseDoctorAndImOpenById(patientPageDTO.getDoctorId());
        if (doctor!= null && doctor.getIndependence() != null && doctor.getIndependence().equals(1)) {
            lbqWrapper.eq(Patient::getDoctorId, patientPageDTO.getDoctorId());
        } else {
            if ("all".equals(patientPageDTO.getDimension())) {
                String sql = " doctor_id in (select doctor_id from u_user_doctor_group where group_id in " +
                        "(select group_id from u_user_doctor_group where doctor_id = "+ patientPageDTO.getDoctorId() +"))";
                lbqWrapper.apply(sql);
            } else if (patientPageDTO.getDoctorId() != null) {
                lbqWrapper.eq(Patient::getDoctorId, patientPageDTO.getDoctorId());
            }
        }
        if (!StringUtils.isEmpty(patientPageDTO.getName())) {
            lbqWrapper.like(Patient::getName, patientPageDTO.getName());
        }
        if (!StringUtils.isEmpty(patientPageDTO.getNameFirstLetter())) {
            lbqWrapper.like(Patient::getNameFirstLetter, patientPageDTO.getNameFirstLetter());
        }
        if (Objects.nonNull(patientPageDTO.getTagId())) {
            lbqWrapper.apply(" id in (select association_id from t_tag_association where tag_id = "+ patientPageDTO.getTagId() +")");
        }
        lbqWrapper.orderByDesc(SuperEntity::getCreateTime);
        lbqWrapper.select(SuperEntity::getId, Patient::getName, Patient::getAvatar, Patient::getImAccount, Patient::getDoctorId,
                Patient::getDoctorRemark, Patient::getDiagnosisName, Patient::getStatus,
                Patient::getDiagnosisId);
        buildPage = baseService.page(buildPage, lbqWrapper);
        return R.success(buildPage);
    }

    @ApiOperation("用户授权登录时，更新用户的微信信息")
    @PostMapping("anno/updateByOpenId")
    public R<Void> updateByOpenId(@RequestBody JSONObject wxOAuth2UserInfo) {
        log.info("updateByOpenId, {}", wxOAuth2UserInfo);
        WxOAuth2UserInfo wxOAuth2User = new WxOAuth2UserInfo();
        wxOAuth2User.setOpenid(wxOAuth2UserInfo.get("openid").toString());
        wxOAuth2User.setHeadImgUrl(wxOAuth2UserInfo.get("headImgUrl").toString());
        wxOAuth2User.setNickname(wxOAuth2UserInfo.get("nickname").toString());
        baseService.updateByOpenId(wxOAuth2User);
        return R.success(null);
    }

    @ApiOperation("通过ID查询患者详细信息")
    @PostMapping("findByIds")
    public R<List<Patient>> findByIds(@RequestBody List<Long> ids) {
        List<Patient> patients = baseService.listByIds(ids);
        return R.success(patients);
    }

    @ApiOperation("通过ID查询医生对患者的备注")
    @PostMapping("findDoctorPatientRemark")
    public R<Map<Long, String>> findDoctorPatientRemark(@RequestBody List<Long> ids) {
        Map<Long, String> patientInfo = baseService.findDoctorPatientRemark(ids);
        return R.success(patientInfo);
    }

    @ApiOperation("通过ID查询患者的基本信息")
    @PostMapping("findPatientBaseInfoByIds")
    public R<Map<Long, Patient>> findPatientBaseInfoByIds(@RequestBody List<Long> ids) {
        Map<Long, Patient> patientInfo = baseService.findPatientBaseInfoByIds(ids);
        return R.success(patientInfo);
    }


    @ApiOperation("通过ID查询医助对患者的备注")
    @PostMapping("findNursingPatientRemark")
    public R<Map<Long, String>> findNursingPatientRemark(@RequestBody List<Long> ids) {
        Map<Long, String> patientInfo = baseService.findNursingPatientRemark(ids);
        return R.success(patientInfo);
    }

    @ApiOperation("im账号匹配查询患者信息")
    @PostMapping("findPatientNameByImAccounts")
    public R<List<Patient>> findPatientByImAccounts(@RequestBody List<String> imAccounts) {
        List<Patient> patientList = baseService.list(Wraps.<Patient>lbQ()
                .in(Patient::getImAccount, imAccounts));
        return R.success(patientList);
    }

    @ApiOperation("患者im账号查询患者信息")
    @GetMapping("findPatientNameByImAccount/{imAccount}")
    public R<Patient> findPatientName(@PathVariable String imAccount) {
        Patient patient = baseService.getOne(Wraps.<Patient>lbQ()
                .select(SuperEntity::getId, Patient::getName, Patient::getDoctorId)
                .eq(Patient::getImAccount, imAccount)
                .last(" limit 0,1 "));
        return R.success(patient);
    }

    @ApiOperation(value = "为注册提醒-弃用")
    @Deprecated
    @PostMapping("unregisteredReminder")
    public R<String> unregisteredReminder() {


        baseService.unregisteredReminder();
        return R.success("");

    }

    /**
     * 批量查询没有unionId的患者
     */
    @ApiOperation(value = "批量更新患者unionId")
    @GetMapping("/batchUpdateUnionId")
    public R<Boolean> batchUpdateUnionId() {
        List<String> openIdList = new ArrayList<>();
        List<Patient> patients = baseService.list(Wraps.<Patient>lbQ().select(Patient::getOpenId)
                .ne(Patient::getStatus, Patient.PATIENT_NO_SUBSCRIBE)
                .isNull(Patient::getUnionId)
                .isNotNull(Patient::getOpenId));
        patients.forEach(patient -> openIdList.add(patient.getOpenId()));
        R<Tenant> tenantR = tenantApi.getByCode(BaseContextHandler.getTenant());
        String wxAppId = tenantR.getData().getWxAppId();
        if (CollUtil.isEmpty(openIdList)) {
            return R.success(true);
        }
        List<List<String>> listList = ListUtils.subList(openIdList, 99);
        for (List<String> strings : listList) {
            R<List<WxMpUser>> wxMpUserList = weiXinApi.batchGetUserInfo(new SyncUserUnionIdDTO().setAppId(wxAppId).setOpenIdList(strings));
            if (wxMpUserList.getIsError()) {
                continue;
            }
            wxMpUserList.getData().forEach(user -> {
                if (user.getUnionId() != null) {
                    baseService.update(Wraps.<Patient>lbU().set(Patient::getUnionId, user.getUnionId()).eq(Patient::getOpenId, user.getOpenId()));
                }
            });
        }
        return R.success(true);
    }

    @ApiOperation("查询医生下患者的im账号")
    @GetMapping("queryDoctorsPatientImAccount/{doctorId}")
    public R<List<Object>> queryDoctorsPatientImAccount(@PathVariable("doctorId") Long doctorId) {

        List<Object> objects = baseService.listObjs(Wraps.<Patient>lbQ().eq(Patient::getDoctorId, doctorId)
                .select(Patient::getImAccount));
        return R.success(objects);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "patientId", value = "患者ID"),
            @ApiImplicitParam(name = "exitChat", value = "0 加入聊天， 1 退出聊天")
    })
    @ApiOperation("医助设置退出聊天")
    @PutMapping("nursingExitChat")
    public R<Void> nursingExitChat(@RequestParam("patientId") Long patientId,
                                   @RequestParam("exitChat") Integer exitChat) {
        List<DictionaryItem> dictionaryItems = dictionaryItemService.list(Wraps.lbQ());
        Map<String, String> dictionaryMap = new HashMap<>();
        for (DictionaryItem item : dictionaryItems) {
            dictionaryMap.put(item.getCode(), item.getName());
        }
        baseService.nursingExitChat(patientId, exitChat, dictionaryMap);
        return R.success(null);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "patientId", value = "患者ID"),
            @ApiImplicitParam(name = "exitChat", value = "0 加入聊天， 1 退出聊天")
    })
    @ApiOperation("医生设置退出聊天")
    @PutMapping("doctorExitChat")
    public R<Void> doctorExitChat(@RequestParam("patientId") Long patientId, @RequestParam("exitChat") Integer exitChat) {
        List<DictionaryItem> dictionaryItems = dictionaryItemService.list(Wraps.lbQ());
        Map<String, String> dictionaryMap = new HashMap<>();
        for (DictionaryItem item : dictionaryItems) {
            dictionaryMap.put(item.getCode(), item.getName());
        }
        baseService.doctorExitChat(patientId, exitChat, dictionaryMap);
        return R.success(null);
    }



    /**
     * 医生查看患者所在的分组
     * @param doctorId
     * @param patientId
     * @return
     */
    @ApiOperation("医生查看患者所在的分组")
    @PutMapping("doctorSeePatientGroup")
    public R<List<String>> doctorSeePatientGroup(Long doctorId, Long patientId) {

        List<String> stringList = new ArrayList<>();
        Patient one = baseService.getOne(Wraps.<Patient>lbQ().eq(SuperEntity::getId, patientId).select(Patient::getDiagnosisName, SuperEntity::getId));
        if (Objects.nonNull(one)) {
            if (StrUtil.isNotEmpty(one.getDiagnosisName())) {
                stringList.add(one.getDiagnosisName());
            }
        }

        List<DoctorCustomGroup> customGroups = doctorCustomGroupService.list(Wraps.<DoctorCustomGroup>lbQ().eq(DoctorCustomGroup::getDoctorId, doctorId)
                .apply(" id in (select doctor_custom_group_id FROM u_user_doctor_custom_group_patient where patient_id = '" + patientId + "') "));
        if (CollUtil.isNotEmpty(customGroups)) {
            for (DoctorCustomGroup group : customGroups) {
                stringList.add(group.getGroupName());
            }
        }

        return R.success(stringList);
    }

    /**
     * 医助查询患者所在的分组
     * @param patientId
     * @return
     */
    @ApiOperation("医助查询患者所在的分组")
    @GetMapping("nursingSeePatientGroup")
    public R<List<String>> nursingSeePatientGroup(Long nursingId, Long patientId) {

        List<String> stringList = new ArrayList<>();
        Patient one = baseService.getOne(Wraps.<Patient>lbQ().eq(SuperEntity::getId, patientId).select(Patient::getDiagnosisName, SuperEntity::getId));
        if (Objects.nonNull(one)) {
            if (StrUtil.isNotEmpty(one.getDiagnosisName())) {
                stringList.add(one.getDiagnosisName());
            }
        }
        List<CustomGrouping> groupList = customGroupingService.findPatientInGroupList(patientId, nursingId);
        if (CollUtil.isNotEmpty(groupList)) {
            for (CustomGrouping group : groupList) {
                stringList.add(group.getName());
            }
        }

        return R.success(stringList);
    }



    @ApiOperation("医生查询自己关闭沟通的患者")
    @PutMapping("doctorExitChatPatientList")
    public R<List<Long>> doctorExitChatPatientList(@RequestParam("doctorId") Long doctorId) {

        List<Patient> patientList = baseService.list(Wraps.<Patient>lbQ().like(Patient::getDoctorExitChatListJson, doctorId)
                .select(SuperEntity::getId, Patient::getDoctorExitChatListJson));
        if (CollUtil.isEmpty(patientList)) {
            return R.success(new ArrayList<>());
        }
        List<Long> patientIds = patientList.stream().map(SuperEntity::getId).collect(Collectors.toList());
        return R.success(patientIds);

    }


    @ApiOperation("手机号校验患者是否存在")
    @GetMapping("anno/checkMobile")
    public R<Boolean> checkOpenId(@RequestParam(value = "phone") @NotEmpty(message = "phone不能为空") String phone) {
        String tenant = BaseContextHandler.getTenant();
        BizAssert.notEmpty(tenant, "项目编码不能为空");
        int count = 0;
        try {
            count = baseService.count(Wraps.<Patient>lbQ()
                    .eq(Patient::getMobile, EncryptionUtil.encrypt(phone)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (count > 0) {
            return R.success(true);
        }
        return R.success(false);
    }


    @ApiOperation("修改密码")
    @PutMapping("updatePassword")
    public R<Boolean> updatePassword(@RequestBody @Validated DoctorUpdatePasswordDTO updatePassword) {

        Long id = updatePassword.getId();
        String password = updatePassword.getPassword();

        Long userId = BaseContextHandler.getUserId();
        if (!userId.equals(id)) {
            throw new BizException(I18nUtils.getMessage("user_not_exist"));
        }

        Patient patient = baseService.getById(id);
        patient.setPassword(SecureUtil.md5(password));
        baseService.updateById(patient);
        return R.success(true);

    }



    @ApiOperation("重置密码")
    @PostMapping("anno/resetPassword")
    public R<Boolean> resetPassword(@RequestBody @Validated DoctorResetPasswordDTO userResetPassword) {

        String mobile = userResetPassword.getUserMobile();
        String smsCode = userResetPassword.getSmsCode();
        String password = userResetPassword.getPassword();

        mobile = mobile.trim();

        Patient user = null;
        try {
            user = baseService.getOne(Wraps.<Patient>lbQ()
                    .eq(Patient::getMobile, EncryptionUtil.encrypt(mobile))
                    .last(" limit 0,1 "));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            throw new BizException(I18nUtils.getMessage("user_not_exist"));
        }
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setMobile(mobile);
        verificationCodeDTO.setCode(smsCode);
        verificationCodeDTO.setClearKey(true);
        verificationCodeDTO.setType(VerificationCodeType.RESET_PASSWORD);
        R<Boolean> verification = verificationCodeApi.verification(verificationCodeDTO);
        if (verification.getIsSuccess() && verification.getData()) {
            user.setPassword(SecureUtil.md5(password));
            baseService.updateById(user);
        }
        return R.success(true);

    }

    @Autowired
    VerificationCodeApi verificationCodeApi;


    @ApiOperation("患者注册或登录")
    @PostMapping("anno/patient/register")
    public R<Patient> register(@RequestBody PatientRegister patientRegister) {
        String phone = patientRegister.getPhone();
        int type = patientRegister.getType();

        Patient patient = null;
        if (StrUtil.isNotEmpty(phone)) {
            try {
                patient = baseService.getOne(Wraps.<Patient>lbQ()
                        .eq(Patient::getMobile, EncryptionUtil.encrypt(phone))
                        .last(" limit 0,1 "));
                if (type == 1) {
                    if (patient != null) {
                        return R.fail("手机号已存在");
                    }
                    boolean validString = PasswordUtils.isValidString(patientRegister.getPassword());
                    if (!validString) {
                        return R.fail("密码需要符合字母+数字长度8位以上要求");
                    }
                    patient = baseService.registerPatient(patientRegister);
                }
                if (patient != null) {
                    if (patient.getPassword().equals(SecureUtil.md5(patientRegister.getPassword()))) {
                        return R.success(patient);
                    } else {
                        return R.fail("密码不正确");
                    }
                }
            } catch (Exception e) {
                log.error("encrypt error : {}", phone);
            }
        } else {
            return R.fail("手机号不能都位空");
        }
        if (patient == null) {
            return R.fail("手机号用户不存在");
        }
        return R.success(patient);
    }



}
