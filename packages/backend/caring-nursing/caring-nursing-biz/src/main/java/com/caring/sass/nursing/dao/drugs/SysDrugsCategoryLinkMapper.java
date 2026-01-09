package com.caring.sass.nursing.dao.drugs;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategoryLink;
import org.springframework.stereotype.Repository;

/**
 * @Author yangShuai
 * @Description 药品类别关联表
 * @Date 2021/3/1 16:18
 *
 * @return
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface SysDrugsCategoryLinkMapper extends SuperMapper<SysDrugsCategoryLink> {

}
