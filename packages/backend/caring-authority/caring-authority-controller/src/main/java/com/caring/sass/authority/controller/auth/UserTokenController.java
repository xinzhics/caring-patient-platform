package com.caring.sass.authority.controller.auth;

import com.caring.sass.authority.dto.auth.UserTokenPageDTO;
import com.caring.sass.authority.dto.auth.UserTokenSaveDTO;
import com.caring.sass.authority.dto.auth.UserTokenUpdateDTO;
import com.caring.sass.authority.entity.auth.UserToken;
import com.caring.sass.authority.service.auth.UserTokenService;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * token
 * </p>
 *
 * @author caring
 * @date 2020-04-02
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/userToken")
@Api(value = "UserToken", tags = "token")
//@PreAuth(replace = "userToken:")
public class UserTokenController extends SuperController<UserTokenService, Long, UserToken, UserTokenPageDTO, UserTokenSaveDTO, UserTokenUpdateDTO> {


}
