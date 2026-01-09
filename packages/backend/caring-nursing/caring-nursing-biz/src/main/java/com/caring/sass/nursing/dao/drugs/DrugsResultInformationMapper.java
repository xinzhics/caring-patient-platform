package com.caring.sass.nursing.dao.drugs;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.dto.drugs.DrugsAvailableDTO;
import com.caring.sass.nursing.entity.drugs.DrugsResultInformation;
import com.caring.sass.nursing.vo.DrugsAvailableMessageData;
import com.caring.sass.nursing.vo.DrugsAvailableVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 患者管理-用药预警-预警结果表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
@Repository
public interface DrugsResultInformationMapper extends SuperMapper<DrugsResultInformation> {

    IPage<DrugsAvailableVo> selectDrugsDeficiencyPageList(Page page, @Param("warningType")Integer warningType, @Param("dto")DrugsAvailableDTO dto);


    List<DrugsAvailableMessageData> selectDrugsDate(@Param("patientId")Long patientId,@Param("warningType") Integer warningType, @Param("dto")DrugsAvailableDTO dto);

    Integer getDrugsNumber(@Param("tenant")String tenant, @Param("userId")Long userId, @Param("warningType")Integer warningType,@Param("drugsId")Long drugsId);
}
