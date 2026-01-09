package com.caring.sass.ai.entity.know;

import com.caring.sass.base.BaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * <p>
 * 实体注释中生成的类型枚举
 * 企业
 * </p>
 *
 * @author caring
 * @date 2019-10-25
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "KnowledgeLabelEnum", description = "知识库文档标签名")
public enum KnowledgeLabelEnum implements BaseEnum {

    /**
     * NORMAL="正常"
     */
    LANGUAGE("语言"),
    /**
     * WAIT_INIT="待初始化"
     */
    AUTHOR("作者"),
    /**
     * FORBIDDEN="禁用"
     */
    RESEARCH_TYPE("研究类型"),
    /**
     * WAITING="待审核"
     */
    CONFERENCE_JOURNAL_NAME("期刊/会议名称"),

    KEY_WORD("关键词"),

    RELEASE_TIME("发布时间"),

    JI_LU_TIME("记录时间"),
    /**
     * 个人成果类型
     */
    PERSONAL_ACHIEVEMENT_TYPE("类型"),


    DIAGNOSTIC_RESULTS("诊断结果"),


    TREATMENT_PLAN("治疗方案"),


    KEY_SYMPTOMS("关键症状"),

    CASE_TYPE("病例类型"),

    TREATMENT_OUTCOME("治疗结果"),

    CASE_SOURCE("病例来源"),

    POST_STATUS("发表状态"),

    FOLLOW_UP_RESULTS("随访结果")
    ;

    @ApiModelProperty(value = "描述")
    private String desc;

    public static KnowledgeLabelEnum match(String val, KnowledgeLabelEnum def) {
        return Stream.of(values()).parallel().filter((item) -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static KnowledgeLabelEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(KnowledgeLabelEnum val) {
        return val == null ? false : eq(val.name());
    }

    @Override
    public String getCode() {
        return this.name();
    }

}
