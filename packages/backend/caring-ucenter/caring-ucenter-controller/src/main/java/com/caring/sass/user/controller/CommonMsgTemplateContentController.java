package com.caring.sass.user.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.entity.CommonMsg;
import com.caring.sass.user.entity.CommonMsgTemplateContent;
import com.caring.sass.user.dto.CommonMsgTemplateContentSaveDTO;
import com.caring.sass.user.dto.CommonMsgTemplateContentUpdateDTO;
import com.caring.sass.user.dto.CommonMsgTemplateContentPageDTO;
import com.caring.sass.user.service.CommonMsgService;
import com.caring.sass.user.service.CommonMsgTemplateContentService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;


/**
 * <p>
 * 前端控制器
 * 常用语模板分类
 * </p>
 *
 * @author 杨帅
 * @date 2023-05-08
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/commonMsgTemplateContent")
@Api(value = "CommonMsgTemplateContent", tags = "常用语模板")
//@PreAuth(replace = "commonMsgTemplateContent:")
public class CommonMsgTemplateContentController extends SuperController<CommonMsgTemplateContentService, Long, CommonMsgTemplateContent, CommonMsgTemplateContentPageDTO, CommonMsgTemplateContentSaveDTO, CommonMsgTemplateContentUpdateDTO> {

    @Autowired
    CommonMsgService commonMsgService;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<CommonMsgTemplateContent> commonMsgTemplateContentList = list.stream().map((map) -> {
            CommonMsgTemplateContent commonMsgTemplateContent = CommonMsgTemplateContent.builder().build();
            //TODO 请在这里完成转换
            return commonMsgTemplateContent;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(commonMsgTemplateContentList));
    }


    /**
     * 重新常用语模板内容的分页加载
     * @param params
     * @return
     */
    @Override
    public R<IPage<CommonMsgTemplateContent>> page(PageParams<CommonMsgTemplateContentPageDTO> params) {

        CommonMsgTemplateContentPageDTO model = params.getModel();
        LbqWrapper<CommonMsgTemplateContent> lbqWrapper = Wraps.<CommonMsgTemplateContent>lbQ();
        if (StrUtil.isNotEmpty(model.getUserType())) {
            lbqWrapper.eq(CommonMsgTemplateContent::getUserType, model.getUserType());
        }
        if (StrUtil.isNotEmpty(model.getSearchKey())) {
            lbqWrapper.and(e ->
                    e.like(CommonMsgTemplateContent::getTemplateTitle, model.getSearchKey()).or()
                    .like(CommonMsgTemplateContent::getTemplateContent, model.getSearchKey()));
        }
        if (Objects.nonNull(model.getTemplateTypeId())) {
            lbqWrapper.eq(CommonMsgTemplateContent::getTemplateTypeId, model.getTemplateTypeId());
        }
        if (CollUtil.isNotEmpty(model.getTemplateTypeIds())) {
            lbqWrapper.in(CommonMsgTemplateContent::getTemplateTypeId, model.getTemplateTypeIds());
        }

        if (Objects.nonNull(model.getTemplateRelease())) {
            lbqWrapper.eq(CommonMsgTemplateContent::getTemplateRelease, model.getTemplateRelease());
        }
        lbqWrapper.orderByDesc(SuperEntity::getCreateTime);
        IPage<CommonMsgTemplateContent> page = params.buildPage();
        baseService.page(page, lbqWrapper);

        Long remarkUserSelect = model.getRemarkUserSelect();
        if (Objects.nonNull(remarkUserSelect)) {
            List<CommonMsgTemplateContent> records = page.getRecords();
            if (CollUtil.isNotEmpty(records)) {
                List<Long> collect = records.stream().map(SuperEntity::getId).collect(Collectors.toList());
                List<CommonMsg> commonMsgs = commonMsgService.list(Wraps.<CommonMsg>lbQ()
                        .select(SuperEntity::getId, CommonMsg::getSourceTemplateId)
                        .eq(CommonMsg::getUserType, model.getUserType())
                        .eq(CommonMsg::getAccountId, remarkUserSelect)
                        .in(CommonMsg::getSourceTemplateId, collect));
                if (CollUtil.isNotEmpty(commonMsgs)) {
                    List<Long> templateIds = commonMsgs.stream().map(CommonMsg::getSourceTemplateId).collect(Collectors.toList());
                    for (CommonMsgTemplateContent record : records) {
                        if (templateIds.contains(record.getId())) {
                            record.setExisted(CommonStatus.YES);
                        }
                    }
                }
            }
        }
        return R.success(page);
    }


    @ApiOperation("常用语导入")
    @PostMapping(value = "/importMsgTemplate")
    public R<Boolean> importMsgTemplate(@RequestParam(value = "file") MultipartFile simpleFile,
                                        @RequestParam("userType") String userType) {
        try {
            InputStream inputStream = simpleFile.getInputStream();
            baseService.importMsgTemplate(inputStream, userType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(true);

    }





}
