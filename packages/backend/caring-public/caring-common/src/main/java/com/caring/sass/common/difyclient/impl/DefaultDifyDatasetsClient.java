package com.caring.sass.common.difyclient.impl;

import com.caring.sass.common.difyclient.DifyDatasetsClient;
import com.caring.sass.common.difyclient.exception.DifyApiException;
import com.caring.sass.common.difyclient.model.common.SimpleResponse;
import com.caring.sass.common.difyclient.model.datasets.*;
import com.caring.sass.common.difyclient.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Dify 知识库客户端默认实现
 * 提供知识库相关的操作
 */
@Slf4j
public class DefaultDifyDatasetsClient extends AbstractDifyClient implements DifyDatasetsClient {
    // API 路径常量
    private static final String DATASETS_PATH = "/datasets";
    private static final String DOCUMENTS_PATH = "/documents";
    private static final String SEGMENTS_PATH = "/segments";
    private static final String DOCUMENT_CREATE_BY_TEXT_PATH = "/document/create-by-text";
    private static final String DOCUMENT_CREATE_BY_FILE_PATH = "/document/create-by-file";
    private static final String UPDATE_BY_TEXT_PATH = "/update-by-text";
    private static final String UPDATE_BY_FILE_PATH = "/update-by-file";
    private static final String INDEXING_STATUS_PATH = "/indexing-status";
    private static final String UPLOAD_FILE_PATH = "/upload-file";
    private static final String RETRIEVE_PATH = "/retrieve";
    private static final String METADATA_PATH = "/metadata";

    /**
     * 构造函数
     *
     * @param baseUrl API基础URL
     * @param apiKey  API密钥
     */
    public DefaultDifyDatasetsClient(String baseUrl, String apiKey) {
        super(baseUrl, apiKey);
    }

    /**
     * 构造函数
     *
     * @param baseUrl    API基础URL
     * @param apiKey     API密钥
     * @param httpClient HTTP客户端
     */
    public DefaultDifyDatasetsClient(String baseUrl, String apiKey, OkHttpClient httpClient) {
        super(baseUrl, apiKey, httpClient);
    }

    @Override
    public DatasetResponse createDataset(CreateDatasetRequest request) throws IOException, DifyApiException {
        return executePost(DATASETS_PATH, request, DatasetResponse.class);
    }

    @Override
    public DatasetListResponse getDatasets(Integer page, Integer limit) throws IOException, DifyApiException {
        Map<String, Object> queryParams = new HashMap<>();
        addIfNotNull(queryParams, "page", page);
        addIfNotNull(queryParams, "limit", limit);

        String url = buildUrlWithParams(DATASETS_PATH, queryParams);
        return executeGet(url, DatasetListResponse.class);
    }

