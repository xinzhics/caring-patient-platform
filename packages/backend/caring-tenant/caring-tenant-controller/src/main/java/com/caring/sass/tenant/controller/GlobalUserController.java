package com.caring.sass.tenant.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.dto.auth.UserUpdatePasswordDTO;
import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.ContentLibraryApi;
import com.caring.sass.cms.entity.ContentLibrary;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.exception.BizException;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.tenant.dao.LibraryGlobalUserMapper;
import com.caring.sass.tenant.dto.GlobalUserPageDTO;
import com.caring.sass.tenant.dto.GlobalUserSaveDTO;
import com.caring.sass.tenant.dto.GlobalUserUpdateDTO;
import com.caring.sass.tenant.entity.GlobalUser;
import com.caring.sass.tenant.entity.LibraryGlobalUser;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.tenant.enumeration.TenantStatusEnum;
import com.caring.sass.tenant.service.GlobalUserService;
import com.caring.sass.tenant.service.TenantService;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.utils.DateUtils;
import com.caring.sass.utils.StrHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * 全局账号
 * </p>
 *
 * @author caring
 * @date 2019-10-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/globalUser")
@Api(value = "GlobalUser", tags = "全局账号")
@SysLog(enabled = false)
public class GlobalUserController extends SuperController<GlobalUserService, Long, GlobalUser, GlobalUserPageDTO, GlobalUserSaveDTO, GlobalUserUpdateDTO> {

    @Autowired
    private UserService userService;
    @Autowired
    private TenantService tenantService;

    @Autowired
    ContentLibraryApi contentLibraryApi;

    @Autowired
    LibraryGlobalUserMapper libraryGlobalUserMapper;

    @Override
    public R<GlobalUser> handlerSave(GlobalUserSaveDTO model) {
        if (StrUtil.isEmpty(model.getTenantCode()) || BizConstant.SUPER_TENANT.equals(model.getTenantCode())) {
            String account = BaseContextHandler.getAccount();
            String userType = BaseContextHandler.getUserType();
            if ("admin".equals(model.getGlobalUserType()) && !"admin".equals(account) && UserType.GLOBAL_ADMIN.equals(userType)) {
                throw new BizException("只有超级管理员才能创建全局管理员");
            }
            return success(baseService.save(model));
        } else {
            Tenant tenant = tenantService.getByCode(model.getTenantCode());
            BizAssert.notNull(tenant, "租户不能为空");
            BizAssert.isTrue(TenantStatusEnum.NORMAL.eq(tenant.getStatus()), StrUtil.format("租户[{}]不可用", tenant.getName()));

            BaseContextHandler.setTenant(model.getTenantCode());
            User user = BeanPlusUtil.toBean(model, User.class);
            user.setName(StrHelper.getOrDef(model.getName(), model.getAccount()));
            if (StrUtil.isEmpty(user.getPassword())) {
                user.setPassword(BizConstant.DEF_PASSWORD);
            }
            user.setStatus(true);
            userService.initUser(user);
            return success(BeanPlusUtil.toBean(user, GlobalUser.class));
        }
    }

    @Override
    public R<GlobalUser> handlerUpdate(GlobalUserUpdateDTO model) {
        if (StrUtil.isEmpty(model.getTenantCode()) || BizConstant.SUPER_TENANT.equals(model.getTenantCode())) {
            return success(baseService.update(model));
        } else {
            BaseContextHandler.setTenant(model.getTenantCode());
            User user = BeanPlusUtil.toBean(model, User.class);
            userService.updateUser(user);
            return success(BeanPlusUtil.toBean(user, GlobalUser.class));
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "企业编码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "account", value = "账号或手机号", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "accountId", value = "账户ID", dataType = "Long", paramType = "query"),
    })
    @ApiOperation(value = "检测账号是否可用", notes = "检测账号是否可用")
    @GetMapping("/check")
    public R<Boolean> check(@RequestParam(required = false) String tenantCode, @RequestParam String account, @RequestParam(required = false) Long accountId) {
        if (StrUtil.isEmpty(tenantCode) || BizConstant.SUPER_TENANT.equals(tenantCode)) {
            return success(baseService.check(account, accountId));
        } else {
            BaseContextHandler.setTenant(tenantCode);
            return success(userService.check(account));
        }
    }


