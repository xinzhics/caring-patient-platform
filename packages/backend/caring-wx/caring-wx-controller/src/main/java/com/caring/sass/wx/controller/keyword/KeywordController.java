package com.caring.sass.wx.controller.keyword;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.wx.dto.keyword.*;
import com.caring.sass.wx.entity.keyword.Keyword;
import com.caring.sass.wx.service.keyword.KeywordService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 微信服务号自动回复关键词
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/keyword")
@Api(value = "Keyword", tags = "微信服务号自动回复关键词")
//@PreAuth(replace = "keyword:")
public class KeywordController extends SuperController<KeywordService, Long, Keyword, KeywordPageDTO, KeywordSaveDTO, KeywordUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<Keyword> keywordList = list.stream().map((map) -> {
            Keyword keyword = Keyword.builder().build();
            //TODO 请在这里完成转换
            return keyword;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(keywordList));
    }

//
//    @ApiOperation("从一个项目中复制关键词")
//    @GetMapping({"/copyKeyword"})
//    public R copyKeyword(@RequestParam("oldProjectId") String oldProjectId, @RequestParam("newProjectId") String newProjectId) {
//
//        return R.success();
//    };

    @ApiOperation("通过消息内容匹配关键字获取自动回复内容")
    @PostMapping({"/matchKeyword"})
    public R<Keyword> matchKeyword(@RequestBody KeyWordDto keyWordDto) {
        Keyword keyword = baseService.matchKeyword(keyWordDto.getContent());
        return R.success(keyword);
    }

    @ApiOperation("获取 自动回复 设置")
    @GetMapping({"/getAutomaticReply"})
    public R<Keyword> getAutomaticReply() {
        Keyword keyword = baseService.getAutomaticReply();
        return R.success(keyword);
    }


    @ApiOperation("修改 自动回复 设置")
    @PutMapping({"/updateAutomaticReply"})
    public R<Keyword> updateAutomaticReply(@RequestBody AutomaticReplyDto automaticReply) {
        Keyword keyword = baseService.updateAutomaticReply(automaticReply);
        return R.success(keyword);
    }

    /**
     * 关键字分页查询
     */
    @Override
    public R<IPage<Keyword>> page(PageParams<KeywordPageDTO> params) {
        IPage<Keyword> page = new Page<>();
        page.setCurrent(params.getCurrent()).setSize(params.getSize());
        IPage<Keyword> keywordIPage = baseService.page(page, Wrappers.<Keyword>lambdaQuery().ne(Keyword::getMatchType, Keyword.MATCH_TYPE_NO));
        return R.success(keywordIPage);
    }
}
