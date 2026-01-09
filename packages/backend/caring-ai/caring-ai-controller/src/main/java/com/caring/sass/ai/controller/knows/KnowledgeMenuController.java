package com.caring.sass.ai.controller.knows;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.know.KnowledgeMenuPageDTO;
import com.caring.sass.ai.dto.know.KnowledgeMenuSaveDTO;
import com.caring.sass.ai.dto.know.KnowledgeMenuUpdateDTO;
import com.caring.sass.ai.entity.know.KnowledgeMenu;
import com.caring.sass.ai.entity.know.KnowledgeMenuDomain;
import com.caring.sass.ai.entity.know.KnowledgeUser;
import com.caring.sass.ai.know.config.KnowChildDomainConfig;
import com.caring.sass.ai.know.service.KnowledgeMenuDomainService;
import com.caring.sass.ai.know.service.KnowledgeMenuService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.database.mybatis.conditions.Wraps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * 前端控制器
 * 知识库菜单
 * </p>
 *
 * @author 杨帅
 * @date 2025-07-21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeMenu")
@Api(value = "KnowledgeMenu", tags = "知识库菜单")
public class KnowledgeMenuController extends SuperController<KnowledgeMenuService, Long, KnowledgeMenu, KnowledgeMenuPageDTO, KnowledgeMenuSaveDTO, KnowledgeMenuUpdateDTO> {


    @Autowired
    KnowledgeMenuDomainService knowledgeMenuDomainService;

    @Autowired
    KnowChildDomainConfig knowChildDomainConfig;

    @ApiOperation("分页查询")
    @PostMapping("anno/page")
    public R<IPage<KnowledgeMenu>> annoPage(@RequestBody PageParams<KnowledgeMenuPageDTO> params) {
        return super.page(params);
    }

    /**
     * 获取前端用户发起请求时的 二级域名
     * @param request
     * @return
     */
    private String getRequestDomain(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        String tempDomain = "";
        String replace = referer.replace("https://", "");
        String[] split = replace.split("\\.");
        if (split.length < 3) {
            tempDomain = knowChildDomainConfig.getDocuknowMainDoamin();
        } else {
            tempDomain = split[0];
        }
        return tempDomain;
    }

    @ApiOperation("查询列表")
    @GetMapping("anno/list")
    public R<List<KnowledgeMenu>> annoList(
            HttpServletRequest request,
            @RequestParam String menuDomain) {

        if (StrUtil.isEmpty(menuDomain)) {
            menuDomain = getRequestDomain(request);
        }
        if (knowChildDomainConfig.getDocuknowMainDoamin().equals(menuDomain) || "cstudio".equals(menuDomain)) {
            KnowledgeMenuDomain knowledgeMenuDomain = knowledgeMenuDomainService.getOne(Wraps.<KnowledgeMenuDomain>lbQ()
                    .eq(KnowledgeMenuDomain::getMenuDomain, menuDomain));
            if (Objects.isNull(knowledgeMenuDomain)) {
                return R.success(new ArrayList<>());
            }
            Long domainId = knowledgeMenuDomain.getId();
            List<KnowledgeMenu> list = baseService.list(Wraps.<KnowledgeMenu>lbQ()
                    .orderByAsc(KnowledgeMenu::getSort)
                    .eq(KnowledgeMenu::getDomainId, domainId));

            return R.success(list);
        } else {
            KnowledgeMenuDomain knowledgeMenuDomain = knowledgeMenuDomainService.getOne(Wraps.<KnowledgeMenuDomain>lbQ()
                    .eq(KnowledgeMenuDomain::getMenuDomain, menuDomain));
            if (Objects.isNull(knowledgeMenuDomain)) {
                return R.success(new ArrayList<>());
            }
            Long domainId = knowledgeMenuDomain.getId();
            List<KnowledgeMenu> list = baseService.list(Wraps.<KnowledgeMenu>lbQ()
                    .orderByAsc(KnowledgeMenu::getSort)
                    .eq(KnowledgeMenu::getDomainId, domainId));
            return R.success(list);
        }

    }

    @ApiOperation("查询修改列表")
    @GetMapping("anno/updateList")
    public R<List<KnowledgeMenu>> annoUpdateList(
            HttpServletRequest request,
            @RequestParam String menuDomain) {

        if (StrUtil.isEmpty(menuDomain)) {
            menuDomain = getRequestDomain(request);
        }
        if (knowChildDomainConfig.getDocuknowMainDoamin().equals(menuDomain)) {
            KnowledgeMenuDomain knowledgeMenuDomain = knowledgeMenuDomainService.getOne(Wraps.<KnowledgeMenuDomain>lbQ()
                    .eq(KnowledgeMenuDomain::getMenuDomain, menuDomain));
            if (Objects.isNull(knowledgeMenuDomain)) {
                return R.success(new ArrayList<>());
            }
            Long domainId = knowledgeMenuDomain.getId();
            List<KnowledgeMenu> list = baseService.list(Wraps.<KnowledgeMenu>lbQ()
                    .orderByAsc(KnowledgeMenu::getSort)
                    .eq(KnowledgeMenu::getDomainId, domainId));

            return R.success(list);
        }
        KnowledgeMenuDomain knowledgeMenuDomain = knowledgeMenuDomainService.getOne(Wraps.<KnowledgeMenuDomain>lbQ()
                .eq(KnowledgeMenuDomain::getMenuDomain, menuDomain));
        if (Objects.isNull(knowledgeMenuDomain)) {
            return R.success(new ArrayList<>());
        }
        Long domainId = knowledgeMenuDomain.getId();
        List<KnowledgeMenu> list = baseService.list(Wraps.<KnowledgeMenu>lbQ()
                .orderByAsc(KnowledgeMenu::getSort)
                .eq(KnowledgeMenu::getDomainId, domainId));
        return R.success(list);
    }



    @ApiOperation("查询域名菜单列表")
    @GetMapping("anno/menuDomainList")
    public R<List<KnowledgeMenuDomain>> menuDomainList(@RequestParam(required = false) String menuDomain,
                                                    @RequestParam(required = false) String menuId) {

        if (StrUtil.isNotBlank(menuDomain)) {
            List<KnowledgeMenuDomain> list = new ArrayList<>();
            KnowledgeMenuDomain knowledgeMenuDomain = knowledgeMenuDomainService.getOne(Wraps.<KnowledgeMenuDomain>lbQ()
                    .eq(KnowledgeMenuDomain::getMenuDomain, menuDomain));
            list.add(knowledgeMenuDomain);
            return R.success(list);
        } else if (StrUtil.isNotBlank(menuId)) {
            List<KnowledgeMenuDomain> menuDomains = knowledgeMenuDomainService.list(Wraps.<KnowledgeMenuDomain>lbQ()
                    .eq(KnowledgeMenuDomain::getMenuId, menuId));
            return R.success(menuDomains);
        }
        return R.success(new ArrayList<>());
    }


    @PostMapping("anno/createDomain")
    @ApiOperation("创建子平台域名")
    public R<String> createDomain(@RequestBody KnowledgeMenuDomain domain) {

        String menuId = domain.getMenuId();
        KnowledgeMenuDomain menu = knowledgeMenuDomainService.getOne(Wraps.<KnowledgeMenuDomain>lbQ()
                .eq(KnowledgeMenuDomain::getMenuId, menuId));
        if (menu == null) {
            knowledgeMenuDomainService.save(domain);
            return R.success("创建成功，请修改nginx配置");
        } else {
            return R.fail(domain.getMenuId() +"域名已经存在");
        }

    }



}
