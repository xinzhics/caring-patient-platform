package com.caring.sass.authority.service.common;

import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.base.service.SuperCacheService;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 业务接口
 * 字典项
 * </p>
 *
 * @author caring
 * @date 2019-07-02
 */
public interface DictionaryItemService extends SuperCacheService<DictionaryItem> {

    public static final String PATIENT = "patient";

    public static final String DOCTOR = "doctor";

    public static final String ASSISTANT = "assistant";

    /**
     * 根据类型查询字典
     *
     * @param types
     * @return
     */
    Map<String, Map<String, String>> map(String[] types);

    /**
     * 根据类型编码查询字典项
     * @param codes
     * @return
     */
    Map<Serializable, Object> findDictionaryItem(Set<Serializable> codes);

    /**
     * 反正字典的code 对应的名称
     * @param code
     * @return
     */
    String findDictionaryItemName(String code);


}
