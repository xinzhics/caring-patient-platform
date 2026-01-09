package com.caring.sass.file.api.fallback;

import com.caring.sass.base.R;
import com.caring.sass.file.api.FileUploadApi;
import com.caring.sass.file.dto.FolderSaveDTO;
import com.caring.sass.file.entity.File;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class FileUploadApiFallback implements FileUploadApi {

    @Override
    public R<FolderSaveDTO> save(FolderSaveDTO folderSaveDTO) {
        return R.timeout();
    }

    @Override
    public R<List<File>> query(File data) {
        return R.timeout();
    }

    @Override
    public R<File> upload(@NotNull(message = "文件夹不能为空") Long folderId, MultipartFile simpleFile) {
        return R.timeout();
    }

    @Override
    public R<File> uploadAppFile(Long folderId, MultipartFile simpleFile, String obsFileName) {
        return R.timeout();
    }

    @Override
    public R<List<File>> getFile() {
        return R.timeout();
    }
}
