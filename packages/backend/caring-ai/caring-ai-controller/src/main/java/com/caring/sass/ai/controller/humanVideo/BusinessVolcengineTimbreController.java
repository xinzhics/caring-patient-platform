package com.caring.sass.ai.controller.humanVideo;


import com.caring.sass.ai.dto.humanVideo.BusinessVolcengineTimbrePageDTO;
import com.caring.sass.ai.dto.humanVideo.BusinessVolcengineTimbreSaveDTO;
import com.caring.sass.ai.dto.humanVideo.BusinessVolcengineTimbreUpdateDTO;
import com.caring.sass.ai.entity.humanVideo.BusinessVolcengineTimbre;
import com.caring.sass.ai.humanVideo.service.BusinessVolcengineTimbreService;
import com.caring.sass.base.controller.SuperController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 火山音色管理
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/businessVolcengineTimbre")
@Api(value = "BusinessVolcengineTimbre", tags = "火山音色管理")
public class BusinessVolcengineTimbreController extends SuperController<BusinessVolcengineTimbreService, Long, BusinessVolcengineTimbre,
        BusinessVolcengineTimbrePageDTO, BusinessVolcengineTimbreSaveDTO, BusinessVolcengineTimbreUpdateDTO> {


}
