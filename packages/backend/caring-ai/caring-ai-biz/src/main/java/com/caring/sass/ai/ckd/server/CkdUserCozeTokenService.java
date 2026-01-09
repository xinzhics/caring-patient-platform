package com.caring.sass.ai.ckd.server;

import com.caring.sass.ai.dto.ckd.CkdUserCozeTokenSaveDTO;
import com.caring.sass.ai.entity.ckd.CkdUserCozeToken;
import com.caring.sass.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * CKD用户coze的token
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-17
 */
public interface CkdUserCozeTokenService extends SuperService<CkdUserCozeToken> {


    CkdUserCozeToken queryOrCreateToken(CkdUserCozeTokenSaveDTO saveDTO);

}
