package com.caring.sass.file.strategy;

import com.caring.sass.file.domain.FileDeleteDO;
import com.caring.sass.file.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件策略接口
 *
 * @author caring
 * @date 2019/06/17
 */
public interface FileStrategy {
    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件对象
     * @author caring
     * @date 2019-05-06 16:38
     */
    File upload(MultipartFile file);

    /**
     * 文件上传
     * @param file
     * @return
     */
    File uploadAppFile(MultipartFile file, String obsFileName);

    /**
     * 删除源文件
     *
     * @param list 列表
     * @return
     * @author caring
     * @date 2019-05-07 11:41
     */
    boolean delete(List<FileDeleteDO> list);

}
