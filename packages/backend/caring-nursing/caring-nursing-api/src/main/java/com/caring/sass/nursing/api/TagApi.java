package com.caring.sass.nursing.api;

import com.caring.sass.base.R;
import com.caring.sass.base.api.SuperApi;
import com.caring.sass.nursing.api.hystrix.TagApiFallback;
import com.caring.sass.nursing.dto.tag.TagPageDTO;
import com.caring.sass.nursing.dto.tag.TagSaveDTO;
import com.caring.sass.nursing.dto.tag.TagUpdateDTO;
import com.caring.sass.nursing.dto.tag.TagUpsertDTO;
import com.caring.sass.nursing.entity.tag.Tag;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author leizhi
 */
@Component
@FeignClient(name = "${caring.feign.nursing-server:caring-nursing-server}", fallback = TagApiFallback.class
        , path = "/tag", qualifier = "TagApi")
public interface TagApi extends SuperApi<Long, Tag, TagPageDTO, TagSaveDTO, TagUpdateDTO> {

    /**
     * 插入或更新标签
     *
     * @param tagUpsertDTO 插入或修改参数
     * @return true成功or失败false
     */
    @PostMapping("upsert")
    R<Boolean> upsert(@RequestBody TagUpsertDTO tagUpsertDTO);

    /**
     * 查询项目标签及标签属性
     */
    @GetMapping("listTagWithAttr")
    R<List<TagUpsertDTO>> listTagWithAttr();

    /**
     * 获取标签及属性
     */
    @GetMapping("getTagWithAttr")
    R<TagUpsertDTO> getTagWithAttr(@RequestParam(value = "id") Long id);

    /**
     * 复制标签
     *
     * @param fromTenantCode 待复制的项目编码
     * @param toTenantCode   目标项目编码
     */
    @PostMapping("copyTag")
    R<Boolean> copyTag(@RequestParam("fromTenantCode") String fromTenantCode,
                       @RequestParam("toTenantCode") String toTenantCode);
}
