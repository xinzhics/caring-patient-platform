package com.caring.sass.ai.dto.know;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("CreateMedicalRecordsModel")
@Data
public class CreateMedicalRecordsModel {

    @ApiModelProperty(value = "sse连接的uid，接受制作出的病例")
    private String uid;


    @ApiModelProperty(value = "录音翻译出来的文本")
    private String soundRecordingText;

}
