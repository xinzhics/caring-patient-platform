package com.caring.sass.nursing.controller.form;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.dao.form.FormScoreRuleMapper;
import com.caring.sass.nursing.dto.field.*;
import com.caring.sass.nursing.dto.form.*;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormScoreRule;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.service.form.FormService;
import com.caring.sass.nursing.util.FormUtil;
import com.caring.sass.utils.BizAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 自定义表单表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/form")
@Api(value = "Form", tags = "自定义表单表")
//@PreAuth(replace = "form:")
public class FormController extends SuperController<FormService, Long, Form, FormPageDTO, FormSaveDTO, FormUpdateDTO> {

    @Autowired
    FormScoreRuleMapper formScoreRuleMapper;

    /**
     * 存在更新记录，否插入一条记录
     *
     * @return 实体
     */
    @ApiOperation(value = "存在更新记录，否插入一条记录")
    @PostMapping(value = "saveOrUpdate")
    public R<Form> saveOrUpdate(@RequestBody FormSaveDTO model) {
        BizAssert.notEmpty(model.getName(), "表单名称不能为空");
        BizAssert.notEmpty(model.getBusinessId(), "业务id不能为空");
        BizAssert.notNull(model.getCategory(), "表单类型不能为空");

        // 重写表单属性
        model.setFieldsJson(FormUtil.reWriteFormField(model.getFieldsJson()));
        Form regGuide = BeanUtil.toBean(model, Form.class);
        // 修改查询条件：表单名称、业务id存在则修改，不存在侧插入
        LbqWrapper<Form> upsert = Wraps.<Form>lbQ()
                .eq(Form::getName, model.getName())
                .eq(Form::getCategory, model.getCategory())
                .eq(Form::getBusinessId, model.getBusinessId());
        baseService.saveOrUpdate(regGuide, upsert);
        return R.success(regGuide);
    }

    @ApiOperation(value = "查询项目的入组方式")
    @Deprecated
    @GetMapping("getIntoTheGroup")
    public R<Form> getIntoTheGroup() {
        Form form = baseService.getIntoTheGroupOneQuestionPage();
        return R.success(form);
    }

    @ApiOperation(value = "查询表单是否一题一页填写,1：是 。 2否")
    @GetMapping("getFormIntoTheGroup")
    public R<Integer> getFormIntoTheGroup(@RequestParam(value = "category", required = false) String category,
                                   @RequestParam(value = "planId", required = false) Long planId) {

        Form form;
        if (StrUtil.isNotEmpty(category)) {
            form = baseService.getFormByRedis(FormEnum.match(category, null), null);

        } else if (Objects.nonNull(planId)) {
            form = baseService.getFormByRedis(null, planId.toString());
        } else {
            throw new BizException("参数不能都为空");
        }
        Integer questionPage = form.getOneQuestionPage();
        if (questionPage == null) {
            return R.success(2);
        } else if (questionPage.equals(1)) {
            return R.success(1);
        } else {
            return R.success(2);
        }
    }


    @ApiOperation(value = "患者新增疾病信息时获取表单配置")
    @GetMapping("patientGetForm")
    public R<Form> patientGetForm() {
        Form form = baseService.getFormByRedis(FormEnum.HEALTH_RECORD, null);
        return R.success(form);
    }

    @ApiOperation(value = "根据表单类型，获取项目的基本信息或疾病信息")
    @GetMapping("getFromByCategory/{tenantCode}")
    public R<List<Form>> getFromByCategory(@PathVariable String tenantCode, @RequestParam("categorys") String categorys) {
        String[] split = categorys.split(",");
        List<Form> formList = new ArrayList<>();
        BaseContextHandler.setTenant(tenantCode);
        for (String s : split) {
            String code = FormEnum.BASE_INFO.getCode();
            if (code.equals(s)) {
                Form form = baseService.getFormByRedis(FormEnum.BASE_INFO, null);
                if (Objects.nonNull(form)) {
                    formList.add(form);
                }
            }
            code = FormEnum.HEALTH_RECORD.getCode();
            if (code.equals(s)) {
                Form form = baseService.getFormByRedis(FormEnum.HEALTH_RECORD, null);
                if (Objects.nonNull(form)) {
                    formList.add(form);
                }
            }
        }
        return R.success(formList);
    }

