package com.caring.sass.tenant.service.router;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.entity.router.PatientManageMenu;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者管理平台菜单
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-07
 */
public interface PatientManageMenuService extends SuperService<PatientManageMenu> {

    void init();


    List<PatientManageMenu> createMenu(String code);

    void copy(String formCode, String toCode);
}
