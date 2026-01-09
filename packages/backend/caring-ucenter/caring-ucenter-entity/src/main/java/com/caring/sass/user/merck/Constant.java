package com.caring.sass.user.merck;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@Data
public class Constant implements Serializable {

    private String key;
    private String label;
    private String type;
    private String description;

    private Long index;

}
