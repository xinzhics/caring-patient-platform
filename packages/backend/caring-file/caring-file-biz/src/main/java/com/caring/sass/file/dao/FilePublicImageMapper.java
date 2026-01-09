package com.caring.sass.file.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.file.entity.FilePublicImage;

import org.junit.experimental.categories.Categories;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 公共图片
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface FilePublicImageMapper extends SuperMapper<FilePublicImage> {

}
