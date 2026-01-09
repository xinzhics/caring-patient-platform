package com.caring.sass.file.strategy.impl;

import com.caring.sass.exception.BizException;
import com.caring.sass.file.domain.FileDeleteDO;
import com.caring.sass.file.entity.File;
import com.caring.sass.file.enumeration.IconType;
import com.caring.sass.file.properties.FileServerProperties;
import com.caring.sass.file.strategy.FileStrategy;
import com.caring.sass.file.utils.FileDataTypeUtil;
import com.caring.sass.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.caring.sass.exception.code.ExceptionCode.BASE_VALID_PARAM;


/**
 * 文件抽象策略 处理类
 *
 * @author caring
 * @date 2019/06/17
 */
@Slf4j
public abstract class AbstractFileStrategy implements FileStrategy {

    private static final String FILE_SPLIT = ".";
    @Autowired
    protected FileServerProperties fileProperties;

    private File buildFile(MultipartFile multipartFile) {
        try {
            if (!multipartFile.getOriginalFilename().contains(FILE_SPLIT)) {
                throw BizException.wrap(BASE_VALID_PARAM.build("缺少后缀名"));
            }

            File file = File.builder()
                    .isDelete(false).submittedFileName(multipartFile.getOriginalFilename())
                    .contextType(multipartFile.getContentType())
                    .dataType(FileDataTypeUtil.getDataType(multipartFile.getContentType()))
                    .size(multipartFile.getSize())
                    .ext(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))
                    .build();
            file.setIcon(IconType.getIcon(file.getExt()).getIcon());
            setDate(file);
            return file;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw BizException.wrap(BASE_VALID_PARAM.build("文件上传失败"));
        }
    }

    /**
     * 上传文件
     *
     * @param multipartFile
     * @return
     */
    @Override
    public File upload(MultipartFile multipartFile) {
        try {
            File file = buildFile(multipartFile);
            uploadFile(file, multipartFile);
            return file;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw BizException.wrap(BASE_VALID_PARAM.build("文件上传失败"));
        }
    }

    /**
     * 设置这里上传的文件。文件名称不需要被转义
     * 慎用
     * @param multipartFile
     * @return
     */
    @Override
    public File uploadAppFile(MultipartFile multipartFile, String obsFileName) {

        File file = buildFile(multipartFile);
        try {
            file.setObsFileName(obsFileName);
            uploadFile(file, multipartFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 具体类型执行上传操作
     *
     * @param file
     * @param multipartFile
     * @throws Exception
     */
    protected abstract void uploadFile(File file, MultipartFile multipartFile) throws Exception;

    private void setDate(File file) {
        LocalDateTime now = LocalDateTime.now();
        file.setCreateMonth(DateUtils.formatAsYearMonthEn(now))
                .setCreateWeek(DateUtils.formatAsYearWeekEn(now))
                .setCreateDay(DateUtils.formatAsDateEn(now));
    }

    @Override
    public boolean delete(List<FileDeleteDO> list) {
        if (list.isEmpty()) {
            return true;
        }
        boolean flag = false;
        for (FileDeleteDO file : list) {
            try {
                delete(list, file);
                flag = true;
            } catch (Exception e) {
                log.error("删除文件失败", e);
            }
        }
        return flag;
    }

    /**
     * 具体执行删除方法， 无需处理异常
     *
     * @param list
     * @param file
     * @author caring
     * @date 2019-05-07
     */
    protected abstract void delete(List<FileDeleteDO> list, FileDeleteDO file) throws IOException;

}
