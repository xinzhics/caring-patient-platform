package com.caring.sass.nursing.dao.form;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.dto.form.MonitorUnprocessedDTO;
import com.caring.sass.nursing.entity.form.IndicatorsResultInformation;
import com.caring.sass.nursing.vo.MonitorProcessedVo;
import com.caring.sass.nursing.vo.ValueAndTimeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 患者监测数据结果及处理表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-14
 */
@Repository
@Mapper
public interface IndicatorsResultInformationMapper extends SuperMapper<IndicatorsResultInformation> {

    int getPatientCountByPlanIdNursingId(@Param("planId") Long planId, @Param("nursingId")Long nursingId);

    /**
     * 异常数据已处理列表自定义分页查询
     * @param page
     * @param dto
     * @return
     */
    IPage<MonitorProcessedVo> selectdataProcessedPageList(IPage page,@Param("handleStatus")Integer handleStatus, @Param("dto") MonitorUnprocessedDTO dto);

    List<ValueAndTimeVo> selectdataUnprocessedPageList(@Param("patientId")Long patientId,@Param("handleStatus")Integer handleStatus, @Param("dto") MonitorUnprocessedDTO dto);
}
