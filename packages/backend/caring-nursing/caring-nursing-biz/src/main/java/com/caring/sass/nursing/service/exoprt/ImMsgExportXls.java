package com.caring.sass.nursing.service.exoprt;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.FileUtils;
import com.caring.sass.msgs.entity.Chat;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 导出聊天记录表
 */
public class ImMsgExportXls extends ExportXls {

    // 文章颜色 46,95,215
    // 语音的颜色 54,160,44
    // 表头 188,206,251
    private XSSFWorkbook wordBook;

    private CellStyle titleCellStyle;

    private CellStyle voiceCellStyle;

    private CellStyle cmsCellStyle;

    private CellStyle centerAlignStyle;

    private CellStyle leftAlignStyle;

    private CellStyle withdrawMsgStyle;

    private XSSFDrawing patriarch;    // 顶级画布控制器

    private static final String AUDIO_HTML_URL = "http://caringsaas.com/saas/im/audio.html?videoUrl=";

    @Override
    public XSSFWorkbook getHSSWordBook(String sheetName) {
        wordBook = super.getHSSWordBook(sheetName);
        XSSFFont font2 = wordBook.createFont();
        font2.setBold(true);
        font2.setFontHeightInPoints((short)12);
        font2.setFontName("DengXian");
        // 表头样式
        titleCellStyle = wordBook.createCellStyle();
//        HSSFPalette palette = wordBook.getCustomPalette();
//        palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex(), (byte) 188, (byte) 206, (byte) 251);
        titleCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex()); // 设置颜色
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);    // 设置居中
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);    // 设置颜色填充方式
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);    // 设置居中
        titleCellStyle.setBorderTop(BorderStyle.THIN);  // 设置细线
        titleCellStyle.setBorderBottom(BorderStyle.THIN);
        titleCellStyle.setBorderLeft(BorderStyle.THIN);
        titleCellStyle.setBorderRight(BorderStyle.THIN);

        voiceCellStyle = wordBook.createCellStyle();
        XSSFFont voiceFont = wordBook.createFont();
