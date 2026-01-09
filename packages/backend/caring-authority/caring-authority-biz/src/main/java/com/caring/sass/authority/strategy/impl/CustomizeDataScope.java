package com.caring.sass.authority.strategy.impl;

import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.authority.strategy.AbstractDataScopeHandler;
import com.caring.sass.exception.BizException;
import com.caring.sass.exception.code.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义模式
 *
 * @author caring
 * @version 1.0
 * @date 2019-06-08 16:31
 */
@Component("CUSTOMIZE")
public class CustomizeDataScope implements AbstractDataScopeHandler {

    @Autowired
    private OrgService orgService;

    @Override
    public List<Long> getOrgIds(List<Long> orgList, Long userId) {
        if (orgList == null || orgList.isEmpty()) {
            throw new BizException(ExceptionCode.BASE_VALID_PARAM.getCode(), "自定义数据权限类型时，组织不能为空");
        }
        // 自定义数据权限。机构ID根据勾选清空来，不查询机构下的子机构
//        List<Org> children = orgService.findChildren(orgList);
        return orgList;
    }
}
