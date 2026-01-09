package com.caring.sass.nursing.service.drugs;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.drugs.DrugsAvailableDTO;
import com.caring.sass.nursing.entity.drugs.DrugsResultHandleHis;
import com.caring.sass.nursing.vo.DrugsHandledVo;

/**
 * <p>
 * 业务接口
 * 患者管理-用药预警-预警处理历史表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
public interface DrugsResultHandleHisService extends SuperService<DrugsResultHandleHis> {

    /**
     * 用药预警-余药不足（逾期）已处理列表
     *
     */
    IPage<DrugsHandledVo> getDrugsHandled(DrugsAvailableDTO dto);

    /**
     * 用药预警-已处理-清除标记
     *
     * @param
     */
    R getDrugsEliminate();
}
