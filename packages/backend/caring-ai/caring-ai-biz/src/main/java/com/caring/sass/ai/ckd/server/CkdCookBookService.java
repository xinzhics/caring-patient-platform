package com.caring.sass.ai.ckd.server;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.ckd.CkdCookBookUpdateDTO;
import com.caring.sass.ai.dto.ckd.CkdCookbookPlanPageDTO;
import com.caring.sass.ai.entity.ckd.CkdCookbookPlan;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.ai.entity.ckd.CkdCookBook;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
public interface CkdCookBookService extends SuperService<CkdCookBook> {



    CkdCookBook uploadImage(CkdCookBookUpdateDTO ckdCookBookUpdateDTO);

}