    @ApiOperation(value = "根据客户姓名，企业搜索客户列表")
    @GetMapping("/list")
    public R<List<GlobalUser>> list(String keyWork) {
        LbqWrapper<GlobalUser> eq = Wraps.<GlobalUser>lbQ()
                .eq(GlobalUser::getGlobalUserType, BizConstant.THIRD_PARTY_CUSTOMERS)
                .eq(GlobalUser::getUserDeleted, 0);
        if (StrUtil.isNotEmpty(keyWork)) {
                eq.apply("(name like '%"+ keyWork +"%' or enterprise like '%" + keyWork + "%')" );
        }
        eq.orderByDesc(SuperEntity::getCreateTime);
        List<GlobalUser> globalUsers = baseService.list(eq);
        return R.success(globalUsers);
    }


    private void handlerUserWrapper(QueryWrap<User> wrapper, PageParams<GlobalUserPageDTO> params) {
        if (CollUtil.isNotEmpty(params.getMap())) {
            Map<String, String> map = params.getMap();
            //拼装区间
            for (Map.Entry<String, String> field : map.entrySet()) {
                String key = field.getKey();
                String value = field.getValue();
                if (StrUtil.isEmpty(value)) {
                    continue;
                }
                if (key.endsWith("_st")) {
                    String beanField = StrUtil.subBefore(key, "_st", true);
                    wrapper.ge(getDbField(beanField, getEntityClass()), DateUtils.getStartTime(value));
                }
                if (key.endsWith("_ed")) {
                    String beanField = StrUtil.subBefore(key, "_ed", true);
                    wrapper.le(getDbField(beanField, getEntityClass()), DateUtils.getEndTime(value));
                }
            }
        }
    }

    @ApiOperation("超管端登录")
    @PostMapping("/admin/login")
    public R<GlobalUser> login(@RequestBody GlobalUser globalUser) {
        if (StrUtil.isEmpty(globalUser.getTenantCode()) || BizConstant.SUPER_TENANT.equals(globalUser.getTenantCode())) {
            String account = globalUser.getAccount();
            String password = globalUser.getPassword();
            List<GlobalUser> userList = baseService.list(Wraps.<GlobalUser>lbQ()
                    .eq(GlobalUser::getUserDeleted, 0)
                    .eq(GlobalUser::getAccount, account)
                    .eq(GlobalUser::getPassword, password));
            if (CollUtil.isNotEmpty(userList)) {
                GlobalUser user = userList.get(0);
                user.setLoginTime(LocalDateTime.now());
                if (user.getFirstLogin() == null || user.getFirstLogin() == 0) {
                    user.setFirstLogin(1);
                    baseService.updateById(user);
                    user.setFirstLogin(0);
                    return R.success(user);
                } else {
                    baseService.updateById(user);
                    return R.success(user);
                }
            }
        }
        return R.success(null);
    }


    @ApiOperation("查询内容库权限账号")
    @PostMapping("/queryGlobalUserByLibrary")
    public R<List<GlobalUser>> queryGlobalUserByLibrary(@RequestParam("libraryId") Long libraryId) {

        List<LibraryGlobalUser> libraryGlobalUserList = libraryGlobalUserMapper.selectList(Wraps.<LibraryGlobalUser>lbQ()
                .eq(LibraryGlobalUser::getLibraryId, libraryId)
                .select(SuperEntity::getId, LibraryGlobalUser::getGlobalUserId));
        if (CollUtil.isNotEmpty(libraryGlobalUserList)) {
            Set<Long> globalUserIds = libraryGlobalUserList.stream().map(LibraryGlobalUser::getGlobalUserId).collect(Collectors.toSet());
            List<GlobalUser> globalUsers = baseService.listByIds(globalUserIds);
            return R.success(globalUsers);
        }
        return R.success(new ArrayList<>());
    }