    @Override
    public SimpleResponse deleteDataset(String datasetId) throws IOException, DifyApiException {
        String path = DATASETS_PATH + "/" + datasetId;
        Request httpRequest = createDeleteRequest(path, null);

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (response.code() == 204) {
                SimpleResponse simpleResponse = new SimpleResponse();
                simpleResponse.setResult("success");
                return simpleResponse;
            }
            return handleResponse(response, SimpleResponse.class);
        }
    }

    @Override
    public DocumentResponse createDocumentByText(String datasetId, CreateDocumentByTextRequest request) throws IOException, DifyApiException {
        String path = DATASETS_PATH + "/" + datasetId + DOCUMENT_CREATE_BY_TEXT_PATH;
        return executePost(path, request, DocumentResponse.class);
    }

    @Override
    public DocumentResponse createDocumentByFile(String datasetId, CreateDocumentByFileRequest request, File file) throws IOException, DifyApiException {
        String path = DATASETS_PATH + "/" + datasetId + DOCUMENT_CREATE_BY_FILE_PATH;

        // 构建multipart请求
        MultipartBody.Builder multipartBuilder = createMultipartBuilder(request, file);
        return executeMultipartRequest(path, multipartBuilder.build(), DocumentResponse.class);
    }

    @Override
    public DocumentResponse createDocumentByFile(String datasetId, CreateDocumentByFileRequest request, InputStream inputStream, String fileName) throws IOException, DifyApiException {
        String path = DATASETS_PATH + "/" + datasetId + DOCUMENT_CREATE_BY_FILE_PATH;

        // 读取输入流内容
        byte[] bytes = readAllBytes(inputStream);

        // 构建multipart请求
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", JsonUtils.toJson(request))
                .addFormDataPart("file", fileName, RequestBody.create(bytes, OCTET_STREAM));

        return executeMultipartRequest(path, multipartBuilder.build(), DocumentResponse.class);
    }

    @Override
    public DocumentResponse updateDocumentByText(String datasetId, String documentId, UpdateDocumentByTextRequest request) throws IOException, DifyApiException {
        String path = buildDocumentPath(datasetId, documentId) + UPDATE_BY_TEXT_PATH;
        return executePost(path, request, DocumentResponse.class);
    }

    @Override
    public DocumentResponse updateDocumentByFile(String datasetId, String documentId, UpdateDocumentByFileRequest request, File file) throws IOException, DifyApiException {
        String path = buildDocumentPath(datasetId, documentId) + UPDATE_BY_FILE_PATH;

        // 构建multipart请求
        MultipartBody.Builder multipartBuilder = createMultipartBuilder(request, file);
        return executeMultipartRequest(path, multipartBuilder.build(), DocumentResponse.class);
    }

    @Override
    public IndexingStatusResponse getIndexingStatus(String datasetId, String batch) throws IOException, DifyApiException {
        String path = DATASETS_PATH + "/" + datasetId + DOCUMENTS_PATH + "/" + batch + INDEXING_STATUS_PATH;
        return executeGet(path, IndexingStatusResponse.class);
    }

    @Override
    public SimpleResponse deleteDocument(String datasetId, String documentId) throws IOException, DifyApiException {
        String path = buildDocumentPath(datasetId, documentId);
        return executeDelete(path, null, SimpleResponse.class);
    }

    @Override
    public DocumentListResponse getDocuments(String datasetId, String keyword, Integer page, Integer limit) throws IOException, DifyApiException {
        Map<String, Object> queryParams = new HashMap<>();
        addIfNotEmpty(queryParams, "keyword", keyword);
        addIfNotNull(queryParams, "page", page);
        addIfNotNull(queryParams, "limit", limit);

        String path = DATASETS_PATH + "/" + datasetId + DOCUMENTS_PATH;
        String url = buildUrlWithParams(path, queryParams);
        return executeGet(url, DocumentListResponse.class);
    }

    @Override
    public SegmentResponse createSegments(String datasetId, String documentId, CreateSegmentsRequest request) throws IOException, DifyApiException {
        String path = buildDocumentPath(datasetId, documentId) + SEGMENTS_PATH;
        return executePost(path, request, SegmentResponse.class);
    }

    @Override
    public SegmentListResponse getSegments(String datasetId, String documentId, String keyword, String status) throws IOException, DifyApiException {
        Map<String, Object> queryParams = new HashMap<>();
        addIfNotEmpty(queryParams, "keyword", keyword);
        addIfNotEmpty(queryParams, "status", status);

        String path = buildDocumentPath(datasetId, documentId) + SEGMENTS_PATH;
        String url = buildUrlWithParams(path, queryParams);
        return executeGet(url, SegmentListResponse.class);
    }

    @Override
    public SimpleResponse deleteSegment(String datasetId, String documentId, String segmentId) throws IOException, DifyApiException {
        String path = buildSegmentPath(datasetId, documentId, segmentId);
        return executeDelete(path, null, SimpleResponse.class);
    }

    @Override
    public SegmentResponse updateSegment(String datasetId, String documentId, String segmentId, UpdateSegmentRequest request) throws IOException, DifyApiException {
        String path = buildSegmentPath(datasetId, documentId, segmentId);
        return executePost(path, request, SegmentResponse.class);
    }

    @Override
    public UploadFileResponse getUploadFile(String datasetId, String documentId) throws IOException, DifyApiException {
        String path = buildDocumentPath(datasetId, documentId) + UPLOAD_FILE_PATH;
        return executeGet(path, UploadFileResponse.class);
    }

    @Override
    public RetrieveResponse retrieveDataset(String datasetId, RetrieveRequest request) throws IOException, DifyApiException {
        String path = DATASETS_PATH + "/" + datasetId + RETRIEVE_PATH;
        return executePost(path, request, RetrieveResponse.class);
    }

    @Override
    public MetadataResponse createMetadata(String datasetId, CreateMetadataRequest request) throws IOException, DifyApiException {
        String path = DATASETS_PATH + "/" + datasetId + METADATA_PATH;
        return executePost(path, request, MetadataResponse.class);
    }

    @Override
    public MetadataResponse updateMetadata(String datasetId, String metadataId, UpdateMetadataRequest request) throws IOException, DifyApiException {
        String path = DATASETS_PATH + "/" + datasetId + METADATA_PATH + "/" + metadataId;
        return executePatch(path, request, MetadataResponse.class);
    }

    @Override
    public String deleteMetadata(String datasetId, String metadataId) throws IOException, DifyApiException {
        String path = DATASETS_PATH + "/" + datasetId + METADATA_PATH + "/" + metadataId;
        return executeDelete(path, null, String.class);
    }

    /**
     * 执行Multipart请求
     *
     * @param path 请求路径
     * @param requestBody 请求体
     * @param responseClass 响应类型
     * @param <T> 响应类型
     * @return 响应对象
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    private <T> T executeMultipartRequest(String path, RequestBody requestBody, Class<T> responseClass) throws IOException, DifyApiException {
        Request httpRequest = new Request.Builder()
                .url(baseUrl + path)
                .post(requestBody)
                .header("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            return handleResponse(response, responseClass);
        }
    }

    /**
     * 创建Multipart请求构建器
     *
     * @param request 请求对象
     * @param file 文件
     * @return Multipart请求构建器
     */
    private MultipartBody.Builder createMultipartBuilder(Object request, File file) {
        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", JsonUtils.toJson(request))
                .addFormDataPart("file", file.getName(), RequestBody.create(file, OCTET_STREAM));
    }

    /**
     * 构建文档路径
     *
     * @param datasetId 知识库ID
     * @param documentId 文档ID
     * @return 文档路径
     */
    private String buildDocumentPath(String datasetId, String documentId) {
        return DATASETS_PATH + "/" + datasetId + DOCUMENTS_PATH + "/" + documentId;
    }

    /**
     * 构建分段路径
     *
     * @param datasetId 知识库ID
     * @param documentId 文档ID
     * @param segmentId 分段ID
     * @return 分段路径
     */
    private String buildSegmentPath(String datasetId, String documentId, String segmentId) {
        return buildDocumentPath(datasetId, documentId) + SEGMENTS_PATH + "/" + segmentId;
    }

    /**
     * 读取输入流的所有字节 (Java 8兼容方法)
     *
     * @param inputStream 输入流
     * @return 字节数组
     * @throws IOException IO异常
     */
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        try {
            // 创建缓冲区
            byte[] buffer = new byte[8192];
            int bytesRead;
            java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();

            // 读取数据直到输入流结束
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            return output.toByteArray();
        } finally {
            // 确保关闭输入流
            try {
                inputStream.close();
            } catch (IOException e) {
                // 忽略关闭异常
            }
        }
    }

}
