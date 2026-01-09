package com.caring.sass.user.merck;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
public class BaseMerckHealthFile extends BaseBpHealthFile {

    private Integer cigaretteCount;

    @ApiModelProperty("家族过敏史")
    private Set<Constant> familyAllergyHistory;

    @ApiModelProperty("过敏源")
    private Set<Constant> allergen;

    private String otherAllergenText;

}
