package com.caring.sass.ai.controller.knows;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.know.*;
import com.caring.sass.ai.entity.know.*;
import com.caring.sass.ai.know.service.KnowledgeDailyCollectionService;
import com.caring.sass.ai.know.service.KnowledgeUserService;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.log.annotation.SysLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 日常收集文本内容
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeDailyCollection")
@Api(value = "KnowledgeDailyCollection", tags = "知识库-日常收集文本内容")
public class KnowledgeDailyCollectionController{

    @Autowired
    KnowledgeUserService knowledgeUserService;


    @Autowired
    KnowledgeDailyCollectionService baseService;

    @ApiOperation("保存日常收集")
    @PostMapping("")
    @SysLog("保存日常收集")
    public R<KnowledgeDailyCollection> save(@RequestBody @Validated KnowledgeDailyCollectionSaveDTO dailyCollectionSaveDTO) {

        KnowledgeDailyCollection collection = new KnowledgeDailyCollection();
        BeanUtils.copyProperties(dailyCollectionSaveDTO, collection);
        collection.setDataType(1);
        baseService.save(collection);

        return R.success(collection);
    }


    @ApiOperation("通过录音对话制作病历SSE返回")
    @PostMapping("createMedicalRecords")
    public R<Boolean> createMedicalRecords(@RequestBody @Validated CreateMedicalRecordsModel createMedicalRecordsModel) {

        baseService.createMedicalRecords(createMedicalRecordsModel);
        return R.success(true);

    }

    @ApiOperation("获取最终的病历内容")
    @GetMapping("getMedicalContent")
    public R<String> getMedicalContent(@RequestParam String uid) {

        String content = baseService.getMedicalContent(uid);
        return R.success(content);

    }


    @ApiOperation("保存病历")
    @PostMapping("saveMedicalRecords")
    @SysLog("保存病历")
    public R<KnowledgeDailyCollection> saveMedicalRecords(@RequestBody @Validated KnowledgeDailyCollectionSaveDTO dailyCollectionSaveDTO) {

        KnowledgeDailyCollection collection = new KnowledgeDailyCollection();
        BeanUtils.copyProperties(dailyCollectionSaveDTO, collection);
        collection.setDataType(2);
        baseService.save(collection);

        return R.success(collection);
    }


    @ApiOperation("获取语音翻译授权")
    @GetMapping("token")
    public R<String> getToken() {
        String buildToken = VolcengineToken.buildToken();
        return R.success(buildToken);
    }

    @ApiOperation("修改文件权限信息")
    @PutMapping("updatePermissions")
    @Deprecated
    @SysLog("修改文件权限信息")
    public R<String> updatePermissions(@RequestBody @Validated KnowledgeDailyCollectionUpdateDTO dailyCollectionUpdateDTO) {
        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = knowledgeUserService.getById(userId);
        if (knowledgeUser == null || KnowDoctorType.GENERAL_PRACTITIONER.equals(knowledgeUser.getUserType())) {
            throw new BizException("权限不足");
        }
//        KnowledgeDailyCollection dailyCollection = baseService.getById(dailyCollectionUpdateDTO.getId());
//        if (dailyCollection.getViewPermissions() == null || !dailyCollection.getViewPermissions().equals(dailyCollectionUpdateDTO.getViewPermissions())) {
//            dailyCollection.setViewPermissions(dailyCollectionUpdateDTO.getViewPermissions());
//            dailyCollection.setIsUpdatePermissions(true);
//        }
//        if (dailyCollection.getDownloadPermission() == null || !dailyCollection.getDownloadPermission().equals(dailyCollectionUpdateDTO.getDownloadPermission())) {
//            dailyCollection.setDownloadPermission(dailyCollectionUpdateDTO.getDownloadPermission());
//            dailyCollection.setIsUpdatePermissions(true);
//        }
//        baseService.updateById(dailyCollection);
        return R.success("success");
    }



    @ApiOperation("更新日常收集")
    @PutMapping("")
    @SysLog("修改日常收集")
    public R<KnowledgeDailyCollection> update(@RequestBody @Validated KnowledgeDailyCollectionUpdateDTO dailyCollectionUpdateDTO) {

        KnowledgeDailyCollection collection = new KnowledgeDailyCollection();
        BeanUtils.copyProperties(dailyCollectionUpdateDTO, collection);
        collection.setId(dailyCollectionUpdateDTO.getId());

        baseService.update(collection);
        return R.success(collection);
    }


