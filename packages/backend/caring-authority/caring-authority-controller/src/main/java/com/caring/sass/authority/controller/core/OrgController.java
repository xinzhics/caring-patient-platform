package com.caring.sass.authority.controller.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.authority.dto.core.OrgSaveDTO;
import com.caring.sass.authority.dto.core.OrgUpdateDTO;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperCacheController;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.log.annotation.SysLog;
import com.caring.sass.utils.BeanPlusUtil;
import com.caring.sass.utils.BizAssert;
import com.caring.sass.utils.TreeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.caring.sass.utils.StrPool.*;


/**
 * <p>
 * 前端控制器
 * 组织
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
@Slf4j
@RestController
@RequestMapping("/org")
@Api(value = "Org", tags = "组织")
//@PreAuth(replace = "org:")
public class OrgController extends SuperCacheController<OrgService, Long, Org, Org, OrgSaveDTO, OrgUpdateDTO> {


    @Override
    public R<Org> handlerSave(OrgSaveDTO model) {
        Org org = BeanPlusUtil.toBean(model, Org.class);
        fillOrg(org);
        this.baseService.save(org);
        return success(org);
    }

    @Override
    public R<Org> handlerUpdate(OrgUpdateDTO model) {
        Org org = BeanPlusUtil.toBean(model, Org.class);
        fillOrg(org);
        this.baseService.updateAllById(org);
        return success(org);
    }

    private Org fillOrg(Org org) {
        if (org.getParentId() == null || org.getParentId() <= 0) {
            org.setParentId(DEF_PARENT_ID);
            org.setTreePath(DEF_ROOT_PATH);
        } else {
            Org parent = this.baseService.getByIdCache(org.getParentId());
            BizAssert.notNull(parent, "父组织不能为空");

            org.setTreePath(StringUtils.join(parent.getTreePath(), parent.getId(), DEF_ROOT_PATH));
        }
        return org;
    }

    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        return this.success(baseService.remove(ids));
    }


    /**
     * 查询系统所有的组织树
     *
     * @param status 状态
     * @return
     * @author caring
     * @date 2019-07-29 11:59
     */
    @ApiOperation(value = "查询系统所有的组织树", notes = "查询系统所有的组织树")
    @GetMapping("/tree")
    @SysLog("查询系统所有的组织树")
    public R<List<Org>> tree(@RequestParam(value = "name", required = false) String name,
                             @RequestParam(value = "status", required = false) Boolean status) {
        List<Org> list = this.baseService.list(Wraps.<Org>lbQ()
                .like(Org::getLabel, name).eq(Org::getStatus, status).orderByAsc(Org::getSortValue));
        return this.success(TreeUtil.buildTree(list));
    }

    /**
     * 查询系统数据范围所有的组织树
     *
     * @param status 状态
     * @return
     * @author caring
     * @date 2019-07-29 11:59
     */
    @ApiOperation(value = "查询系统数据范围所有的组织树", notes = "查询系统数据范围所有的组织树")
    @GetMapping("/treeWithScope")
    @SysLog("查询系统数据范围所有的组织树")
    public R<List<Org>> treeWithScope(@RequestParam(value = "name", required = false) String name,
                             @RequestParam(value = "status", required = false) Boolean status) {
        List<Org> list = this.baseService.listWithScope(Wraps.<Org>lbQ()
                .like(Org::getLabel, name).eq(Org::getStatus, status).orderByAsc(Org::getSortValue));
        return this.success(TreeUtil.buildTree(list));
    }

    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<Org> userList = list.stream().map((map) -> {
            Org item = new Org();
            item.setDescribe(map.getOrDefault("描述", EMPTY));
            item.setLabel(map.getOrDefault("名称", EMPTY));
            item.setAbbreviation(map.getOrDefault("简称", EMPTY));
            item.setStatus(Convert.toBool(map.getOrDefault("状态", EMPTY)));
            return item;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(userList));
    }


    @GetMapping("queryByTenant")
    public R<Org> queryByTenant() {
        String tenant = BaseContextHandler.getTenant();
        if (StrUtil.isBlank(tenant)) {
            return R.fail("项目编码错误");
        }
        Org org = baseService.getOne(Wraps.<Org>lbQ());
        return R.success(org);
    }

    @ApiOperation("通过机构码获取机构")
    @GetMapping("getByCode/{code}")
    public R<Org> getByCode(@PathVariable("code") String code) {
        LbqWrapper<Org> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(Org::getCode, code);
        Org one = baseService.getOne(lbqWrapper);
        return R.success(one);
    }

}
