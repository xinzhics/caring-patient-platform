package com.caring.sass.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.mybaits.EncryptionUtil;
import com.caring.sass.common.utils.PasswordUtils;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.msgs.dto.ChatGroupSendDto;
import com.caring.sass.msgs.entity.Chat;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.user.dao.DoctorMapper;
import com.caring.sass.user.dao.NursingStaffMapper;
import com.caring.sass.user.dao.NursingStaffQueryMapper;
import com.caring.sass.user.dto.*;
import com.caring.sass.user.entity.*;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.user.service.UserBizService;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.utils.SpringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.caring.sass.utils.BizAssert.isFalse;


/**
 * <p>
 * 前端控制器
 * 用户-医助
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/nursingStaff")
@Api(value = "NursingStaff", tags = "用户-医助")
//@PreAuth(replace = "nursingStaff:")
public class NursingStaffController extends SuperController<NursingStaffService, Long, NursingStaff, NursingStaffPageDTO, NursingStaffSaveDTO, NursingStaffUpdateDTO> {

    private final UserBizService userBizService;

    private final PatientService patientService;

    private final DoctorService doctorService;

    private final OrgService orgService;

    private final UserService userService;

    @Autowired
    TenantApi tenantApi;


    public NursingStaffController(UserBizService userBizService, PatientService patientService, DoctorService doctorService, OrgService orgService, UserService userService) {
        this.userBizService = userBizService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.orgService = orgService;
        this.userService = userService;
    }

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<NursingStaff> nursingStaffList = list.stream().map((map) -> {
            NursingStaff nursingStaff = NursingStaff.builder().build();
            //TODO 请在这里完成转换
            return nursingStaff;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(nursingStaffList));
    }


    /**
     * @return com.caring.sass.base.R<com.caring.sass.user.entity.Doctor>
     * @Author yangShuai
     * @Description 创建默认的医生 TODO: 需要确认是否有正确的 租户信息 注入
     * @Date 2020/9/27 10:15
     */
    @PostMapping("createNursingStaff")
    public R<NursingStaff> createNursingStaff(@RequestBody NursingStaff nursingStaff) {
        NursingStaff serviceDoctor = baseService.createNursingStaff(nursingStaff);
        return R.success(serviceDoctor);
    }

    /**
     * @return com.caring.sass.base.R<com.caring.sass.user.entity.Doctor>
     * @Author yangShuai
     * @Description 根据机构 创建 机构下的 护理专员，医生，小组
     * @Date 2020/9/27 10:15
     */
    @PostMapping("createDefaultUser/{domain}/{projectName}")
    public R<Boolean> createDefaultUser(@RequestBody Org org, @PathVariable("domain") String domain, @PathVariable("projectName") String projectName) {
        boolean defaultUser = userBizService.createDefaultUser(org, domain, projectName);
        return R.success(defaultUser);
    }


    @ApiOperation("app注册")
    @PostMapping("anno/nursingStaff/register")
    public R<NursingStaff> register(@RequestBody NursingStaffRegister nursingStaffRegister) {
        String phone = nursingStaffRegister.getPhone();
        NursingStaff s = baseService.getByMobile(phone);
        if (s != null) {
            return R.fail("手机号已注册");
        }
        NursingStaff nursingStaff = new NursingStaff();
        nursingStaff.setMobile(phone);
        nursingStaff.setLoginName(phone);
        nursingStaff.setPassword(nursingStaffRegister.getPassword());

        boolean validString = PasswordUtils.isValidString(nursingStaffRegister.getPassword());
        BizAssert.isTrue(validString, "密码需要符合字母+数字长度8位以上要求");
        baseService.createNursingStaff(nursingStaff);

        return R.success(nursingStaff);
    }


    @ApiOperation("绑定机构")
    @PostMapping("anno/bindOrg")
    public R<NursingStaff> bindOrg(@RequestBody NursingStaffBindOrg nursingStaffBindOrg) {

        NursingStaff nursingStaff = baseService.bindOrg(nursingStaffBindOrg);

        return R.success(nursingStaff);

    }


    @ApiOperation("重置密码")
    @PostMapping("anno/resetPassword")
    public R<NursingStaff> resetPassword(@RequestBody NursingStaffResetPw nursingStaffResetPw) {

        NursingStaff nursingStaff = baseService.resetPassword(nursingStaffResetPw);

        return R.success(nursingStaff);

    }

    @ApiOperation("修改头像，姓名")
    @PostMapping("/updateInfo")
    public R<NursingStaff> updateInfo(@RequestBody NursingStaffBindOrg nursingStaffBindOrg) {
        Long nursingStaffId = nursingStaffBindOrg.getId();
        String staffName = nursingStaffBindOrg.getName();
        NursingStaff nursingStaff = new NursingStaff();
        nursingStaff.setAvatar(nursingStaffBindOrg.getAvatar());
        if (StrUtil.isNotEmpty(staffName)) {
            nursingStaff.setName(staffName);
        }
        nursingStaff.setId(nursingStaffId);
        baseService.updateById(nursingStaff);
        if (StrUtil.isNotEmpty(staffName)) {
            baseService.changeName(nursingStaffId, staffName);
        }
        return R.success(nursingStaff);
    }


    @ApiOperation("旧密码修改新密码")
    @PostMapping("anno/updatePassword")
    public R<NursingStaff> updatePassword(@RequestBody NursingStaffFindPw nursingStaffFindPw) {
        NursingStaff nursingStaff = baseService.updatePassword(nursingStaffFindPw);
        return R.success(nursingStaff);

    }

    @ApiOperation("手机号是否已经注册")
    @GetMapping("anno/existNursingStaff/{mobile}")
    public R<NursingStaff> existNursingStaff(@PathVariable(value = "mobile", required = false) String mobile) {
        NursingStaff nursingStaff = baseService.getByMobile(mobile);
        if (Objects.nonNull(nursingStaff)) {
            return R.success(nursingStaff);
        }
        return R.fail("未注册");
    }

    @ApiOperation("使用手机号查找专员")
    @GetMapping("/getNursingStaff/{mobile}")
    public R<NursingStaff> getNursingStaff(@PathVariable(value = "mobile", required = false) String mobile) {
        NursingStaff nursingStaff = baseService.getByMobile(mobile);
        if (Objects.nonNull(nursingStaff)) {
            return R.success(nursingStaff);
        }
        return R.fail("未找到用户");
    }

    @ApiOperation("发送消息")
    @PostMapping("sendChat")
    public R<List<Chat>> sendChat(@RequestBody Chat chat) {
        List<Chat> chatList = baseService.sendChatToWeiXin(chat, 0);
        return R.success(chatList);
    }


    @ApiOperation("系统以医助身份发送im提示消息")
    @PutMapping("sendPatientImRemind")
    public R<Boolean> sendPatientImRemind(@RequestParam("tenantCode") String tenantCode,
                                          @RequestParam("receiverImAccount") String receiverImAccount) {

        BaseContextHandler.setTenant(tenantCode);
        baseService.sendPatientImRemind(receiverImAccount);
        return R.success(true);
    }


    @ApiOperation("群发消息")
    @PostMapping("sendGroupChat")
    public R<List<Chat>> sendGroupChat(@RequestBody Chat chat) {
         baseService.sendMoreChatToWeiXin(chat);
        return R.success(new ArrayList<>());
    }


    @ApiOperation("获取患者的聊天小组")
    @GetMapping("getPatientImGroup")
    public R<List<ImGroupUser>> getPatientImGroup(@RequestParam("patientId") Long patientId) {

        List<ImGroupUser> imGroupUser = patientService.getImGroupUser(patientId);
        return R.success(imGroupUser);

    }

    @ApiOperation("获取聊天小组详细成员2.4")
    @GetMapping("getPatientGroupDetail")
    public R<ImGroupDetail> getPatientGroupDetail(@RequestParam("patientId") Long patientId,
                                                  @RequestParam("userId") Long userId) {

        ImGroupDetail imGroupUser = patientService.getImGroupDetail(patientId, userId, null);
        return R.success(imGroupUser);

    }

    @Override
    public void handlerResult(IPage<NursingStaff> page) {
        if (CollUtil.isEmpty(page.getRecords())) {
            return;
        }

        // 填充会员和医生的人数
        page.getRecords().forEach(
                nursingStaff -> {
                    Long staffId = nursingStaff.getId();
                    nursingStaff.setDoctorCount(doctorService.countDoctorByNursing(staffId));
                    nursingStaff.setPatientCount(patientService.countPatientNoStatus(staffId, UserType.UCENTER_NURSING_STAFF));
                }
        );

    }

    @Override
    public R<NursingStaff> get(Long id) {
        NursingStaff nursingStaff = getBaseService().getById(id);
        if (Objects.isNull(nursingStaff)) {
            return success(null);
        }
        // 完成注册
        Integer completeEnterGroup = patientService.count(Wraps.<Patient>lbQ().in(Patient::getServiceAdvisorId, id)
                .eq(Patient::getStatus, 1));
        nursingStaff.setRPatientCount(completeEnterGroup);
        return success(nursingStaff);
    }


    @ApiOperation("绑定OpenId")
    @PutMapping("bindOpenId")
    public R<Boolean> bindOpenId(@RequestParam(name = "id") Long id,
                                 @RequestParam(name = "openId") String openId) {

        // 医助绑定openId之前要先吧openId所在的其他医助的openId置空
        List<NursingStaff> nursingStaffs = baseService.list(Wraps.<NursingStaff>lbQ().eq(NursingStaff::getOpenId, openId).ne(SuperEntity::getId, id));
        if (CollUtil.isNotEmpty(nursingStaffs)) {
            for (NursingStaff staff : nursingStaffs) {
                staff.setOpenId("");
                baseService.updateById(staff);
            }
        }
        NursingStaff nursingStaff = baseService.getById(id);
        nursingStaff.setOpenId(openId);
        nursingStaff.setWxStatus(1);
        baseService.updateById(nursingStaff);

        return R.success(true);
    }

    @ApiOperation("解绑OpenId")
    @PutMapping("unbindOpenId")
    public R<Boolean> unbindOpenId(@RequestParam(name = "id") Long id) {

        NursingStaff nursingStaff = baseService.getById(id);
        nursingStaff.setOpenId("");
        nursingStaff.setWxStatus(3);
        baseService.updateById(nursingStaff);
        return R.success(true);
    }


    @ApiOperation("设置微信模版消息接收状态")
    @PutMapping("setImWxTemplateStatus")
    public R<Boolean> setImWxTemplateStatus(@RequestParam(name = "id") Long id,
                                            @RequestParam(name = "imWxTemplateStatus") Integer imWxTemplateStatus) {

        NursingStaff nursingStaff = new NursingStaff();
        nursingStaff.setId(id);
        nursingStaff.setImWxTemplateStatus(imWxTemplateStatus);
        baseService.updateById(nursingStaff);
        return R.success(true);
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
    public R<IPage<NursingStaff>> pageWithScope(@RequestBody @Validated PageParams<NursingStaffPageDTO> params) {
        IPage<NursingStaff> page = params.buildPage();
        NursingStaffPageDTO userPage = params.getModel();
        NursingStaff queryParam = BeanUtil.toBean(userPage, NursingStaff.class);
        QueryWrap<NursingStaff> wrap = handlerWrapper(queryParam, params);

        List<Long> childIds = new ArrayList<>();
        Long organId = userPage.getOrganId();
        if (organId != null) {
            List<Org> orgList = orgService.findChildren(CollUtil.list(false, organId));
            orgList.forEach(s -> childIds.add(s.getId()));
            childIds.add(organId);
        }
        LbqWrapper<NursingStaff> wrapper = wrap.lambda()
                .like(NursingStaff::getName, queryParam.getName())
                .in(CollUtil.isNotEmpty(childIds), NursingStaff::getOrganId, childIds);
        try {
            wrapper.like(NursingStaff::getMobile, EncryptionUtil.encrypt(queryParam.getMobile()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        baseService.findPage(page, wrapper);
        if (CollUtil.isEmpty(page.getRecords())) {
            return R.success(page);
        }
        String currentUserType = userPage.getCurrentUserType();
        if (StrUtil.isNotEmpty(currentUserType)) {
            BaseContextHandler.setUserType(currentUserType);
        }
        baseService.desensitization(page.getRecords());
        Boolean noNeedCountPatient = userPage.getNoNeedCountPatient();
        if (noNeedCountPatient != null && noNeedCountPatient) {
            return R.success(page);
        }

        // 填充会员和医生的人数
        page.getRecords().forEach(
                nursingStaff -> {
                    Long staffId = nursingStaff.getId();
                    nursingStaff.setDoctorCount(doctorService.countDoctorByNursing(staffId));
                    long status = patientService.countPatientNoStatus(staffId, UserType.UCENTER_NURSING_STAFF);
                    nursingStaff.setPatientCount(status);
                }
        );

        return R.success(page);
    }


    @ApiOperation(value = "护理专员新增会员趋势")
    @GetMapping(value = "/patientNewTrend")
    public R<List<Map<String, Object>>> patientNewTrend(@RequestParam(name = "nursingStaffId") Long nursingStaffId,
                                                        @RequestParam LocalDate startTime, @RequestParam LocalDate endTime) {
        List<Map<String, Object>> ret = new ArrayList<>();
        boolean isBefore = endTime.isBefore(startTime);
        if (isBefore) {
            return R.success(ret);
        }
        Period bet = Period.between(startTime, endTime);
        boolean showInYear = bet.getMonths() > 1 || bet.getYears() > 0;
        List<Map<String, Object>> tmp;
        if (showInYear) {
            tmp = patientService.listMaps(Wraps.<Patient>q().select("MONTH(create_time) as createTime", "count(id) countNum")
                    .eq("service_advisor_id", nursingStaffId)
                    .between("create_time", startTime, endTime)
                    .groupBy("MONTH(create_time)"));
        } else {
            tmp = patientService.listMaps(Wraps.<Patient>q().select("DATE(create_time) as createTime", "count(id) countNum")
                    .eq("service_advisor_id", nursingStaffId)
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
                Map<String, Object> tm = MapUtil.of("name", thisMonth+"月");
                tm.put("value", tmpMap.get(thisMonth) == null ? 0 : tmpMap.get(thisMonth));
                ret.add(tm);
            });
            return R.success(ret);
        }
        // 按月份统计
        long distance = ChronoUnit.DAYS.between(startTime, endTime);
        Stream.iterate(startTime, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> {
            String thisDay = Convert.toStr(f.toString());
            Map<String, Object> tm = MapUtil.of("name", thisDay);
            tm.put("value", tmpMap.get(thisDay) == null ? 0 : tmpMap.get(thisDay));
            ret.add(tm);
        });
        return R.success(ret);
    }

    @ApiOperation(value = "医生助理&服务会员")
    @GetMapping(value = "/statistics")
    public R<Map<String, Object>> sexDistribution() {
        Map<String, Object> ret = new HashMap<>();
        Map<String, Object> m = userService.getDataScopeById(BaseContextHandler.getUserId());
        List<Long> orgIds = (List<Long>) m.get("orgIds");

        // 医生助理总数
        int total = baseService.count(Wraps.<NursingStaff>lbQ().in(NursingStaff::getOrganId, orgIds));
        // 服务的会员数
        int patientCount = patientService.count(Wraps.<Patient>lbQ().in(Patient::getOrganId, orgIds).isNotNull(Patient::getServiceAdvisorId));
        ret.put("patientCount", patientCount);
        ret.put("total", total);
        return R.success(ret);
    }

    @Override
    public R<Boolean> handlerDelete(List<Long> longs) {
        // 判断是否有医生，患者，若存在则提示不能删除
        if (CollUtil.isEmpty(longs)) {
            return R.success();
        }
        int patientNum = patientService.count(Wraps.<Patient>lbQ().in(Patient::getServiceAdvisorId, longs));
        int doctorNum = doctorService.count(Wraps.<Doctor>lbQ().in(Doctor::getNursingId, longs));
        // 禁止删除
        boolean banRemove = patientNum > 0 || doctorNum > 0;
        if (banRemove) {
            return R.fail("专员存在所属用户，禁止删除");
        }
        return R.successDef();
    }


    @ApiOperation(value = "将专员的下小组，医生，患者变更到新专员下")
    @GetMapping("/changeBelongingToNursingId")
    public R<Boolean> changeBelongingToNursingId(Long formNursingId, Long toNursingId) {


        Boolean change = baseService.changeBelongingToNursingId(formNursingId, toNursingId);
        return R.success(change);
    }

    @ApiOperation(value = "更换医助的机构")
    @GetMapping("/changeNursingOrganId")
    public R<Boolean> changeNursingOrganId(Long nursingId, Long organId) {
        Boolean change = baseService.changeNursingOrganId(nursingId, organId);
        return R.success(change);
    }



    @ApiOperation("查询app的小组和医生2.4")
    @GetMapping(value = "/getAppDoctor")
    public R<AppMyDoctorDto> getAppDoctor(@RequestParam("nursingId") Long nursingId) {

        AppMyDoctorDto doctorDto = baseService.getAppDoctor(nursingId);
        return R.success(doctorDto);
    }

    @ApiOperation("更新机构下用户的表机构名称({orgId: '', orgName: ''})")
    @PostMapping(value = "/updateUserOrgName")
    public R<Boolean> updateUserOrgName(@RequestBody JSONObject jsonObject) {

        Object orgId = jsonObject.get("orgId");
        Object orgName = jsonObject.get("orgName");
        if (orgId == null || orgName == null) {
            return R.success(true);
        }
        baseService.updateUserOrgName(Long.parseLong(orgId.toString()), orgName.toString());
        return R.success(true);
    }

    @ApiOperation("查询项目下专员的Id")
    @PostMapping(value = "/getNursingStaffIds")
    public R<IPage<NursingStaff>> getNursingStaffIds(@RequestBody PageParams<NursingStaffPageDTO> pageParams) {

        IPage<NursingStaff> buildPage = pageParams.buildPage();
        LbqWrapper<NursingStaff> wrapper = Wraps.<NursingStaff>lbQ().select(SuperEntity::getId, NursingStaff::getName);
        baseService.page(buildPage,wrapper);
        return R.success(buildPage);
    }


    @ApiOperation("查询专员所在机构及下属机构id")
    @GetMapping(value = "/getNursingOrgIds")
    public R<List<Long>> getNursingOrgIds(@RequestParam("nursingId") Long nursingId) {

        return R.success(baseService.getNursingOrgIds(nursingId));
    }

    @ApiOperation("统计医助，不保护默认")
    @GetMapping(value = "/countUser")
    public R<Integer> countUser() {
        String tenant = BaseContextHandler.getTenant();
        R<Tenant> tenantR = tenantApi.getByCode(tenant);
        Tenant data = tenantR.getData();
        if (Objects.isNull(data)) {
            int count = baseService.count(Wraps.<NursingStaff>lbQ());
            return R.success(count);
        }
        @Length(max = 50, message = "域名长度不能超过50") String domainName = data.getDomainName();
        String string = domainName + Constant.SERVICE_LOGIN_NAME_SUFFIX;
        String encrypt;
        try {
            encrypt = EncryptionUtil.encrypt(string);
        } catch (Exception e) {
            encrypt = string;
        }
        int count = baseService.count(Wraps.<NursingStaff>lbQ().ne(NursingStaff::getLoginName, encrypt));
        return R.success(count);
    }


}
