package com.caring.sass.user.controller;

import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.entity.CommonMsgTemplateType;
import com.caring.sass.user.dto.CommonMsgTemplateTypeSaveDTO;
import com.caring.sass.user.dto.CommonMsgTemplateTypeUpdateDTO;
import com.caring.sass.user.dto.CommonMsgTemplateTypePageDTO;
import com.caring.sass.user.service.CommonMsgTemplateTypeService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
@RequestMapping("/commonMsgTemplateType")
@Api(value = "CommonMsgTemplateType", tags = "常用语模板分类")
//@PreAuth(replace = "commonMsgTemplateType:")
public class CommonMsgTemplateTypeController extends SuperController<CommonMsgTemplateTypeService, Long, CommonMsgTemplateType, CommonMsgTemplateTypePageDTO, CommonMsgTemplateTypeSaveDTO, CommonMsgTemplateTypeUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<CommonMsgTemplateType> commonMsgTemplateTypeList = list.stream().map((map) -> {
            CommonMsgTemplateType commonMsgTemplateType = CommonMsgTemplateType.builder().build();
            //TODO 请在这里完成转换
            return commonMsgTemplateType;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(commonMsgTemplateTypeList));
    }


    @Override
    public R<List<CommonMsgTemplateType>> query(CommonMsgTemplateType data) {

        LbqWrapper<CommonMsgTemplateType> typeLbqWrapper = Wraps.<CommonMsgTemplateType>lbQ()
                .eq(CommonMsgTemplateType::getUserType, data.getUserType())
                .orderByDesc(CommonMsgTemplateType::getTypeSort)
                .orderByAsc(SuperEntity::getCreateTime);
        List<CommonMsgTemplateType> templateTypes = baseService.list(typeLbqWrapper);
        return R.success(templateTypes);
    }
}
