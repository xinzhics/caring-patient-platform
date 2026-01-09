package com.caring.sass.tenant.controller.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.wx.ConfigAdditionalApi;
import com.caring.sass.wx.KeyWordApi;
import com.caring.sass.wx.dto.keyword.AutomaticReplyDto;
import com.caring.sass.wx.dto.keyword.KeywordPageDTO;
import com.caring.sass.wx.dto.keyword.KeywordSaveDTO;
import com.caring.sass.wx.dto.keyword.KeywordUpdateDTO;
import com.caring.sass.wx.entity.keyword.Keyword;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author xinzh
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/keywordConfig")
@Api(value = "keywordConfig", tags = "项目微信关键字配置")
public class KeyWordConfigController {

    private final KeyWordApi keyWordApi;

    private final ConfigAdditionalApi configAdditionalApi;

    public KeyWordConfigController(KeyWordApi keyWordApi, ConfigAdditionalApi configAdditionalApi) {
        this.keyWordApi = keyWordApi;
        this.configAdditionalApi = configAdditionalApi;
    }

    /**
     * 分页查询关键字
     *
     * @param params 查询参数
     */
    @ApiOperation(value = "分页查询关键字")
    @PostMapping(value = "page/{code}")
    public R<IPage<Keyword>> keywordPage(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                         @RequestBody @Validated PageParams<KeywordPageDTO> params) {
        BaseContextHandler.setTenant(code);
        return keyWordApi.page(params);
    }


    /**
     * 新增关键字
     *
     * @param saveDTO 保存参数
     * @return 实体
     */
    @ApiOperation(value = "新增关键字")
    @PostMapping(value = "/{code}")
    public R<Keyword> save(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                           @RequestBody @Validated KeywordSaveDTO saveDTO) {
        BaseContextHandler.setTenant(code);
        return keyWordApi.save(saveDTO);
    }

    /**
     * 修改关键字
     *
     * @param updateDTO 修改参数
     */
    @ApiOperation(value = "修改关键字")
    @PutMapping(value = "/{code}")
    public R<Keyword> keywordUpdate(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                    @RequestBody KeywordUpdateDTO updateDTO) {
        BaseContextHandler.setTenant(code);
        return keyWordApi.update(updateDTO);
    }

    /**
     * 删除方法
     *
     * @param ids 需要删除的主键
     */
    @ApiOperation(value = "删除关键字")
    @DeleteMapping(value = "/{code}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids[]", value = "主键id", dataType = "array", paramType = "query"),
    })
    public R<Boolean> keywordDelete(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                    @RequestParam("ids[]") List<Long> ids) {
        BaseContextHandler.setTenant(code);
        return keyWordApi.delete(ids);
    }

    /**
     * 获取收到消息回复消息
     */
    @ApiOperation("获取收到消息回复消息")
    @GetMapping(value = "/getAutomaticReply/{code}")
    public R<Keyword> getAutomaticReply(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code) {
        BaseContextHandler.setTenant(code);
        return keyWordApi.getAutomaticReply();
    }

    @ApiOperation("修改收到消息回复设置")
    @PutMapping({"/updateAutomaticReply/{code}"})
    public R<Keyword> updateAutomaticReply(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                           @RequestBody AutomaticReplyDto automaticReply) {
        BaseContextHandler.setTenant(code);
        return keyWordApi.updateAutomaticReply(automaticReply);
    }

    @ApiOperation("查询开关状态")
    @ApiImplicitParams(@ApiImplicitParam(name = "type", value = "开关类型不能为空：1关键字开关，2消息回复开关", dataType = "int", paramType = "query"))
    @GetMapping({"/switchStatus/{code}"})
    public R<Integer> switchStatus(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                   @RequestParam @NotNull(message = "开关类型不能为空") Integer type) {
        BaseContextHandler.setTenant(code);
        return configAdditionalApi.switchStatus(type);
    }


    @ApiOperation("开启或关闭关键字回复")
    @ApiImplicitParams(@ApiImplicitParam(name = "status", value = "开启或关闭关键字回复：1打开，2关闭", dataType = "int", paramType = "query"))
    @PutMapping({"/switchKeyword/{code}"})
    public R<Boolean> switchKeyword(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                    @RequestParam  Integer status) {
        BaseContextHandler.setTenant(code);
        return configAdditionalApi.switchKeyword(status);
    }

    @ApiOperation("开启或关闭收到消息回复")
    @ApiImplicitParams(@ApiImplicitParam(name = "status", value = "开启或关闭收到消息回复：1打开，2关闭", dataType = "int", paramType = "query"))
    @PutMapping({"/switchAutomaticReply/{code}"})
    public R<Boolean> switchAutomaticReply(@PathVariable("code") @NotEmpty(message = "项目编码不能为空") String code,
                                           @RequestParam  Integer status) {
        BaseContextHandler.setTenant(code);
        return configAdditionalApi.switchAutomaticReply(status);
    }


}
