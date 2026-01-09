package com.caring.sass.msgs.api;

import com.caring.sass.base.R;
import com.caring.sass.msgs.api.fallback.GroupImFallback;
import com.caring.sass.msgs.dto.ImGroupDto;
import com.caring.sass.msgs.entity.ConsultationChat;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName GroupImApi
 * @Description
 * @Author yangShuai
 * @Date 2021/6/2 16:29
 * @Version 1.0
 **/
@Component
@FeignClient(name = "${caring.feign.msgs-server:caring-msgs-server}", fallback = GroupImFallback.class
        ,qualifier = "GroupImApi",path = "groupIm")
public interface GroupImApi {

    @ApiOperation("创建一个环信的im群组")
    @PostMapping("/created")
    R<String> createImGroup(@RequestBody ImGroupDto imGroupDto);

    @ApiOperation("删除环信的im群组")
    @GetMapping("/delete/{imGroupId}")
    R<Boolean> deleteImGroup(@PathVariable("imGroupId") String imGroupId);

    @DeleteMapping("deleteImGroupMessage/{groupId}")
    R<Boolean> deleteImGroupMessage(@PathVariable("groupId") Long groupId);

    @ApiOperation("添加成员到环信的im群组")
    @GetMapping("/addUserToGroup/{groupId}/{userImAccount}")
    R<Boolean> addUserToGroup(@PathVariable("groupId") String groupId,
                                     @PathVariable("userImAccount") String userImAccount,
                                     @RequestParam(value = "newOwner", required = false) Boolean newOwner);

    @ApiOperation("移除成员从环信的im群组")
    @GetMapping("/removeUserFromGroup/{groupId}/{userImAccount}")
    R<Boolean> removeUserFromGroup(@PathVariable("groupId") String groupId, @PathVariable("userImAccount") String userImAccount);

    @ApiOperation("发送群组消息")
    @PostMapping("sendConsultationChat")
    R<ConsultationChat> sendConsultationChat(@RequestBody ConsultationChat consultationChat);

    @ApiOperation("统计未读消息数量")
    @PostMapping("appReadMessage/{receiverId}")
    R<Map<Long, Integer>> countNoReadMessage(@PathVariable("receiverId") Long receiverId,
                                             @RequestParam(value = "userRole", required = false) String userRole,
                                             @RequestBody List<Long> groupIds);

}
