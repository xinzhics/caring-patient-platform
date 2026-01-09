package com.caring.sass.msgs.utils.im.api.impl;

import com.caring.sass.msgs.config.IMConfigWrapper;
import com.caring.sass.msgs.utils.im.api.FileAPI;
import com.caring.sass.msgs.utils.im.comm.ResponseHandler;
import io.swagger.client.api.UploadAndDownloadFilesApi;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class EasemobFile implements FileAPI {
    private ResponseHandler responseHandler = new ResponseHandler();
    private UploadAndDownloadFilesApi api = new UploadAndDownloadFilesApi();

    public EasemobFile() {
    }

    @Override
    public Object uploadFile(Object file) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatfilesPost(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), (File)file, true);
        });
    }

    @Override
    public Object downloadFile(String fileUUID, String shareSecret, Boolean isThumbnail) {
        return this.responseHandler.handle(() -> {
            return this.api.orgNameAppNameChatfilesUuidGet(IMConfigWrapper.getOrgName(), IMConfigWrapper.getAppName(), IMConfigWrapper.getAccessToken(), fileUUID, shareSecret, isThumbnail);
        });
    }
}
