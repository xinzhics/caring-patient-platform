package com.caring.sass.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.user.dto.DoctorCustomGroupPatientPageDTO;
import com.caring.sass.user.dto.GroupPageDTO;
import com.caring.sass.user.dto.GroupSaveDTO;
import com.caring.sass.user.dto.GroupUpdateDTO;
import com.caring.sass.user.entity.Group;
import com.caring.sass.user.entity.NursingStaff;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorService;
import com.caring.sass.user.service.GroupService;
import com.caring.sass.user.service.NursingStaffService;
import com.caring.sass.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 小组
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/group")
@Api(value = "Group", tags = "小组")
//@PreAuth(replace = "group:")
public class GroupController extends SuperController<GroupService, Long, Group, GroupPageDTO, GroupSaveDTO, GroupUpdateDTO> {

    @Autowired
    GroupService groupService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    PatientService patientService;

    @Autowired
    NursingStaffService nursingStaffService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<Group> groupList = list.stream().map((map) -> {
            Group group = Group.builder().build();
            //TODO 请在这里完成转换
            return group;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(groupList));
    }


    /**
     * @Author yangShuai
     * @Description 创建 项目默认的 小组 TODO：需要测试是否有正确的租户信息注入
     * @Date 2020/9/27 10:15
     *
     * @return com.caring.sass.base.R<com.caring.sass.user.entity.Doctor>
     */
    @PostMapping("createDefaultGroup")
    public R<Group> createGroup(@RequestBody Group group) {
        group = baseService.createGroup(group);
        return R.success(group);
    }

    @Override
    public R<IPage<Group>> page(@RequestBody @Validated PageParams<GroupPageDTO> params) {
        IPage<Group> page = params.buildPage();
        this.query(params, page, null);
        List<Group> pageRecords = page.getRecords();
        patientService.countPatientByGroupId(pageRecords);
        doctorService.countDoctorByGroupId(pageRecords);
        return R.success(page);
    }


    @ApiOperation("删除一个小组")
    @DeleteMapping("/delete/{id}")
    public R<Boolean> deleteById(@PathVariable("id") Long id) {
        baseService.removeById(id);
        return R.success();
    }

    @Override
    public R<Group> handlerSave(GroupSaveDTO groupSaveDTO) {
        Group group = BeanUtil.toBean(groupSaveDTO, Group.class);
        String userType = BaseContextHandler.getUserType();
        // 如果是护理专员新增小组，附带护理专员名称
        if (Objects.equals(userType, UserType.NURSING_STAFF)) {
            group.setNurseName(BaseContextHandler.getName());
        }
        if (group.getNurseId() != null) {
            NursingStaff nursingStaff = nursingStaffService.getById(group.getNurseId());
            if (Objects.nonNull(nursingStaff)) {
                group.setClassCode(nursingStaff.getClassCode());
                group.setOrganId(nursingStaff.getOrganId());
                group.setOrganName(nursingStaff.getOrganName());
            }
        }
        baseService.save(group);
        return R.success(group);
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
    public R<IPage<Group>> pageWithScope(@RequestBody @Validated PageParams<GroupPageDTO> params) {
        IPage<Group> page = params.buildPage();
        GroupPageDTO userPage = params.getModel();
        Group queryParam = BeanUtil.toBean(userPage, Group.class);
        QueryWrap<Group> wrap = handlerWrapper(queryParam, params);
        LbqWrapper<Group> wrapper = wrap.lambda()
                .like(Group::getName, queryParam.getName());
        baseService.findPage(page, wrapper);
        if (CollUtil.isEmpty(page.getRecords())) {
            return R.success(page);
        }
        List<Group> pageRecords = page.getRecords();
        baseService.desensitization(pageRecords);
        patientService.countPatientByGroupId(pageRecords);
        doctorService.countDoctorByGroupId(pageRecords);
        return R.success(page);
    }

}
