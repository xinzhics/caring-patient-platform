package com.caring.sass.wx;

import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.wx.dto.keyword.*;
import com.caring.sass.wx.entity.keyword.Keyword;
import com.caring.sass.wx.hystrix.KeyWordApiFallback;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName com.caring.sass.wx.IKeyWordService
 * @Description
 * @Author yangShuai
 * @Date 2020/9/16 10:01
 * @Version 1.0
 */
@Component
@Api(value = "/api", tags = {"项目关键字对外服务"})
@FeignClient(name = "${caring.feign.wx-server:caring-wx-server}", path = "/keyword",
        qualifier = "KeyWordApi", fallback = KeyWordApiFallback.class)
public interface KeyWordApi extends SuperApi<Long, Keyword, KeywordPageDTO, KeywordSaveDTO, KeywordUpdateDTO> {

    @ApiOperation("从一个项目中复制关键词")
    @GetMapping({"/copyKeyword"})
    R<Void> copyKeyword(@RequestParam("oldProjectId") String oldProjectId, @RequestParam("newProjectId") String newProjectId);


    @ApiOperation("通过消息内容匹配关键字获取自动回复内容")
    @PostMapping({"/matchKeyword"})
    R<Keyword> matchKeyword(@RequestBody KeyWordDto keyWordDto);

    /**
     * 获取自动回复设置
     */
    @GetMapping({"/getAutomaticReply"})
    R<Keyword> getAutomaticReply();


    /**
     * 修改自动回复设置
     */
    @PutMapping({"/updateAutomaticReply"})
    R<Keyword> updateAutomaticReply(@RequestBody AutomaticReplyDto automaticReply);
}
