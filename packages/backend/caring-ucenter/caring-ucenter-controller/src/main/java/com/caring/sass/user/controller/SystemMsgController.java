package com.caring.sass.user.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.dao.DoctorMapper;
import com.caring.sass.user.dto.SystemMsgPageDTO;
import com.caring.sass.user.dto.SystemMsgSaveDTO;
import com.caring.sass.user.dto.SystemMsgUpdateDTO;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.SystemMsg;
import com.caring.sass.user.service.SystemMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


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
@RequestMapping("/systemMsg")
@Api(value = "SystemMsg", tags = "系统消息")
//@PreAuth(replace = "commonMsg:")
public class SystemMsgController extends SuperController<SystemMsgService, Long, SystemMsg, SystemMsgPageDTO, SystemMsgSaveDTO, SystemMsgUpdateDTO> {

    @Autowired
    DoctorMapper doctorMapper;


    @PutMapping("setRead")
    @ApiOperation("设置消息已读")
    public R<Boolean> setRead(@RequestBody List<Long> messageId) {
        if (CollUtil.isNotEmpty(messageId)) {
            UpdateWrapper<SystemMsg> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", messageId);
            updateWrapper.set("read_status", CommonStatus.YES);
            baseService.update(updateWrapper);
        }
        return R.success(true);
    }



    @GetMapping("countMessage")
    @ApiOperation("统计医生未读的患者关注消息")
    public R<Integer> countMessage(@RequestParam Long doctorId) {
        return R.success(baseService.count(Wraps.<SystemMsg>lbQ()
                .eq(SystemMsg::getReadStatus, CommonStatus.NO)
                .eq(SystemMsg::getUserRole, UserType.UCENTER_DOCTOR)
                .eq(SystemMsg::getUserId, doctorId)));
    }



    @Override
    public R<IPage<SystemMsg>> page(PageParams<SystemMsgPageDTO> params) {
        params.setOrder("descending");
        params.setSort("createTime");
        SystemMsgPageDTO model = params.getModel();
        if (StringUtils.isEmpty(model.getUserRole())) {
            model.setUserRole(UserType.UCENTER_NURSING_STAFF);
            params.setModel(model);
        }
        Long userId = model.getUserId();
        if ("doctor".equals(model.getUserRole())) {
            // 更新一下医生的最后登录时间。
            Doctor doctor = new Doctor();
            doctor.setId(userId);
            doctor.setLastLoginTime(new Date().getTime());
            doctorMapper.updateById(doctor);
        }
        return super.page(params);
    }
}
