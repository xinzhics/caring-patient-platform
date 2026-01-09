package com.caring.sass.ai.controller.aiBiz;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.ai.dto.know.KnowledgeFileSaveDTO;
import com.caring.sass.ai.dto.userbiz.AiKnowUserSaveBo;
import com.caring.sass.ai.dto.userbiz.AiUserBizBo;
import com.caring.sass.ai.entity.UserJoin;
import com.caring.sass.ai.entity.article.ArticleUser;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.config.KnowChildDomainConfig;
import com.caring.sass.ai.know.service.KnowledgeFileService;
import com.caring.sass.ai.know.service.KnowledgeMenuDomainService;
import com.caring.sass.ai.know.service.KnowledgeMenuService;
import com.caring.sass.ai.service.UserJoinService;
import com.caring.sass.ai.utils.PinyinFormattedUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.file.api.FileUploadApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/AiUser")
@Api(value = "AiUser", tags = "数字分身平台-用户")
public class AiUserBizController {


    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UserJoinService userJoinService;

    @Autowired
    KnowledgeMenuService menuService;

    @Autowired
    KnowledgeMenuDomainService menuDomainService;

    @Autowired
    KnowChildDomainConfig knowChildDomainConfig;

    // 分身平台提交信息。 修改知识库， 并同步覆盖到 科普名片，ai工作室
    @ApiOperation("分身平台提交信息")
    @PostMapping("/submitInfo")
    public R<Boolean> submitInfo(@RequestBody @Validated AiUserBizBo aiUserBizBo) {

        log.info("分身平台提交信息：{}", aiUserBizBo);

        userJoinService.updateKnowledgeUser(aiUserBizBo);

        UserJoin userJoin = userJoinService.getOne(Wraps.<UserJoin>lbQ()
                .eq(UserJoin::getAiKnowUserId, aiUserBizBo.getUserId()));
        if (userJoin == null) {
            userJoin = new UserJoin();
            userJoin.setAiKnowUserId(aiUserBizBo.getUserId());
            userJoinService.save(userJoin);
        }

        // 创建或者更新科普名片
        userJoinService.updateCreateOrUpdateBusinessCard(userJoin, aiUserBizBo);


        // 更新ai工作室医生信息
        if (userJoin.getAiStudioDoctorId() != null) {
            userJoinService.updateAiStudioDoctor(userJoin.getAiStudioTenantId(), aiUserBizBo, userJoin.getAiStudioDoctorId());
        }

        userJoinService.updateById(userJoin);


        return R.success(true);

    }