    @PostMapping("customPage")
    @ApiOperation(value = "日常收集查询")
    public R<IPage<KnowledgeDailyCollection>> customPage(@RequestBody PageParams<KnowledgeDailyCollectionPageDTO> params) {

        IPage<KnowledgeDailyCollection> builtPage = params.buildPage();
        KnowledgeDailyCollectionPageDTO model = params.getModel();

        Long userId = model.getUserId();
        if (userId == null) {
            userId = BaseContextHandler.getUserId();
        }
        if (Objects.isNull(userId)) {
            return R.success(null);
        }
        LbqWrapper<KnowledgeDailyCollection> wrapper = Wraps.<KnowledgeDailyCollection>lbQ()
                .eq(KnowledgeDailyCollection::getUserId, userId)
                .eq(KnowledgeDailyCollection::getDataType, model.getDataType())
                .like(KnowledgeDailyCollection::getTextTitle, model.getTextTitle());
        wrapper.orderByDesc(KnowledgeDailyCollection::getFileUploadTime);

        wrapper.select(SuperEntity::getId, KnowledgeDailyCollection::getTextTitle,
                KnowledgeDailyCollection::getTextContent, KnowledgeDailyCollection::getFileUrl,
                KnowledgeDailyCollection::getAudioDuration, KnowledgeDailyCollection::getDataType,
                KnowledgeDailyCollection::getFileUploadTime);
        baseService.page(builtPage, wrapper);
        return R.success(builtPage);
    }


    @ApiOperation("查询一个文档的详情")
    @GetMapping("detail/{id}")
    public R<KnowledgeDailyCollection> getAllKnowledgeFile(@PathVariable Long id) {
        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = knowledgeUserService.getById(userId);
        if (Objects.isNull(knowledgeUser)) {
            return R.fail("用户不存在");
        }
        KnowledgeDailyCollection dailyCollection = baseService.getById(id);
//        if (KnowDoctorType.GENERAL_PRACTITIONER.equals(knowledgeUser.getUserType())) {
//            Integer viewPermissions = knowledgeUser.getViewPermissions();
//            if (knowledgeUser.getMembershipExpiration() == null || knowledgeUser.getMembershipExpiration().isBefore(LocalDateTime.now())) {
//                viewPermissions = 1;
//            }
//            if (dailyCollection.getViewPermissions() > viewPermissions) {
//                return R.fail("权限不足");
//            }
//        }

        return R.success(dailyCollection);

    }



    @PostMapping("languageSearch")
    @ApiOperation(value = "自然语言搜索日常收集")
    public R<IPage<KnowledgeDailyCollection>> languageSearch(@RequestBody PageParams<LanguageSearchKnowledge> params) {

        IPage<KnowledgeDailyCollection> builtPage = params.buildPage();
        LanguageSearchKnowledge model = params.getModel();

        String query = model.getQuery();
        // 调用AI ，解析query 其中的条件

        Long userId = model.getUserId();
        if (userId == null) {
            userId = BaseContextHandler.getUserId();
        }
        if (Objects.isNull(userId)) {
            return R.success(null);
        }
        baseService.page(builtPage, query, userId);
        return R.success(builtPage);
    }




    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    @SysLog("删除")
    public R<Boolean> delete(@PathVariable Long id) {

        Long userId = BaseContextHandler.getUserId();
        KnowledgeUser knowledgeUser = knowledgeUserService.getById(userId);
        if (knowledgeUser!= null && KnowDoctorType.GENERAL_PRACTITIONER.equals(knowledgeUser.getUserType())) {
            return R.fail("权限不足");
        }
        baseService.delete(id);
        return R.success(true);
    }


    @ApiOperation("获取标题")
    @PutMapping("title")
    @SysLog("自动生成语音文本的标题")
    public R<String> getTitle(@RequestBody @Validated KnowledgeDailyCollectionTitleDTO titleDTO) {

        String title = baseService.getTitle(titleDTO.getTextContent());
        return R.success(title);

    }


}
