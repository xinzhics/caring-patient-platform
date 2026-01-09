package com.caring.sass.ai.controller.call;

import com.caring.sass.ai.call.service.CallContentService;
import com.caring.sass.ai.dto.call.CallContentPageDTO;
import com.caring.sass.ai.dto.call.CallContentSaveDTO;
import com.caring.sass.ai.dto.call.CallContentUpdateDTO;
import com.caring.sass.ai.entity.call.CallContent;
import com.caring.sass.base.controller.SuperController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 分身通话内容
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/callContent")
@Api(value = "CallContent", tags = "分身通话内容")
public class CallContentController extends SuperController<CallContentService, Long, CallContent, CallContentPageDTO, CallContentSaveDTO, CallContentUpdateDTO> {


}
