package com.caring.sass.authority.controller.common;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.authority.dto.common.HospitalImport;
import com.caring.sass.authority.dto.common.HospitalPageDTO;
import com.caring.sass.authority.dto.common.HospitalSaveDTO;
import com.caring.sass.authority.dto.common.HospitalUpdateDTO;
import com.caring.sass.authority.entity.common.Hospital;
import com.caring.sass.authority.service.common.HospitalService;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * 字典项
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/hospital")
@Api(value = "Hospital", tags = "医院")
//@PreAuth(replace = "dict:")
public class HospitalController extends SuperController<HospitalService, Long, Hospital, HospitalPageDTO, HospitalSaveDTO, HospitalUpdateDTO> {

    @ApiOperation("自定义导入数据")
    @PostMapping("customize/importExcel")
    public void customizeImportExcel(MultipartFile simpleFile, HttpServletResponse response) {

        InputStream inputStream = null;
        try {
            inputStream = simpleFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            baseService.readExcel(workbook);
            String filename = "医院数据导入结果.xlsx";
            exportFile(response, workbook, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("自定义导出数据")
    @PostMapping("customize/exportExcel")
    public void exportExcel(@RequestBody HospitalPageDTO params,
                            HttpServletRequest request, HttpServletResponse response) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet("医院数据");
        workbook = baseService.exportExcel(params, workbook);
        String filename = "医院数据.xlsx";
        if (Objects.nonNull(workbook)) {
            exportFile(response, workbook, filename);
        }

    }

    @ApiOperation("导入各省医院")
    @PostMapping("customize/exportExcel1")
    public void exportExcel1(MultipartFile simpleFile,
                             HttpServletResponse response) {

        InputStream inputStream = null;
        try {
            inputStream = simpleFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            baseService.read1Excel(workbook);
            String filename = "医院数据导入结果.xlsx";
            exportFile(response, workbook, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @ApiOperation("导入西藏医院")
    @PostMapping("customize/exportExcel2")
    public List<HospitalImport> exportExcel2(MultipartFile simpleFile) {

        InputStream inputStream = null;
        try {
            inputStream = simpleFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            List<HospitalImport> hospitalImports = baseService.read2Excel(workbook);
            return hospitalImports;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    @ApiOperation("导入全国民营医院")
    @PostMapping("customize/exportExcel3")
    public List<HospitalImport> exportExcel3(MultipartFile simpleFile) {

        InputStream inputStream = null;
        try {
            inputStream = simpleFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            List<HospitalImport> hospitalImports = baseService.read3Excel(workbook);
            return hospitalImports;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    @ApiOperation("导入全国医院")
    @PostMapping("customize/exportExcel4")
    public List<HospitalImport> exportExcel4(MultipartFile simpleFile) {

        InputStream inputStream = null;
        try {
            inputStream = simpleFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            List<HospitalImport> hospitalImports = baseService.read4Excel(workbook);
            return hospitalImports;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }



    public static void exportFile(HttpServletResponse response, Workbook workbook, String filename) {
        response.setContentType("multipart/form-data");
        try {
            response.setHeader("Content-Disposition", "attachment;fileName="+ URLEncoder.encode(filename, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            OutputStream ouputStream = response.getOutputStream();
            workbook.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public R<IPage<Hospital>> page(PageParams<HospitalPageDTO> params) {
        HospitalPageDTO model = params.getModel();
        String userType = BaseContextHandler.getUserType();
        if (StrUtil.isNotEmpty(userType) && (UserType.GLOBAL_ADMIN.equals(userType) || UserType.THIRD_PARTY_CUSTOMERS.equals(userType))) {
            return super.page(params);
        } else {
            R<IPage<Hospital>> page = super.page(params);
            IPage<Hospital> pageData = page.getData();
            if (pageData.getPages() == 0) {
                pageData.setRecords(new ArrayList<>());
                Hospital hospital = new Hospital().setHospitalName(model.getHospitalName());
                hospital.setId(-1L);
                pageData.getRecords().add(hospital);
            } else if (pageData.getPages() == pageData.getCurrent()) {
                if (Objects.nonNull(model)) {
                    if (StrUtil.isNotEmpty(model.getHospitalName())) {
                        int count = baseService.count(Wraps.<Hospital>lbQ().eq(Hospital::getHospitalName, model.getHospitalName()));
                        if (count == 0) {
                            Hospital hospital = new Hospital().setHospitalName(model.getHospitalName());
                            hospital.setId(-1L);
                            pageData.getRecords().add(hospital);
                        }
                    }
                }
            }
            return page;
        }
    }

    @ApiOperation("无授权分页查询")
    @PostMapping("anno/page")
    public R<IPage<Hospital>> annoPage(@RequestBody PageParams<HospitalPageDTO> params) {
        return this.page(params);
    }


    @ApiOperation("获取医院基本信息")
    @GetMapping("getHospital/{id}")
    public R<Hospital> getHospital(@PathVariable(name = "id") Long id) {


        Hospital hospital = baseService.getHospital(id);
        return R.success(hospital);
    }

}
