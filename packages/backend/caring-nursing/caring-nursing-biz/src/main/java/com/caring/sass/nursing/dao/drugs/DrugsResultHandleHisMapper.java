package com.caring.sass.nursing.dao.drugs;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.dto.drugs.DrugsAvailableDTO;
import com.caring.sass.nursing.entity.drugs.DrugsResultHandleHis;
import com.caring.sass.nursing.vo.DrugsHandledVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 患者管理-用药预警-预警处理历史表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
@Repository
public interface DrugsResultHandleHisMapper extends SuperMapper<DrugsResultHandleHis> {

    IPage<DrugsHandledVo> selectDrugsHandled(Page page, @Param("clearStatus")Integer clearStatus, @Param("dto")DrugsAvailableDTO dto);
}
