package com.caring.sass.user.dto;

import com.caring.sass.base.BaseEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * @ClassName StatisticeDto
 * @Description
 * @Author yangShuai
 * @Date 2020/11/27 9:50
 * @Version 1.0
 */
@Data
public class StatisticsPatientDto {

    private String key;

    private String name;

    private Integer count;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum Age implements BaseEnum {

        less18("18岁以下"),
        more18less25("18-24岁"),
        more25less30("25-30岁"),
        more30less50("31-50岁"),
        more51("51岁以上");

        private String desc;

        public static Age match(String val, Age def) {
            return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
        }

        public boolean eq(Age val) {
            return val != null && eq(val.name());
        }

        @Override
        public String getCode() {
            return this.name();
        }


        @Override
        public boolean eq(String val) {
            return false;
        }

    }

}
