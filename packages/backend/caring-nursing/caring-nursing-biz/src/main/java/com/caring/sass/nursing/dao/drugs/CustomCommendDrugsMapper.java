package com.caring.sass.nursing.dao.drugs;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.drugs.CustomCommendDrugs;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 自定义推荐药品
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
public interface CustomCommendDrugsMapper extends SuperMapper<CustomCommendDrugs> {



    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    @Delete({"DELETE FROM t_custom_commend_drugs where drugs_id in (${ids})"})
    void deleteByDrugsLd(@Param("ids") String ids);


}
