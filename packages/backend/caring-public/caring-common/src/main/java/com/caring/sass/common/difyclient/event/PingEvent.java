package com.caring.sass.common.difyclient.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 心跳事件
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PingEvent extends BaseEvent {
}
