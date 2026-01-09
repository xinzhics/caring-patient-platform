package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.ContentLibrary;

/**
 * @ClassName ContentLibraryServer
 * @Description
 * @Author yangShuai
 * @Date 2022/5/5 13:18
 * @Version 1.0
 */
public interface ContentLibraryService extends SuperService<ContentLibrary> {


    void copyContentLibrary(Long libraryId);


}
