package com.caring.sass.nursing.controller.tag;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.nursing.dto.tag.*;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.entity.tag.Tag;
import com.caring.sass.nursing.service.tag.AttrService;
import com.caring.sass.nursing.service.tag.TagService;
import com.caring.sass.oauth.api.DoctorGroupApi;
import com.caring.sass.utils.BeanPlusUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 标签管理
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tag")
@Api(value = "Tag", tags = "标签管理")
public class TagController extends SuperController<TagService, Long, Tag, TagPageDTO, TagSaveDTO, TagUpdateDTO> {

    private final AttrService attrService;

    @Autowired
    DoctorGroupApi doctorGroupApi;

    public TagController(AttrService attrService) {
        this.attrService = attrService;
    }


    /**
     * 查询项目的标签列表。并统计标签下的人数
     * @return
     */
    @ApiOperation("标签列表")
    @PostMapping("getTagListAndCountPatientNumber")
    public R<List<Tag>> getTagListAndCountPatientNumber(@RequestBody @Validated TagPatientCountQuery tagPatientCountQuery) {

        List<Tag> tagList = baseService.list(Wraps.lbQ());
        // 使用tabId 和 医助医生条件。关联患者表。以tabId分组统计。
        Long serviceAdvisorId = tagPatientCountQuery.getServiceAdvisorId();
        Long doctorId = tagPatientCountQuery.getDoctorId();
        List<Long> doctorIds = new ArrayList<>();
        String dimension = tagPatientCountQuery.getDimension();
        if (StrUtil.isNotEmpty(dimension) && StrUtil.equals(dimension, "all")) {
            // 查询医生所在小组的所有医生ID
            R<List<Long>> doctorIdByDoctorId = doctorGroupApi.findGroupDoctorIdByDoctorId(doctorId);
            doctorIds = doctorIdByDoctorId.getData();
            if (CollUtil.isEmpty(doctorIds)) {
                return R.success(tagList);
            }
        }
        if (StrUtil.isNotEmpty(dimension) && StrUtil.equals(dimension, "group")) {
            R<List<Long>> doctorIdByDoctorId = doctorGroupApi.findGroupDoctorIdByGroupId(tagPatientCountQuery.getGroupId());
            doctorIds = doctorIdByDoctorId.getData();
            if (CollUtil.isEmpty(doctorIds)) {
                return R.success(tagList);
            }
        }
        List<TagPatientCountResult> countResult = baseService.selectTagCountPatientNumber(serviceAdvisorId, doctorId, doctorIds);
        Map<Long, Integer> map = countResult.stream().collect(Collectors.toMap(TagPatientCountResult::getTagId, TagPatientCountResult::getPatientCount));
        for (Tag tag : tagList) {
            Integer count = map.get(tag.getId());
            if (count != null) {
                tag.setCountPatient(count);
            } else {
                tag.setCountPatient(0);
            }
        }
        return R.success(tagList);

    }


    /**
     * 插入或更新标签
     */
    @ApiOperation("插入或更新标签")
    @PostMapping("upsert")
    public R<Boolean> upsert(@RequestBody @Validated TagUpsertDTO tagUpsertDTO) {
        Tag tag = BeanUtil.toBean(tagUpsertDTO, Tag.class);
        Long tagId = tag.getId();
        baseService.judgeUpdate(tagId);
        // 保存或更新标签信息
        baseService.saveOrUpdate(tag);
        // 删除已有的标签属性配置
        if (Objects.nonNull(tagId)) {
            attrService.remove(Wraps.<Attr>lbQ().eq(Attr::getTagId, tagId));
        }

        // 保存或更新标签属性信息
        List<TagAttrUpsertDTO> tagAttrs = tagUpsertDTO.getTagAttrs();
        if (CollUtil.isEmpty(tagAttrs)) {
            return R.success();
        }
        // 新增前面tagId为空，需要新增后重新取
        tagId = Objects.isNull(tagId) ? tag.getId() : tagId;
        Long finalTagId = tagId;
        List<Attr> attrs = tagAttrs.stream().map(t -> {
            Attr a = BeanUtil.toBean(t, Attr.class);
            a.setTagId(finalTagId);
            return a;
        }).collect(Collectors.toList());
        attrService.saveOrUpdateBatch(attrs);
        return R.success();
    }

    @ApiOperation("打开标签绑定患者任务")
    @GetMapping("openTagBindPatientTask/{tenantCode}/{tagId}")
    public R<String> openTagBindPatientTask(@PathVariable("tenantCode") String tenantCode,
                                            @PathVariable("tagId") Long tagId) {
        BaseContextHandler.setTenant(tenantCode);
        baseService.openTagBindPatientTask(tagId);
        return R.success("success");
    }

    /**
     * 查询项目标签及标签属性
     */
    @ApiOperation("查询项目标签及标签属性")
    @GetMapping("listTagWithAttr")
    public R<List<TagUpsertDTO>> listTagWithAttr() {
        List<TagUpsertDTO> data = new ArrayList<>();
        List<Tag> tags = baseService.list();
        if (CollUtil.isEmpty(tags)) {
            return R.success(data);
        }
        // 查询标签属性
        for (Tag tag : tags) {
            TagUpsertDTO tagUpsertDTO = new TagUpsertDTO();
            BeanUtil.copyProperties(tag, tagUpsertDTO);
            List<Attr> attrs = attrService.list(Wraps.<Attr>lbQ().eq(Attr::getTagId, tag.getId()));
            List<TagAttrUpsertDTO> attrUpsertDTOS = BeanPlusUtil.toBeanList(attrs, TagAttrUpsertDTO.class);
            tagUpsertDTO.setTagAttrs(attrUpsertDTOS);
            data.add(tagUpsertDTO);
        }
        return R.success(data);
    }

    /**
     * 获取标签及属性
     */
    @ApiOperation("获取标签及属性")
    @GetMapping("getTagWithAttr")
    public R<TagUpsertDTO> getTagWithAttr(@RequestParam(value = "id") @NotNull(message = "id不能为空") Long id) {
        TagUpsertDTO tagUpsertDTO = new TagUpsertDTO();
        Tag tag = baseService.getById(id);
        if (Objects.isNull(tag)) {
            return R.fail("标签不存在");
        }
        // 查询标签属性
        BeanUtil.copyProperties(tag, tagUpsertDTO);
        List<Attr> attrs = attrService.list(Wraps.<Attr>lbQ().eq(Attr::getTagId, tag.getId()));
        List<TagAttrUpsertDTO> attrUpsertDTOS = BeanPlusUtil.toBeanList(attrs, TagAttrUpsertDTO.class);
        tagUpsertDTO.setTagAttrs(attrUpsertDTOS);
        return R.success(tagUpsertDTO);
    }


    @ApiOperation("复制标签")
    @PostMapping("copyTag")
    public R<Boolean> copyTag(@RequestParam("fromTenantCode") @NotEmpty(message = "待复制的项目编码不能为空") String fromTenantCode,
                              @RequestParam("toTenantCode") @NotEmpty(message = "目标项目编码不能为空") String toTenantCode) {
        baseService.copyTag(fromTenantCode, toTenantCode);
        return R.success();
    }


}