//        HSSFPalette voicePalette = wordBook.getCustomPalette();
//        voicePalette.setColorAtIndex(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex(), (byte) 54, (byte) 160, (byte) 44);
        voiceFont.setColor(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
        voiceFont.setFontHeightInPoints((short)12);
        voiceCellStyle.setFont(voiceFont);
        voiceCellStyle.setBorderTop(BorderStyle.THIN);  // 设置细线
        voiceCellStyle.setBorderBottom(BorderStyle.THIN);
        voiceCellStyle.setBorderLeft(BorderStyle.THIN);
        voiceCellStyle.setBorderRight(BorderStyle.THIN);

        cmsCellStyle = wordBook.createCellStyle();
        XSSFFont cmsFont = wordBook.createFont();
//        HSSFPalette cmsPalette = wordBook.getCustomPalette();
//        cmsPalette.setColorAtIndex(HSSFColor.HSSFColorPredefined.BLUE.getIndex(), (byte) 46, (byte) 95, (byte) 215);
        cmsFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        cmsFont.setFontHeightInPoints((short)12);
        cmsCellStyle.setFont(cmsFont);
        cmsCellStyle.setBorderTop(BorderStyle.THIN);  // 设置细线
        cmsCellStyle.setBorderBottom(BorderStyle.THIN);
        cmsCellStyle.setBorderLeft(BorderStyle.THIN);
        cmsCellStyle.setBorderRight(BorderStyle.THIN);
        cmsCellStyle.setWrapText(true);


        centerAlignStyle = wordBook.createCellStyle();
        centerAlignStyle.setAlignment(HorizontalAlignment.CENTER);
        centerAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerAlignStyle.setAlignment(HorizontalAlignment.CENTER);
        centerAlignStyle.setWrapText(true);


        leftAlignStyle = wordBook.createCellStyle();
        leftAlignStyle.setAlignment(HorizontalAlignment.LEFT);
        leftAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        leftAlignStyle.setWrapText(true);

        XSSFFont withdrawFont = wordBook.createFont();
        withdrawFont.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        withdrawMsgStyle = wordBook.createCellStyle();
        withdrawMsgStyle.setAlignment(HorizontalAlignment.LEFT);
        withdrawMsgStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        withdrawMsgStyle.setWrapText(true);
        withdrawMsgStyle.setFont(withdrawFont);

        return wordBook;
    }


    @Override
    public void setSheetCellWidth(XSSFSheet sheet) {
        sheet.setColumnWidth(0, 256 * 10);
        sheet.setColumnWidth(1, 256 * 25);
        sheet.setColumnWidth(2, 256 * 16);
        sheet.setColumnWidth(3, 256 * 16);
        sheet.setColumnWidth(4, 256 * 16);
        sheet.setColumnWidth(5, 256 * 70);
    }

    /**
     *
     * @param width
     * @param height
     */
    public static int imageWidthHeight(int width, int height) {
        BigDecimal heightBigDe = new BigDecimal(height);
        BigDecimal widthBigDe = new BigDecimal(width);
        BigDecimal height300 = new BigDecimal(328);
        BigDecimal width600 = new BigDecimal(610);
        BigDecimal dx980 = new BigDecimal(993);
        int dx2 = 0;
//        System.out.println("原始的宽度" + width + "原始的高度" + height);
        if (height >= 328) {
            // 压缩后 图片应该的宽度
            BigDecimal multiply = widthBigDe.multiply(height300.divide(heightBigDe, 4, BigDecimal.ROUND_HALF_UP));
            BigDecimal decimal = dx980.multiply(multiply.divide(width600, 4, BigDecimal.ROUND_HALF_UP));
            if (width == height) {
//                System.out.println("宽度是" + multiply.intValue() + "高度是" + 328);
                dx2 = decimal.intValue() + 10;
//                System.out.println("x的第二左边是"+ dx2);
            } else if (width > height) {
//                BigDecimal decimal = dx980.multiply(multiply.divide(width600, 4, BigDecimal.ROUND_HALF_UP));
//                System.out.println("宽度是" + multiply.intValue() + "高度是" + 328);
                if (multiply.intValue() > 610) {
                    dx2 = 1013;
                } else {
                    dx2 = decimal.intValue() + 10;
                }
//                System.out.println("x的第二左边是"+ dx2);
            } else {
//                BigDecimal decimal = dx980.multiply(multiply.divide(width600, 4, BigDecimal.ROUND_HALF_UP));
//                System.out.println("宽度是" + multiply.intValue() + "高度是" + 328);
                dx2 = decimal.intValue() + 10;
//                System.out.println("x的第二左边是"+ dx2);
            }
        } else {
            if (width >= 610) {
                dx2 = 1013;
            } else {
                BigDecimal multiply = dx980.multiply(widthBigDe.divide(width600, 4, BigDecimal.ROUND_HALF_UP));
                dx2 = multiply.intValue() + 10;
//                System.out.println("宽度是" + multiply.intValue() + "高度是" + 328);
            }
        }
        return dx2;
    }





    /**
     * 画布控制器
     * @return
     */
    private XSSFDrawing getHSSFPatriarch() {
        XSSFSheet sheet = wordBook.getSheetAt(0);
        if (patriarch == null) {
            patriarch = sheet.createDrawingPatriarch();
        }
        return patriarch;
    }

    /**
     * 设置导出的聊天内容
     * @param row
     * @param numbering
     * @param rowIdx
     * @param chat
     * @param dictionaryItem
     */
    public void setValue(XSSFRow row, long numbering, int rowIdx, Chat chat, String cmsUrl, Map<String, String> dictionaryItem) {

        int cellIdx = 0;
        XSSFCell cell;
        CreationHelper helper;
        Hyperlink hyperlink;
        LocalDateTime createTime = chat.getCreateTime();
        XSSFDrawing patriarch = getHSSFPatriarch();
        cell = row.createCell(cellIdx++);
        cell.setCellValue(numbering);
        cell.setCellStyle(centerAlignStyle);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(createTime.toLocalDate().toString() + " " + createTime.toLocalTime().toString());
        cell.setCellStyle(centerAlignStyle);

        cell = row.createCell(cellIdx++);
        cell.setCellValue(chat.getSenderName());
        cell.setCellStyle(centerAlignStyle);


        String senderRoleType = chat.getSenderRoleType();
        cell = row.createCell(cellIdx++);
        if (UserType.UCENTER_DOCTOR.equals(senderRoleType)) {
            String doctor = dictionaryItem.get("doctor");
            cell.setCellValue(StrUtil.isEmpty(doctor) ? "医生" : doctor);
        }
        if (UserType.UCENTER_NURSING_STAFF.equals(senderRoleType)) {
            String assistant = dictionaryItem.get("assistant");
            cell.setCellValue(StrUtil.isEmpty(assistant) ? "医助" : assistant);
        }
        if (UserType.UCENTER_PATIENT.equals(senderRoleType)) {
            String patient = dictionaryItem.get("patient");
            cell.setCellValue(StrUtil.isEmpty(patient) ? "患者" : patient);
        }
        if (UserType.UCENTER_SYSTEM_IM.equals(senderRoleType)) {
            cell.setCellValue("AI");
        }
        cell.setCellStyle(centerAlignStyle);

        cell = row.createCell(cellIdx++);
        XSSFCell contentCell = row.createCell(cellIdx);
        cell.setCellStyle(centerAlignStyle);
        String content = chat.getContent();
        MediaType type = chat.getType();
        JSONObject jsonObject;
        if (chat.getWithdrawChatStatus() != null && chat.getWithdrawChatStatus() == 1) {
            switch (type) {
                case voice: {
                    cell.setCellValue("语音");
                    break;
                }
                case image: {
                    cell.setCellValue("图片");
                    break;
                }
                case text: {
                    cell.setCellValue("文本");
                    break;
                }
                case video: {
                    cell.setCellValue("视频");
                    break;
                }
                case file: {
                    cell.setCellValue("文件");
                    break;
                }
                case cms: {
                    cell.setCellValue("文章");
                    break;
                }
                default: cell.setCellValue("-");
            }
            contentCell.setCellValue("该消息已撤回");
            contentCell.setCellStyle(withdrawMsgStyle);
        } else {
            switch (type) {
                case voice: {
                    cell.setCellValue("语音");
                    jsonObject = JSON.parseObject(content);
                    helper = wordBook.getCreationHelper();
                    hyperlink = helper.createHyperlink(HyperlinkType.FILE);
                    hyperlink.setAddress(AUDIO_HTML_URL + jsonObject.getString("url"));
                    contentCell.setHyperlink(hyperlink);
                    contentCell.setCellValue("【点击播放】");
                    contentCell.setCellStyle(voiceCellStyle);
                    break;
                }
                case image: {
                    cell.setCellValue("图片");
                    row.setHeight((short) 4300);
                    try {
                        File article = FileUtils.downLoadArticleFromUrl(content, chat.getId() + ".png");
                        if (!article.exists()) {
                            contentCell.setCellValue("图片下载失败");
                        } else {
                            BufferedImage bi = null;
                            ByteArrayOutputStream out = null;
                            FileInputStream inputStream = new FileInputStream(article);
                            try {
                                bi = ImageIO.read(inputStream);
                                out = new ByteArrayOutputStream();
                                Thumbnails.of(bi).scale(0.5).outputFormat("png").toOutputStream(out);
                                int width = bi.getWidth();
                                int height = bi.getHeight();
                                int dx2 = imageWidthHeight(width, height);
                                XSSFClientAnchor anchor = new XSSFClientAnchor(15,10,dx2,245,(short) 5, ((short) (rowIdx)), (short) 5, ((short) (rowIdx)));
                                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
                                patriarch.createPicture(anchor, wordBook.addPicture(out.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG));
                            } finally {
                                inputStream.close();
                                if (out != null) {
                                    out.close();
                                }
                                if (article.exists()) {
                                    article.delete();
                                }
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        contentCell.setCellValue("图片写入失败");
                    }
                    break;
                }
                case text: {
                    cell.setCellValue("文本");
                    contentCell.setCellValue(content);
                    contentCell.setCellStyle(leftAlignStyle);
                    break;
                }
                case exitChat:
                case openChat: {
                    cell.setCellValue("提醒");
                    contentCell.setCellValue(content);
                    contentCell.setCellStyle(leftAlignStyle);
                    break;
                }
                case video: {
                    cell.setCellValue("视频");
                    helper = wordBook.getCreationHelper();
                    hyperlink = helper.createHyperlink(HyperlinkType.URL);
                    hyperlink.setAddress(content);
                    contentCell.setHyperlink(hyperlink);
                    contentCell.setCellValue("【点击播放】");
                    contentCell.setCellStyle(voiceCellStyle);
                    break;
                }
                case file: {
                    cell.setCellValue("文件");
                    helper = wordBook.getCreationHelper();
                    hyperlink = helper.createHyperlink(HyperlinkType.URL);
                    hyperlink.setAddress(content);
                    contentCell.setHyperlink(hyperlink);
                    contentCell.setCellValue("【点击下载】");
                    contentCell.setCellStyle(voiceCellStyle);
                    break;
                }
                case cms: {
                    cell.setCellValue("文章");
                    jsonObject = JSON.parseObject(content);
                    helper = wordBook.getCreationHelper();
                    hyperlink = helper.createHyperlink(HyperlinkType.URL);
                    hyperlink.setAddress(cmsUrl + "?id=" +jsonObject.get("id"));
                    contentCell.setHyperlink(hyperlink);
                    contentCell.setCellValue("【文章】 " + jsonObject.get("title"));
                    contentCell.setCellStyle(cmsCellStyle);
                    break;
                }
                default:
                    return;
            }
        }
    }

    /**
     * 设置表格第一行 第二行内容
     */
    public void setFieldName(String title1, String title2) {
        setRowTitle(wordBook, title1, 0, 6, true);
        setRowTitle(wordBook, title2, 1, 6, false);
        XSSFSheet sheet = wordBook.getSheetAt(0);
        XSSFRow row = createRow(sheet,2);
        int cellIdx = 0;
        XSSFCell cell;
        cell = row.createCell(cellIdx++);
        cell.setCellValue("编号");
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("时间");
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("姓名");
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(cellIdx++);
        cell.setCellValue("角色");
        cell.setCellStyle(titleCellStyle);


        cell = row.createCell(cellIdx++);
        cell.setCellValue("类型");
        cell.setCellStyle(titleCellStyle);

        cell = row.createCell(cellIdx);
        cell.setCellValue("内容");
        cell.setCellStyle(titleCellStyle);


    }

}
