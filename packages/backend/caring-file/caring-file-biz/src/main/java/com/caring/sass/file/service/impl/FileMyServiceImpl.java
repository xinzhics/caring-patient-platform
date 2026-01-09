package com.caring.sass.file.service.impl;


import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.file.dao.FileMyMapper;
import com.caring.sass.file.entity.FileMy;
import com.caring.sass.file.service.FileMyService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 我的图片
 * </p>
 *
 * @author 杨帅
 * @date 2022-08-29
 */
@Slf4j
@Service

public class FileMyServiceImpl extends SuperServiceImpl<FileMyMapper, FileMy> implements FileMyService {


    @Override
    public boolean save(FileMy model) {
        @NotNull(message = "文件id不能为空") Long fileId = model.getFileId();
        Long userId = BaseContextHandler.getUserId();
        LbqWrapper<FileMy> wrapper = Wraps.<FileMy>lbQ().eq(FileMy::getFileId, fileId).eq(SuperEntity::getCreateUser, userId).last(" limit 0,1 ");
        if (Objects.nonNull(userId)) {
            wrapper.eq(SuperEntity::getCreateUser, userId);
        }
        FileMy fileMy = baseMapper.selectOne(wrapper);
        if (fileMy != null) {
            model.setId(fileMy.getId());
            return super.updateById(model);
        } else {
            return super.save(model);
        }
    }
}
