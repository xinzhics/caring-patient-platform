package com.caring.sass.authority.controller.auth;


import cn.hutool.core.util.RandomUtil;
import com.caring.sass.authority.dto.auth.ApplicationPageDTO;
import com.caring.sass.authority.dto.auth.ApplicationSaveDTO;
import com.caring.sass.authority.dto.auth.ApplicationUpdateDTO;
import com.caring.sass.authority.entity.auth.Application;
import com.caring.sass.authority.service.auth.ApplicationService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperCacheController;
import com.caring.sass.context.BaseContextHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * 应用
 * </p>
 *
 * @author caring
 * @date 2019-12-15
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/application")
@Api(value = "Application", tags = "应用")
//@PreAuth(replace = "application:")
public class ApplicationController extends SuperCacheController<ApplicationService, Long, Application, ApplicationPageDTO, ApplicationSaveDTO, ApplicationUpdateDTO> {

    @ApiOperation(value = "根据客户端信息查询应用")
    @GetMapping("/getByClient")
    public R<Application> getByClient(@RequestParam("clientId") String clientId,
                                      @RequestParam("clientSecret") String clientSecret) {
        Application application = baseService.getByClient(clientId, clientSecret);
        return R.success(application);
    }

    @Override
    public R<Application> handlerSave(ApplicationSaveDTO applicationSaveDTO) {
        applicationSaveDTO.setTenantCode(BaseContextHandler.getTenant());
        applicationSaveDTO.setClientId(RandomUtil.randomString(24));
        applicationSaveDTO.setClientSecret(RandomUtil.randomString(32));
        return super.handlerSave(applicationSaveDTO);
    }

}
