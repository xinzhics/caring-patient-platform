package com.caring.sass.cms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.cms.dto.*;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.cms.entity.ContentCollect;
import com.caring.sass.cms.entity.ContentReply;
import com.caring.sass.cms.entity.ReplyLikeLog;
import com.caring.sass.cms.service.ChannelContentService;
import com.caring.sass.cms.service.ContentReplyService;
import com.caring.sass.cms.service.ReplyLikeLogService;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Patient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 内容留言
 * </p>
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/contentReply")
@Api(value = "ContentReply", tags = "文章评论")
//@PreAuth(replace = "contentReply:")
public class ContentReplyController extends SuperController<ContentReplyService, Long, ContentReply, ContentReplyPageDTO, ContentReplySaveDTO, ContentReplyUpdateDTO> {

    private final PatientApi patientApi;

    private final DoctorApi doctorApi;

    private final ChannelContentService contentService;

    private final ReplyLikeLogService replyLikeLogService;

    public ContentReplyController(PatientApi patientApi, ChannelContentService contentService, ReplyLikeLogService replyLikeLogService, DoctorApi doctorApi) {
        this.patientApi = patientApi;
        this.contentService = contentService;
        this.replyLikeLogService = replyLikeLogService;
        this.doctorApi = doctorApi;
    }

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<ContentReply> contentReplyList = list.stream().map((map) -> {
            ContentReply contentReply = ContentReply.builder().build();
            //TODO 请在这里完成转换
            return contentReply;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(contentReplyList));
    }

    @Override
    public R<ContentReply> handlerSave(ContentReplySaveDTO saveDTO) {

        Long replierId = saveDTO.getReplierId();

        String roleType = saveDTO.getRoleType();
        if (StringUtils.isEmpty(roleType)) {
            roleType = UserType.UCENTER_PATIENT;
        }
        // 文章
        Long contentId = saveDTO.getContentId();
        ChannelContent content = contentService.getCmsByRedis(contentId);
        if (Objects.isNull(content)) {
            return R.fail("文章不存在或已删除");
        }
        // 修改文章的评论数
        content.setMessageNum(content.getMessageNum() == null ? 1 : content.getMessageNum() + 1);
        contentService.updateById(content);

        ContentReply.ContentReplyBuilder builder = ContentReply.builder()
                .content(saveDTO.getContent())
                .contentId(contentId)
                .replierId(replierId)
                .roleType(roleType);
        if (UserType.UCENTER_PATIENT.equals(roleType)) {
            R<Patient> patientR = patientApi.get(replierId);
            if (patientR.getIsError() || Objects.isNull(patientR.getData())) {
                return R.fail("用户不存在");
            }
            Patient patient = patientR.getData();
            ContentReply build = builder.replierWxName(patient.getName())
                    .replierAvatar(patient.getAvatar()).build();
            baseService.save(build);
            return R.success(build);
        } else {
            R<Doctor> doctorR = doctorApi.get(replierId);
            if (doctorR.getIsError() || Objects.isNull(doctorR.getData())) {
                return R.fail("用户不存在");
            }
            Doctor doctor = doctorR.getData();
            ContentReply build = builder.replierWxName(doctor.getName())
                    .replierAvatar(doctor.getAvatar()).build();
            baseService.save(build);
            return R.success(build);
        }

    }


    @ApiOperation("评论的文章")
    @PostMapping("pageContent")
    public R<IPage<ContentReply>> pageContent(@RequestBody PageParams<ContentReplyPageDTO> params) {
        IPage<ContentReply> page = params.buildPage();
        ContentReplyPageDTO model = params.getModel();
        LbqWrapper<ContentReply> query = Wraps.<ContentReply>lbQ()
                .select(ContentReply::getContentId)
                .eq(ContentReply::getRoleType, model.getRoleType())
                .eq(ContentReply::getReplierId, model.getReplierId())
                .groupBy(ContentReply::getContentId)
                .orderByDesc(ContentReply::getCreateTime);
        page = baseService.page(page, query);
        List<ContentReply> records = page.getRecords();
        List<String> collect = records.stream().map(record -> record.getContentId().toString()).collect(Collectors.toList());

        List<ChannelContent> channelContents = contentService.listNoTenantCode(collect);

        Map<Long, ChannelContent> contentMap = channelContents.stream().collect(Collectors.toMap(ChannelContent::getId, item -> item, (channelContent, channelContent2) -> channelContent2));

        records.forEach(item -> item.setChannelContent(contentMap.get(item.getContentId())));

        return R.success(page);
    }

    @ApiOperation(value = "点赞")
    @PostMapping("/like")
    public R<Boolean> like(@Validated @RequestBody ContentReplyLikeDTO contentReplyLikeDTO) {
        Long replyId = contentReplyLikeDTO.getReplyId();
        Long userId = contentReplyLikeDTO.getUserId();
        String roleType = contentReplyLikeDTO.getRoleType();
        // 操作，1点赞，2取消点赞
        Integer operation = contentReplyLikeDTO.getOperation();
        ContentReply reply = baseService.getById(replyId);
        if (Objects.isNull(reply)) {
            return R.fail("评论不存在或已删除");
        }

        boolean hasLike = replyLikeLogService.hasLike(replyId, userId, roleType);

        Integer likeCount = reply.getLikeCount() == null ? 0 : reply.getLikeCount();

        // 点赞
        if (Objects.equals(operation, 1)) {
            if (hasLike) {
                return R.fail("您已点赞");
            }
            likeCount++;
            // 记录点赞日志
            ReplyLikeLog build = ReplyLikeLog.builder().hasCancel(1)
                    .replyId(replyId).userId(userId).roleType(roleType).build();
            build.setCreateUser(userId);
            replyLikeLogService.save(build);
        }

        // 取消点赞
        if (Objects.equals(operation, 2)) {
            // 未点赞，不需要取消
            if (!hasLike) {
                return R.success();
            }
            // 取消点赞
            likeCount = likeCount < 1 ? 0 : --likeCount;
            replyLikeLogService.update(Wraps.<ReplyLikeLog>lbU()
                    .set(ReplyLikeLog::getHasCancel, 2)
                    .eq(ReplyLikeLog::getUserId, userId)
                    .eq(ReplyLikeLog::getReplyId, replyId)
                    .eq(ReplyLikeLog::getRoleType, roleType));
        }

        reply.setLikeCount(likeCount);
        baseService.updateById(reply);
        return R.success();
    }
}
