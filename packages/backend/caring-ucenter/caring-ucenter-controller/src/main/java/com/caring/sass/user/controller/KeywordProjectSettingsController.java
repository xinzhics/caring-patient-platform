package com.caring.sass.user.controller;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.dto.KeywordProjectSettingsPageDTO;
import com.caring.sass.user.dto.KeywordProjectSettingsSaveDTO;
import com.caring.sass.user.dto.KeywordProjectSettingsUpdateDTO;
import com.caring.sass.user.entity.KeywordProjectSettings;
import com.caring.sass.user.service.KeywordProjectSettingsService;
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
 * 项目关键字开关配置
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/keywordProjectSettings")
@Api(value = "KeywordProjectSettings", tags = "项目关键字开关配置")
//@PreAuth(replace = "keywordProjectSettings:")
public class KeywordProjectSettingsController extends SuperController<KeywordProjectSettingsService, Long, KeywordProjectSettings, KeywordProjectSettingsPageDTO, KeywordProjectSettingsSaveDTO, KeywordProjectSettingsUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<KeywordProjectSettings> keywordProjectSettingsList = list.stream().map((map) -> {
            KeywordProjectSettings keywordProjectSettings = KeywordProjectSettings.builder().build();
            //TODO 请在这里完成转换
            return keywordProjectSettings;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(keywordProjectSettingsList));
    }

    @ApiOperation("获取项目关键字配置")
    @GetMapping("getOneKeywordProjectSettings")
    public R<KeywordProjectSettings> getOneKeywordProjectSettings() {
        KeywordProjectSettings projectSettings = baseService.getOne(Wraps.<KeywordProjectSettings>lbQ().last(" limit 0,1 "));
        return R.success(projectSettings);
    }




}
