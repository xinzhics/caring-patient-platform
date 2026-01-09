package com.caring.sass.nursing.controller.form;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.form.FormResultBackUpMapper;
import com.caring.sass.nursing.dao.form.FormResultBackUpQueryMapper;
import com.caring.sass.nursing.dao.form.FormResultMapper;
import com.caring.sass.nursing.dao.form.FormResultQueryMapper;
import com.caring.sass.nursing.dto.form.FormResultBackUpPageDTO;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.form.FormResultBackUp;
import com.caring.sass.nursing.entity.form.FormResultBackUpQuery;
import com.caring.sass.nursing.entity.form.FormResultQuery;
import com.caring.sass.nursing.service.form.FormResultBackUpService;
import com.caring.sass.oauth.api.UcenterUserBizApi;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dto.UserBizDto;
import com.caring.sass.user.dto.UserBizInfo;
import com.caring.sass.utils.SpringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName FormResultBackUpController
 * @Description
 * @Author yangShuai
 * @Date 2022/4/12 17:29
 * @Version 1.0
 */
@Validated
@RestController
@RequestMapping("/formResultBackUp")
@Api(value = "FormResultBackUp", tags = "表单修改记录表")
public class FormResultBackUpController extends SuperController<FormResultBackUpService, Long, FormResultBackUp, FormResultBackUpPageDTO, Object, Object> {
    @Autowired
    TenantApi tenantApi;

    @Autowired
    FormResultBackUpQueryMapper formResultBackUpQueryMapper;

    @Autowired
    UcenterUserBizApi userBizApi;

    public void encryptedCurrentData(String tenantCode) {

        FormResultBackUpMapper resultMapper = SpringUtils.getBean(FormResultBackUpMapper.class);
        BaseContextHandler.setTenant(tenantCode);
        Page<FormResultBackUpQuery> page = new Page<>();
        int current = 1;
        page.setCurrent(current);
        page.setSize(50);
        LbqWrapper<FormResultBackUpQuery> lbqWrapper = Wraps.<FormResultBackUpQuery>lbQ();
        lbqWrapper.eq(FormResultBackUpQuery::getEncrypted, CommonStatus.NO);
        lbqWrapper.select(SuperEntity::getId, FormResultBackUpQuery::getJsonContent);
        do {
            formResultBackUpQueryMapper.selectPage(page, lbqWrapper);
            List<FormResultBackUpQuery> pageRecords = page.getRecords();
            if (CollUtil.isNotEmpty(pageRecords)) {
                FormResultBackUp result;
                for (FormResultBackUpQuery record : pageRecords) {
                    result = new FormResultBackUp();
                    result.setId(record.getId());
                    result.setJsonContent(record.getJsonContent());
                    result.setEncrypted(CommonStatus.YES);
                    resultMapper.updateById(result);
                }
            }
        } while (page.getCurrent() < page.getPages());

    }
    @ApiOperation(value = "加密已有数据")
    @GetMapping("encryptedCurrentData")
    public R<Boolean> encryptedCurrentData() {
//        R<List<Tenant>> allTenant = tenantApi.getAllTenant();
//        for (Tenant datum : allTenant.getData()) {
//            String code = datum.getCode();
//            encryptedCurrentData(code);
//        }
        return R.success(true);
    }



    @ApiOperation("分页查询表单历史修改记录")
    @Override
    public R<IPage<FormResultBackUp>> page(@RequestBody PageParams<FormResultBackUpPageDTO> params) {
        IPage<FormResultBackUp> buildPage = params.buildPage();
        FormResultBackUpPageDTO paramsModel = params.getModel();

        LbqWrapper<FormResultBackUp> wrapper = Wraps.<FormResultBackUp>lbQ().select(SuperEntity::getId,
                        SuperEntity::getCreateTime,
                        FormResultBackUp::getUpdateUserId,
                        FormResultBackUp::getUserType)
                .eq(FormResultBackUp::getFormResultId, paramsModel.getFormResultId());
        if (StringUtils.isNotEmptyString(paramsModel.getUserType())) {
            wrapper.eq(FormResultBackUp::getUserType, paramsModel.getUserType());
        }
        if (paramsModel.getUpdateUserId() != null) {
            wrapper.eq(FormResultBackUp::getUserType, paramsModel.getUpdateUserId());
        }

        IPage<FormResultBackUp> page = baseService.page(buildPage, wrapper);
        List<FormResultBackUp> records = page.getRecords();
        List<UserBizInfo> userBizInfos = new ArrayList<>();
        records.forEach(item -> userBizInfos.add(UserBizInfo.builder()
                .userType(item.getUserType())
                .userId(item.getUpdateUserId())
                .build()));
        Map<String, List<UserBizInfo>> collect = userBizInfos.stream().collect(Collectors.groupingBy(UserBizInfo::getUserType));
        UserBizDto userBizDto = new UserBizDto();
        userBizDto.setMap(collect);
        R<Map<String, Map<Long, String>>> queryUserInfo = userBizApi.queryUserInfo(userBizDto);
        Map<Long, String> stringMap;
        if (queryUserInfo.getIsSuccess() != null && queryUserInfo.getIsSuccess()) {
            Map<String, Map<Long, String>> userInfoMap = queryUserInfo.getData();
            if (userInfoMap != null) {
                for (FormResultBackUp record : records) {
                    stringMap = userInfoMap.get(record.getUserType());
                    if (CollUtil.isNotEmpty(stringMap)) {
                        record.setUpdateUserName(stringMap.get(record.getUpdateUserId()));
                    }
                }
            }
        }
        return R.success(page);
    }


    @Override
    public R<FormResultBackUp> get(@PathVariable Long id) {

        FormResultBackUp resultBackUp = baseService.getById(id);
        if (Objects.nonNull(resultBackUp)) {
            UserBizInfo build = UserBizInfo.builder().userId(resultBackUp.getUpdateUserId()).userType(resultBackUp.getUserType()).build();
            R<UserBizInfo> userInfo = userBizApi.queryUserInfo(build);
            if (userInfo.getIsSuccess() != null && userInfo.getIsSuccess()) {
                UserBizInfo infoData = userInfo.getData();
                if (Objects.nonNull(infoData)) {
                    resultBackUp.setUpdateUserName(infoData.getName());
                }
            }
        }
        return R.success(resultBackUp);
    }


    @ApiOperation("创建历史疾病信息通过疾病信息的历史修改记录")
    @GetMapping("createHealthRecordByHistory")
    public R<Boolean> createHealthRecordByHistory() {

        baseService.createHealthRecordByHistory();
        return R.success();
    }


}
