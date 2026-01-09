package com.caring.sass.authority.service.common.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.authority.dao.common.CityMapper;
import com.caring.sass.authority.dao.common.HospitalMapper;
import com.caring.sass.authority.dao.common.ProvinceMapper;
import com.caring.sass.authority.dto.common.HospitalImport;
import com.caring.sass.authority.dto.common.HospitalPageDTO;
import com.caring.sass.authority.entity.common.City;
import com.caring.sass.authority.entity.common.Hospital;
import com.caring.sass.authority.entity.common.Province;
import com.caring.sass.authority.service.common.HospitalService;
import com.caring.sass.authority.utils.HospitalExport;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 字典类型
 * </p>
 *
 * @author caring
 * @date 2019-07-02
 */
@Slf4j
@Service

public class HospitalServiceImpl extends SuperServiceImpl<HospitalMapper, Hospital> implements HospitalService {

    @Autowired
    ProvinceMapper provinceMapper;

    @Autowired
    CityMapper cityMapper;

    /**
     * 医院名称
     * @param hospitalName
     * @return
     */
    private boolean existHospital(String hospitalName) {

        Integer count = baseMapper.selectCount(Wraps.<Hospital>lbQ().eq(Hospital::getHospitalName, hospitalName));
        if (count != null && count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public <E extends IPage<Hospital>> E page(E page, Wrapper<Hospital> queryWrapper) {
        E selectPage = baseMapper.selectPage(page, queryWrapper);
        List<Hospital> pageRecords = selectPage.getRecords();
        if (CollUtil.isNotEmpty(pageRecords)) {
            Set<Long> provinceIdSet = pageRecords.stream()
                    .filter(item -> Objects.nonNull(item.getProvinceId()))
                    .map(Hospital::getProvinceId).collect(Collectors.toSet());

            Set<Long> cityIdSet = pageRecords.stream()
                    .filter(item -> Objects.nonNull(item.getCityId()))
                    .map(Hospital::getCityId).collect(Collectors.toSet());
            Map<Long, String> provinceMap = null;
            Map<Long, String> cityMap = null;
            if (CollUtil.isNotEmpty(provinceIdSet)) {
                List<Province> provinces = provinceMapper.selectList(Wraps.<Province>lbQ()
                        .in(SuperEntity::getId, provinceIdSet).select(SuperEntity::getId, Province::getProvince));
                if (CollUtil.isNotEmpty(provinces)) {
                    provinceMap = provinces.stream().collect(Collectors.toMap(SuperEntity::getId, Province::getProvince, (o1, o2) -> o2));
                }
            }

            if (CollUtil.isNotEmpty(cityIdSet)) {
                List<City> cityList = cityMapper.selectList(Wraps.<City>lbQ()
                        .in(SuperEntity::getId, cityIdSet).select(SuperEntity::getId, City::getCity));
                if (CollUtil.isNotEmpty(cityList)) {
                    cityMap = cityList.stream().collect(Collectors.toMap(SuperEntity::getId, City::getCity, (o1, o2) -> o2));
                }
            }
            final Map<Long, String> provinceMapFinal = provinceMap;
            final Map<Long, String> cityMapFinal = cityMap;
            pageRecords.forEach(hospital -> {
                if (provinceMapFinal != null) {
                    hospital.setProvinceName(provinceMapFinal.get( hospital.getProvinceId()));
                }
                if (cityMapFinal != null) {
                    hospital.setCityName(cityMapFinal.get( hospital.getCityId()));
                }
            });
        }
        return selectPage;
    }

    private Map<String, Long> provinceList() {
        List<Province> provinceList = provinceMapper.selectList(Wraps.<Province>lbQ().select(SuperEntity::getId, Province::getProvince));
        return provinceList.stream().collect(Collectors.toMap(Province::getProvince, SuperEntity::getId, (o1, o2) -> o2));
    }

    private Map<Long, Map<String, Long>> cityList () {
        List<City> cityList = cityMapper.selectList(Wraps.<City>lbQ().select(SuperEntity::getId, City::getCity, City::getProvinceId));
        Map<Long, Map<String, Long>> map = new HashMap<>(cityList.size());
        Map<String, Long> cityMap;
        for (City city : cityList) {
            cityMap = map.get(city.getProvinceId());
            if (cityMap == null) {
                cityMap = new HashMap<>(20);
            }
            cityMap.put(city.getCity(), city.getId());
            map.put(city.getProvinceId(), cityMap);
        }
        return map;
    }

    private Map<Long, String> provinceMap() {
        List<Province> provinceList = provinceMapper.selectList(Wraps.<Province>lbQ().select(SuperEntity::getId, Province::getProvince));
        return provinceList.stream().collect(Collectors.toMap(SuperEntity::getId, Province::getProvince, (o1, o2) -> o2));
    }

    private Map<Long, String> cityMap () {
        List<City> cityList = cityMapper.selectList(Wraps.<City>lbQ().select(SuperEntity::getId, City::getCity, City::getProvinceId));
        return cityList.stream().collect(Collectors.toMap(SuperEntity::getId, City::getCity, (o1, o2) -> o2));
    }


    @Override
    public Hospital getHospital(Long id) {
        Hospital hospital = baseMapper.selectOne(Wraps.<Hospital>lbQ().eq(SuperEntity::getId, id)
                .select(SuperEntity::getId, Hospital::getHospitalName, Hospital::getCityId, Hospital::getProvinceId));
        if (hospital == null) {
            return null;
        }
        Long cityId = hospital.getCityId();
        if (cityId != null) {
            City city = cityMapper.selectById(cityId);
            if (Objects.nonNull(city)) {
                hospital.setProvinceName(city.getProvinceName());
                hospital.setCityName(city.getCity());
            }
        }
        return hospital;
    }

    /**
     * 导入 医院数据
     * @param hospitalImports
     * @return
     */
    @Override
    public List<HospitalImport> importHospital(List<HospitalImport> hospitalImports) {
        if (CollUtil.isNotEmpty(hospitalImports)) {
            List<HospitalImport> errorHospital = new ArrayList<>(hospitalImports.size() / 2);
            List<HospitalImport> importHospital = new ArrayList<>(hospitalImports.size());
            hospitalImports.forEach(item -> {
                if (existHospital(item.getHospitalName())) {
                    item.setError(true);
                    item.setErrorMessage("医院已存在");
                    errorHospital.add(item);
                } else {
                    importHospital.add(item);
                }
            });
            if (CollUtil.isNotEmpty(importHospital)) {
                Map<String, Long> provinceMap = provinceList();
                Map<Long, Map<String, Long>> cityList = cityList();
                List<Hospital> hospitals = new ArrayList<>(importHospital.size());
                Hospital hospital;
                Long provinceId;
                String cityName;
                String provinceName;
                Map<String, Long> cityMap;
                Long cityId;
                for (HospitalImport hospitalImport : importHospital) {
                    hospital = Hospital.builder()
                            .address(hospitalImport.getAddress())
                            .hospitalName(hospitalImport.getHospitalName())
                            .hospitalLevel(hospitalImport.getLevel())
                            .build();
                    provinceName = hospitalImport.getProvinceName();
                    if (StrUtil.isNotEmpty(provinceName)) {
                        provinceId = provinceMap.get(provinceName);
                        if (provinceId == null) {
                            Province province = provinceMapper.selectOne(Wraps.<Province>lbQ().likeRight(Province::getProvince, provinceName).last(" limit 0,1 "));
                            if (province != null) {
                                provinceId = province.getId();
                                provinceMap.put(provinceName, provinceId);
                            }
                        }
                        cityName = hospitalImport.getCityName();
                        if (provinceId != null) {
                            hospital.setProvinceId(provinceId);
                            cityMap = cityList.get(provinceId);
                            if (cityMap != null) {
                                cityId = cityMap.get(cityName);
                                if (cityId == null) {
                                    City city = cityMapper.selectOne(Wraps.<City>lbQ()
                                            .eq(City::getProvinceId, provinceId)
                                            .likeRight(City::getCity, cityName)
                                            .last(" limit 0, 1"));
                                    if (city != null) {
                                        cityId = city.getId();
                                        cityMap.put(cityName, cityId);
                                    }
                                }
                                hospital.setCityId(cityId);
                            }
                        }
                    }
                    hospitals.add(hospital);
                }
                if (CollUtil.isNotEmpty(hospitals)) {
                    baseMapper.insertBatchSomeColumn(hospitals);
                }
            }
            return errorHospital;
        }

        return new ArrayList<>();
    }


    @Override
    public XSSFWorkbook exportExcel(HospitalPageDTO params, XSSFWorkbook workbook) {
        int current = 1, size = 300;
        IPage<Hospital> page;

        String name = BaseContextHandler.getName();
        LbqWrapper<Hospital> wrapper = Wraps.<Hospital>lbQ();

        if (Objects.nonNull(params.getProvinceId())) {
            wrapper.eq(Hospital::getProvinceId, params.getProvinceId());
        }

        if (Objects.nonNull(params.getCityId())) {
            wrapper.eq(Hospital::getCityId, params.getCityId());
        }

        if (StringUtils.isNotEmptyString(params.getHospitalLevel())) {
            wrapper.like(Hospital::getHospitalLevel, params.getHospitalLevel());
        }

        if (StringUtils.isNotEmptyString(params.getHospitalName())) {
            wrapper.like(Hospital::getHospitalName, params.getHospitalName());
        }
        wrapper.select(Hospital::getHospitalName, Hospital::getProvinceId, Hospital::getCityId, Hospital::getAddress, Hospital::getHospitalLevel);

        XSSFSheet sheet = workbook.getSheetAt(0);
        HospitalExport.setTitle(sheet, name);
        Map<Long, String> provinceMap = provinceMap();
        Map<Long, String> cityMap = cityMap();
        HospitalExport.setProvinceMap(provinceMap);
        HospitalExport.setCityMap(cityMap);
        int index = 1;
        while (true) {
            page = new Page(current, size);
            IPage<Hospital> selectPage = baseMapper.selectPage(page, wrapper);
            List<Hospital> records = selectPage.getRecords();
            index = HospitalExport.setValues(sheet, records, index);
            long pages = selectPage.getPages();
            current++;
            if (pages == 0) {
                break;
            }
            if (current > pages) {
                break;
            }
        }
        HospitalExport.clean();
        return workbook;
    }


    @Override
    public List<HospitalImport> read4Excel(XSSFWorkbook workbook) {


        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum >= 1) {
            XSSFRow row;
            List<HospitalImport> hospitalImports = new ArrayList<>(lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                // 医院名称
                XSSFCell hospitalName = row.getCell(1);
                if (hospitalName == null || hospitalName.getStringCellValue() == null) {
                    continue;
                }
                // 省名称
                XSSFCell province = row.getCell(0);
                String provinceName = province != null ? province.getStringCellValue() : "";
                // 级别
                XSSFCell address = row.getCell(2);
                hospitalImports.add(HospitalImport.builder()
                        .hospitalName(hospitalName.getStringCellValue())
                        .row(i)
                        .provinceName(provinceName)
                        .address(address != null ? address.getStringCellValue() : null)
                        .build());
            }
            List<HospitalImport> error = importHospital(hospitalImports);
            return error;
        }


        return Collections.emptyList();
    }

    @Override
    public List<HospitalImport> read3Excel(XSSFWorkbook workbook) {


        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum >= 1) {
            XSSFRow row;
            List<HospitalImport> hospitalImports = new ArrayList<>(lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                // 医院名称
                XSSFCell hospitalName = row.getCell(4);
                if (hospitalName == null || hospitalName.getStringCellValue() == null) {
                    continue;
                }
                // 省名称
                XSSFCell province = row.getCell(0);
                XSSFCell city = row.getCell(1);
                String provinceName = province != null ? province.getStringCellValue() : "";
                String cityName = city != null ? city.getStringCellValue() : "";
                // 级别
                XSSFCell address = row.getCell(9);
                hospitalImports.add(HospitalImport.builder()
                        .hospitalName(hospitalName.getStringCellValue())
                        .row(i)
                        .provinceName(provinceName)
                        .cityName(cityName)
                        .address(address != null ? address.getStringCellValue() : null)
                        .build());
            }
            List<HospitalImport> error = importHospital(hospitalImports);
            return error;
        }



        return Collections.emptyList();
    }

