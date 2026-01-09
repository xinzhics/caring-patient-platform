package com.caring.sass.tenant.controller.router;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class H5InitUtil {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String token = "";
    public static final String userFile = "D:\\tenant_code.xlsx";


    // 判断是 .xls 还是 .xlsx 格式
    private static Workbook getWorkbook(FileInputStream fis, String filePath) throws IOException {
        if (filePath.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (filePath.endsWith(".xls")) {
            return new HSSFWorkbook(fis);
        } else {
            throw new IllegalArgumentException("文件格式不支持！");
        }
    }
    public static void main(String[] args) {

        try (FileInputStream fis = new FileInputStream(new File(userFile));
             Workbook workbook = getWorkbook(fis, userFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            // 遍历每一行
            int i = 0;
            for (Row row : sheet) {
                // 遍历每一列
                if (i == 0) {
                    i++;
                    continue;
                }
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String cellValue = cell.getStringCellValue();
                    System.out.println(cellValue);
//                        saveNursing(cellValue);
//                        saveDoctor(cellValue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void saveNursing(String tenantCode) {
        // 目标API地址
        String apiUrl = "https://domain/api/tenant/h5Router/save/" + tenantCode;

        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", tenantCode);
        jsonObject.put("banDelete", false);
        jsonObject.put("dictItemId", 47);
        jsonObject.put("dictItemName", "科普患教");
        jsonObject.put("iconUrl", "https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/admin%2F2025%2F11%2Fcda62b6d-bf4f-44a1-a950-07be5e973f7f.png");
        jsonObject.put("name", "科普患教");
        jsonObject.put("path", "/studio/cms");
        jsonObject.put("sortValue", "10");
        jsonObject.put("userType", "NURSING_STAFF");
        jsonObject.put("status", true);
        jsonObject.put("dictItemType", "NURSING_DOCTOR_CMS");


        RequestBody body = RequestBody.create(JSON, jsonObject.toJSONString());

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Token", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (response.isSuccessful() && response.body() != null) {
                System.out.println("Response Code: " + response.code());
            } else {
                System.err.println("Request failed: " + response.code());
                if (response.body() != null) {
                    System.err.println("Error Body: " + responseBody);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static void saveDoctor(String tenantCode) {
        // 目标API地址
        String apiUrl = "https://domain/api/tenant/h5Router/save/" + tenantCode;

        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", tenantCode);
        jsonObject.put("banDelete", false);
        jsonObject.put("dictItemId", 46);
        jsonObject.put("dictItemName", "科普患教");
        jsonObject.put("iconUrl", "https://caing-test.obs.cn-north-4.myhuaweicloud.com:443/admin%2F2025%2F11%2Fcda62b6d-bf4f-44a1-a950-07be5e973f7f.png");
        jsonObject.put("name", "科普患教");
        jsonObject.put("path", "/studio/cms");
        jsonObject.put("sortValue", "15");
        jsonObject.put("userType", "DOCTOR");
        jsonObject.put("status", true);
        jsonObject.put("dictItemType", "DOCTOR_CMS");


        RequestBody body = RequestBody.create(JSON, jsonObject.toJSONString());

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Token", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (response.isSuccessful() && response.body() != null) {
                System.out.println("Response Code: " + response.code());
            } else {
                System.err.println("Request failed: " + response.code());
                if (response.body() != null) {
                    System.err.println("Error Body: " + responseBody);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