    @ApiOperation("复制表单")
    @PostMapping("copyForm")
    public R<Boolean> copyForm(@RequestBody @Validated  CopyFormDTO copyFormDTO) {
        baseService.copyForm(copyFormDTO.getFromTenantCode(), copyFormDTO.getToTenantCode(), copyFormDTO.getPlanIdMaps());
        return R.success();
    }

    @GetMapping("getDiagnosis")
    @ApiOperation("获取诊断类型")
    public R<JSONArray> getDiagnosis() {
        Form form = baseService.getFormByRedis(FormEnum.HEALTH_RECORD, null);
        if (Objects.nonNull(form)) {
            String fieldsJson = form.getFieldsJson();
            List<FormField> fields = JSONArray.parseArray(fieldsJson, FormField.class);
            JSONArray ja = new JSONArray();
            for (FormField formField : fields) {
                if (FormFieldExactType.DIAGNOSE.equals(formField.getExactType())) {
                    List<FormOptions> options = formField.getOptions();
                    for (FormOptions option : options) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("id", option.getId());
                        jsonObject1.put("name", option.getAttrValue());
                        jsonObject1.put("type", "diagnosis");
                        ja.add(jsonObject1);
                    }
                }
            }
            return R.success(ja);
        }
        return R.success(new JSONArray());
    }

    @ApiOperation(value = "查询标签使用的表单字段")
    @GetMapping("getTagAttrField/{tenantCode}")
    public R<List<FormField>> getTagAttrField(@PathVariable String tenantCode,
                                           @RequestParam(value = "category", required = true) FormEnum category) {
        BaseContextHandler.setTenant(tenantCode);
        List<FormField> fields = baseService.getTagAttrField(category);
        return R.success(fields);
    }

    @ApiOperation(value = "查询标签使用的监测计划表单字段")
    @ApiImplicitParam(value = "businessId", name = "计划ID", required = true)
    @GetMapping("getTagAttrMonitoringIndicatorsField/{tenantCode}")
    public R<List<FormField>> getTagAttrMonitoringIndicatorsField(@PathVariable String tenantCode,
                       @RequestParam(value = "businessId", required = true) String businessId) {
        BaseContextHandler.setTenant(tenantCode);
        List<FormField>  fields = baseService.getTagAttrMonitoringIndicatorsField(businessId);
        return R.success(fields);
    }


    @ApiOperation(value = "根据计划ID查询计划配置的表单")
    @ApiImplicitParam(value = "planId", name = "计划ID", required = true)
    @GetMapping("getPlanForm")
    public R<Form> getPlanForm(@RequestParam(value = "planId") Long planId) {
        Form form = baseService.getFormByRedis(null, planId.toString());
        return R.success(form);
    }

    @ApiOperation(value = "查询监控指标使用的表单字段")
    @GetMapping("getMonitoringIntervalFields/{tenantCode}")
    public R<Form> getMonitoringIntervalFields(@PathVariable String tenantCode,
                                                          @RequestParam(value = "planId", required = false) Long planId,
                                                          @RequestParam(value = "planType", required = false) Integer planType,
                                                          @RequestParam(value = "category", required = false) FormEnum category) {
        BaseContextHandler.setTenant(tenantCode);
        Form fields = baseService.getMonitoringIntervalFields(planId, planType, category);
        return R.success(fields);
    }


    @ApiOperation(value = "查询表单并设置计分规则")
    @GetMapping(value = "/getFormByCategoryAndBizId")
    public R<Form> getFormByCategoryAndBizId(@RequestParam(name = "category") FormEnum category,
                                             @RequestParam(name = "bizId", required = false) String bizId) {

        Form form = baseService.getFormByRedis(category, bizId);
        if (Objects.isNull(form)) {
            form = new Form();
        } else {
            if (Objects.nonNull(form.getScoreQuestionnaire()) && form.getScoreQuestionnaire().equals(1)) {
                FormScoreRule scoreRule = formScoreRuleMapper.selectOne(Wraps.<FormScoreRule>lbQ().eq(FormScoreRule::getFormId, form.getId()).last(" limit 0,1 "));
                if (Objects.nonNull(scoreRule)) {
                    FormScoreRuleUpdateDTO ruleUpdateDTO = new FormScoreRuleUpdateDTO();
                    BeanUtils.copyProperties(scoreRule, ruleUpdateDTO);
                    ruleUpdateDTO.setId(scoreRule.getId());
                    form.setFormScoreRuleUpdateDTO(ruleUpdateDTO);
                }
            }
        }
        return R.success(form);
    }

}
