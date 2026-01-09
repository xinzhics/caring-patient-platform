package com.caring.sass.authority.controller.core;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.dto.core.StationPageDTO;
import com.caring.sass.authority.dto.core.StationSaveDTO;
import com.caring.sass.authority.dto.core.StationUpdateDTO;
import com.caring.sass.authority.entity.core.Station;
import com.caring.sass.authority.service.core.StationService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperCacheController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.model.RemoteData;
import com.caring.sass.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * 岗位
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
@Slf4j
@RestController
@RequestMapping("/station")
@Api(value = "Station", tags = "岗位")
//@PreAuth(replace = "station:")
public class StationController extends SuperCacheController<StationService, Long, Station, StationPageDTO, StationSaveDTO, StationUpdateDTO> {

    @Override
    public void query(PageParams<StationPageDTO> params, IPage<Station> page, Long defSize) {
        baseService.findStationPage(page, params.getModel());
    }

    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<Station> stationList = list.stream().map((map) -> {
            Station item = new Station();
            item.setDescribe(map.getOrDefault("描述", ""));
            item.setName(map.getOrDefault("名称", ""));
            item.setOrg(new RemoteData<>(Convert.toLong(map.getOrDefault("组织", ""))));
            item.setStatus(Convert.toBool(map.getOrDefault("状态", "")));
            return item;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(stationList));
    }

}
