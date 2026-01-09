package com.caring.sass.ai.controller;


import com.caring.sass.ai.entity.dto.DigitalPeoplePageDTO;
import com.caring.sass.ai.entity.dto.DigitalPeopleSaveDTO;
import com.caring.sass.ai.entity.dto.DigitalPeopleUpdateDTO;
import com.caring.sass.ai.entity.face.DigitalPeople;
import com.caring.sass.ai.face.service.DigitalPeopleService;
import com.caring.sass.base.controller.SuperController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 数字人
 * </p>
 *
 * @author 杨帅
 * @date 2024-06-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/digitalPeople")
@Api(value = "DigitalPeople", tags = "数字人")
public class DigitalPeopleController extends SuperController<DigitalPeopleService, Long, DigitalPeople, DigitalPeoplePageDTO, DigitalPeopleSaveDTO, DigitalPeopleUpdateDTO> {


}
