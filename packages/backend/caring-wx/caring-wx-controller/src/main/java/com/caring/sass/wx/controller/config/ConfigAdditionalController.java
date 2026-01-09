package com.caring.sass.wx.controller.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.wx.dto.config.GeneralForm;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.dto.config.ConfigAdditionalSaveDTO;
import com.caring.sass.wx.dto.config.ConfigAdditionalUpdateDTO;
import com.caring.sass.wx.dto.config.ConfigAdditionalPageDTO;
import com.caring.sass.wx.service.config.ConfigAdditionalService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * <p>
 * 前端控制器
 * 微信附加信息配置
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/configAdditional")
@Api(value = "ConfigAdditional", tags = "微信附加信息配置")
//@PreAuth(replace = "configAdditional:")
public class ConfigAdditionalController extends SuperController<ConfigAdditionalService, Long, ConfigAdditional, ConfigAdditionalPageDTO, ConfigAdditionalSaveDTO, ConfigAdditionalUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<ConfigAdditional> configAdditionalList = list.stream().map((map) -> {
            ConfigAdditional configAdditional = ConfigAdditional.builder().build();
            //TODO 请在这里完成转换
            return configAdditional;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(configAdditionalList));
    }


    @ApiOperation("获取公众号路径配置  模板url 登录url 等配置")
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(paramType = "body", name = "form", value = "参数，微信回调时传递的各种参数", required = true)})
    @PostMapping({"/getConfigAdditionalByWxAppId"})
    public R<ConfigAdditional> getConfigAdditionalByWxAppId(@RequestBody GeneralForm paramGeneralForm) {
        ConfigAdditional setting;
        if (!StringUtils.isEmpty(paramGeneralForm.getWxAppId())) {
            setting = baseService.getConfigAdditionalByWxAppId(paramGeneralForm.getWxAppId());
            if (Objects.nonNull(setting)) {
                return R.success(setting);
            }
        }

        // 使用租户 查询他的 公众号路径配置
        setting = baseService.getOne(Wrappers.lambdaQuery());
        if (Objects.nonNull(setting)) {
            return R.success(setting);
        } else {
            return R.fail("参数异常。wxAppId和租户 都为空");
        }
    }



    @ApiOperation("更新公众号路径配置  模板url 登录url 等配置")
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(paramType = "body", name = "form", required = true, dataTypeClass = ConfigAdditional.class)})
    @PostMapping({"/updateConfigAdditional"})
    public R<Boolean> updateConfigAdditional(@RequestBody ConfigAdditional configAdditional) {
        boolean all = baseService.updateAllById(configAdditional);
        return R.success(all);
    }



    @ApiOperation("获取项目 关键词回复状态是否开启")
    @GetMapping({"/keyWordStatus"})
    public R<Integer> getKeyWordStatus() {
        Integer status = baseService.getKeyWordStatus();
        return R.success(status);
    }

    @ApiOperation("更新 关键词回复状态")
    @PutMapping({"/keyWordStatus"})
    public R<Integer> updateKeyWordStatus(@PathVariable(value = "keyWordStatus", name = "1 开启， 2 关闭") Integer keyWordStatus) {
        Integer status = baseService.updateKeyWordStatus(keyWordStatus);
        return R.success(status);
    }


    @ApiOperation("获取项目 自动回复状态是否开启")
    @GetMapping({"/automaticReply"})
    public R<Integer> getAutomaticReply() {
        Integer status = baseService.getAutomaticReply();
        return R.success(status);
    }


    @ApiOperation("更新 自动回复状态")
    @PutMapping({"/automaticReply"})
    public R<Integer> updateAutomaticReply(@PathVariable(value = "automaticReply", name = "1 开启， 2 关闭") Integer automaticReply) {
        Integer status = baseService.updateAutomaticReply(automaticReply);
        return R.success(status);
    }

    @ApiOperation("开启或关闭关键字回复")
    @ApiImplicitParams(@ApiImplicitParam(name = "status", value = "是否开启关键字回复：1打开，2关闭", dataType = "int", paramType = "query"))
    @PutMapping({"/switchKeyword"})
    public R<Boolean> switchKeyword(@RequestParam(value = "status") Integer status) {
        baseService.updateKeyWordStatus(status);
        return R.success(true);
    }

    @ApiOperation("开启或关闭自动回复")
    @ApiImplicitParams(@ApiImplicitParam(name = "status", value = "开启或关闭自动回复：1打开，2关闭", dataType = "int", paramType = "query"))
    @PutMapping({"/switchAutomaticReply"})
    public R<Boolean> switchAutomaticReply(@RequestParam(value = "status") Integer status) {
        baseService.updateAutomaticReply(status);
        return R.success(true);
    }


    @ApiOperation("查询开关状态")
    @ApiImplicitParams(@ApiImplicitParam(name = "type", value = "开关类型不能为空：1关键字开关，2消息回复开关", dataType = "int", paramType = "query"))
    @GetMapping({"/switchStatus"})
    public R<Integer> switchStatus(@RequestParam(value = "type") @NotNull(message = "开关类型不能为空") Integer type) {

        ConfigAdditional configAdditional = baseService.getOne(Wraps.lbQ());
        if (configAdditional != null) {
            Integer s = type == 1 ? configAdditional.getKeyWordStatus() : configAdditional.getAutomaticReply();
            return R.success(s);
        }
        return R.success(2);
    }


}