    @ApiOperation("根据条件创建AI知识库博主")
    @PostMapping("/syncCreateKnowUser")
    public R<JSONObject> syncCreateKnowUser(@RequestBody @Validated AiKnowUserSaveBo aiUserBizBo) {

        KnowledgeUser knowledgeUser = userJoinService.checkUserExist(aiUserBizBo.getPhone());
        if (knowledgeUser == null) {
            knowledgeUser = new KnowledgeUser();
        }

        knowledgeUser.setUserName(aiUserBizBo.getUserName());
        knowledgeUser.setUserMobile(aiUserBizBo.getPhone());
        knowledgeUser.setUserAvatar(aiUserBizBo.getUserAvatar());
        knowledgeUser.setUserType(KnowDoctorType.CHIEF_PHYSICIAN);
        if (aiUserBizBo.getShowInDocuknow() != null) {
            knowledgeUser.setShowInDocuKnow(aiUserBizBo.getShowInDocuknow().equals("是"));
        }
        knowledgeUser.setWorkUnit(aiUserBizBo.getWorkUnit());
        knowledgeUser.setDepartment(aiUserBizBo.getDepartment());
        knowledgeUser.setRealHumanAvatar(aiUserBizBo.getRealHumanAvatar());
        knowledgeUser.setGreetingVideo(aiUserBizBo.getGreetingVideo());
        knowledgeUser.setGreetingVideoCover(aiUserBizBo.getGreetingVideoCover());
        knowledgeUser.setDoctorTitle(aiUserBizBo.getDoctorTitle());
        knowledgeUser.setSpecialty(aiUserBizBo.getSpecialty());
        knowledgeUser.setPersonalProfile(aiUserBizBo.getPersonalProfile());
        knowledgeUser.setPassword(SecureUtil.md5("123456asd"));
        if (StrUtil.isBlank(aiUserBizBo.getDataSource())) {
            aiUserBizBo.setDataSource("ai_studio");
        }
        knowledgeUser.setDataSource(aiUserBizBo.getDataSource());
        knowledgeUser.setThirdPartyUserId(aiUserBizBo.getChildUserId());

        String pinyin = PinyinFormattedUtil.getFormattedPinyin(knowledgeUser.getUserName());
        while (true) {
            // 如果域名已经被使用。在pinyin 后面随机拼接两个字母。
            if (userJoinService.checkUserDomain(pinyin)) {
                break;
            }
            pinyin = pinyin + RandomStringUtils.randomAlphabetic(1);
        }

        knowledgeUser.setUserDomain(pinyin);

        String domain1Name = aiUserBizBo.getDomain1Name();
        String domain2Name = aiUserBizBo.getDomain2Name();
        String domain2 = aiUserBizBo.getDomain2();
        String menuDomainName = aiUserBizBo.getMenuDomainName();
        KnowledgeMenu menu;
        KnowledgeMenuDomain menuDomain;

        menuDomain = menuDomainService.getOne(Wraps.<KnowledgeMenuDomain>lbQ()
                .eq(KnowledgeMenuDomain::getMenuDomain, knowChildDomainConfig.getDocuknowMainDoamin())
                .last("limit 1"));
        knowledgeUser.setMenuDomain(menuDomain.getMenuDomain());

        // 总平台
        if (StrUtil.isNotBlank(domain1Name)) {
            menu = menuService.getOne(Wraps.<KnowledgeMenu>lbQ()
                    .eq(KnowledgeMenu::getMenuName, domain1Name)
                    .eq(KnowledgeMenu::getDomainId, menuDomain.getId()));
            if (menu == null) {
                menu = new KnowledgeMenu();
                menu.setMenuName(domain1Name);
                menu.setParentId("0");
                menu.setDomainId(menuDomain.getId());
                menuService.save(menu);
            }
            knowledgeUser.setKnowledgeMenuId(menu.getId().toString());
        }
        // 子平台域名
        if (StrUtil.isNotBlank(menuDomainName)) {
            menuDomain = menuDomainService.getOne(Wraps.<KnowledgeMenuDomain>lbQ()
                    .eq(KnowledgeMenuDomain::getMenuDomain, menuDomainName)
                    .last("limit 1"));
            if (menuDomain == null) {
                menuDomain = new KnowledgeMenuDomain();
                menuDomain.setMenuId(domain2);
                menuDomain.setMenuDomain(menuDomainName);
                menuDomainService.save(menuDomain);
            }
            List<KnowledgeUserDomain> knowledgeUserDomains = new ArrayList<>();
            KnowledgeUserDomain userDomain = new KnowledgeUserDomain();
            knowledgeUserDomains.add(userDomain);
            userDomain.setDomainName(menuDomainName);

            if (StrUtil.isNotBlank(domain2Name)) {
                menu = menuService.getOne(Wraps.<KnowledgeMenu>lbQ()
                        .eq(KnowledgeMenu::getMenuName, domain2Name)
                        .eq(KnowledgeMenu::getDomainId, menuDomain.getId()));
                if (menu == null) {
                    menu = new KnowledgeMenu();
                    menu.setMenuName(domain2Name);
                    menu.setParentId("0");
                    menu.setDomainId(menuDomain.getId());
                    menuService.save(menu);
                }
                userDomain.setDomainMenuId(menu.getId().toString());
            }
            knowledgeUser.setKnowledgeUserDomains(knowledgeUserDomains);
        }

        // 创建博主的数据是来自 ai工作室
        JSONObject jsonObject = new JSONObject();
        if (aiUserBizBo.getDataSource().equals("ai_studio")) {

            String openDocuknow = aiUserBizBo.getOpenDocuknow();
            knowledgeUser.setAiStudioUrl(knowChildDomainConfig.getAiStudioDomain() +"/doctor?doctorId=" +aiUserBizBo.getDoctorId());
            if (openDocuknow != null && openDocuknow.equals("是")) {
                knowledgeUser.setShowInDocuKnow(true);
                String userMobile = knowledgeUser.getUserMobile();
                String code = userMobile.substring(userMobile.length() - 6);
                redisTemplate.boundSetOps("ai_know_temp_code:" + userMobile).add(code);

                userJoinService.createKnowledgeUser(knowledgeUser, true);

                jsonObject.put("knowledgeLink", "https://knows.docknowai.com/" + knowledgeUser.getUserDomain());

                UserJoin userJoin = new UserJoin();
                if (knowledgeUser.getId() != null) {
                    userJoin.setAiKnowUserId(knowledgeUser.getId());
                }
                userJoin.setAiStudioDoctorId(aiUserBizBo.getDoctorId());
                userJoin.setAiStudioTenantId(aiUserBizBo.getTenantId());
                userJoinService.save(userJoin);

            }


            String openArticle = aiUserBizBo.getOpenArticle();
            if (openArticle != null && openArticle.equals("是")) {
                ArticleUser articleUser = userJoinService.createArticleUser(knowledgeUser.getUserMobile());
                jsonObject.put("articleUserId", articleUser.getId().toString());
            }

            jsonObject.put("knowledgeMenuId", knowledgeUser.getKnowledgeMenuId());
            jsonObject.put("menuDomain", knowledgeUser.getMenuDomain());
            jsonObject.put("childDomains", knowledgeUser.getKnowledgeUserDomains());
        } else if (aiUserBizBo.getDataSource().equals("saibolan")) {

            String saibolanDomain = knowChildDomainConfig.getSaibolanDomain();
            knowledgeUser.setThirdPartyKnowledgeLink(saibolanDomain + "/" + knowledgeUser.getUserDomain());
            userJoinService.createKnowledgeUser(knowledgeUser, false);
        }


        return R.success(jsonObject);


    }


