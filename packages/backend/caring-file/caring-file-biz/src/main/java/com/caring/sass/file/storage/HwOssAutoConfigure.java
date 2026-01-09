package com.caring.sass.file.storage;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.file.domain.FileDeleteDO;
import com.caring.sass.file.dto.chunk.FileChunksMergeDTO;
import com.caring.sass.file.entity.File;
import com.caring.sass.file.properties.FileServerProperties;
import com.caring.sass.file.strategy.impl.AbstractFileChunkStrategy;
import com.caring.sass.file.strategy.impl.AbstractFileStrategy;
import com.caring.sass.utils.StrPool;
import com.obs.services.ObsClient;
import com.obs.services.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.caring.sass.utils.DateUtils.DEFAULT_MONTH_FORMAT_SLASH;

/**
 * 华为obs
 */
@EnableConfigurationProperties(FileServerProperties.class)
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = FileServerProperties.PREFIX, name = "type", havingValue = "HW")
public class HwOssAutoConfigure {

    @Service
    public class HwServiceImpl extends AbstractFileStrategy {


        /**
         * @link https://support.huaweicloud.com/sdk-java-devg-obs/obs_21_0603.html
         */
        @Override
        protected void uploadFile(File file, MultipartFile multipartFile) throws Exception {
            FileServerProperties.HW hw = fileProperties.getHw();
            ObsClient obsClient = new ObsClient(hw.getAccessKey(), hw.getSecurityKey(), hw.getEndPoint());
            String bucketName = hw.getBucketName();
            if (!obsClient.headBucket(bucketName)) {
                obsClient.createBucket(bucketName);
            }

            //生成文件名
            String fileName;
            if (StringUtils.isNotEmpty(file.getObsFileName())) {
                fileName = StrUtil.join(StrPool.EMPTY, file.getObsFileName(), StrPool.DOT, file.getExt());
            } else {
                fileName = StrUtil.join(StrPool.EMPTY, UUID.randomUUID().toString(), StrPool.DOT, file.getExt());
            }
            //日期文件夹
            String tenant = BaseContextHandler.getTenant();
            String relativePath = "";
            if (StrUtil.isNotBlank(tenant)) {
                relativePath = tenant + StrPool.SLASH + LocalDate.now().format(DateTimeFormatter.ofPattern(DEFAULT_MONTH_FORMAT_SLASH));
            } else {
                relativePath = LocalDate.now().format(DateTimeFormatter.ofPattern(DEFAULT_MONTH_FORMAT_SLASH));
            }
            // 服务器存放的绝对路径
            String relativeFileName = relativePath + StrPool.SLASH + fileName;

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, relativeFileName, multipartFile.getInputStream());
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContextType());
            putObjectRequest.setMetadata(metadata);
            PutObjectResult result = obsClient.putObject(putObjectRequest);
            obsClient.setObjectAcl(bucketName, relativeFileName, AccessControlList.REST_CANNED_PUBLIC_READ);
            log.info("result={}", JSONObject.toJSONString(result));

            String url = result.getObjectUrl();
            file.setUrl(url);
            file.setFilename(fileName);
            file.setRelativePath(relativePath);

            file.setGroup(result.getBucketName());
            file.setPath(result.getRequestId());

