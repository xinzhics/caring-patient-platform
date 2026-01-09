package com.caring.sass.user.controller;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.user.dto.KeywordTriggerRecordPageDTO;
import com.caring.sass.user.dto.KeywordTriggerRecordSaveDTO;
import com.caring.sass.user.dto.KeywordTriggerRecordUpdateDTO;
import com.caring.sass.user.entity.KeywordTriggerRecord;
import com.caring.sass.user.service.KeywordTriggerRecordService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 关键字触发日期
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/keywordTriggerRecord")
@Api(value = "KeywordTriggerRecord", tags = "关键字触发日期")
//@PreAuth(replace = "keywordTriggerRecord:")
public class KeywordTriggerRecordController extends SuperController<KeywordTriggerRecordService, Long, KeywordTriggerRecord, KeywordTriggerRecordPageDTO, KeywordTriggerRecordSaveDTO, KeywordTriggerRecordUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<KeywordTriggerRecord> keywordTriggerRecordList = list.stream().map((map) -> {
            KeywordTriggerRecord keywordTriggerRecord = KeywordTriggerRecord.builder().build();
            //TODO 请在这里完成转换
            return keywordTriggerRecord;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(keywordTriggerRecordList));
    }
}
