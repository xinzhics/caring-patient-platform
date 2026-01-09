package com.caring.sass.user.controller;


import java.sql.Wrapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.dto.FollowReplyPageDTO;
import com.caring.sass.user.dto.FollowReplySaveDTO;
import com.caring.sass.user.dto.FollowReplyUpdateDTO;
import com.caring.sass.user.entity.FollowReply;
import com.caring.sass.user.service.FollowReplyService;
import com.caring.sass.user.service.redis.FollowUpNotRegisterCallBackCenter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 关注后回复和关注未注册回复
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-06
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/followReply")
@Api(value = "FollowReply", tags = "关注后回复和关注未注册回复")
//@PreAuth(replace = "followReply:")
public class FollowReplyController extends SuperController<FollowReplyService, Long, FollowReply, FollowReplyPageDTO, FollowReplySaveDTO, FollowReplyUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<FollowReply> followReplyList = list.stream().map((map) -> {
            FollowReply followReply = FollowReply.builder().build();
            //TODO 请在这里完成转换
            return followReply;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(followReplyList));
    }


    @ApiOperation("获取关注后回复或未注册回复")
    @GetMapping("getFollowReply")
    public R<FollowReply> getFollowReply(@RequestParam("replyCategory") String replyCategory) {
        FollowReply followReply = baseService.getOne(Wraps.<FollowReply>lbQ().eq(FollowReply::getReplyCategory, replyCategory).last(" limit 0, 1"));
        return R.success(followReply);
    }


    @Autowired
    FollowUpNotRegisterCallBackCenter followUpNotRegisterCallBackCenter;

    @ApiOperation("测试任务")
    @GetMapping("testTask")
    public R<Void> testTask() {
        followUpNotRegisterCallBackCenter.handleTask();
        return R.success(null);
    }


}
