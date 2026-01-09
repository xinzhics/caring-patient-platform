package com.caring.sass.ai.entity.dto;

import com.caring.sass.ai.entity.chat.MiniAppUserChatHistoryGroup;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @className: MiniAppChatHistoryGroupDTO
 * @author: 杨帅
 * @date: 2024/3/28
 */
@Data
@ApiModel("历史记录的分组")
public class MiniAppChatHistoryGroupDTO {

    public Map<String, List<MiniAppUserChatHistoryGroup>> historyGroup;

    /**
     * 当前分页总页数
     */
    private long pages;

    /**
     * 当前满足条件总行数
     *
     * @return 总条数
     */
    private long total;

    private long size;

    private long current;

}
