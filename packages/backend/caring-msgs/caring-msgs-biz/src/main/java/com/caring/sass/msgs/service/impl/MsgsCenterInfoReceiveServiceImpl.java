package com.caring.sass.msgs.service.impl;


import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.msgs.dao.MsgsCenterInfoReceiveMapper;
import com.caring.sass.msgs.entity.MsgsCenterInfoReceive;
import com.caring.sass.msgs.service.MsgsCenterInfoReceiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 消息中心 接收表
 * 全量数据
 * </p>
 *
 * @author caring
 * @date 2019-08-01
 */
@Slf4j
@Service

public class MsgsCenterInfoReceiveServiceImpl extends SuperServiceImpl<MsgsCenterInfoReceiveMapper, MsgsCenterInfoReceive> implements MsgsCenterInfoReceiveService {

}
