package com.caring.sass.user.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.dto.CommonMsgPageDTO;
import com.caring.sass.user.dto.CommonMsgSaveDTO;
import com.caring.sass.user.dto.CommonMsgTemplateContentPageDTO;
import com.caring.sass.user.dto.CommonMsgUpdateDTO;
import com.caring.sass.user.entity.CommonMsg;
import com.caring.sass.user.entity.CommonMsgTemplateContent;
import com.caring.sass.user.entity.CommonMsgType;
import com.caring.sass.user.service.CommonMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 小组
 * </p>
 *
 * @author leizhi
 * @date 2020-09-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/commonMsg")
@Api(value = "CommonMsg", tags = "常用语")
//@PreAuth(replace = "commonMsg:")
public class CommonMsgController extends SuperController<CommonMsgService, Long, CommonMsg, CommonMsgPageDTO, CommonMsgSaveDTO, CommonMsgUpdateDTO> {


    @ApiOperation("删除一个常用语")
    @DeleteMapping("/delete/{id}")
    public R<Boolean> deleteById(@PathVariable("id") Long id) {
        baseService.removeById(id);
        return R.success();
    }

    @ApiOperation("清空常用语")
    @DeleteMapping("/delete/all/{accountId}")
    public R<Boolean> deleteAll(@PathVariable("accountId") Long accountId,
                                @RequestParam(value = "userType", required = false) String userType) {
        baseService.cleanAll(accountId, userType);
        return R.success();
    }


    /**
     * 将选择的常用语模版导入到自己的常用语库
     * @return
     */
    @ApiOperation("将选择的常用语模版导入到自己的常用语")
    @PostMapping("importCommonMsgTemplate/{userId}/{userType}")
    public R<Boolean> importCommonMsgTemplate(
            @PathVariable("userId") Long userId,
            @PathVariable("userType") String userType,
            @RequestBody List<Long> commonMsgTemplateIds) {

        baseService.importCommonMsgTemplate(userId, userType, commonMsgTemplateIds);
        return R.success(true);

    }


    /**
     * 将选择的常用语模版导入到自己的常用语库
     * @return
     */
    @ApiOperation("查询自己的常用语分类")
    @GetMapping("queryMyType/{userId}/{userType}")
    public R<List<CommonMsgType>> queryMyType(@PathVariable("userId") Long userId,
                                              @PathVariable("userType") String userType) {
        List<CommonMsgType> msgTypes = baseService.queryMyType(userId, userType);
        return R.success(msgTypes);
    }




    /**
     * 将选择的常用语模版导入到自己的常用语库
     *
     * @return
     */
    @ApiOperation("添加常用语时 查询所有的分类，系统的和自己的")
    @GetMapping("queryAllType/{userId}/{userType}")
    public R<List<CommonMsgType>> queryAllType(@PathVariable("userId") Long userId,
                                               @PathVariable("userType") String userType) {

        List<CommonMsgType> msgTypes = baseService.queryAllType(userId, userType);
        return R.success(msgTypes);
    }


    @ApiOperation("新增或保存常用语和分类")
    @PostMapping("saveOrUpdateCommonMsgAndType")
    public R<Boolean> saveOrUpdateCommonMsgAndType(@RequestBody CommonMsg commonMsg) {

        baseService.saveOrUpdateCommonMsgAndType(commonMsg);
        return R.success(true);
    }


    /**
     * 新常用语分页查询
     * @param params
     * @return
     */
    @ApiOperation("新常用语分页查询")
    @PostMapping("pageCommonMsg")
    public R<IPage<CommonMsg>> pageCommonMsg(@RequestBody PageParams<CommonMsgPageDTO> params) {

        CommonMsgPageDTO model = params.getModel();
        LbqWrapper<CommonMsg> lbqWrapper = Wraps.<CommonMsg>lbQ();
        if (StrUtil.isNotEmpty(model.getUserType())) {
            lbqWrapper.eq(CommonMsg::getUserType, model.getUserType());
        }
        if (StrUtil.isNotEmpty(model.getContent())) {
            lbqWrapper.and(e ->
                    e.like(CommonMsg::getTitle, model.getContent()).or()
                            .like(CommonMsg::getContent, model.getContent()));
        }
        if (Objects.nonNull(model.getAccountId())) {
            lbqWrapper.eq(CommonMsg::getAccountId, model.getAccountId());
        }
        if (Objects.nonNull(model.getTypeId())) {
            lbqWrapper.eq(CommonMsg::getTypeId, model.getTypeId());
        }
        lbqWrapper.orderByDesc(SuperEntity::getCreateTime);
        IPage<CommonMsg> page = params.buildPage();
        baseService.page(page, lbqWrapper);
        return R.success(page);
    }

}
