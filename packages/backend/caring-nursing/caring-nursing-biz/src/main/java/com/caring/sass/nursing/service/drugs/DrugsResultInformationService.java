package com.caring.sass.nursing.service.drugs;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.drugs.DrugsAvailableDTO;
import com.caring.sass.nursing.entity.drugs.DrugsResultInformation;
import com.caring.sass.nursing.vo.DrugsAvailableVo;
import com.caring.sass.nursing.vo.DrugsListVo;
import com.caring.sass.nursing.vo.DrugsStatisticsVo;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者管理-用药预警-预警结果表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
public interface DrugsResultInformationService extends SuperService<DrugsResultInformation> {

    /**
     * 用药预警-患者数
     *
     * @param
     */
    Integer getDrugsNumber();

    /**
     * 用药预警-余药不足
     * @param dto
     * @return
     */
    IPage<DrugsAvailableVo> getDrugsDeficiency(DrugsAvailableDTO dto);

    /**
     * 用药预警-购药逾期
     * @param dto
     * @return
     */
    IPage<DrugsAvailableVo> getDrugsBeOverdue(DrugsAvailableDTO dto);


    /**
     * 用药预警-统计
     *
     * @param
     * @return
     */
    DrugsStatisticsVo getDrugsStatistics(Long drugsId);

    /**
     * 用药预警-统计-药品列表
     *
     *
     *
     */
    List<DrugsListVo> getDrugsList();

    /**
     * 用药预警-余药不足-单个（全部）处理
     *
     * @param patientId
     */
    R getDrugsDeficiencyHandle(Long patientId);

    /**
     * 用药预警-用药逾期-单个（全部）处理
     *
     * @param patientId
     */
    R getDrugsBeOverdueHandle(Long patientId);



}

