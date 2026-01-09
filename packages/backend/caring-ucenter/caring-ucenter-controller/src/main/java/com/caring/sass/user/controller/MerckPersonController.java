package com.caring.sass.user.controller;

import com.caring.sass.base.R;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.merck.MerckPerson;
import com.caring.sass.user.merck.MerckPersonService;
import com.caring.sass.user.merck.SyncMerckDto;
import com.caring.sass.wx.WeiXinApi;
import com.caring.sass.wx.dto.config.BindUserTagsForm;
import com.caring.sass.wx.dto.enums.TagsEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 敏识用户
 * </p>
 *
 * @author 杨帅
 * @date 2023-12-20
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/merckPerson")
@Api(value = "MerckPerson", tags = "敏识用户")
public class MerckPersonController  {

    @Autowired
    MerckPersonService merckPersonService;

    @Autowired
    WeiXinApi weiXinApi;

    /**
     * 拉取unionId. 需要公众号授权到微信开放平台。
     *
     * @param tenantCode 表示公众号和系统的哪个项目关联。便于在找到微信公众号信息
     * @param wxAppId   微信公众号的ID
     * @return
     */
    @GetMapping("refreshUnionId")
    @ApiOperation("刷新敏识医云粉丝的unionId")
    public R<Boolean> refreshUnionId(String tenantCode, String wxAppId) {
        merckPersonService.refreshUnionId(tenantCode, wxAppId);
        return R.success(true);
    }



    @PostMapping("syncMerckPerson")
    @ApiOperation("拉取敏识数据同步")
    public R<Boolean> syncMerckPerson(@RequestBody SyncMerckDto syncMerckDto) {
        String tenantCode = syncMerckDto.getTenantCode();
        Long doctorId = syncMerckDto.getDoctorId();
        String merckToken = syncMerckDto.getMerckToken();
        String merckDomain = syncMerckDto.getMerckDomain();
        merckPersonService.syncMerckPerson(tenantCode, doctorId, merckToken, merckDomain);
        return R.success(true);
    }


    @ApiOperation("新增敏识的用户")
    @PostMapping("insertPerson")
    public R<Boolean> insertPerson(@RequestBody MerckPerson merckPerson) {

        int count = merckPersonService.count(Wraps.<MerckPerson>lbQ()
                .eq(MerckPerson::getOpenId, merckPerson.getOpenId())
                .eq(MerckPerson::getSubscribe, merckPerson.getSubscribe()));
        if (count > 0) {
            return R.fail("openId已经导入过");
        }
        merckPerson.setInformationSync(0);
        merckPersonService.save(merckPerson);
        return R.success(true);
    }

    @GetMapping("batchWeixinTag")
    @ApiOperation("给敏识用户打sass的标签")
    public R<Boolean> personOpenIds(String wxAppId) {

        List<MerckPerson> people = merckPersonService.list(Wraps.<MerckPerson>lbQ().eq(MerckPerson::getSubscribe, CommonStatus.YES));

        List<List<MerckPerson>> lists = ListUtils.subList(people, 50);
        for (List<MerckPerson> list : lists) {
            List<String> collect = list.stream().map(MerckPerson::getOpenId).collect(Collectors.toList());
            String openIds = String.join(",", collect);
            BindUserTagsForm bindUserTagsForm = new BindUserTagsForm();
            bindUserTagsForm.setWxAppId(wxAppId);
            bindUserTagsForm.setOpenIds(openIds);
            bindUserTagsForm.setTagId(TagsEnum.CARING_SASS_PATIENT.getValue());
            List<String> stringList = new ArrayList<>();
            bindUserTagsForm.setClearTagId(stringList);
            weiXinApi.bindUserTags(bindUserTagsForm);
        }
        return R.success(true);

    }


}
