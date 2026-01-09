package com.caring.sass.cms.controller;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.cms.dto.ContentLibraryPageDTO;
import com.caring.sass.cms.dto.ContentLibrarySaveDTO;
import com.caring.sass.cms.dto.ContentLibraryUpdateDTO;
import com.caring.sass.cms.entity.ContentLibrary;
import com.caring.sass.cms.service.ChannelContentService;
import com.caring.sass.cms.service.ContentLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@Validated
@RestController
@RequestMapping("/contentLibrary")
@Api(value = "ContentLibrary", tags = "内容库")
public class ContentLibraryController extends SuperController<ContentLibraryService, Long, ContentLibrary, ContentLibraryPageDTO, ContentLibrarySaveDTO, ContentLibraryUpdateDTO> {




    @PutMapping("copyContentLibrary/{libraryId}")
    @ApiOperation("复制内容库")
    public R copyContentLibrary(@PathVariable("libraryId") Long libraryId) {

        baseService.copyContentLibrary(libraryId);
        return R.success();

    }




}