            obsClient.close();
        }

        @Override
        protected void delete(List<FileDeleteDO> list, FileDeleteDO file) throws IOException {
            FileServerProperties.HW hw = fileProperties.getHw();
            ObsClient obsClient = new ObsClient(hw.getAccessKey(), hw.getSecurityKey(), hw.getEndPoint());
            String bucketName = hw.getBucketName();
            obsClient.deleteObject(bucketName, file.getRelativePath() + StrPool.SLASH + file.getFileName());
            obsClient.close();
        }
    }

    @Service
    public class HwChunkServiceImpl extends AbstractFileChunkStrategy {

        @Override
        protected void copyFile(File file) {
            FileServerProperties.HW hw = fileProperties.getHw();
            String sourceBucketName = hw.getBucketName();
            String destinationBucketName = hw.getBucketName();
            ObsClient obsClient = new ObsClient(hw.getAccessKey(), hw.getSecurityKey(), hw.getEndPoint());

            String sourceObjectName = file.getRelativePath() + StrPool.SLASH + file.getFilename();
            String fileName = UUID.randomUUID().toString() + StrPool.DOT + file.getExt();
            String destinationObjectName = file.getRelativePath() + StrPool.SLASH + fileName;
            ObjectMetadata objectMetadata = obsClient.getObjectMetadata(sourceBucketName, sourceObjectName);
            // 获取被拷贝文件的大小。
            long contentLength = objectMetadata.getContentLength();
            // 设置分片大小为10MB。
            long partSize = 1024 * 1024 * 10;
            // 计算分片总数。
            int partCount = (int) (contentLength / partSize);
            if (contentLength % partSize != 0) {
                partCount++;
            }
            log.info("total part count:{}", partCount);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContextType());

            // 初始化拷贝任务。可以通过InitiateMultipartUploadRequest指定目标文件元信息。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(destinationBucketName, destinationObjectName);
            request.setMetadata(metadata);
            InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);
            String uploadId = result.getUploadId();

            // 分片拷贝。
            final List<PartEtag> partEtags = Collections.synchronizedList(new ArrayList<>());
            for (int i = 0; i < partCount; i++) {
                // 复制段起始位置
                long rangeStart = i * partSize;
                // 复制段结束位置
                long rangeEnd = (i + 1 == partCount) ? contentLength - 1 : rangeStart + partSize - 1;
                // 分段号
                int partNumber = i + 1;

                CopyPartRequest uploadPartCopyRequest = new CopyPartRequest();
                uploadPartCopyRequest.setUploadId(uploadId);
                uploadPartCopyRequest.setSourceBucketName(sourceBucketName);
                uploadPartCopyRequest.setSourceObjectKey(sourceObjectName);
                uploadPartCopyRequest.setDestinationBucketName(destinationBucketName);
                uploadPartCopyRequest.setDestinationObjectKey(destinationObjectName);
                uploadPartCopyRequest.setByteRangeStart(rangeStart);
                uploadPartCopyRequest.setByteRangeEnd(rangeEnd);
                uploadPartCopyRequest.setPartNumber(partNumber);

                CopyPartResult copyPartResult = obsClient.copyPart(uploadPartCopyRequest);
                // 将返回的分片ETag保存到partETags中。
                partEtags.add(new PartEtag(copyPartResult.getEtag(), copyPartResult.getPartNumber()));
            }

            // 提交分片拷贝任务。
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(destinationBucketName, destinationObjectName, uploadId, partEtags);
            CompleteMultipartUploadResult completeMultipartUploadResult = obsClient.completeMultipartUpload(completeMultipartUploadRequest);

            file.setUrl(completeMultipartUploadResult.getLocation());
            file.setFilename(fileName);

            // 关闭OSSClient。
            try {
                obsClient.close();
            } catch (Exception e) {
                log.error("关闭obs异常", e);
            }
        }

        /**
         * @param files    文件
         * @param path     路径
         * @param fileName 唯一名 含后缀
         * @param info     文件信息
         * @link https://support.huaweicloud.com/sdk-java-devg-obs/obs_21_0607.html
         */
        @Override
        protected R<File> merge(List<java.io.File> files, String path, String fileName, FileChunksMergeDTO info) throws IOException {
            FileServerProperties.HW hw = fileProperties.getHw();
            String bucketName = hw.getBucketName();
            ObsClient obsClient = new ObsClient(hw.getAccessKey(), hw.getSecurityKey(), hw.getEndPoint());

            //日期文件夹
            String relativePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            // web服务器存放的绝对路径
            String relativeFileName = relativePath + StrPool.SLASH + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(info.getContextType());

            //步骤1：初始化分段上传任务
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, "objectname");
            InitiateMultipartUploadResult result = obsClient.initiateMultipartUpload(request);
            // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个ID来发起相关的操作，如取消分片上传、查询分片上传等。
            String uploadId = result.getUploadId();

            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartEtag> partETags = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                java.io.File file = files.get(i);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setObjectKey(relativeFileName);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setFile(file);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100KB。
                uploadPartRequest.setPartSize(file.length());
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
                uploadPartRequest.setPartNumber(i + 1);

                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = obsClient.uploadPart(uploadPartRequest);

                // 每次上传分片之后，OSS的返回结果会包含一个PartETag。PartETag将被保存到partETags中。
                partETags.add(new PartEtag(uploadPartResult.getEtag(), uploadPartResult.getPartNumber()));
            }

            /* 步骤3：完成分片上传。 */
            // 排序。partETags必须按分片号升序排列。
            partETags.sort(Comparator.comparingInt(PartEtag::getPartNumber));

            // 在执行该操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest("bucketname", "objectname", uploadId, partETags);

            CompleteMultipartUploadResult uploadResult = obsClient.completeMultipartUpload(completeMultipartUploadRequest);

            String url = uploadResult.getLocation();

            File filePo = File.builder()
                    .relativePath(relativePath)
                    .group(uploadResult.getBucketName())
                    .path(uploadResult.getRequestId())
                    .url(StringUtils.replace(url, "\\\\", StrPool.SLASH))
                    .build();

            // 关闭OSSClient。
            obsClient.close();
            return R.success(filePo);
        }
    }

}