    @ApiOperation("赛柏蓝博主信息修改后，同步到分身平台")
    @PostMapping("/syncToSaibolanKnowledgeUser")
    public R<Boolean> syncToSaibolanKnowledgeUser(@RequestBody @Validated AiUserBizBo aiUserBizBo) {


        userJoinService.updateKnowledgeUser(aiUserBizBo);

        return R.success(true);

    }

    @ApiOperation("AI工作室修改语音通话信息")
    @PostMapping("/updateAiStudioDoctorId/{doctorId}")
    public R<Boolean> updateAiStudioDoctorId(@PathVariable Long doctorId,
                                                @RequestParam Boolean openFunction,
                                                @RequestParam Long aiStudioDoctorId) {

        UserJoin userJoin = userJoinService.getOne(Wraps.<UserJoin>lbQ()
                .eq(UserJoin::getAiStudioDoctorId, doctorId));
        if (userJoin == null) {
            return R.fail("用户不存在");
        }
        if (userJoin.getAiKnowUserId() != null) {
            userJoinService.updateAiStudioDoctorId(userJoin.getAiKnowUserId(), openFunction, aiStudioDoctorId);
        }
        return R.success(true);

    }



    @ApiOperation("AI工作室修改后， 同步到 分身平台 和 科普名片")
    @PostMapping("/syncToAiStudioAndKnowledge/{doctorId}")
    public R<Boolean> syncToAiStudioAndKnowledge(@PathVariable Long doctorId,
                                                 @RequestBody @Validated AiUserBizBo aiUserBizBo) {

        log.info("AI工作室修改后， 同步到 分身平台 和 科普名片：{}", aiUserBizBo);
        UserJoin userJoin = userJoinService.getOne(Wraps.<UserJoin>lbQ()
                .eq(UserJoin::getAiStudioDoctorId, doctorId));
        if (userJoin == null) {
            return R.fail("用户不存在");
        }
        if (userJoin.getAiKnowUserId() != null) {
            aiUserBizBo.setUserId(userJoin.getAiKnowUserId());
            userJoinService.updateKnowledgeUser(aiUserBizBo);
        }
        if (userJoin.getBusinessCardId() != null) {
            userJoinService.updateCreateOrUpdateBusinessCard(userJoin, aiUserBizBo);
        }
        return R.success(true);

    }



