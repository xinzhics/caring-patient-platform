package com.caring.sass.tenant.service.router;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.tenant.dto.router.H5RouterPatientDto;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.enumeration.RouterModuleTypeEnum;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者路由
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
public interface H5RouterService extends SuperService<H5Router> {

    /**
     * 路由不存在就创建
     */
    List<H5Router> createIfNotExist();

    List<H5Router> createIfNotDoctorExist();

    /**
     * 复制项目信息
     * @param fromTenantCode
     * @param toTenantCode
     */
    void copy(String fromTenantCode, String toTenantCode);

    List<H5Router> createIfNotNursingExist();

    void initNursingAppMenu();

    void initFollowUpMenu();

    /**
     * 增加医助 医生 可见条件
     * @param currentUserType
     * @return
     */
    H5RouterPatientDto patientRouter(String currentUserType);

    /**
     * 查询患者个人中心主页的设置
     */
    H5RouterPatientDto patientRouter();

    /**
     * 更新患者的个人中心主页设置
     * @param h5RouterPatientDto
     */
    void patientRouter(H5RouterPatientDto h5RouterPatientDto);

    /**
     * 查询患者路由
     * @param moduleType
     * @return
     */
    List<H5Router> patientRouter(RouterModuleTypeEnum moduleType, String userType);

    List<H5Router>  getPatientJoinGroupAfterMenu();


    /**
     * 根据医助每个类型的菜单
     * @param dictItemType
     * @return
     */
    H5Router getNursingH5RouterByModuleType(String dictItemType);



    List<String> checkFolderShareUrlExist(String url);
}
