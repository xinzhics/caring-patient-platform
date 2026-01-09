package com.caring.sass.cms.controller;

import com.caring.sass.cms.entity.CmsKeyWord;
import com.caring.sass.cms.dto.KeyWordSaveDTO;
import com.caring.sass.cms.dto.KeyWordUpdateDTO;
import com.caring.sass.cms.dto.KeyWordPageDTO;
import com.caring.sass.cms.service.KeyWordService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.msgs.api.GptDoctorChatApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 关键词表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/cmsKeyWord")
@Api(value = "CmsKeyWord", tags = "关键词表")
public class KeyWordController extends SuperController<KeyWordService, Long, CmsKeyWord, KeyWordPageDTO, KeyWordSaveDTO, KeyWordUpdateDTO> {


    @Autowired
    GptDoctorChatApi gptDoctorChatApi;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<CmsKeyWord> keyWordList = list.stream().map((map) -> {
            CmsKeyWord keyWord = CmsKeyWord.builder().build();
            //TODO 请在这里完成转换
            return keyWord;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(keyWordList));
    }


    @ApiOperation("分析出关键词")
    @GetMapping("getKeyWord")
    public R<Boolean> getKeyWord() {
        baseService.getKeyWord();
        return R.success();
    }

    @ApiOperation("生成AI使用的关键词")
    @GetMapping("filterKeyWord")
    public R<Boolean> filterKeyWord() {
        baseService.filterKeyWord();
        return R.success();
    }



    @ApiOperation("检查医生是否有关键词")
    @GetMapping("checkDoctorHasKeyWord")
    public R<Boolean> checkDoctorHasKeyWord(@RequestParam Long doctorId,  @RequestParam("imAccount") String imAccount) {
        boolean keyWord = baseService.checkDoctorHasKeyWord(doctorId);
        if (keyWord) {
            return R.success(true);
        }
        gptDoctorChatApi.checkSendAiIntroduction(doctorId, imAccount);
        return R.success(false);
    }


    @ApiOperation("查询关键词并返回订阅状态")
    @GetMapping("queryKeyWord")
    public R<List<CmsKeyWord>> queryKeyWord(@RequestParam Long doctorId) {

        List<CmsKeyWord> cmsKeyWords = baseService.queryKeyWord(doctorId);
        return R.success(cmsKeyWords);

    }

    @ApiOperation("订阅关键词")
    @PutMapping("subscribeKeyword/{doctorId}")
    public R<Boolean> subscribeKeyword(
            @PathVariable Long doctorId,
            @RequestBody List<Long> keyWordIds) {
        baseService.subscribeKeyword(doctorId, keyWordIds);
        String tenant = BaseContextHandler.getTenant();
        SaasGlobalThreadPool.execute(() -> sendKeywordCmsToDoctor(tenant, doctorId, keyWordIds));
        return R.success();

    }

    public void sendKeywordCmsToDoctor(String tenant, Long doctorId, List<Long> keyWordIds) {

        BaseContextHandler.setTenant(tenant);
        baseService.sendKeyWordToDoctor(tenant, doctorId, keyWordIds);
        return ;
    }

    @ApiOperation("医生通过ai助手取消订阅关键词")
    @DeleteMapping("cancelSubscribeKeyWord")
    public R<Boolean> cancelSubscribeKeyWord(@RequestParam("doctorId") Long doctorId, @RequestParam("keyWord") String keyWord) {
        boolean b = baseService.cancelSubscribeKeyWord(doctorId, keyWord);
        return R.success(b);
    }


    @ApiOperation("超管推送文章到医生")
    @PutMapping("sendKeyWordCmsToDoctor")
    public R<Boolean> sendKeyWordCmsToDoctor(@RequestParam("cmsId") Long cmsId, @RequestBody List<Long> doctorIds) {

        baseService.sendKeyWordCmsToDoctor(cmsId, doctorIds);
        return R.success(true);
    }





}
