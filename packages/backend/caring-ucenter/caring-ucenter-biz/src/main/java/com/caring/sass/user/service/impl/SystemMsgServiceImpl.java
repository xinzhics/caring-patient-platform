package com.caring.sass.user.service.impl;


import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.user.dao.SystemMsgMapper;
import com.caring.sass.user.entity.SystemMsg;
import com.caring.sass.user.service.SystemMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Slf4j
@Service
public class SystemMsgServiceImpl extends SuperServiceImpl<SystemMsgMapper, SystemMsg> implements SystemMsgService {

}