    @Autowired
    KnowledgeFileService fileService;

    @Autowired
    FileUploadApi fileUploadApi;



    @ApiOperation("导入医生账号")
    @GetMapping("importKnowledgeUser")
    public R<Void> importKnowledgeUser(@RequestParam String menuId, @RequestParam String menuDomain) {

        String path = "/mnt/data/caring-ai";
        // 读取本地文件。
        String userFile = path + "/医生个人资料.xlsx";
        String gerenchengguoPath = path + "/个人成果/";
        String userAvatar =  path + "/用户头像/";
        String numberAvatar = path + "/数字人头像/";

        try (FileInputStream fis = new FileInputStream(new File(userFile));
             Workbook workbook = getWorkbook(fis, userFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            // 遍历每一行
            int i = 0;
            for (Row row : sheet) {
                // 遍历每一列
                if (i > 0) {
                    KnowledgeUser knowledgeUser = new KnowledgeUser();
                    Cell cell = row.getCell(0); // 姓名

                    knowledgeUser.setUserName(getCellValue(cell));

                    cell = row.getCell(1); // 手机号
                    knowledgeUser.setUserMobile(getCellValue(cell));

                    cell = row.getCell(2); // 单位
                    if (cell != null) {
                        knowledgeUser.setWorkUnit(getCellValue(cell));
                    }

                    cell = row.getCell(3); // 科室
                    if (cell != null) {
                        knowledgeUser.setDepartment(getCellValue(cell));
                    }

                    cell = row.getCell(4); // 职称
                    if (cell != null) {
                        knowledgeUser.setDoctorTitle(getCellValue(cell));
                    }

                    cell = row.getCell(5); // 个人简介
                    if (cell != null) {
                        knowledgeUser.setPersonalProfile(getCellValue(cell));
                    }

                    String createBusinessCard = "no";
                    cell = row.getCell(6); // 是否创建科普名片
                    if (cell != null) {
                        createBusinessCard = getCellValue(cell);
                    }

                    String tenantId = null;
                    cell = row.getCell(7); // ai工作室医生租户
                    if (cell != null) {
                        tenantId = getCellValue(cell);
                    }

                    String assistantId = null;
                    cell = row.getCell(8); // ai工作室医生医助id
                    if (cell != null) {
                        assistantId = getCellValue(cell);
                    }

                    knowledgeUser.setUserDomain(PinyinFormattedUtil.getFormattedPinyin(knowledgeUser.getUserName()));
                    knowledgeUser.setKnowledgeMenuId(menuId);
                    knowledgeUser.setMenuDomain(menuDomain);

                    // 获取医生的 数字人头像 和 真人头像
                    File file = new File(numberAvatar + knowledgeUser.getUserName() + ".jpg");
                    if (file.exists()) {
                        R<com.caring.sass.file.entity.File> uploaded = fileUploadApi.upload(1L, FileUtils.fileToFileItem(file));
                        com.caring.sass.file.entity.File data = uploaded.getData();
                        if (data != null) {
                            knowledgeUser.setUserAvatar(data.getUrl());
                        }
                    }
                    file = new File(userAvatar + knowledgeUser.getUserName() + ".jpg");
                    if (file.exists()) {
                        R<com.caring.sass.file.entity.File> uploaded = fileUploadApi.upload(1L, FileUtils.fileToFileItem(file));
                        com.caring.sass.file.entity.File data = uploaded.getData();
                        if (data != null) {
                            knowledgeUser.setRealHumanAvatar(data.getUrl());
                        }
                    }
                    String userMobile = knowledgeUser.getUserMobile();
                    userJoinService.createKnowledgeUser(knowledgeUser,true);

                    // 导入个人成果
                    List<KnowledgeFileSaveDTO> fileList = new ArrayList<>();
                    File gerenchengguo = new File(gerenchengguoPath + knowledgeUser.getUserName());
                    if (gerenchengguo.exists()) {
                        File[] listFiles = gerenchengguo.listFiles();
                        if (listFiles != null) {

                            for (File listFile : listFiles) {
                                KnowledgeFileSaveDTO saveDTO = new KnowledgeFileSaveDTO();
                                saveDTO.setFileSize(listFile.length());
                                saveDTO.setFileUrl(fileUploadApi.upload(1L, FileUtils.fileToFileItem(listFile)).getData().getUrl());
                                saveDTO.setFileName(listFile.getName());
                                fileList.add(saveDTO);
                            }
                        }
                    }

                    if (CollUtil.isNotEmpty(fileList)) {
                        fileService.importKnowledgeFile(knowledgeUser.getId(), KnowledgeType.PERSONAL_ACHIEVEMENTS, fileList);
                    }

                    if (StrUtil.isNotBlank(userMobile) && userMobile.length() > 6) {
                        String code = userMobile.substring(userMobile.length() - 6);
                        redisTemplate.boundSetOps("ai_know_temp_code:" + userMobile).add(code);
                    }

                    UserJoin userJoin = new UserJoin();
                    userJoin.setAiKnowUserId(knowledgeUser.getId());
                    userJoinService.save(userJoin);

                    AiUserBizBo aiUserBizBo = new AiUserBizBo();
                    aiUserBizBo.setUserId(knowledgeUser.getId());
                    aiUserBizBo.setUserName(knowledgeUser.getUserName());
                    aiUserBizBo.setUserAvatar(knowledgeUser.getUserAvatar());
                    aiUserBizBo.setRealHumanAvatar(knowledgeUser.getRealHumanAvatar());
                    aiUserBizBo.setWorkUnit(knowledgeUser.getWorkUnit());
                    aiUserBizBo.setDepartment(knowledgeUser.getDepartment());
                    aiUserBizBo.setDoctorTitle(knowledgeUser.getDoctorTitle());
                    aiUserBizBo.setPersonalProfile(knowledgeUser.getPersonalProfile());

                    if ("yes".equals(createBusinessCard)) {
                        userJoinService.updateCreateOrUpdateBusinessCard(userJoin, aiUserBizBo);
                    }
                    if (tenantId != null && assistantId != null) {
                        userJoin.setAiStudioTenantId(tenantId);
                        JSONObject studioDoctor = userJoinService.createAiStudioDoctor(tenantId, aiUserBizBo, userMobile, Long.parseLong(assistantId), menuDomain, knowledgeUser.getUserDomain());
                        if (studioDoctor != null) {
                            JSONObject data = studioDoctor.getJSONObject("data");
                            String studioDoctorId = data.getString("id");

                            knowledgeUser.setAiStudioUrl("https://cstudio.allercura.cn?doctorId=" + studioDoctorId);
                            userJoinService.updateKnowledgeUser(knowledgeUser);
                        }
                    }

                }
                i++;

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(null);

    }


    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    // 判断是 .xls 还是 .xlsx 格式
    private static Workbook getWorkbook(FileInputStream fis, String filePath) throws IOException {
        if (filePath.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (filePath.endsWith(".xls")) {
            return new HSSFWorkbook(fis);
        } else {
            throw new IllegalArgumentException("文件格式不支持！");
        }
    }


}
