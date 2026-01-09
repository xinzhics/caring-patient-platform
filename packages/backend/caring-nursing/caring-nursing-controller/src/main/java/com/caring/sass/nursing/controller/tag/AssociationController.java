package com.caring.sass.nursing.controller.tag;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.tag.AssociationPageDTO;
import com.caring.sass.nursing.dto.tag.AssociationSaveDTO;
import com.caring.sass.nursing.dto.tag.AssociationUpdateDTO;
import com.caring.sass.nursing.entity.tag.Association;
import com.caring.sass.nursing.service.tag.AssociationService;
import com.caring.sass.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 业务关联标签记录表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/association")
@Api(value = "Association", tags = "业务关联标签记录表")
@PreAuth(replace = "association:")
public class AssociationController extends SuperController<AssociationService, Long, Association, AssociationPageDTO, AssociationSaveDTO, AssociationUpdateDTO> {


}
