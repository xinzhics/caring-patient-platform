package com.caring.sass.user.merck;


import lombok.Data;

/**
 *
 * @author james
 */
@Data
public class BaseBpHealthFile extends BaseHealthFile {


    private String smoke;

    private Integer smokeage;

    private String passiveCigarette;

    private String drink;

    private String status;


}
