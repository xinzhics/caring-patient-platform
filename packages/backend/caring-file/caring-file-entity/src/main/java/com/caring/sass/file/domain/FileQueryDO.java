package com.caring.sass.file.domain;


import com.caring.sass.file.entity.File;
import lombok.Data;

/**
 * 文件查询 DO
 *
 * @author caring
 * @date 2019/05/07
 */
@Data
public class FileQueryDO extends File {
    private File parent;

}
