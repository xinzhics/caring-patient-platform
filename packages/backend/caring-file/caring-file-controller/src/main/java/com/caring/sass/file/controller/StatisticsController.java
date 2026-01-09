package com.caring.sass.file.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.exception.BizException;
import com.caring.sass.file.domain.FileStatisticsDO;
import com.caring.sass.file.dto.FileOverviewDTO;
import com.caring.sass.file.dto.FileStatisticsAllDTO;
import com.caring.sass.file.entity.FileMy;
import com.caring.sass.file.entity.FilePublicImage;
import com.caring.sass.file.entity.FileRecentlyUsed;
import com.caring.sass.file.service.FileMyService;
import com.caring.sass.file.service.FilePublicImageService;
import com.caring.sass.file.service.FileRecentlyUsedService;
import com.caring.sass.file.service.FileService;
import com.caring.sass.security.annotation.LoginUser;
import com.caring.sass.security.model.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 文件用户大小表 前端控制器
 * </p>
 *
 * @author caring
 * @since 2019-05-07
 */
@Slf4j
@RestController
@RequestMapping("/statistics")
@Api(value = "Statistics", tags = "统计接口")
public class StatisticsController {

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "云盘首页数据概览", notes = "云盘首页数据概览")
    @GetMapping(value = "/overview")
    public R<FileOverviewDTO> overview(@ApiIgnore @LoginUser SysUser user) {
        return R.success(fileService.findOverview(user.getId(), null, null));
    }

    @ApiOperation(value = "按照类型，统计各种类型的 大小和数量", notes = "按照类型，统计当前登录人各种类型的大小和数量")
    @GetMapping(value = "/type")
    public R<List<FileStatisticsDO>> findAllByDataType(@ApiIgnore @LoginUser SysUser user) {
        return R.success(fileService.findAllByDataType(user.getId()));
    }

//    @ApiOperation(value = "云盘首页个人文件下载数量排行", notes = "云盘首页个人文件下载数量排行")
//    @GetMapping(value = "/downTop20")
//    public R<List<FileStatisticsDO>> downTop20() {
//        return success(fileService.downTop20(getUserId()));
//    }

    @ApiOperation(value = "按照时间统计各种类型的文件的数量和大小", notes = "按照时间统计各种类型的文件的数量和大小 不指定时间，默认查询一个月")
    @GetMapping(value = "")
    public R<FileStatisticsAllDTO> findNumAndSizeToTypeByDate(@RequestParam(value = "startTime", required = false) LocalDateTime startTime,
                                                              @RequestParam(value = "endTime", required = false) LocalDateTime endTime,
                                                              @ApiIgnore @LoginUser SysUser user) {
        return R.success(fileService.findNumAndSizeToTypeByDate(user.getId(), startTime, endTime));
    }

//    @ApiOperation(value = "按照时间统计下载数量", notes = "按照时间统计下载数量 不指定时间，默认查询一个月")
//    @GetMapping(value = "/down")
//    public R<FileStatisticsAllDTO> findDownSizeByDate(@RequestParam(value = "startTime", required = false) LocalDateTime startTime,
//                                                      @RequestParam(value = "endTime", required = false) LocalDateTime endTime) {
//        Long userId = getUserId();
//        return success(fileService.findDownSizeByDate(userId, startTime, endTime));
//    }

    @Autowired
    FileMyService fileMyService;

    @Autowired
    FilePublicImageService filePublicImageService;

    @Autowired
    FileRecentlyUsedService fileRecentlyUsedService;

    /**
     * 统计图片分类下有多少
     * @return
     */
    @ApiOperation("统计图片的分类下有多少")
    @GetMapping("countImage")
    public R<JSONObject> countImage(@RequestParam(value = "tenantCode", required = false) String tenantCode) {
        // 公共图片直接使用超管的来统计。
        int count = filePublicImageService.count(Wraps.<FilePublicImage>lbQ());
        if (StrUtil.isNotEmpty(tenantCode)) {
            BaseContextHandler.setTenant(tenantCode);
        } else {
            String tenant = BaseContextHandler.getTenant();
            if (StrUtil.isEmpty(tenant)) {
                throw new BizException("租户不能为空");
            }
        }
        String userType = BaseContextHandler.getUserType();
        JSONObject jsonObject = new JSONObject();

        LbqWrapper<FileMy> fileMyLbqWrapper = Wraps.<FileMy>lbQ();
        LbqWrapper<FileRecentlyUsed> usedLbqWrapper = Wraps.<FileRecentlyUsed>lbQ();
        Long userId = BaseContextHandler.getUserId();
        // 如果是超管端，需要增加个人ID统计数据
        if (UserType.GLOBAL_ADMIN.equals(userType) || UserType.THIRD_PARTY_CUSTOMERS.equals(userType)) {
            fileMyLbqWrapper.eq(SuperEntity::getCreateUser, userId);
            usedLbqWrapper.eq(SuperEntity::getCreateUser, userId);
        }
        int count2 = fileRecentlyUsedService.count(usedLbqWrapper);
        int count1 = fileMyService.count(fileMyLbqWrapper);
        jsonObject.put("my", count1);
        jsonObject.put("recentlyUsed", count2);
        jsonObject.put("publicImage", count);
        return R.success(jsonObject);

    }

}
