package com.caring.sass.cms.controller;

import com.caring.sass.cms.entity.DoctorLastPull;
import com.caring.sass.cms.dto.DoctorLastPullSaveDTO;
import com.caring.sass.cms.dto.DoctorLastPullUpdateDTO;
import com.caring.sass.cms.dto.DoctorLastPullPageDTO;
import com.caring.sass.cms.service.DoctorLastPullService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 医生订阅最新文章推送表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/doctorLastPull")
@Api(value = "DoctorLastPull", tags = "医生订阅最新文章推送表")
@PreAuth(replace = "doctorLastPull:")
public class DoctorLastPullController extends SuperController<DoctorLastPullService, Long, DoctorLastPull, DoctorLastPullPageDTO, DoctorLastPullSaveDTO, DoctorLastPullUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<DoctorLastPull> doctorLastPullList = list.stream().map((map) -> {
            DoctorLastPull doctorLastPull = DoctorLastPull.builder().build();
            //TODO 请在这里完成转换
            return doctorLastPull;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(doctorLastPullList));
    }
}
