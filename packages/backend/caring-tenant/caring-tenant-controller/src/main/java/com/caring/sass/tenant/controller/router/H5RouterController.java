package com.caring.sass.tenant.controller.router;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.security.annotation.PreAuth;
import com.caring.sass.tenant.dto.router.*;
import com.caring.sass.tenant.entity.router.H5Router;
import com.caring.sass.tenant.enumeration.RouterModuleTypeEnum;
import com.caring.sass.tenant.service.router.H5RouterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.sql.Wrapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 患者路由
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/h5Router")
@Api(value = "H5Router", tags = "患者路由")
//@PreAuth(replace = "h5Router:")
public class H5RouterController extends SuperController<H5RouterService, Long, H5Router, H5RouterPageDTO, H5RouterSaveDTO, H5RouterUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<H5Router> h5RouterList = list.stream().map((map) -> {
            H5Router h5Router = H5Router.builder().build();
            //TODO 请在这里完成转换
            return h5Router;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(h5RouterList));
    }

    @ApiOperation("超管端查询患者个人中心路由设置")
    @GetMapping("/patientRouter/{code}")
    public R<H5RouterPatientDto> patientRouter(@PathVariable("code") String code) {
        BaseContextHandler.setTenant(code);
        H5RouterPatientDto h5RouterPatientDto = baseService.patientRouter();
        return R.success(h5RouterPatientDto);
    }

    @ApiOperation("超管端更新患者个人中心路由设置")
    @PostMapping("/patientRouter/{code}")
    public R<H5RouterPatientDto> patientRouter(@PathVariable("code") String code, @RequestBody H5RouterPatientDto h5RouterPatientDto) {
        BaseContextHandler.setTenant(code);
        baseService.patientRouter(h5RouterPatientDto);
        return R.success(h5RouterPatientDto);
    }

    @ApiOperation("查询患者管理平台信息")
    @GetMapping("getPatientManagePlatform")
    public R<H5Router> getH5RouterByModuleType(@RequestParam("tenantCode") String tenantCode) {

        BaseContextHandler.setTenant(tenantCode);
        H5Router h5Router = baseService.getNursingH5RouterByModuleType("NURSING_PATIENT_MANAGE_PLATFORM");
        return R.success(h5Router);

    }

    @ApiOperation("根据功能分类查询显示的患者菜单")
    @GetMapping("getH5RouterByModuleType")
    public R<List<H5Router>> getH5RouterByModuleType(@RequestParam("moduleType") RouterModuleTypeEnum moduleType,
                                                     @RequestParam("userType") String userType) {

        List<H5Router> routerList = baseService.patientRouter(moduleType, userType);
        return R.success(routerList);

    }

    @ApiOperation("查询文件夹的分享链接被用在那个项目")
    @GetMapping({"/checkFolderShareUrlExist"})
    public R<List<String>> checkFolderShareUrlExist(@RequestParam("url") String url) {


        List<String> tenantCodes = baseService.checkFolderShareUrlExist(url);
        return R.success(tenantCodes);

    }

    @ApiOperation("用户端查询患者个人中心路由设置")
    @ApiImplicitParam(name = "currentUserType", value = "NURSING_STAFF, DOCTOR, 患者端不需要传")
    @GetMapping("getPatientRouter")
    public R<H5RouterPatientDto> getPatientRouter(@RequestParam(value = "currentUserType", required = false) String currentUserType) {
        H5RouterPatientDto h5RouterPatientDto;
        if (StrUtil.isNotEmpty(currentUserType)) {
            h5RouterPatientDto = baseService.patientRouter(currentUserType);
        } else {
            h5RouterPatientDto = baseService.patientRouter();
        }
        return R.success(h5RouterPatientDto);
    }

    @ApiOperation("用户端查询患者个人中心路由设置")
    @ApiImplicitParam(name = "currentUserType", value = "NURSING_STAFF, DOCTOR, 患者端不需要传")
    @GetMapping("anno/patientRouter/{code}")
    public R<H5RouterPatientDto> annoPatientRouter(@PathVariable(value = "code") String code,
                                                   @RequestParam(value = "currentUserType", required = false) String currentUserType) {
        BaseContextHandler.setTenant(code);
        return getPatientRouter(currentUserType);
    }

    @ApiOperation("患者基本信息完成后的菜单")
    @GetMapping("getPatientJoinGroupAfterMenu")
    public R<List<H5Router>> getPatientJoinGroupAfterMenu() {
        List<H5Router> routerList = baseService.getPatientJoinGroupAfterMenu();
        return R.success(routerList);
    }

    @ApiOperation("查询项目路由不存在，则创建")
    @GetMapping("/createIfNotExist/{code}")
    public R<List<H5Router>> createIfNotExist(@PathVariable("code") String code) {
        BaseContextHandler.setTenant(code);
        List<H5Router> routers = baseService.createIfNotExist();
        return R.success(routers);
    }

    @ApiOperation("查询项目路由不存在，则创建")
    @GetMapping("/createDoctorIfNotExist/{code}")
    public R<List<H5Router>> createDoctorIfNotExist(@PathVariable("code") String code) {
        BaseContextHandler.setTenant(code);
        List<H5Router> routers = baseService.createIfNotDoctorExist();
        return R.success(routers);
    }

    @ApiOperation("查询app的菜单路由不存在，则创建")
    @GetMapping("/createNursingIfNotExist/{code}")
    public R<List<H5Router>> createNursingIfNotExist(@PathVariable("code") String code) {
        BaseContextHandler.setTenant(code);
        List<H5Router> routers = baseService.createIfNotNursingExist();
        return R.success(routers);
    }


    @ApiOperation("匿名查询项目路由")
    @Deprecated
    @GetMapping("/anno/query/{code}")
    public R<List<H5Router>> anonQuery(@PathVariable("code") String code, @RequestParam(value = "userType", required = false) String userType) {
        BaseContextHandler.setTenant(code);
        if (StringUtils.isEmpty(userType)) {
            userType = UserType.PATIENT;
        }
        LbqWrapper<H5Router> wrapper = Wraps.<H5Router>lbQ(H5Router.builder().userType(userType).build()).orderByAsc(H5Router::getSortValue);
        // 查询患者菜单。
        if (userType.equals(UserType.PATIENT)) {
            String currentUserType = BaseContextHandler.getUserType();
            // 医助请求患者的菜单
            if (UserType.NURSING_STAFF.equals(currentUserType)) {
                wrapper.eq(H5Router::getPatientMenuNursingStatus, true);
            }
            if (UserType.DOCTOR.equals(currentUserType)) {
                wrapper.eq(H5Router::getPatientMenuDoctorStatus, true);
            }
        }
        wrapper.orderByAsc(H5Router::getSortValue);
        List<H5Router> routers = baseService.list(wrapper);
        return R.success(routers);
    }

    @ApiOperation(
            value = "根据项目code批量查询",
            notes = "根据项目code批量查询"
    )
    @Deprecated
    @PostMapping({"/query/{code}"})
    public R<List<H5Router>> queryByTenant(@PathVariable("code") String code, @RequestBody H5Router data) {
        BaseContextHandler.setTenant(code);
        if (StringUtils.isEmpty(data.getUserType())) {
            data.setUserType(UserType.PATIENT);
        }
        QueryWrap<H5Router> wrapper = Wraps.q(data);
        // 查询患者菜单。
        if (data.getUserType().equals(UserType.PATIENT)) {
            String currentUserType = BaseContextHandler.getUserType();
            // 医助请求患者的菜单
            if (UserType.NURSING_STAFF.equals(currentUserType)) {
                wrapper.eq("patient_menu_nursing_status", true);
            }
            if (UserType.DOCTOR.equals(currentUserType)) {
                wrapper.eq("patient_menu_doctor_status", true);
            }
        }
        return success(baseService.list(wrapper));
    }

    @ApiOperation("根据项目code修改")
    @PutMapping("/update/{code}")
    public R<H5Router> updateByTenant(@PathVariable("code") String code, @RequestBody @Validated H5RouterUpdateDTO updateDTO) {
        BaseContextHandler.setTenant(code);
        H5Router model = BeanUtil.toBean(updateDTO, H5Router.class);
        baseService.updateAllById(model);
        return success(model);
    }

    @ApiOperation("根据项目code修改顺序")
    @PutMapping("/updateSort/{code}")
    public R<Boolean> updateSortByTenant(@PathVariable("code") String code, @RequestBody @Validated H5RouterSortUpdateDTO h5RouterSortUpdateDTO) {
        BaseContextHandler.setTenant(code);
        H5Router lastRouter = H5Router.builder().id(h5RouterSortUpdateDTO.getLastRouterId()).sortValue(h5RouterSortUpdateDTO.getLastRouterSort()).build();
        H5Router nextRouter = H5Router.builder().id(h5RouterSortUpdateDTO.getNextRouterId()).sortValue(h5RouterSortUpdateDTO.getNextRouterSort()).build();
        baseService.updateById(lastRouter);
        baseService.updateById(nextRouter);
        return success();
    }

    @ApiOperation("根据项目code新增")
    @PostMapping("/save/{code}")
    public R<H5Router> saveByTenant(@PathVariable("code") String code, @RequestBody @Validated H5RouterSaveDTO saveDTO) {
        BaseContextHandler.setTenant(code);
        H5Router model = BeanUtil.toBean(saveDTO, H5Router.class);
        if (model.getPatientMenuDoctorStatus() == null) {
            model.setPatientMenuDoctorStatus(true);
        }
        if (model.getPatientMenuNursingStatus() == null) {
            model.setPatientMenuNursingStatus(true);
        }
        baseService.save(model);
        return success(model);
    }

    @ApiOperation("根据项目code删除")
    @DeleteMapping("/delete/{code}")
    @ApiImplicitParams({@ApiImplicitParam(
            name = "ids[]",
            value = "主键id",
            dataType = "array",
            paramType = "query"
    )})
    public R<Boolean> delete(@PathVariable("code") String code, @RequestParam("ids[]") List<Long> ids) {
        BaseContextHandler.setTenant(code);
        if (CollUtil.isEmpty(ids)) {
            return R.success();
        }
        boolean r = baseService.removeByIds(ids);
        return success(r);
    }


    /**
     * 根据path查询路径是否存在
     */
    @ApiOperation("统计一下path使用了多少次，主要验证护理计划被使用了多少次")
    @GetMapping("/countByPath")
    public R<Integer> countByPath(@RequestParam("path") String path) {
        int count = baseService.count(Wraps.<H5Router>lbQ().eq(H5Router::getPath, path));
        return R.success(count);
    }

    @ApiOperation("查询某一个菜单的信息")
    @GetMapping("getH5Router")
    public R<H5Router> getH5Router(@RequestParam("dictItemType") String dictItemType,  @RequestParam("userType") String userType) {
        H5Router h5Router = baseService.getOne(Wraps.<H5Router>lbQ().eq(H5Router::getUserType, userType).eq(H5Router::getDictItemType, dictItemType));
        return R.success(h5Router);
    }

}
