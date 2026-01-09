package com.caring.sass.cms.controller;

import com.caring.sass.cms.entity.DoctorSubscribeKeyWord;
import com.caring.sass.cms.dto.DoctorSubscribeKeyWordSaveDTO;
import com.caring.sass.cms.dto.DoctorSubscribeKeyWordUpdateDTO;
import com.caring.sass.cms.dto.DoctorSubscribeKeyWordPageDTO;
import com.caring.sass.cms.service.DoctorSubscribeKeyWordService;
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
 * 医生订阅关键词表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/doctorSubscribeKeyWord")
@Api(value = "DoctorSubscribeKeyWord", tags = "医生订阅关键词表")
@PreAuth(replace = "doctorSubscribeKeyWord:")
public class DoctorSubscribeKeyWordController extends SuperController<DoctorSubscribeKeyWordService, Long, DoctorSubscribeKeyWord, DoctorSubscribeKeyWordPageDTO, DoctorSubscribeKeyWordSaveDTO, DoctorSubscribeKeyWordUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<DoctorSubscribeKeyWord> doctorSubscribeKeyWordList = list.stream().map((map) -> {
            DoctorSubscribeKeyWord doctorSubscribeKeyWord = DoctorSubscribeKeyWord.builder().build();
            //TODO 请在这里完成转换
            return doctorSubscribeKeyWord;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(doctorSubscribeKeyWordList));
    }
}
