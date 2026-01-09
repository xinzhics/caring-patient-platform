package com.caring.sass.authority.service.common;

import com.caring.sass.authority.dto.common.HospitalImport;
import com.caring.sass.authority.dto.common.HospitalPageDTO;
import com.caring.sass.authority.entity.common.Hospital;
import com.caring.sass.base.service.SuperService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

/**
 * 医院
 */
public interface HospitalService extends SuperService<Hospital> {


    /**
     * 导入医院数据
     * @param hospitalImports
     * @return
     */
    List<HospitalImport> importHospital(List<HospitalImport> hospitalImports);


    /**
     * 设置导出的医院数据
     * @param params
     * @param workbook
     * @return
     */
    XSSFWorkbook exportExcel(HospitalPageDTO params, XSSFWorkbook workbook);

    /**
     * 读取表格中的数据
     * @param workbook
     * @return
     */
    void readExcel(XSSFWorkbook workbook);

    /**
     * 读取省医院
     * @param workbook
     * @return
     */
    void read1Excel(XSSFWorkbook workbook);


    List<HospitalImport> read2Excel(XSSFWorkbook workbook);


    List<HospitalImport> read3Excel(XSSFWorkbook workbook);


    List<HospitalImport> read4Excel(XSSFWorkbook workbook);


    Hospital getHospital(Long id);
}
