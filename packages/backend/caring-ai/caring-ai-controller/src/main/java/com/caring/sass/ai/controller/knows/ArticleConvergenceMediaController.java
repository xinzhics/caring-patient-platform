package com.caring.sass.ai.controller.knows;

import cn.hutool.core.util.StrUtil;
import com.caring.sass.ai.article.service.ArticleConvergenceMediaService;
import com.caring.sass.ai.dto.article.ArticleConvergenceMediaPageDTO;
import com.caring.sass.ai.dto.article.ArticleConvergenceMediaSaveDTO;
import com.caring.sass.ai.dto.article.ArticleConvergenceMediaUpdateDTO;
import com.caring.sass.ai.entity.article.ArticleConvergenceMedia;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;


/**
 * <p>
 * 前端控制器
 * 知识库融媒体
 * </p>
 *
 * @author 杨帅
 * @date 2025-09-12
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/articleConvergenceMedia")
@Api(value = "ArticleConvergenceMedia", tags = "知识库融媒体")
public class ArticleConvergenceMediaController extends SuperController<ArticleConvergenceMediaService, Long, ArticleConvergenceMedia, ArticleConvergenceMediaPageDTO, ArticleConvergenceMediaSaveDTO, ArticleConvergenceMediaUpdateDTO> {


    @ApiOperation(value = "上传文件", notes = "上传文件 ")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public R<Boolean> upload(@RequestParam(value = "file") MultipartFile simpleFile) {

        // 读取 上传的 excel 文件
        // 支持 .xls 和 .xlsx
        String fileName = simpleFile.getOriginalFilename();
        boolean isXlsx = fileName != null && fileName.toLowerCase().endsWith(".xlsx");

        try (Workbook workbook = isXlsx ?
                new XSSFWorkbook(simpleFile.getInputStream()) :
                new HSSFWorkbook(simpleFile.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0); // 默认读第一个sheet
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                return R.fail("Excel 文件为空");
            }

            // 假设第一行为列名（headers）
            Row headerRow = rowIterator.next();

            // 遍历数据行
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell cell1 = row.getCell(0); // 手机号
                if (cell1 == null || StrUtil.isEmpty(cell1.getStringCellValue())) {
                    break;
                }
                ArticleConvergenceMedia convergenceMedia = new ArticleConvergenceMedia();
                Cell cell2 = row.getCell(1); // 标题
                if (cell2 != null) {
                    convergenceMedia.setFileTitle(cell2.getStringCellValue());
                }
                Cell cell3 = row.getCell(2); // 标题
                if (cell3 != null) {
                    convergenceMedia.setFileUrl(cell3.getStringCellValue());
                }
                convergenceMedia.setUserMobile(cell1.getStringCellValue());
                baseService.save(convergenceMedia);
            }

            return R.success(); // 返回每列的数据

        } catch (IOException e) {
            e.printStackTrace();
            return R.fail("读取文件失败：" + e.getMessage());
        }


    }




}
