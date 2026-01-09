package com.caring.sass.authority.strategy.impl;

import com.caring.sass.authority.entity.auth.User;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.authority.strategy.AbstractDataScopeHandler;
import com.caring.sass.model.RemoteData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 本级以及子级
 *
 * @author caring
 * @version 1.0
 * @date 2019-06-08 16:30
 */
@Component("THIS_LEVEL_CHILDREN")
public class ThisLevelChildrenDataScope implements AbstractDataScopeHandler {
    @Autowired
    private UserService userService;
    @Autowired
    private OrgService orgService;

    @Override
    public List<Long> getOrgIds(List<Long> orgList, Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        Long orgId = RemoteData.getKey(user.getOrg());
//        List<Org> children = orgService.findChildren(Arrays.asList(orgId));
        // 数据查询时。直接实时查询数据库中角色关联的机构
        orgList.add(orgId);
        return orgList;
    }
}
