package com.caring.sass.common.difyclient;

import com.caring.sass.common.difyclient.exception.DifyApiException;
import com.caring.sass.common.difyclient.model.common.SimpleResponse;
import com.caring.sass.common.difyclient.model.datasets.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Dify 知识库客户端接口
 * 提供知识库相关的操作
 */
public interface DifyDatasetsClient {

    /**
     * 创建空知识库
     *
     * @param request 创建知识库请求
     * @return 知识库信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    DatasetResponse createDataset(CreateDatasetRequest request) throws IOException, DifyApiException;

    /**
     * 获取知识库列表
     *
     * @param page  页码
     * @param limit 每页数量
     * @return 知识库列表
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    DatasetListResponse getDatasets(Integer page, Integer limit) throws IOException, DifyApiException;

    /**
     * 删除知识库
     *
     * @param datasetId 知识库ID
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SimpleResponse deleteDataset(String datasetId) throws IOException, DifyApiException;

    /**
     * 通过文本创建文档
     *
     * @param datasetId 知识库ID
     * @param request   创建文档请求
     * @return 文档信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    DocumentResponse createDocumentByText(String datasetId, CreateDocumentByTextRequest request) throws IOException, DifyApiException;

    /**
     * 通过文件创建文档
     *
     * @param datasetId 知识库ID
     * @param request   创建文档请求
     * @param file      文件
     * @return 文档信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    DocumentResponse createDocumentByFile(String datasetId, CreateDocumentByFileRequest request, File file) throws IOException, DifyApiException;

    /**
     * 通过文件创建文档
     *
     * @param datasetId   知识库ID
     * @param request     创建文档请求
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @return 文档信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    DocumentResponse createDocumentByFile(String datasetId, CreateDocumentByFileRequest request, InputStream inputStream, String fileName) throws IOException, DifyApiException;

    /**
     * 通过文本更新文档
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @param request    更新文档请求
     * @return 文档信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    DocumentResponse updateDocumentByText(String datasetId, String documentId, UpdateDocumentByTextRequest request) throws IOException, DifyApiException;

    /**
     * 通过文件更新文档
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @param request    更新文档请求
     * @param file       文件
     * @return 文档信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    DocumentResponse updateDocumentByFile(String datasetId, String documentId, UpdateDocumentByFileRequest request, File file) throws IOException, DifyApiException;

    /**
     * 获取文档嵌入状态
     *
     * @param datasetId 知识库ID
     * @param batch     批次号
     * @return 文档嵌入状态
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    IndexingStatusResponse getIndexingStatus(String datasetId, String batch) throws IOException, DifyApiException;

    /**
     * 删除文档
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SimpleResponse deleteDocument(String datasetId, String documentId) throws IOException, DifyApiException;

    /**
     * 获取知识库文档列表
     *
     * @param datasetId 知识库ID
     * @param keyword   关键词
     * @param page      页码
     * @param limit     每页数量
     * @return 文档列表
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    DocumentListResponse getDocuments(String datasetId, String keyword, Integer page, Integer limit) throws IOException, DifyApiException;

    /**
     * 新增文档分段
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @param request    新增分段请求
     * @return 分段信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SegmentResponse createSegments(String datasetId, String documentId, CreateSegmentsRequest request) throws IOException, DifyApiException;

    /**
     * 查询文档分段
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @param keyword    关键词
     * @param status     状态
     * @return 分段列表
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SegmentListResponse getSegments(String datasetId, String documentId, String keyword, String status) throws IOException, DifyApiException;

    /**
     * 删除文档分段
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @param segmentId  分段ID
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SimpleResponse deleteSegment(String datasetId, String documentId, String segmentId) throws IOException, DifyApiException;

    /**
     * 更新文档分段
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @param segmentId  分段ID
     * @param request    更新分段请求
     * @return 分段信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    SegmentResponse updateSegment(String datasetId, String documentId, String segmentId, UpdateSegmentRequest request) throws IOException, DifyApiException;

    /**
     * 获取上传文件
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @return 文件信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    UploadFileResponse getUploadFile(String datasetId, String documentId) throws IOException, DifyApiException;

    /**
     * 检索知识库
     *
     * @param datasetId 知识库ID
     * @param request   检索请求
     * @return 检索结果
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    RetrieveResponse retrieveDataset(String datasetId, RetrieveRequest request) throws IOException, DifyApiException;


    /**
     * 新增元数据
     *
     * @param datasetId  知识库ID
     * @param request    新增元数据请求
     * @return 元数据信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    MetadataResponse createMetadata(String datasetId, CreateMetadataRequest request) throws IOException, DifyApiException;

    /**
     * 更新元数据
     *
     * @param datasetId  知识库ID
     * @param metadataId  元数据 ID
     * @param request    更新元数据请求
     * @return 元数据信息
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    MetadataResponse updateMetadata(String datasetId, String metadataId, UpdateMetadataRequest request) throws IOException, DifyApiException;

    /**
     * 删除元数据
     *
     * @param datasetId  知识库ID
     * @param metadataId 文档ID
     * @return 响应
     * @throws IOException IO异常
     * @throws DifyApiException API异常
     */
    String deleteMetadata(String datasetId, String metadataId) throws IOException, DifyApiException;
}
