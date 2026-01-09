package com.caring.sass.user.merck;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.ChineseToEnglishUtil;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.common.utils.key.Key;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.nursing.api.FormResultApi;
import com.caring.sass.nursing.api.PatientNursingPlanApi;
import com.caring.sass.nursing.api.PlanApi;
import com.caring.sass.nursing.dto.form.FormField;
import com.caring.sass.nursing.dto.form.FormOptions;
import com.caring.sass.nursing.dto.form.FormResultSaveDTO;
import com.caring.sass.nursing.dto.form.FormResultUpdateDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.enumeration.PlanEnum;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dao.DoctorMapper;
import com.caring.sass.user.dao.MerckConstantMapper;
import com.caring.sass.user.dao.MerckPersonMapper;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.PatientService;
import com.caring.sass.user.service.impl.ImService;
import com.caring.sass.utils.DateUtils;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.SyncUserUnionIdDTO;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 1. 使用敏识医云公众号访问openId对应的unionId
 *
 * 2. 查询merckPerson中 未同步的用户信息。
 * 使用personId访问敏识web客户端服务，获取personId的基本信息，健康档案，初诊病例。
 *
 * 3. 根据用户的基本信息，健康档案，初诊病例。 创建或更新接口指定的项目下的患者信息。
 *
 * @className: MerckUserService
 * @author: 杨帅
 * @date: 2023/12/20
 */
@Service
public class MerckPersonService extends SuperServiceImpl<MerckPersonMapper, MerckPerson> {

    @Autowired
    WeiXinApi weiXinApi;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    DatabaseProperties databaseProperties;

    @Autowired
    DoctorMapper doctorMapper;

    @Autowired
    PatientService patientService;

    @Autowired
    ImService imService;

    @Autowired
    FormResultApi formResultApi;

    @Autowired
    FormApi formApi;

    @Autowired
    PlanApi planApi;

    @Autowired
    PatientNursingPlanApi patientNursingPlanApi;

    @Autowired
    MerckConstantMapper merckConstantMapper;

    // 敏识token
    private String merckToken = "";

    // 敏识的域名
    private String merckDomain = "";

    private Map<String, String> merckConstant = new HashMap<>();

    private static String tmpImagePath = "/tmp/image/";

    private static String imageDownloadUrl = "http://%s.caringsaas.com/caring_managers/api/file/download/image/%d";

    // 敏识患者的健康档案
    private static String personHealthFile = "http://%s.caringsaas.com/caring_managers/api/customers/%d/healthfile";

    // 敏识患者的初诊病例
    private static String personFirstVisitDisease = "http://%s.caringsaas.com/caring_managers/api/customers/%d";

    private static String PERSON_CHECK_DATA = "http://%s.caringsaas.com/caring_managers/api/file/inspection_report/%d";


    public static void main(String[] args) {
        String personHealthFileUrl = String.format(personHealthFile, "merck", 29671);

        HttpRequest request = HttpRequest.get(personHealthFileUrl);
        request.header("X-Mg-Auth-Token", System.getenv().getOrDefault("MERCK_AUTH_TOKEN", ""));
        HttpResponse execute = request.execute();
        String body = execute.body();
        JSONObject object = JSONObject.parseObject(body);
        System.out.println(object);
    }

    /**
     * 刷新openId的 unionId
     */
    public void refreshUnionId(String tenantCode, String wxAppId) {

        List<MerckPerson> merckPeople = baseMapper.selectList(Wraps.<MerckPerson>lbQ().eq(MerckPerson::getSubscribe, 1).isNull(MerckPerson::getUnionId));
        if (CollUtil.isEmpty(merckPeople)) {
            return;
        }
        BaseContextHandler.setTenant(tenantCode);
        List<String> strings = merckPeople.stream().map(MerckPerson::getOpenId).collect(Collectors.toList());
        List<List<String>> lists = ListUtils.subList(strings, 99);
        for (List<String> list : lists) {
            SyncUserUnionIdDTO unionIdDTO = new SyncUserUnionIdDTO();
            unionIdDTO.setAppId(wxAppId);
            unionIdDTO.setOpenIdList(list);
            R<List<WxMpUser>> userInfo = weiXinApi.batchGetUserInfo(unionIdDTO);
            if (userInfo.getIsSuccess()) {
                List<WxMpUser> userList = userInfo.getData();
                for (WxMpUser wxMpUser : userList) {
                    String unionId = wxMpUser.getUnionId();
                    if (StrUtil.isNotEmpty(unionId)) {
                        super.update(Wraps.<MerckPerson>lbU().set(MerckPerson::getUnionId, unionId).eq(MerckPerson::getOpenId, wxMpUser.getOpenId()));
                    }
                }
            }
        }
    }



