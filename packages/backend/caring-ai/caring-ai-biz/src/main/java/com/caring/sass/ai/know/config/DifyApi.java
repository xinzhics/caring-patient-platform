package com.caring.sass.ai.know.config;

public enum DifyApi {

    /**
     * 创建知识库
     */
    createDatasets("/datasets","POST"),

    /**
     * 删除知识库
     */
    deleteDatasets("/datasets/{dataset_id}", "DELETE"),

    /**
     * 通过文本更新文档
     */
    updateDocumentsByText("/datasets/{dataset_id}/documents/{document_id}/update_by_text", "POST"),


    /**
     * 通过文件更新文档
     */
    updateDocumentsByFile("/datasets/{dataset_id}/documents/{document_id}/update_by_file", "POST"),


    /**
     * 通过文本创建文档
     */
    createDocumentByText("/datasets/{dataset_id}/document/create_by_text", "POST"),


    /**
     * 新建知识库元数据
     */
    datasetsMetadata("/datasets/{dataset_id}/metadata", "POST"),


    /**
     * 给文档添加元数据
     */
    datasetsDocumentsMetadata("/datasets/{dataset_id}/documents/metadata", "POST"),


    /**
     * 通过文件创建文档
     */
    createDocumentByFile("/datasets/{dataset_id}/document/create_by_file", "POST"),

    /**
     * 获取文档索引状态
     */
    getDocumentsIndexingStatus("/datasets/{dataset_id}/documents/{batch}/indexing-status", "GET"),


    retryDocumentIndex("/datasets/{dataset_id}/document/{document_id}/retry", "GET"),

    /**
     * 删除文档
     */
    deleteDocuments("/datasets/{dataset_id}/documents/{document_id}", "DELETE"),


    /**
     * 发送消息
     */
    completionMessages("/completion-messages", "POST"),


    workflowsRun("/workflows/run", "POST"),


    chatMessage("/chat-messages", "POST"),


    filesUpload("/files/upload", "POST"),


    segments("/datasets/{dataset_id}/documents/{document_id}/segments", "GET")
    ;

    public String path;

    public String method;

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    DifyApi(String path, String method) {
        this.path = path;
        this.method = method;
    }

}
