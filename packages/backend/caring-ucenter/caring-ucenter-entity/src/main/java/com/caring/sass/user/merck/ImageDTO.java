package com.caring.sass.user.merck;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class ImageDTO {

    private Long id;

    private String purpose;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inspectionDate;

    private Set<Image> imageSet;

    private String caringUpdateDate;

    private String key;

    private String caringSassUrl;

}