    @Override
    public void query(PageParams<GlobalUserPageDTO> params, IPage<GlobalUser> page, Long defSize) {
        GlobalUserPageDTO param = params.getModel();
        // 查询全局账号
        boolean queryGlobalUser = StrUtil.isEmpty(param.getTenantCode())
                || BizConstant.SUPER_TENANT.equals(param.getTenantCode());
        if (queryGlobalUser) {
            QueryWrap<GlobalUser> wrapper = handlerWrapper(null, params);
            wrapper.lambda().eq(GlobalUser::getTenantCode, param.getTenantCode())
                    .eq(GlobalUser::getGlobalUserType, param.getGlobalUserType())
                    .eq(GlobalUser::getUserDeleted, 0)
                    .like(GlobalUser::getAccount, param.getAccount())
                    .like(GlobalUser::getName, param.getName());
            IPage<GlobalUser> globalUserIPage = baseService.page(page, wrapper);
            List<GlobalUser> globalUsers = globalUserIPage.getRecords();
            if (CollUtil.isEmpty(globalUsers)) {
                return;
            }

            // 匹配内容库权限账号
            List<Long> globalUserIds = globalUsers.stream().map(SuperEntity::getId).collect(Collectors.toList());
            List<LibraryGlobalUser> libraryUsers = libraryGlobalUserMapper.selectList(Wraps.<LibraryGlobalUser>lbQ()
                    .in(LibraryGlobalUser::getGlobalUserId, globalUserIds));
            if (CollUtil.isEmpty(libraryUsers)) {
                return;
            }

            // 匹配内容库
            List<Long> libraryIds = libraryUsers.stream().map(LibraryGlobalUser::getLibraryId).collect(Collectors.toList());
            R<List<ContentLibrary>> contentLibrary = contentLibraryApi.listByIds(libraryIds);
            if (contentLibrary.getIsError() && CollUtil.isEmpty(contentLibrary.getData())) {
                return;
            }
            List<ContentLibrary> libraryData = contentLibrary.getData();
            Map<Long, ContentLibrary> libraryMap = libraryData.stream().collect(Collectors.toMap(SuperEntity::getId, item -> item, (o1, o2) -> o2));
            for (LibraryGlobalUser user : libraryUsers) {
                ContentLibrary library = libraryMap.get(user.getLibraryId());
                if (Objects.nonNull(library)) {
                    user.setLibraryName(library.getLibraryName());
                }
            }

            Map<Long, List<LibraryGlobalUser>> listMap = libraryUsers.stream().collect(Collectors.groupingBy(LibraryGlobalUser::getGlobalUserId));
            for (GlobalUser record : globalUsers) {
                record.setLibraryGlobalUsers(listMap.get(record.getId()));
            }
            return;
        }

        // 查项目管理员信息
        BaseContextHandler.setTenant(param.getTenantCode());
        IPage<User> userPage = params.buildPage();
        QueryWrap<User> wrapper = Wraps.q();
        handlerUserWrapper(wrapper, params);
        wrapper.lambda()
                .like(User::getAccount, param.getAccount())
                .like(User::getName, param.getName());

        userService.page(userPage, wrapper);

        page.setCurrent(userPage.getCurrent());
        page.setSize(userPage.getSize());
        page.setTotal(userPage.getTotal());
        page.setPages(userPage.getPages());
        List<GlobalUser> list = BeanPlusUtil.toBeanList(userPage.getRecords(), GlobalUser.class);
        page.setRecords(list);
    }


