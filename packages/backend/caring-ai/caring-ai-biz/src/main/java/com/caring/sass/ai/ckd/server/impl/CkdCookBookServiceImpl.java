package com.caring.sass.ai.ckd.server.impl;


import com.caring.sass.ai.ckd.dao.CkdCookBookMapper;
import com.caring.sass.ai.ckd.server.CkdCookBookService;
import com.caring.sass.ai.ckd.server.CkdUserInfoService;
import com.caring.sass.ai.dto.ckd.CkdCookBookUpdateDTO;
import com.caring.sass.ai.entity.ckd.CkdCookBook;
import com.caring.sass.ai.entity.ckd.CkdCookbookPlan;
import com.caring.sass.ai.entity.ckd.CkdUserInfo;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Service

public class CkdCookBookServiceImpl extends SuperServiceImpl<CkdCookBookMapper, CkdCookBook> implements CkdCookBookService {

    @Override
    public CkdCookBook uploadImage(CkdCookBookUpdateDTO ckdCookBookUpdateDTO) {


        CkdCookBook ckdCookBook = baseMapper.selectById(ckdCookBookUpdateDTO.getId());
        if (ckdCookBook != null) {
            ckdCookBook.setFileUrl(ckdCookBookUpdateDTO.getFileUrl());
            baseMapper.updateById(ckdCookBook);
        } else {
            throw new BizException("上传图片失败");
        }

        return null;
    }
}