    @Override
    public List<HospitalImport> read2Excel(XSSFWorkbook workbook) {


        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum >= 1) {
            XSSFRow row;
            List<HospitalImport> hospitalImports = new ArrayList<>(lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                // 医院名称
                XSSFCell hospitalName = row.getCell(16);
                if (hospitalName == null || hospitalName.getStringCellValue() == null) {
                    continue;
                }
                // 省名称
                XSSFCell province = row.getCell(0);
                XSSFCell city = row.getCell(1);
                String provinceName = province != null ? province.getStringCellValue() : "";
                String cityName = city != null ? city.getStringCellValue() : "";
                // 级别
                XSSFCell hospitalLevel = row.getCell(3);
                hospitalImports.add(HospitalImport.builder()
                        .hospitalName(hospitalName.getStringCellValue())
                        .row(i)
                        .provinceName(provinceName)
                        .cityName(cityName)
                        .level(hospitalLevel != null ? hospitalLevel.getStringCellValue() : "")
                        .build());
            }
            List<HospitalImport> error = importHospital(hospitalImports);
            return error;
        }
        return null;


    }

    @Override
    public void read1Excel(XSSFWorkbook workbook) {

        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum >= 1) {
            XSSFRow row;
            List<HospitalImport> hospitalImports = new ArrayList<>(lastRowNum);
            for (int i = 0; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                // 医院名称
                XSSFCell hospitalName = row.getCell(0);
                if (hospitalName == null || hospitalName.getStringCellValue() == null) {
                    continue;
                }
                // 省名称
                XSSFCell province = row.getCell(1);
                String cellValue = province.getStringCellValue();
                String[] split = cellValue.split("-");
                String provinceName = split[0];
                String cityName = split[1];
                // 级别
                XSSFCell hospitalLevel = row.getCell(2);
                hospitalImports.add(HospitalImport.builder()
                        .hospitalName(hospitalName.getStringCellValue())
                        .row(i)
                        .provinceName(provinceName)
                        .cityName(cityName)
                        .level(hospitalLevel != null ? hospitalLevel.getStringCellValue() : "")
                        .build());
            }
            List<HospitalImport> error = importHospital(hospitalImports);
            if (CollUtil.isNotEmpty(error)) {
                for (HospitalImport hospitalImport : error) {
                    int rowIndex = hospitalImport.getRow();
                    row = sheet.getRow(rowIndex);
                    if (row != null) {
                        XSSFCell cell = row.createCell(5);
                        cell.setCellValue(hospitalImport.getErrorMessage());
                    }
                }
            }
        }



    }

    @Override
    public void readExcel(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum >= 1) {
            XSSFRow row;
            List<HospitalImport> hospitalImports = new ArrayList<>(lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                // 医院名称
                XSSFCell hospitalName = row.getCell(0);
                if (hospitalName == null || hospitalName.getStringCellValue() == null) {
                    continue;
                }
                // 省名称
                XSSFCell provinceName = row.getCell(1);
                // 市名称
                XSSFCell cityName = row.getCell(2);
                // 详细地址
                XSSFCell address = row.getCell(3);
                // 级别
                XSSFCell hospitalLevel = row.getCell(4);
                hospitalImports.add(HospitalImport.builder()
                        .hospitalName(hospitalName.getStringCellValue())
                        .row(i)
                        .provinceName(provinceName.getStringCellValue())
                        .cityName(cityName.getStringCellValue())
                        .address(address.getStringCellValue())
                        .level(hospitalLevel.getStringCellValue())
                        .build());
            }
            List<HospitalImport> error = importHospital(hospitalImports);
            if (CollUtil.isNotEmpty(error)) {
                for (HospitalImport hospitalImport : error) {
                    int rowIndex = hospitalImport.getRow();
                    row = sheet.getRow(rowIndex);
                    if (row != null) {
                        XSSFCell cell = row.createCell(5);
                        cell.setCellValue(hospitalImport.getErrorMessage());
                    }
                }
            }
        }
    }
}
