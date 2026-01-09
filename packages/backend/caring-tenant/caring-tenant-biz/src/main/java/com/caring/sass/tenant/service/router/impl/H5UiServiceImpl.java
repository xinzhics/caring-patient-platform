package com.caring.sass.tenant.service.router.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.tenant.dao.router.H5UiMapper;
import com.caring.sass.tenant.entity.router.H5Ui;
import com.caring.sass.tenant.entity.sys.DictItem;
import com.caring.sass.tenant.service.router.H5UiService;
import com.caring.sass.tenant.service.sys.DictItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * UI组件
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Slf4j
@Service

public class H5UiServiceImpl extends SuperServiceImpl<H5UiMapper, H5Ui> implements H5UiService {

    private final DictItemService dictItemService;

    public H5UiServiceImpl(DictItemService dictItemService) {
        this.dictItemService = dictItemService;
    }

    @Override
    public List<H5Ui> createIfNotExist() {
        List<H5Ui> h5Uis = super.list(Wraps.<H5Ui>lbQ().orderByAsc(H5Ui::getSortValue));
        if (CollUtil.isNotEmpty(h5Uis)) {
            return h5Uis;
        }
        return createDefaultUI();
    }

    private void fillAttr(H5Ui h5Ui, String attr) {
        String attr1 = "", attr2 = "";
        if (StrUtil.isNotBlank(attr)) {
            if (attr.contains(",")) {
                String[] array = attr.split(",");
                attr1 = array[0];
                attr2 = array[1];
            } else {
                attr1 = attr;
            }
        }
        h5Ui.setAttribute1(attr1);
        h5Ui.setAttribute2(attr2);
    }

    private List<H5Ui> createDefaultUI() {
        List<H5Ui> h5Uis = new ArrayList<>();
        List<DictItem> h5UiItem = dictItemService.list(Wraps.<DictItem>lbQ().eq(DictItem::getDictionaryType, "H5_UI"));
        List<DictItem> h5UiImageItem = dictItemService.list(Wraps.<DictItem>lbQ().eq(DictItem::getDictionaryType, "H5_UI_IMAGE"));
        /*code值为路径，name为类型名，describe为详情 */
        for (DictItem item : h5UiItem) {
            H5Ui h5Ui = H5Ui.builder().name(item.getName()).code(item.getCode()).type(1).build();
            fillAttr(h5Ui, item.getDescribe());
            h5Uis.add(h5Ui);
        }
        for (DictItem item : h5UiImageItem) {
            H5Ui h5Ui = H5Ui.builder().name(item.getName()).code(item.getCode()).type(2).build();
            fillAttr(h5Ui, item.getDescribe());
            h5Uis.add(h5Ui);
        }
        super.saveBatch(h5Uis);
        return h5Uis;
    }

    @Override
    public List<H5Ui> resetUI() {
        // 删除项目已有的ui配置
        super.remove(Wraps.lbQ());
        return createDefaultUI();
    }

    @Override
    public void copy(String fromTenantCode, String toTenantCode) {
        BaseContextHandler.setTenant(fromTenantCode);
        List<H5Ui> h5Uis = baseMapper.selectList(Wraps.<H5Ui>lbQ());
        if (CollUtil.isEmpty(h5Uis)) {
            return;
        }
        for (H5Ui h5Ui : h5Uis) {
            h5Ui.setId(null);
        }
        BaseContextHandler.setTenant(toTenantCode);
        baseMapper.insertBatchSomeColumn(h5Uis);
        BaseContextHandler.setTenant(fromTenantCode);

    }
}
