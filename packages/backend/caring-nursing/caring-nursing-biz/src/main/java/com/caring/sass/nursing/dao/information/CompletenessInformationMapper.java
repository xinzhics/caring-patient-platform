package com.caring.sass.nursing.dao.information;

import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.information.CompletenessInformation;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 患者信息完整度概览表
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Repository
public interface CompletenessInformationMapper extends SuperMapper<CompletenessInformation> {

//    IPage<CompletenessInformation> selectPage(@Param("name") String patientName,
//                                              @Param("min") Integer minValue, @Param("max") Integer maxValue, Page<?> page);

}
