package com.caring.sass.msgs.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.msgs.dto.MsgsCenterInfoPageResultDTO;
import com.caring.sass.msgs.dto.MsgsCenterInfoQueryDTO;
import com.caring.sass.msgs.entity.MsgsCenterInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 消息中心
 * </p>
 *
 * @author caring
 * @date 2019-08-01
 */
@Repository
public interface MsgsCenterInfoMapper extends SuperMapper<MsgsCenterInfo> {
    /**
     * 查询消息中心分页数据
     *
     * @param page
     * @param param
     * @return
     */
    IPage<MsgsCenterInfoPageResultDTO> page(IPage page, @Param("data") MsgsCenterInfoQueryDTO param);
}
