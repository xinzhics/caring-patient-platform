package com.caring.sass.file.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.file.constant.ClassificationBelongEnum;
import com.caring.sass.file.dto.image.FileClassificationPageDTO;
import com.caring.sass.file.dto.image.FileClassificationSaveDTO;
import com.caring.sass.file.dto.image.FileClassificationUpdateDTO;
import com.caring.sass.file.entity.FileClassification;

import com.caring.sass.file.service.FileClassificationService;

import java.sql.Wrapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.caring.sass.security.annotation.PreAuth;


/**
 * <p>
 * 前端控制器
 * 图片分组
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/fileClassification")
@Api(value = "FileClassification", tags = "图片分组")
//@PreAuth(replace = "fileClassification:")
public class FileClassificationController extends SuperController<FileClassificationService, Long, FileClassification, FileClassificationPageDTO, FileClassificationSaveDTO, FileClassificationUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<FileClassification> fileClassificationList = list.stream().map((map) -> {
            FileClassification fileClassification = FileClassification.builder().build();
            //TODO 请在这里完成转换
            return fileClassification;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(fileClassificationList));
    }

    @ApiOperation("自定义新增图片的分组")
    @PostMapping("customSave")
    public R<FileClassification> customSave(
            @RequestParam(required = false) String tenantCode,
            @RequestBody @Validated FileClassificationSaveDTO fileClassificationSaveDTO) {
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        FileClassification classification = new FileClassification();
        BeanUtils.copyProperties(fileClassificationSaveDTO, classification);
        baseService.save(classification);
        return R.success(classification);
    }

    @ApiOperation("自定义修改图片的分组")
    @PostMapping("customUpdate")
    public R<FileClassification> customUpdate(
            @RequestParam(required = false) String tenantCode,
            @RequestBody @Validated FileClassificationUpdateDTO fileClassificationUpdateDTO) {
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        FileClassification classification = new FileClassification();
        BeanUtils.copyProperties(fileClassificationUpdateDTO, classification);
        classification.setId(fileClassificationUpdateDTO.getId());
        baseService.updateById(classification);
        return R.success(classification);
    }


    @ApiOperation("更换两个分组的顺序")
    @PostMapping("change/classificationSort")
    public R<FileClassification> customUpdate(
            @RequestParam(required = false) String tenantCode,
            @RequestParam("firstId") Long firstId,
            @RequestParam("nextId") Long nextId) {
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        FileClassification classification = baseService.getById(firstId);
        Integer classificationSort = classification.getClassificationSort();
        FileClassification nextClassification = baseService.getById(nextId);
        // 向下拖动
        if (nextClassification.getClassificationSort() > classification.getClassificationSort()) {
            // 吧小于nextClassification.getClassificationSort()的分组查出来。然后排序都减1
            List<FileClassification> classifications = baseService.list(Wraps.<FileClassification>lbQ()
                    .eq(FileClassification::getClassificationBelong, classification.getClassificationBelong())
                    .lt(FileClassification::getClassificationSort, nextClassification.getClassificationSort())
                    .gt(FileClassification::getClassificationSort, classificationSort));
            classification.setClassificationSort(nextClassification.getClassificationSort());
            nextClassification.setClassificationSort(nextClassification.getClassificationSort() - 1);
            if (CollUtil.isNotEmpty(classifications)) {
                for (FileClassification fileClassification : classifications) {
                    fileClassification.setClassificationSort(fileClassification.getClassificationSort() - 1);
                }
                baseService.updateBatchById(classifications);
            }
        } else
        // 向上拖动
        {
            // 吧大于nextClassification.getClassificationSort()的分组查出来。然后排序都减1
            List<FileClassification> classifications = baseService.list(Wraps.<FileClassification>lbQ()
                    .eq(FileClassification::getClassificationBelong, classification.getClassificationBelong())
                    .gt(FileClassification::getClassificationSort, nextClassification.getClassificationSort())
                    .lt(FileClassification::getClassificationSort, classificationSort));
            classification.setClassificationSort(nextClassification.getClassificationSort());
            nextClassification.setClassificationSort(nextClassification.getClassificationSort() + 1);
            if (CollUtil.isNotEmpty(classifications)) {
                for (FileClassification fileClassification : classifications) {
                    fileClassification.setClassificationSort(fileClassification.getClassificationSort() + 1);
                }
                baseService.updateBatchById(classifications);
            }
        }
        baseService.updateById(classification);
        baseService.updateById(nextClassification);
        return R.success(classification);
    }

    @ApiOperation("查询分组列表")
    @GetMapping("listCustom")
    public R<List<FileClassification>> listCustom(@RequestParam(required = false) String tenantCode,
                                                  @RequestParam ClassificationBelongEnum classificationBelong) {

        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        String userType = BaseContextHandler.getUserType();
        Long userId = BaseContextHandler.getUserId();
        // 如果是超管端，需要增加个人ID统计数据
        LbqWrapper<FileClassification> lbqWrapper = Wraps.<FileClassification>lbQ()
                .eq(FileClassification::getClassificationBelong, classificationBelong.toString())
                .orderByAsc(FileClassification::getClassificationSort);
        if (UserType.GLOBAL_ADMIN.equals(userType) || UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            lbqWrapper.eq(SuperEntity::getCreateUser, userId);
        }
        List<FileClassification> classifications = baseService.list(lbqWrapper);
        return R.success(classifications);

    }

    @ApiOperation("删除某个分组")
    @GetMapping("removeCustomById/{id}")
    public R<Boolean> removeCustomById(@RequestParam(required = false) String tenantCode,
                                       @PathVariable("id") Long id) {

        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        }
        baseService.removeById(id);
        return R.success(true);
    }


}
