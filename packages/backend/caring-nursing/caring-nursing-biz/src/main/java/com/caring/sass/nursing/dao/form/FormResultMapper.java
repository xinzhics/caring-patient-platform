package com.caring.sass.nursing.dao.form;

import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.form.FormResult;
import com.caring.sass.nursing.enumeration.FormEnum;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 表单填写结果表
 * </p>
 *  //原始sql @Select("SELECT t.* FROM ( select *  from t_custom_form_result where form_id = '1328295049082961920' and user_id in('1336497862275497984','1496030787441524736') ORDER BY create_time DESC limit 999999 ) t GROUP BY form_id,user_id")
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
public interface FormResultMapper extends SuperMapper<FormResult> {

}
