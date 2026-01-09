package com.caring.sass.cms.controller;

import com.caring.sass.cms.entity.SiteJumpInformation;
import com.caring.sass.cms.dto.site.SiteJumpInformationSaveDTO;
import com.caring.sass.cms.dto.site.SiteJumpInformationUpdateDTO;
import com.caring.sass.cms.dto.site.SiteJumpInformationPageDTO;
import com.caring.sass.cms.service.SiteJumpInformationService;
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
 * 建站组件元素跳转信息
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/siteJumpInformation")
@Api(value = "SiteJumpInformation", tags = "建站组件元素跳转信息")
public class SiteJumpInformationController extends SuperController<SiteJumpInformationService, Long, SiteJumpInformation, SiteJumpInformationPageDTO, SiteJumpInformationSaveDTO, SiteJumpInformationUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<SiteJumpInformation> siteJumpInformationList = list.stream().map((map) -> {
            SiteJumpInformation siteJumpInformation = SiteJumpInformation.builder().build();
            //TODO 请在这里完成转换
            return siteJumpInformation;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(siteJumpInformationList));
    }
}
