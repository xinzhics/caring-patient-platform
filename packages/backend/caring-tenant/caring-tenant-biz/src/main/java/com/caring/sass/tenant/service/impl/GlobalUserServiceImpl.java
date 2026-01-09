package com.caring.sass.tenant.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.SecureUtil;
import com.caring.sass.authority.dto.auth.UserUpdatePasswordDTO;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.utils.PasswordUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.tenant.dao.GlobalUserMapper;
import com.caring.sass.tenant.dao.GlobalUserTenantMapper;
import com.caring.sass.tenant.dao.LibraryGlobalUserMapper;
import com.caring.sass.tenant.dto.GlobalUserSaveDTO;
import com.caring.sass.tenant.dto.GlobalUserUpdateDTO;
import com.caring.sass.tenant.entity.GlobalUser;
import com.caring.sass.tenant.entity.GlobalUserTenant;
import com.caring.sass.tenant.entity.LibraryGlobalUser;
import com.caring.sass.tenant.service.GlobalUserService;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.utils.StrHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.caring.sass.utils.BizAssert.isFalse;

/**
 * <p>
 * 业务实现类
 * 全局账号
 * </p>
 *
 * @author caring
 * @date 2019-10-25
 */
@Slf4j
@Service

public class GlobalUserServiceImpl extends SuperServiceImpl<GlobalUserMapper, GlobalUser> implements GlobalUserService {

    @Autowired
    LibraryGlobalUserMapper libraryGlobalUserMapper;

    @Autowired
    GlobalUserTenantMapper globalUserTenantMapper;

    @Override
    public Boolean check(String account, Long accountId) {
        LbqWrapper<GlobalUser> eq = Wraps.<GlobalUser>lbQ()
                .eq(GlobalUser::getUserDeleted, 0)
                .eq(GlobalUser::getAccount, account);
        if (Objects.nonNull(accountId)) {
            eq.ne(SuperEntity::getId, accountId);
        }
        return super.count(eq) > 0;
    }

    /**
     * 删除账号时， 删除账号关联的项目。
     * @param id
     */
    @Override
    public void delete(Long id) {

        GlobalUser user = super.getById(id);
        if (BizConstant.THIRD_PARTY_CUSTOMERS.equals(user.getGlobalUserType())) {
            user.setUserDeleted(1);
            super.updateById(user);
            globalUserTenantMapper.delete(Wraps.<GlobalUserTenant>lbQ().eq(GlobalUserTenant::getAccountId, id));
        } else {
            baseMapper.deleteById(id);
        }
    }

    /**
     * @param data
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public GlobalUser save(GlobalUserSaveDTO data) {
        BizAssert.equals(data.getPassword(), data.getConfirmPassword(), "2次输入的密码不一致");
        isFalse(check(data.getAccount(), null), "账号已经存在");
        boolean validString = PasswordUtils.isValidString(data.getPassword());
        BizAssert.isTrue(validString, "密码需要符合字母+数字长度8位以上要求");

        String md5Password = SecureUtil.md5(data.getPassword());

        GlobalUser globalAccount = BeanPlusUtil.toBean(data, GlobalUser.class);
        // 全局表就不存用户数据了
        globalAccount.setPassword(md5Password);
        globalAccount.setName(StrHelper.getOrDef(data.getName(), data.getAccount()));
        globalAccount.setReadonly(false);
        List<LibraryGlobalUser> globalUsers = data.getLibraryGlobalUsers();
        save(globalAccount);
        if (CollUtil.isNotEmpty(globalUsers)) {
            for (LibraryGlobalUser globalUser : globalUsers) {
                globalUser.setGlobalUserId(globalAccount.getId());
            }
            libraryGlobalUserMapper.insertBatchSomeColumn(globalUsers);
        }
        return globalAccount;
    }

    /**
     * @param data
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public GlobalUser update(GlobalUserUpdateDTO data) {
        Boolean resetPassword = data.getResetPassword();
        GlobalUser globalUser = BeanPlusUtil.toBean(data, GlobalUser.class);
        if (resetPassword != null && resetPassword) {
            String md5Password = SecureUtil.md5("tempPassword123");
            globalUser.setPassword(md5Password);
        } else {
            globalUser.setPassword(null);
        }
        updateById(globalUser);
        return globalUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePassword(UserUpdatePasswordDTO data) {
        BizAssert.equals(data.getConfirmPassword(), data.getPassword(), "密码与确认密码不一致");
        boolean validString = PasswordUtils.isValidString(data.getPassword());
        BizAssert.isTrue(validString, "密码需要符合字母+数字长度8位以上要求");

        GlobalUser user = getById(data.getId());
        String oldPassword = data.getOldPassword();
        BizAssert.notNull(user, "用户不存在");
        boolean superAdmin = Objects.equals(BaseContextHandler.getAccount(), BizConstant.SUPER_TENANT);
       // 非第三方客户且不是超管账号，则需要校验旧密码
        if (!superAdmin && BizConstant.THIRD_PARTY_CUSTOMERS.equals(user.getGlobalUserType())) {
            if (!SecureUtil.md5(oldPassword).equals(user.getPassword())) {
                throw new BizException("旧密码输入错误");
            }
        }

        GlobalUser build = GlobalUser.builder().password(SecureUtil.md5(data.getPassword())).build();
        build.setId(data.getId());
        return updateById(build);
    }

    @Override
    @Transactional
    public void updateLibraryUser(GlobalUserUpdateDTO updateDTO) {
        List<LibraryGlobalUser> globalUsers = updateDTO.getLibraryGlobalUsers();
        libraryGlobalUserMapper.delete(Wraps.<LibraryGlobalUser>lbQ().eq(LibraryGlobalUser::getGlobalUserId, updateDTO.getId()));
        for (LibraryGlobalUser user : globalUsers) {
            user.setGlobalUserId(updateDTO.getId());
        }
        libraryGlobalUserMapper.insertBatchSomeColumn(globalUsers);

    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        baseMapper.deleteBatchIds(idList);
        libraryGlobalUserMapper.delete(Wraps.<LibraryGlobalUser>lbQ().in(LibraryGlobalUser::getGlobalUserId, idList));
        globalUserTenantMapper.delete(Wraps.<GlobalUserTenant>lbQ().in(GlobalUserTenant::getAccountId, idList));
        return false;
    }

    @Override
    public GlobalUser getById(Serializable id) {
        GlobalUser user = baseMapper.selectById(id);
        if (Objects.nonNull(user) && "cms".equals(user.getGlobalUserType())) {
            List<LibraryGlobalUser> globalUsers = libraryGlobalUserMapper.selectList(Wraps.<LibraryGlobalUser>lbQ()
                    .eq(LibraryGlobalUser::getGlobalUserId, id));
            user.setLibraryGlobalUsers(globalUsers);
        }
        return user;
    }

    /**
     * @title 查询客户的项目
     * @author 杨帅
     * @updateTime 2023/4/12 14:17
     * @throws
     */
    @Override
    public List<GlobalUserTenant> selectTenantIds(Long userId) {
        return globalUserTenantMapper.selectList(Wraps.<GlobalUserTenant>lbQ().eq(GlobalUserTenant::getAccountId, userId));
    }

    /**
     * @title 查询项目的关联的客户
     * @author 杨帅
     * @updateTime 2023/4/12 14:39
     * @throws
     */
    @Override
    public List<GlobalUserTenant> selectTenantIds(List<Long> tenantIds) {
        return globalUserTenantMapper.selectList(Wraps.<GlobalUserTenant>lbQ().in(GlobalUserTenant::getTenantId, tenantIds));
    }
}