    /**
     * 敏识数据导入逻辑
     */
    public void syncMerckPerson(String tenantCode, Long doctorId, String merckToken, String merckDomain) {
        this.merckToken = merckToken;
        this.merckDomain = merckDomain;

        BaseContextHandler.setTenant(tenantCode);
        R<Tenant> byCode = tenantApi.getByCode(tenantCode);
        Tenant data = byCode.getData();
        Doctor doctor = doctorMapper.selectById(doctorId);
        R<Plan> planByType = planApi.getPlanByType(PlanEnum.REVIEW_REMIND.getCode());
        Long checkDataId = null;
        if (planByType.getIsSuccess()) {
            Plan plan = planByType.getData();
            if (Objects.nonNull(plan)) {
                checkDataId = plan.getId();
            }
        }
        Form checkDataForm = null;
        if (Objects.nonNull(checkDataId)) {
            R<Form> formR = formApi.getFormByCategoryAndBizId(FormEnum.NURSING_PLAN, checkDataId.toString());
            if (formR.getIsSuccess()) {
                checkDataForm = formR.getData();
            }
        }

        @Length(max = 50, message = "微信AppId50") String wxAppId = data.getWxAppId();
        int current = 1;
        long pages;
        do {
            IPage<MerckPerson> iPage = new Page(current, 50);
            IPage<MerckPerson> selectPage = baseMapper.selectPage(iPage, Wraps.<MerckPerson>lbQ().eq(MerckPerson::getInformationSync, 0));

            List<MerckPerson> records = selectPage.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                for (MerckPerson record : records) {
                    createOrUpdatePatient(record, doctor, wxAppId, checkDataForm);
                }
            }
            pages = selectPage.getPages();
        } while (current < pages);
    }


    /**
     * 创建患者或更新患者数据
     * @param record
     * @param doctor
     * @param wxAppId
     */
    public void createOrUpdatePatient(MerckPerson record, Doctor doctor, String wxAppId, Form checkDataForm) {
        @Length(max = 255, message = "长度不能超过255") String unionId = record.getUnionId();
        Long personId = record.getPersonId();

        Patient patient = null;
        if (StrUtil.isNotEmpty(unionId)) {
            patient = patientService.findByUnionId(unionId);
        }
        // 患者不存在
        if (patient == null) {
            patient = createPatient(doctor, wxAppId, record.getOpenId(), record.getUnionId());
        }
        // 查询患者在敏识的健康档案
        HealthFile healthFile = getMerckHealthFile(personId);
        FirstVisitCasesDTO visitCasesDTO = findFirstVisitCasesByCustomerId(personId);

        Long patientId = patient.getId();
        R<FormResult> patientBaseInfo = formResultApi.getFromByCategory(patientId, 1, FormEnum.BASE_INFO);

        R<FormResult> patientHealth = formResultApi.getFromByCategory(patientId, 1, FormEnum.HEALTH_RECORD);

        // 根据 健康档案。 和 初诊病例 生成患者的基本信息，疾病信息
        FormResult baseInfoData = patientBaseInfo.getData();
        if (Objects.nonNull(baseInfoData)) {
            createOrUpdateBaseInfo(healthFile, patientId, baseInfoData);
        }

        // 患者的疾病信息
        FormResult healthData = patientHealth.getData();
        if (Objects.nonNull(healthData)) {
            createOrUpdateHealth(visitCasesDTO, patientId, healthData, healthFile);
        }
        if (Objects.nonNull(visitCasesDTO)) {
            // 检验报告
            List<ImageDTO> reportImgList = visitCasesDTO.getReportImgList();
            if (CollUtil.isNotEmpty(reportImgList)) {
                createUpdateImageCreateCheckData(checkDataForm, reportImgList, patientId);
            }
        }

        List<Image> dataImage = findCheckDataImage(personId);
        if (Objects.nonNull(dataImage) && CollUtil.isNotEmpty(dataImage)) {
            for (Image image : dataImage) {
                ImageDTO dto = new ImageDTO();
                dto.setInspectionDate(image.getInspectionDate());
                if (StrUtil.isNotEmpty(image.getComment())) {
                    dto.setComment(image.getComment());
                }
                dto.setId(image.getId());
                dto.setPurpose(image.getImagePurpose());
                createCheckDataForm(checkDataForm, dto, patientId);
            }
        }

        record.setInformationSync(1);
        baseMapper.updateById(record);

    }


    @Autowired
    FileUploadApi fileUploadApi;

    /**
     * 创建检验数据
     * @param checkDataForm
     * @param reportImgList
     * @param patientId
     */
    private void createUpdateImageCreateCheckData(Form checkDataForm, List<ImageDTO> reportImgList, Long patientId) {

        if (Objects.isNull(checkDataForm)) {
            return;
        }
        // 根据上传时间分组。
        // 根据检查类型分组
        for (ImageDTO image : reportImgList) {
            Date inspectionDate = image.getInspectionDate();
            String format = DateUtils.format(inspectionDate, DateUtils.DEFAULT_DATE_FORMAT);
            image.setCaringUpdateDate(format);
            image.setKey(format + image.getPurpose());
            // 下载校验报告
            String personHealthFileUrl = String.format(imageDownloadUrl, this.merckDomain, image.getId());
            try {
                File file = FileUtils.downLoadFromFile(personHealthFileUrl, new Date().getTime() + image.getId().toString(), tmpImagePath);
                if (file == null) {
                    continue;
                }
                MockMultipartFile multipartFile = FileUtils.fileToFileItem(file);
                R<com.caring.sass.file.entity.File> upload = fileUploadApi.upload(2L, multipartFile);
                if (upload.getIsSuccess()) {
                    com.caring.sass.file.entity.File data = upload.getData();
                    image.setCaringSassUrl(data.getUrl());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 根据检查类型分组。
        Map<String, List<ImageDTO>> collect = reportImgList.stream().collect(Collectors.groupingBy(ImageDTO::getKey));
        Set<Map.Entry<String, List<ImageDTO>>> entries = collect.entrySet();
        for (Map.Entry<String, List<ImageDTO>> entry : entries) {
            List<ImageDTO> imageDTOS = entry.getValue();
            createCheckDataForm(checkDataForm, imageDTOS, patientId);
        }


    }

    /**
     * 将同类型 同一天上传的检验单 上传到一个检验表单中
     * @param checkDataForm
     * @param patientId
     */
    private void createCheckDataForm(Form checkDataForm, ImageDTO image, Long patientId) {
        Date inspectionDate = image.getInspectionDate();
        String format = DateUtils.format(inspectionDate, DateUtils.DEFAULT_DATE_FORMAT);
        image.setCaringUpdateDate(format);
        image.setKey(format + image.getPurpose());
        // 下载校验报告
        String personHealthFileUrl = String.format(imageDownloadUrl, this.merckDomain, image.getId());
        try {
            File file = FileUtils.downLoadFromFile(personHealthFileUrl, new Date().getTime() + image.getId().toString(), tmpImagePath);
            if (file == null) {
                return;
            }
            MockMultipartFile multipartFile = FileUtils.fileToFileItem(file);
            R<com.caring.sass.file.entity.File> upload = fileUploadApi.upload(2L, multipartFile);
            if (upload.getIsSuccess()) {
                com.caring.sass.file.entity.File data = upload.getData();
                image.setCaringSassUrl(data.getUrl());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        FormResultSaveDTO saveDTO = new FormResultSaveDTO();
        saveDTO.setName(checkDataForm.getName());
        saveDTO.setFormId(checkDataForm.getId());
        saveDTO.setOneQuestionPage(checkDataForm.getOneQuestionPage());
        saveDTO.setUserId(patientId);
        saveDTO.setBusinessId(checkDataForm.getBusinessId());
        saveDTO.setCategory(checkDataForm.getCategory());

        String fieldsJson = checkDataForm.getFieldsJson();
        if (StrUtil.isEmpty(fieldsJson)) {
            return;
        }
        List<FormField> formFields = JSONArray.parseArray(fieldsJson, FormField.class);
        if (CollUtil.isEmpty(formFields)) {
            return;
        }
        JSONObject object;
        String purpose = image.getPurpose();
        String checkProject = "";
        // 过敏原
        if (Image.PURPOSE_INSPECTION_REPORT_ALLERGEN_IMAGE.equals(purpose)) {
            checkProject = "过敏原检测";
        } else if (Image.PURPOSE_INSPECTION_REPORT_PULMONARY_FUNCTION_IMAGE.equals(purpose)) {
            checkProject = "肺功能";
        } else if (Image.PURPOSE_INSPECTION_REPORT_FENO_IMAGE.equals(purpose)) {
            checkProject = "FeNO";
        } else if (Image.PURPOSE_INSPECTION_REPORT_OTHER_IMAGE.equals(purpose)) {
            checkProject = "其他";
        }
        String updateDate = image.getCaringUpdateDate();
        for (FormField field : formFields) {

            String label = field.getLabel();
            if (label.equals("检验单上传")) {
                JSONArray jsonArray = new JSONArray();
                object = new JSONObject();
                object.put("attrValue", image.getCaringSassUrl());
                jsonArray.add(object);
                field.setValues(jsonArray.toJSONString());
            } else if (label.equals("检查项目")) {
                List<FormOptions> options = field.getOptions();
                FormOptions formOptions = findFormOptions(checkProject, options, true);
                if (Objects.isNull(formOptions)) {
                    continue;
                }
                JSONArray jsonArray = new JSONArray();
                object = new JSONObject();
                object.put("valueText", formOptions.getAttrValue());
                object.put("attrValue", formOptions.getId());
                object.put("questions", formOptions.getQuestions());
                jsonArray.add(object);
                field.setValues(jsonArray.toJSONString());
            } else if (label.equals("备注")) {
                JSONArray jsonArray = new JSONArray();
                object = new JSONObject();
                object.put("valueText", image.getComment());
                object.put("attrValue", image.getComment());
                jsonArray.add(object);
                field.setValues(jsonArray.toJSONString());
            } else if (label.equals("检查时间")) {
                JSONArray jsonArray = new JSONArray();
                object = new JSONObject();
                object.put("valueText", updateDate);
                object.put("attrValue", updateDate);
                jsonArray.add(object);
                field.setValues(jsonArray.toJSONString());
            }
        }
        saveDTO.setJsonContent(JSONArray.toJSONString(formFields));

        formResultApi.save(saveDTO);

    }

    /**
     * 将同类型 同一天上传的检验单 上传到一个检验表单中
     * @param checkDataForm
     * @param reportImgList
     * @param patientId
     */
    private void createCheckDataForm(Form checkDataForm, List<ImageDTO> reportImgList, Long patientId) {

        if (CollUtil.isEmpty(reportImgList)) {
            return;
        }

        FormResultSaveDTO saveDTO = new FormResultSaveDTO();
        saveDTO.setName(checkDataForm.getName());
        saveDTO.setFormId(checkDataForm.getId());
        saveDTO.setOneQuestionPage(checkDataForm.getOneQuestionPage());
        saveDTO.setUserId(patientId);
        saveDTO.setBusinessId(checkDataForm.getBusinessId());
        saveDTO.setCategory(checkDataForm.getCategory());

        String fieldsJson = checkDataForm.getFieldsJson();
        if (StrUtil.isEmpty(fieldsJson)) {
            return;
        }
        List<FormField> formFields = JSONArray.parseArray(fieldsJson, FormField.class);
        if (CollUtil.isEmpty(formFields)) {
            return;
        }
        JSONObject object;
        ImageDTO dto = reportImgList.get(0);
        String purpose = dto.getPurpose();
        String checkProject = "";
        // 过敏原
        if (Image.PURPOSE_INSPECTION_REPORT_ALLERGEN_IMAGE.equals(purpose)) {
            checkProject = "过敏原检测";
        } else if (Image.PURPOSE_INSPECTION_REPORT_PULMONARY_FUNCTION_IMAGE.equals(purpose)) {
            checkProject = "肺功能";
        } else if (Image.PURPOSE_INSPECTION_REPORT_FENO_IMAGE.equals(purpose)) {
            checkProject = "FeNO";
        } else if (Image.PURPOSE_INSPECTION_REPORT_OTHER_IMAGE.equals(purpose)) {
            checkProject = "其他";
        }
        String updateDate = dto.getCaringUpdateDate();
        for (FormField field : formFields) {

            String label = field.getLabel();
            if (label.equals("检验单上传")) {
                JSONArray jsonArray = new JSONArray();
                for (ImageDTO imageDTO : reportImgList) {
                    object = new JSONObject();
                    object.put("attrValue", imageDTO.getCaringSassUrl());
                    jsonArray.add(object);
                }
                field.setValues(jsonArray.toJSONString());
            } else if (label.equals("检查项目")) {
                List<FormOptions> options = field.getOptions();
                FormOptions formOptions = findFormOptions(checkProject, options, true);
                if (Objects.isNull(formOptions)) {
                    continue;
                }
                JSONArray jsonArray = new JSONArray();
                object = new JSONObject();
                object.put("valueText", formOptions.getAttrValue());
                object.put("attrValue", formOptions.getId());
                object.put("questions", formOptions.getQuestions());
                jsonArray.add(object);
                field.setValues(jsonArray.toJSONString());
            } else if (label.equals("备注")) {

            } else if (label.equals("检查时间")) {
                JSONArray jsonArray = new JSONArray();
                object = new JSONObject();
                object.put("valueText", updateDate);
                object.put("attrValue", updateDate);
                jsonArray.add(object);
                field.setValues(jsonArray.toJSONString());
            }
        }
        saveDTO.setJsonContent(JSONArray.toJSONString(formFields));

        formResultApi.save(saveDTO);

    }



    /**
     * 创建或者更新 疾病信息
     * @param visitCasesDTO
     * @param patientId
     * @param healthData
     */
    private void createOrUpdateHealth(FirstVisitCasesDTO visitCasesDTO, Long patientId, FormResult healthData, HealthFile healthFile) {
        if (Objects.nonNull(visitCasesDTO)) {
            // 初诊疾病
            Set<String> firstVisitDisease = visitCasesDTO.getFirstVisitDisease();
            if (CollUtil.isEmpty(firstVisitDisease)) {
                return;
            }
            // 并发症备注
            String complicationsNote = visitCasesDTO.getComplicationsNote();

            // 过敏原
            Set<Constant> allergen = null;
            Set<Constant> allergyHistory = null;
            String otherAllergenText = null;
            if (Objects.nonNull(healthFile)) {
                allergen = healthFile.getAllergen();
                // 家族过敏史
                allergyHistory = healthFile.getFamilyAllergyHistory();
                otherAllergenText = healthFile.getOtherAllergenText();
            }

            // 有无过敏药物
            String medicineAllergyHistory = visitCasesDTO.getHasMedicineAllergyHistory();

            // 过敏药物
            Set<String> histories = null;
            if (!"key_medicine_allergy_history_no".equals(medicineAllergyHistory)) {
                // 有过敏药物
                histories = visitCasesDTO.getMedicineAllergyHistories();
            }

            List<?> fieldList = healthData.getFieldList();
            if (CollUtil.isEmpty(fieldList)) {
                return;
            }
            List<FormField> formFields = JSONArray.parseArray(JSON.toJSONString(fieldList), FormField.class);
            for (FormField field : formFields) {
                String values = field.getValues();
                JSONArray valJson;
                if (StrUtil.isEmpty(values)) {
                    valJson = new JSONArray();
                } else {
                    valJson = JSONArray.parseArray(values);
                }
                Object o = valJson.get(0);
                String label = field.getLabel();
                if ("诊断类型".equals(label) && CollUtil.isNotEmpty(firstVisitDisease)) {
                    // 过敏性鼻炎
                    if (firstVisitDisease.contains("key_allergic_rhinitis")) {
                        String allergicRhinitis = queryMerckConstantLabel("key_allergic_rhinitis");
                        if (StrUtil.isNotEmpty(allergicRhinitis)) {
                            setRadioValue(o, allergicRhinitis, field, valJson, false);
                            firstVisitDisease.remove("key_allergic_rhinitis");
                        }
                        // 过敏性支气管哮喘
                    } else if (firstVisitDisease.contains("key_allergic_bronchial_asthma")) {
                        setRadioValue(o, "过敏性哮喘", field, valJson, true);
                        firstVisitDisease.remove("key_allergic_bronchial_asthma");
                    }
                } else if (label.contains("伴随诊断") && (CollUtil.isNotEmpty(firstVisitDisease) || StrUtil.isNotEmpty(complicationsNote))) {
                    List<String> labels = new ArrayList<>();
                    for (String s : firstVisitDisease) {
                        String constantLabel = queryMerckConstantLabel(s);
                        if (StrUtil.isNotEmpty(constantLabel)) {
                            labels.add(constantLabel);
                        }
                    }
                    if (StrUtil.isNotEmpty(complicationsNote)) {
                        labels.add(complicationsNote);
                    }
                    setCheckBoxValue(labels, field, valJson);
                } else if (label.contains("过敏原") && CollUtil.isNotEmpty(allergen)) {

                    Map<String, List<Constant>> collectMap = allergen.stream().collect(Collectors.groupingBy(Constant::getType));
                    setAllergen(collectMap, field, valJson, otherAllergenText);
                } else if (label.contains("您的父母及兄弟姐妹") && CollUtil.isNotEmpty(allergyHistory)) {
                    // 家族过敏史

                    List<String> allergyHistoryList = allergyHistory.stream().map(Constant::getLabel).collect(Collectors.toList());
                    setCheckBoxValue(allergyHistoryList, field, valJson);
                } else if (label.contains("有无药物过敏史") && (CollUtil.isNotEmpty(histories))) {

                    setMedicineAllergyHistory(histories, field, valJson);
                }
            }

            FormResult updateDTO = new FormResult();
            updateDTO.setCategory(FormEnum.HEALTH_RECORD);
            updateDTO.setFormId(healthData.getFormId());
            updateDTO.setUserId(patientId);
            updateDTO.setJsonContent(JSON.toJSONString(formFields));
            updateDTO.setId(healthData.getId());
            updateDTO.setName(healthData.getName());
            updateDTO.setOneQuestionPage(healthData.getOneQuestionPage());
            updateDTO.setDeleteMark(0);
            updateDTO.setFillInIndex(-1);
            formResultApi.saveFormResultStage(updateDTO);
        }

    }

    /**
     * 有 药物过敏史
     * @param histories
     * @param formField
     * @param valJson
     */
    private void setMedicineAllergyHistory(Set<String> histories, FormField formField, JSONArray valJson) {
        if (CollUtil.isNotEmpty(valJson)) {
            Object o = valJson.get(0);
            JSONObject object = JSONObject.parseObject(o.toString());
            if (object.get("attrValue") != null) {
                return;
            }
        }
        List<FormOptions> options = formField.getOptions();
        valJson = new JSONArray();
        for (FormOptions option : options) {
            if ("有".equals(option.getAttrValue())) {
                JSONObject object = new JSONObject();
                object.put("valueText", option.getAttrValue());
                object.put("attrValue", option.getId());
                List<FormField> questions = option.getQuestions();
                if (CollUtil.isNotEmpty(questions)) {
                    FormField field = questions.get(0);
                    List<String> labelNames = new ArrayList<>();
                    for (String history : histories) {
                        String label = queryMerckConstantLabel(history);
                        if (StrUtil.isNotEmpty(label)) {
                            labelNames.add(label);
                        }
                    }
                    if (CollUtil.isNotEmpty(labelNames)) {
                        setCheckBoxValue(labelNames, field, null);
                    }
                }
                object.put("questions", questions);
                valJson.add(object);
                formField.setValues(valJson.toJSONString());
                break;
            }
        }

    }

    /**
     * 设置过敏原
     *  // 食入 const_allergen_ingestion
     *
     * // 接触 const_allergen_contact
     *
     * // 吸入  const_allergen_suction
     *
     * // 其他过敏原 const_allergen_other
     * @param collectMap
     * @param formField
     * @param valJson
     */
    private void setAllergen(Map<String, List<Constant>> collectMap, FormField formField, JSONArray valJson, String otherAllergenText) {
        Set<String> keySet = collectMap.keySet();

        // 有值 就不要操作
        if (CollUtil.isNotEmpty(valJson)) {
            Object o = valJson.get(0);
            JSONObject object = JSONObject.parseObject(o.toString());
            if (object.get("attrValue") != null) {
                return;
            }
        }
        List<FormOptions> options = formField.getOptions();
        valJson = new JSONArray();
        for (FormOptions option : options) {
            if (option.getAttrValue().equals("吸入式") && keySet.contains("const_allergen_suction")) {

                List<Constant> constants = collectMap.get("const_allergen_suction");
                JSONObject values = getAllergenValues(option, constants);
                valJson.add(values);
            } else if (option.getAttrValue().equals("食入式") && keySet.contains("const_allergen_ingestion")) {

                List<Constant> constants = collectMap.get("const_allergen_ingestion");
                JSONObject values = getAllergenValues(option, constants);
                valJson.add(values);
            } else if (option.getAttrValue().equals("接触式") && keySet.contains("const_allergen_contact")) {

                List<Constant> constants = collectMap.get("const_allergen_contact");
                JSONObject values = getAllergenValues(option, constants);
                valJson.add(values);
            } else if (option.getAttrValue().equals("其他") && keySet.contains("const_allergen_other")) {

                JSONObject object = new JSONObject();
                object.put("valueText", option.getAttrValue());
                object.put("attrValue", option.getId());
                formField.setOtherValue(otherAllergenText);
                valJson.add(object);
            };
        }
        if (CollUtil.isNotEmpty(valJson)) {
            formField.setValues(valJson.toJSONString());
        }
    }

    /**
     * 设置过敏原的方式。并这是方式下的子题目的过敏原
     * @param option
     * @param allergenSuction
     * @return
     */
    private JSONObject getAllergenValues(FormOptions option, List<Constant> allergenSuction) {
        JSONObject object = new JSONObject();
        object.put("valueText", option.getAttrValue());
        object.put("attrValue", option.getId());
        List<FormField> questions = option.getQuestions();
        if (CollUtil.isNotEmpty(questions)) {
            FormField field = questions.get(0);
            List<String> labelNames = allergenSuction.stream().map(Constant::getLabel).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(labelNames)) {
                setCheckBoxValue(labelNames, field, null);
            }
        }
        object.put("questions", questions);
        return object;
    }

    /**
     * 家族过敏史 和 伴随诊断
     * @param merckValues
     * @param formField
     * @param valJson
     */
    private void setCheckBoxValue(List<String> merckValues, FormField formField, JSONArray valJson) {

        List<FormOptions> options = formField.getOptions();
        // 有值 就不要操作
        if (CollUtil.isNotEmpty(valJson)) {
            Object o = valJson.get(0);
            JSONObject object = JSONObject.parseObject(o.toString());
            if (object.get("attrValue") != null) {
                return;
            }
        }
        valJson = new JSONArray();
        if (CollUtil.isNotEmpty(options)) {
            for (FormOptions option : options) {
                String attrValue = option.getAttrValue();
                if (merckValues.contains(attrValue)) {
                    JSONObject object = new JSONObject();
                    object.put("valueText", option.getAttrValue());
                    object.put("attrValue", option.getId());
                    valJson.add(object);
                }
            }
            if (CollUtil.isEmpty(valJson)) {
                return;
            }
            formField.setValues(JSONArray.toJSONString(valJson));
        }

    }

    /**
     * 创建或者更新患者的基本信息表单
     * @param healthFile
     * @param patientId
     * @param baseInfoData
     */
    private void createOrUpdateBaseInfo(HealthFile healthFile, Long patientId, FormResult baseInfoData) {
        if (Objects.isNull(healthFile)) {
            return;
        }
        Person person = healthFile.getPerson();
        String name = null;
        String sex = null;
        String contactPhone = null;
        String relationship = null;
        String birth = null;
        String imageUrl = null;
        String injectionMedicineName = null;
        if (Objects.nonNull(person)) {
            // 姓名
            name = person.getName();
            sex = person.getSex();   // 是常量。 需要翻译
            contactPhone = person.getContactPhone(); // 联系电话
            relationship = person.getRelationship(); // 联系人关系
            birth = person.getBirth(); // 出生年月
            Image headImage = person.getHeadImage();
            imageUrl = headImage.getImageUrl();
            // 注射用药
            injectionMedicineName = person.getInjectionMedicineName();
        }

        List<?> fieldList = baseInfoData.getFieldList();
        String jsonString = JSONArray.toJSONString(fieldList);
        List<FormField> formFields = JSONArray.parseArray(jsonString, FormField.class);
        // 根据悠扬的笛声项目配置的基本信息表单实现。
        for (FormField formField : formFields) {
            String label = formField.getLabel();
            if (StrUtil.isEmpty(label)) {
                continue;
            }
            String values = formField.getValues();
            JSONArray valJson;
            if (StrUtil.isEmpty(values)) {
                valJson = new JSONArray();
            } else {
                valJson = JSONArray.parseArray(values);
            }
            Object o = valJson.get(0);
            if ("头像".equals(label) && StrUtil.isNotEmpty(imageUrl)) {

                setStringValues(o, imageUrl, formField, valJson);
            } else if ("姓名".equals(label) && StrUtil.isNotEmpty(name)) {

                setStringValues(o, name, formField, valJson);
            } else if ("性别".equals(label) && StrUtil.isNotEmpty(sex)) {

                String sexLabel = getMerckConstantLabel(sex);
                if (StrUtil.isEmpty(sexLabel)) {
                    continue;
                }
                setRadioValue(o, sexLabel, formField, valJson, true);
            } else if ("出生年月".equals(label) && StrUtil.isNotEmpty(birth)) {
                // "1994-11-18"
                birth = birth.substring(0, 10);
                setStringValues(o, birth, formField, valJson);
            } else if ("联系人电话".equals(label)) {
                setStringValues(o, contactPhone, formField, valJson);
            } else if (label.contains("联系人与患者的关系") && StrUtil.isNotEmpty(relationship)) {

                setRadioValue(o, relationship, formField, valJson, false);
            } else if (label.contains("目前是否在进行脱敏治疗") && StrUtil.isNotEmpty(injectionMedicineName)) {

                List<FormOptions> options = formField.getOptions();
                FormOptions formOptions = findFormOptions("脱敏治疗", options, true);
                if (Objects.isNull(formOptions)) {
                    return;
                }
                List<FormField> questions = formOptions.getQuestions();
                if (CollUtil.isNotEmpty(questions)) {
                    FormField field = questions.get(0);
                    String fieldValues = field.getValues();
                    JSONArray questionValJson;
                    if (StrUtil.isEmpty(fieldValues)) {
                        questionValJson = new JSONArray();
                    } else {
                        questionValJson = JSONArray.parseArray(values);
                    }
                    Object questionValue = valJson.get(0);
                    setRadioValue(questionValue, injectionMedicineName, field, questionValJson, true);
                }
                if (Objects.isNull(o)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("valueText", formOptions.getAttrValue());
                    jsonObject.put("attrValue", formOptions.getId());
                    jsonObject.put("questions", questions);
                    valJson.add(jsonObject);
                    formField.setValues(valJson.toJSONString());
                } else {
                    JSONObject jsonObject = JSON.parseObject(o.toString());
                    if (Objects.isNull(jsonObject.get("attrValue"))) {

                        jsonObject.put("valueText", formOptions.getAttrValue());
                        jsonObject.put("attrValue", formOptions.getId());
                        jsonObject.put("questions", questions);
                        valJson = new JSONArray();
                        valJson.add(jsonObject);
                        formField.setValues(valJson.toJSONString());
                    }
                }
            }
        }

        if (Objects.isNull(baseInfoData.getId())) {
            // 走新增接口
            FormResultSaveDTO resultSaveDTO = new FormResultSaveDTO();
            resultSaveDTO.setCategory(baseInfoData.getCategory());
            resultSaveDTO.setFormId(baseInfoData.getFormId());
            resultSaveDTO.setUserId(patientId);
            resultSaveDTO.setJsonContent(JSON.toJSONString(formFields));
            resultSaveDTO.setName(baseInfoData.getName());
            resultSaveDTO.setOneQuestionPage(baseInfoData.getOneQuestionPage());
            formResultApi.save(resultSaveDTO);

        } else {
            // 走更新接口
            FormResultUpdateDTO updateDTO = new FormResultUpdateDTO();
            updateDTO.setCategory(baseInfoData.getCategory());
            updateDTO.setFormId(baseInfoData.getFormId());
            updateDTO.setUserId(patientId);
            updateDTO.setJsonContent(JSON.toJSONString(formFields));
            updateDTO.setId(baseInfoData.getId());
            updateDTO.setName(baseInfoData.getName());
            updateDTO.setOneQuestionPage(baseInfoData.getOneQuestionPage());
            formResultApi.update(updateDTO);
        }
    }

    /**
     * 设置单选类的值
     * @param value
     * @param merckValue
     * @param formField
     * @param valJson
     */
    private void setRadioValue(Object value, String merckValue, FormField formField, JSONArray valJson, Boolean equals) {
        List<FormOptions> options = formField.getOptions();
        if (Objects.isNull(value)) {
            JSONObject jsonObject = new JSONObject();
            FormOptions formOptions = findFormOptions(merckValue, options, equals);
            if (Objects.isNull(formOptions)) {
                return;
            }
            jsonObject.put("valueText", formOptions.getAttrValue());
            jsonObject.put("attrValue", formOptions.getId());
            valJson.add(jsonObject);
            formField.setValues(valJson.toJSONString());
        } else {
            JSONObject jsonObject = JSON.parseObject(value.toString());
            if (Objects.isNull(jsonObject.get("attrValue"))) {
                FormOptions formOptions = findFormOptions(merckValue, options, equals);
                if (Objects.isNull(formOptions)) {
                    return;
                }
                jsonObject.put("valueText", formOptions.getAttrValue());
                jsonObject.put("attrValue", formOptions.getId());
                valJson = new JSONArray();
                valJson.add(jsonObject);
                formField.setValues(valJson.toJSONString());
            }
        }
    }

    /**
     * 设置普通单行类的值
     * @param value
     * @param merckValue
     * @param formField
     * @param valJson
     */
    private void setStringValues(Object value, String merckValue, FormField formField, JSONArray valJson) {
        if (Objects.isNull(value)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("attrValue", merckValue);
            jsonObject.put("attrText", merckValue);
            valJson.add(jsonObject);
            formField.setValues(valJson.toJSONString());
        } else {
            JSONObject jsonObject = JSON.parseObject(value.toString());
            if (Objects.isNull(jsonObject.get("attrValue"))) {
                jsonObject.put("attrValue", merckValue);
                jsonObject.put("attrText", merckValue);
                valJson = new JSONArray();
                valJson.add(jsonObject);
                formField.setValues(valJson.toJSONString());
            }
        }
    }

    /**
     * 查询单选中符合条件的选项
     * @param merckLabel
     * @param options
     * @return
     */
    private FormOptions findFormOptions(String merckLabel, List<FormOptions> options, Boolean equals) {
        FormOptions formOptions = null;
        for (FormOptions option : options) {
            if (equals && merckLabel.equals(option.getAttrValue())) {
                formOptions = option;
                break;
            } else if (merckLabel.contains(option.getAttrValue())) {
                formOptions = option;
                break;
            }
        }
        return formOptions;
    }

    /**
     * 查询患者的敏识检验报告
     * @param personId
     * @return
     */
    private List<Image> findCheckDataImage(Long personId) {
        try {
            String personHealthFileUrl = String.format(PERSON_CHECK_DATA, this.merckDomain, personId);
            HttpRequest request = HttpRequest.get(personHealthFileUrl);
            request.header("X-Mg-Auth-Token", this.merckToken);
            HttpResponse execute = request.execute();
            String body = execute.body();
            if (StrUtil.isNotEmpty(body)) {
                return JSONArray.parseArray(body, Image.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取敏识患者的初诊病例异常");
        }
        return null;

    }

    /**
     * 获取敏识患者的初诊病例
     * @param personId
     * @return
     */
    private FirstVisitCasesDTO findFirstVisitCasesByCustomerId(Long personId) {
        try {
            String personHealthFileUrl = String.format(personFirstVisitDisease, this.merckDomain, personId);
            HttpRequest request = HttpRequest.get(personHealthFileUrl);
            request.header("X-Mg-Auth-Token", this.merckToken);
            HttpResponse execute = request.execute();
            String body = execute.body();
            if (StrUtil.isNotEmpty(body)) {
                return JSONObject.parseObject(body, FirstVisitCasesDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取敏识患者的初诊病例异常");
        }
        return null;
    }

    /**
     * 获取健康档案
     * @param personId
     * @return
     */
    private HealthFile getMerckHealthFile(Long personId) {
        try {
            String personHealthFileUrl = String.format(personHealthFile, this.merckDomain, personId);
            HttpRequest request = HttpRequest.get(personHealthFileUrl);
            request.header("X-Mg-Auth-Token", this.merckToken);
            HttpResponse execute = request.execute();
            String body = execute.body();
            if (StrUtil.isNotEmpty(body)) {
                return JSONObject.parseObject(body, HealthFile.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取敏识患者的健康档案异常 {}");
        }
        return null;

    }


    /**
     * 创建一个患者
     * @param doctor
     * @param wxAppId
     * @param importOpenId
     * @param unionId
     * @return
     */
    public Patient createPatient(Doctor doctor, String wxAppId, String importOpenId, String unionId) {
        Patient patient = new Patient();
        patient.setServiceAdvisorId(doctor.getNursingId());
        patient.setOrganCode(doctor.getOrganCode());
        patient.setOrganId(doctor.getOrganId());
        patient.setOrganName(doctor.getOrganName());
        patient.setDoctorId(doctor.getId());
        patient.setDoctorName(doctor.getName());
        patient.setClassCode(doctor.getClassCode());
        patient.setServiceAdvisorId(doctor.getNursingId());
        patient.setServiceAdvisorName(doctor.getNursingName());

        long id = IdUtil.getSnowflake(databaseProperties.getId().getWorkerId(), databaseProperties.getId().getDataCenterId()).nextId();
        String imAccount = imService.registerAccount(ImService.ImAccountKey.PATIENT, id);
        patient.setWxAppId(wxAppId);
        patient.setImportWxOpenId(importOpenId);
        patient.setUnionId(unionId);
        patient.setIsCompleteEnterGroup(1);
        patient.setImGroupStatus(1);
        patient.setNursingExitChat(0);
        patient.setDoctorExitChat(0);


        // 由于通过微信关注已经无法获取微信用户的昵称 头像，此处 取消 拉取关注用户信息。
        patient.setAvatar("https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/head-portrait-patient.png");
        // 给患者初始化一个随机字符串作为标记， web端和微信端对比查找患者的依据
        String number = Key.generate6Number();
        patient.setNickName(number);
        // 用户初次入组时，name字段要根据系统设置决定是否填充
        patient.setName(number);
        String letter = ChineseToEnglishUtil.getPinYinFirstLetter(number);
        if (StringUtils.isEmpty(letter)) {
            letter = "Z#";
        }
        patient.setNameFirstLetter(letter);

        patient.setImAccount(imAccount);
        patient.setStatus(Patient.PATIENT_NO_SUBSCRIBE);
        patientService.save(patient);
        return patient;
    }



    public String getMerckConstantLabel(String constant) {
        if (merckConstant == null) {
            merckConstant = new HashMap<>();
        }
        String label = merckConstant.get(constant);
        if (StrUtil.isNotEmpty(label)) {
            return label;
        }
        return queryMerckConstantLabel(constant);
    }

    private synchronized String queryMerckConstantLabel(String constant) {
        String label = merckConstant.get(constant);
        if (StrUtil.isNotEmpty(label)) {
            return label;
        }
        MerckConstant selectOne = merckConstantMapper.selectOne(Wraps.<MerckConstant>lbQ().eq(MerckConstant::getConstKey, constant));
        if (Objects.isNull(selectOne)) {
            return null;
        }
        label = selectOne.getLabel();
        merckConstant.put(constant, label);
        return label;
    }

}
