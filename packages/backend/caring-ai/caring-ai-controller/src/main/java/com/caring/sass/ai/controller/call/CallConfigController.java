package com.caring.sass.ai.controller.call;

import com.caring.sass.ai.call.service.CallConfigService;
import com.caring.sass.ai.dto.call.CallConfigUpdateDTO;
import com.caring.sass.ai.dto.call.CallConfigPageDTO;
import com.caring.sass.ai.dto.call.CallConfigSaveDTO;
import com.caring.sass.ai.entity.call.CallConfig;
import com.caring.sass.base.controller.SuperController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 通话充值配置
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-02
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/callConfig")
@Api(value = "CallConfig", tags = "通话充值配置")
public class CallConfigController extends SuperController<CallConfigService, Long, CallConfig, CallConfigPageDTO, CallConfigSaveDTO, CallConfigUpdateDTO> {

}
