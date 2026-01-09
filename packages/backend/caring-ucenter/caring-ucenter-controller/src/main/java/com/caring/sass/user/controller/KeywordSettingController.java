package com.caring.sass.user.controller;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.user.dto.KeywordSettingPageDTO;
import com.caring.sass.user.dto.KeywordSettingSaveDTO;
import com.caring.sass.user.dto.KeywordSettingUpdateDTO;
import com.caring.sass.user.entity.KeywordSetting;
import com.caring.sass.user.service.KeywordSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 关键字设置
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/keywordSetting")
@Api(value = "KeywordSetting", tags = "关键字设置")
//@PreAuth(replace = "keywordSetting:")
public class KeywordSettingController extends SuperController<KeywordSettingService, Long, KeywordSetting, KeywordSettingPageDTO, KeywordSettingSaveDTO, KeywordSettingUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<KeywordSetting> keywordSettingList = list.stream().map((map) -> {
            KeywordSetting keywordSetting = KeywordSetting.builder().build();
            //TODO 请在这里完成转换
            return keywordSetting;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(keywordSettingList));
    }


    @ApiOperation("测试")
    @GetMapping("test")
    public void getTest() {
        baseService.syncUpdateKeywordLeaderboard();
    }
}
