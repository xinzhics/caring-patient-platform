package com.caring.sass.wx.controller.template;

import com.caring.sass.wx.entity.template.TemplateMsgFields;
import com.caring.sass.wx.dto.template.TemplateMsgFieldsSaveDTO;
import com.caring.sass.wx.dto.template.TemplateMsgFieldsUpdateDTO;
import com.caring.sass.wx.dto.template.TemplateMsgFieldsPageDTO;
import com.caring.sass.wx.service.template.TemplateMsgFieldsService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 模板消息 属性表通过 templateId 和 TemplateMessage 表关联。
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/templateMsgFields")
@Api(value = "TemplateMsgFields", tags = "模板消息 属性表通过 templateId 和 TemplateMessage 表关联。")
//@PreAuth(replace = "templateMsgFields:")
public class TemplateMsgFieldsController extends SuperController<TemplateMsgFieldsService, Long, TemplateMsgFields, TemplateMsgFieldsPageDTO, TemplateMsgFieldsSaveDTO, TemplateMsgFieldsUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<TemplateMsgFields> templateMsgFieldsList = list.stream().map((map) -> {
            TemplateMsgFields templateMsgFields = TemplateMsgFields.builder().build();
            //TODO 请在这里完成转换
            return templateMsgFields;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(templateMsgFieldsList));
    }
}
