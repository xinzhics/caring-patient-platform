package com.caring.sass.authority.service.common.impl;


import com.caring.sass.authority.dao.common.OptLogMapper;
import com.caring.sass.authority.entity.common.OptLog;
import com.caring.sass.authority.service.common.OptLogService;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.log.entity.OptLogDTO;
import com.caring.sass.utils.BeanPlusUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 业务实现类
 * 系统日志
 * </p>
 *
 * @author caring
 * @date 2019-07-02
 */
@Slf4j
@Service

public class OptLogServiceImpl extends SuperServiceImpl<OptLogMapper, OptLog> implements OptLogService {

    @Override
    public boolean save(OptLogDTO entity) {
        return super.save(BeanPlusUtil.toBean(entity, OptLog.class));
    }

    @Override
    public boolean clearLog(LocalDateTime clearBeforeTime, Integer clearBeforeNum) {
        return baseMapper.clearLog(clearBeforeTime, clearBeforeNum);
    }
}
