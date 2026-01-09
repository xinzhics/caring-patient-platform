package com.caring.sass.nursing.controller.tag;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.tag.AttrPageDTO;
import com.caring.sass.nursing.dto.tag.AttrSaveDTO;
import com.caring.sass.nursing.dto.tag.AttrUpdateDTO;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.service.tag.AttrService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 标签属性
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/attr")
@Api(value = "Attr", tags = "标签属性")
//@PreAuth(replace = "attr:")
public class AttrController extends SuperController<AttrService, Long, Attr, AttrPageDTO, AttrSaveDTO, AttrUpdateDTO> {


}