    @ApiOperation(value = "删除客户")
    @DeleteMapping("/delete/third_party_customers")
    public R<Boolean> delete( @RequestParam("id") Long id) {
        baseService.delete(id);
        return R.success(true);
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "企业编码", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "ids[]", value = "主键id", dataType = "array", paramType = "query"),
    })
    public R<Boolean> delete(@RequestParam String tenantCode, @RequestParam("ids[]") List<Long> ids) {
        if (StrUtil.isEmpty(tenantCode) || BizConstant.SUPER_TENANT.equals(tenantCode)) {
            return success(baseService.removeByIds(ids));
        } else {
            BaseContextHandler.setTenant(tenantCode);
            return success(userService.removeByIds(ids));
        }
    }

    @ApiOperation(value = "修改用户权限", notes = "修改用户权限")
    @PutMapping("/updateLibraryUser")
    public R<Boolean> updateLibraryUser(@RequestBody  GlobalUserUpdateDTO updateDTO) {
        if (StrUtil.isEmpty(updateDTO.getTenantCode()) || BizConstant.SUPER_TENANT.equals(updateDTO.getTenantCode())) {
            baseService.updateLibraryUser(updateDTO);
            return success();
        }
        return R.fail("租户不正确");
    }

    /**
     * 修改密码
     *
     * @param model 修改实体
     * @return
     */
    @ApiOperation(value = "修改、重置密码", notes = "修改、重置密码")
    @PutMapping("/reset")
    public R<Boolean> updatePassword(@RequestBody @Validated(SuperEntity.Update.class) UserUpdatePasswordDTO model) {
        if (StrUtil.isEmpty(model.getTenantCode()) || BizConstant.SUPER_TENANT.equals(model.getTenantCode())) {
            return success(baseService.updatePassword(model));
        } else {
            BaseContextHandler.setTenant(model.getTenantCode());
            return success(userService.reset(model));
        }
    }

    /**
     * 校验密码
     */
    @ApiOperation(value = "校验密码")
    @GetMapping("checkPassword")
    public R<Boolean> checkPassword(@RequestParam(value = "password") @NotEmpty(message = "密码不能为空") String password) {
        Long userId = BaseContextHandler.getUserId();
        if (userId == null) {
            return R.fail("请登录");
        }
        GlobalUser user = baseService.getById(userId);
        if (Objects.isNull(user) || !Objects.equals(SecureUtil.md5(password), user.getPassword())) {
            return R.fail("密码错误");
        }
        return R.success();
    }

    @ApiOperation("手机号登录")
    @PostMapping("/mobile/login")
    public R<GlobalUser> mobileLogin(@RequestParam(value = "mobile") String mobile,
                                     @RequestParam(value = "password", required = false) String password) {
        GlobalUser user = baseService.getOne(Wraps.<GlobalUser>lbQ()
                .last(" limit 1")
                .eq(GlobalUser::getMobile, mobile)
                .eq(GlobalUser::getUserDeleted, 0));
        if (Objects.isNull(user)) {
            return R.fail("用户不存在");
        }
        if (StrUtil.isNotBlank(password)) {
            if (password.equals(user.getPassword())) {
                return R.fail("密码错误");
            }
        }
        user.setLoginTime(LocalDateTime.now());
        if (user.getFirstLogin() == null || user.getFirstLogin() == 0) {
            user.setFirstLogin(1);
            baseService.updateById(user);
            user.setFirstLogin(0);
        } else {
            baseService.updateById(user);
        }
        return R.success(user);
    }


    @ApiOperation("手机号查询")
    @PostMapping("/mobile/query")
    public R<GlobalUser> mobile(@RequestParam(value = "mobile") String mobile) {

        List<GlobalUser> users = baseService.list(Wraps.<GlobalUser>lbQ()
                .eq(GlobalUser::getMobile, mobile)
                .eq(GlobalUser::getUserDeleted, 0));
        if (CollUtil.isNotEmpty(users)) {
            GlobalUser globalUser = users.get(0);
            return R.success(globalUser);
        }
        return R.success(null);
    }


}
