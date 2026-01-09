package com.caring.sass.nursing.controller.form;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.model.RemoteData;
import com.caring.sass.nursing.dto.form.FormResultExportPageDTO;
import com.caring.sass.nursing.dto.form.FormResultExportSaveDTO;
import com.caring.sass.nursing.dto.form.FormResultExportUpdateDTO;
import com.caring.sass.nursing.entity.form.FormResultExport;
import com.caring.sass.nursing.service.form.FormResultExportService;
import com.caring.sass.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 表单结果导出记录表
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-13
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/formResultExport")
@Api(value = "FormResultExport", tags = "表单结果导出记录表")
//@PreAuth(replace = "formResultExport:")
public class FormResultExportController extends SuperController<FormResultExportService, Long, FormResultExport, FormResultExportPageDTO, FormResultExportSaveDTO, FormResultExportUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<FormResultExport> formResultExportList = list.stream().map((map) -> {
            FormResultExport formResultExport = FormResultExport.builder().build();
            //TODO 请在这里完成转换
            return formResultExport;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(formResultExportList));
    }


    @Override
    public R<IPage<FormResultExport>> page(@RequestBody PageParams<FormResultExportPageDTO> params) {
        IPage<FormResultExport> page = params.buildPage();
        LbqWrapper<FormResultExport> wrapper = Wraps.<FormResultExport>lbQ();
        IPage<FormResultExport> servicePage = baseService.findPage(page, wrapper);
        return R.success(servicePage);
    }

    @Autowired
    UserService userService;

    @Override
    public R<FormResultExport> handlerSave(FormResultExportSaveDTO model) {

        List<Long> planIds = model.getPlanIds();
        List<String> baseInfoScopes = model.getBaseInfoScopes();
        FormResultExport export = new FormResultExport();
        BeanUtils.copyProperties(model, export);
        if (CollUtil.isNotEmpty(planIds)) {
            export.setPlanIdArrayJson(JSON.toJSONString(planIds));
        }
        if (StrUtil.isNotEmpty(model.getPlanIdArrayJson())) {
            export.setPlanIdArrayJson(model.getPlanIdArrayJson());
        }
        if (CollUtil.isNotEmpty(baseInfoScopes)) {
            export.setBaseInfoScopeArrayJson(JSON.toJSONString(baseInfoScopes));
        }
        Long userId = BaseContextHandler.getUserId();
        String userType = BaseContextHandler.getUserType();
        export.setCurrentUserType(userType);
        User user = userService.getByIdCache(userId);
        RemoteData<Long, Org> userOrg = user.getOrg();
        Long key = userOrg.getKey();
        if (Objects.nonNull(key)) {
            export.setOrganId(key);
        }
        export.setBaseInfoScopeArrayJson(JSON.toJSONString(baseInfoScopes));
        export.setExportProgress(0);
        baseService.save(export);
        R ok = new R(0, export, "ok");
        ok.setDefExec(false);
        return ok;
    }
}
