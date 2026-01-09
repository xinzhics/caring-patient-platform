package com.caring.sass.nursing.dao.drugs;

import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.drugs.PatientDrugsTime;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 每次推送生成一条记录，（记录药量，药品）
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
public interface PatientDrugsTimeMapper extends SuperMapper<PatientDrugsTime> {

}
