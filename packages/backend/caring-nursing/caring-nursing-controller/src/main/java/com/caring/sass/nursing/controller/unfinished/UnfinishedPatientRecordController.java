package com.caring.sass.nursing.controller.unfinished;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dto.traceInto.AppTracePlanList;
import com.caring.sass.nursing.dto.unfinished.*;
import com.caring.sass.nursing.entity.unfinished.UnfinishedPatientRecord;
import com.caring.sass.nursing.service.unfinished.UnfinishedPatientRecordService;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.security.annotation.PreAuth;
import com.caring.sass.user.entity.Doctor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


/**
 * <p>
 * 前端控制器
 * 未完成推送的患者记录
 * </p>
 *
 * @author 杨帅
 * @date 2024-05-27
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/unfinishedPatientRecord")
@Api(value = "UnfinishedPatientRecord", tags = "未完成推送的患者记录")
@PreAuth(replace = "unfinishedPatientRecord:")
public class UnfinishedPatientRecordController extends SuperController<UnfinishedPatientRecordService, Long, UnfinishedPatientRecord, UnfinishedPatientRecordPageDTO, UnfinishedPatientRecordSaveDTO, UnfinishedPatientRecordUpdateDTO> {


    @Autowired
    RedisTemplate redisTemplate;


    @GetMapping("getUnFinishedPlanList")
    @ApiOperation("获取未完成随访计划列表")
    public R<List<AppTracePlanList>> getAppTracePlanList(@RequestParam("nursingId") Long nursingId) {

        List<AppTracePlanList> appTracePlanLists = baseService.getAppUnFinishedPlanList(nursingId);
        return R.success(appTracePlanLists);

    }


    @PostMapping("countHandleNumber")
    @ApiOperation("统计未处理已处理数量")
    public R<JSONObject> countHandleNumber(@RequestBody UnfinishedQuery model) {
        QueryWrapper<UnfinishedPatientRecord> queryWrapper = Wrappers.<UnfinishedPatientRecord>query();
        queryWrapper.select("handle_status as handleStatus", "count(*) as total");
        queryWrapper.eq("unfinished_form_setting_id", model.getUnFinishedSettingId());
        if (model.getStartDate() != null && model.getEndDate() != null) {
            queryWrapper.between("remind_time", model.getStartDate().atStartOfDay(), model.getEndDate().atTime(LocalTime.MAX));
        }
        if (model.getDoctorIds() != null && !model.getDoctorIds().isEmpty()) {
            queryWrapper.in("doctor_id", model.getDoctorIds());
        }
        if (model.getSeeStatus() != null) {
            queryWrapper.eq("see_status", model.getSeeStatus());
        }
        queryWrapper.eq("nursing_id", model.getNursingId());
        queryWrapper.eq("delete_flag", CommonStatus.NO);
        queryWrapper.eq("clear_status", CommonStatus.NO);
        Long nursingId = model.getNursingId();
        String patientName = model.getPatientName();
        String tenant = BaseContextHandler.getTenant();
        boolean andSql = false;
        String sql = "select id from u_user_patient where tenant_code = '"+tenant+"' and service_advisor_id = '"+nursingId+"'";
        if (StrUtil.isNotBlank(patientName)) {
            andSql = true;
            sql += " and name like '%" + patientName + "%'";
        }
        if (Objects.nonNull(model.getPatientFollowStatus())) {
            andSql = true;
            if (model.getPatientFollowStatus().equals(1)) {
                sql += " and status_ in (0,1) ";
            }
            if (model.getPatientFollowStatus().equals(2)) {
                sql += " and status_ = 2 ";
            }
        }
        if (andSql) {
            queryWrapper.apply(true, "patient_id in (" + sql + ")");
        }
        queryWrapper.groupBy("handle_status");
        List<Map<String, Object>> mapList = baseService.listMaps(queryWrapper);
        Map<Integer, Integer> unFinishedMap = new HashMap<>();
        if (CollUtil.isNotEmpty(mapList)) {
            for (Map<String, Object> objectMap : mapList) {
                Object handleStatus = objectMap.get("handleStatus");
                Object total = objectMap.get("total");
                if (total != null && handleStatus != null) {
                    unFinishedMap.put(Integer.parseInt(handleStatus.toString()), Integer.parseInt(total.toString()));
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("noHandlePatientNumber", unFinishedMap.get(0));
        jsonObject.put("handlePatientNumber", unFinishedMap.get(1));
        return R.success(jsonObject);

    }

    @Autowired
    DoctorApi doctorApi;


    @GetMapping("queryDoctorHistory")
    @ApiOperation("历史查询医生记录")
    public R<List<Doctor>> queryDoctorHistory(Long nursingId) {

        Set set = redisTemplate.opsForZSet().reverseRange("un_finished_nursing_search_doctor_history:" + nursingId, 0, 3);
        List<Long> doctorIds = new ArrayList<>();
        for (Object o : set) {
            doctorIds.add(Long.parseLong(o.toString()));
        }
        if (CollUtil.isNotEmpty(doctorIds)) {
            R<List<Doctor>> doctorListR = doctorApi.listByIds(doctorIds);
            List<Doctor> doctorList = doctorListR.getData();
            return R.success(doctorList);
        } else {
            return R.success(new ArrayList<>());
        }

    }



    @PostMapping("appPage")
    @ApiOperation("app未处理已处理数据列表")
    public R<IPage<UnfinishedListResult>> appPage(@RequestBody @Validated PageParams<UnfinishedQuery> pageParams) {

        IPage<UnfinishedPatientRecord> page = pageParams.buildPage();
        UnfinishedQuery model = pageParams.getModel();
        LbqWrapper<UnfinishedPatientRecord> lbqWrapper = Wraps.<UnfinishedPatientRecord>lbQ();
        lbqWrapper.eq(UnfinishedPatientRecord::getNursingId, model.getNursingId());
        lbqWrapper.eq(UnfinishedPatientRecord::getUnfinishedFormSettingId, model.getUnFinishedSettingId());
        lbqWrapper.eq(UnfinishedPatientRecord::getHandleStatus, model.getHandleStatus());
        lbqWrapper.eq(UnfinishedPatientRecord::getClearStatus, CommonStatus.NO);
        if (model.getDoctorIds() != null && !model.getDoctorIds().isEmpty()) {
            lbqWrapper.in(UnfinishedPatientRecord::getDoctorId, model.getDoctorIds());
            for (Long doctorId : model.getDoctorIds()) {
                redisTemplate.opsForZSet().add("un_finished_nursing_search_doctor_history:" + model.getNursingId(), doctorId.toString(), new Date().getTime());
            }
        }
        if (model.getSeeStatus() != null) {
            lbqWrapper.eq(UnfinishedPatientRecord::getSeeStatus, model.getSeeStatus());
        }
        if (model.getStartDate() != null && model.getEndDate() != null) {
            lbqWrapper.between(UnfinishedPatientRecord::getRemindTime, model.getStartDate().atStartOfDay(), model.getEndDate().atTime(LocalTime.MAX));
        }
        Long nursingId = model.getNursingId();
        String patientName = model.getPatientName();
        String tenant = BaseContextHandler.getTenant();
        boolean andSql = false;
        String sql = "select id from u_user_patient where tenant_code = '"+tenant+"' and service_advisor_id = '"+nursingId+"'";
        if (StrUtil.isNotBlank(patientName)) {
            andSql = true;
            sql += " and name like '%" + patientName + "%'";
        }
        if (Objects.nonNull(model.getPatientFollowStatus())) {
            andSql = true;
            if (model.getPatientFollowStatus().equals(1)) {
                sql += " and status_ in (0,1) ";
            }
            if (model.getPatientFollowStatus().equals(2)) {
                sql += " and status_ = 2 ";
            }
        }
        if (andSql) {
            lbqWrapper.apply(true, "patient_id in (" + sql + ")");
        }
        String sort = model.getSort();
        if (StrUtil.isNotEmpty(sort) && "asc".equals(sort)) {
            lbqWrapper.orderByAsc(UnfinishedPatientRecord::getRemindTime);
        } else {
            lbqWrapper.orderByDesc(UnfinishedPatientRecord::getRemindTime);
        }

        IPage<UnfinishedListResult> pageDTOIPage = baseService.appPage(page, lbqWrapper);
        return R.success(pageDTOIPage);
    }



    @GetMapping("handleOneResult")
    @ApiOperation("app处理一条未完成提醒记录")
    public R<Boolean> handleResult(@RequestParam Long recordId) {
        UnfinishedPatientRecord record = new UnfinishedPatientRecord();
        record.setId(recordId);
        record.setHandleStatus(CommonStatus.YES);
        record.setHandleTime(LocalDateTime.now());
        record.setHandleUser(BaseContextHandler.getUserId());
        baseService.updateById(record);
        return R.success(true);
    }

    @GetMapping("seeOneResult")
    @ApiOperation("app查看一个记录")
    public R<Boolean> seeOneResult(@RequestParam Long recordId) {
        UnfinishedPatientRecord record = new UnfinishedPatientRecord();
        record.setId(recordId);
        record.setSeeStatus(CommonStatus.YES);
        record.setSeeTime(LocalDateTime.now());
        baseService.updateById(record);
        return R.success(true);
    }


    @GetMapping("deleteOneResult")
    @ApiOperation("app移除一个记录")
    public R<Boolean> deleteOneResult(@RequestParam Long recordId) {
        UnfinishedPatientRecord record = new UnfinishedPatientRecord();
        record.setId(recordId);
        record.setDeleteFlag(CommonStatus.YES);
        baseService.updateById(record);
        return R.success(true);
    }



    @PostMapping("allHandleResult")
    @ApiOperation("app处理全部患者异常数据")
    public R<Boolean> allHandleResult(@RequestBody UnfinishedQuery model) {

        UpdateWrapper<UnfinishedPatientRecord> lbqWrapper = new UpdateWrapper<>();
        lbqWrapper.eq("nursing_id", model.getNursingId());
        lbqWrapper.eq("unfinished_form_setting_id", model.getUnFinishedSettingId());
        lbqWrapper.eq("handle_status", CommonStatus.NO);
        lbqWrapper.eq("delete_flag", CommonStatus.NO);
        if (model.getDoctorIds() != null && !model.getDoctorIds().isEmpty()) {
            lbqWrapper.in("doctor_id", model.getDoctorIds());
        }
        if (model.getSeeStatus() != null) {
            lbqWrapper.eq("see_status", model.getSeeStatus());
        }
        if (model.getStartDate() != null && model.getEndDate() != null) {
            lbqWrapper.between("remind_time", model.getStartDate().atStartOfDay(), model.getEndDate().atTime(LocalTime.MAX));
        }
        Long nursingId = model.getNursingId();
        String patientName = model.getPatientName();
        String tenant = BaseContextHandler.getTenant();
        boolean andSql = false;
        String sql = "select id from u_user_patient where tenant_code = '"+tenant+"' and service_advisor_id = '"+nursingId+"'";
        if (StrUtil.isNotBlank(patientName)) {
            andSql = true;
            sql += " and name like '%" + patientName + "%'";
        }
        if (Objects.nonNull(model.getPatientFollowStatus())) {
            andSql = true;
            if (model.getPatientFollowStatus().equals(1)) {
                sql += " and status_ in (0,1) ";
            }
            if (model.getPatientFollowStatus().equals(2)) {
                sql += " and status_ = 2 ";
            }
        }
        if (andSql) {
            lbqWrapper.apply(true, "patient_id in (" + sql + ")");
        }
        Long userId = BaseContextHandler.getUserId();
        lbqWrapper.set("handle_status", CommonStatus.YES);
        lbqWrapper.set("handle_user", userId);
        lbqWrapper.set("handle_time", LocalDateTime.now());
        baseService.update(new UnfinishedPatientRecord(), lbqWrapper);
        return R.success(true);
    }

    @PostMapping("allClearData")
    @ApiOperation("全部清空数据")
    public R<Boolean> allClearData(@RequestBody UnfinishedQuery model) {
        UpdateWrapper<UnfinishedPatientRecord> lbqWrapper = new UpdateWrapper<>();
        lbqWrapper.eq("nursing_id", model.getNursingId());
        lbqWrapper.eq("unfinished_form_setting_id", model.getUnFinishedSettingId());
        lbqWrapper.eq("handle_status", CommonStatus.YES);
        lbqWrapper.eq("delete_flag", CommonStatus.NO);
        if (model.getDoctorIds() != null && !model.getDoctorIds().isEmpty()) {
            lbqWrapper.in("doctor_id", model.getDoctorIds());
        }
        if (model.getSeeStatus() != null) {
            lbqWrapper.eq("see_status", model.getSeeStatus());
        }
        if (model.getStartDate() != null && model.getEndDate() != null) {
            lbqWrapper.between("remind_time", model.getStartDate().atStartOfDay(), model.getEndDate().atTime(LocalTime.MAX));
        }
        Long nursingId = model.getNursingId();
        String patientName = model.getPatientName();
        String tenant = BaseContextHandler.getTenant();
        boolean andSql = false;
        String sql = "select id from u_user_patient where tenant_code = '"+tenant+"' and service_advisor_id = '"+nursingId+"'";
        if (StrUtil.isNotBlank(patientName)) {
            andSql = true;
            sql += " and name like '%" + patientName + "%'";
        }
        if (Objects.nonNull(model.getPatientFollowStatus())) {
            andSql = true;
            if (model.getPatientFollowStatus().equals(1)) {
                sql += " and status_ in (0,1) ";
            }
            if (model.getPatientFollowStatus().equals(2)) {
                sql += " and status_ = 2 ";
            }
        }
        if (andSql) {
            lbqWrapper.apply(true, "patient_id in (" + sql + ")");
        }
        lbqWrapper.set("clear_status", CommonStatus.YES);
        baseService.update(new UnfinishedPatientRecord(), lbqWrapper);
        return R.success(true);
    }






}
