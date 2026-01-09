package com.caring.sass.nursing.dto.form;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


/**
 * 表单字段
 *
 * @author xinzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "FormField", description = "表单字段")
public class FormField implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;

    String indefiner;

    String businessId;

    @ApiModelProperty(value = "题目所属的分组ID")
    String fieldGroupUUId;

    @ApiModelProperty(value = "标题")
    String label;

    @ApiModelProperty(value = "标题的描述")
    String labelDesc;

    @ApiModelProperty(value = "输入时的提示")
    String placeholder;

    @ApiModelProperty(value = "单选 多选才会使用，是否增加其它选项 ( 1 是, 2)")
    Integer hasOtherOption = 2;

    @ApiModelProperty(value = "其他选项要求的备注显示")
    String otherLabelRemark;

    @ApiModelProperty(value = "右侧单位")
    String rightUnit;

    @ApiModelProperty(value = "其他选项 保存的值")
    String otherValue;

    @ApiModelProperty(value = "其他输入框可以输入的长度")
    Integer otherValueSize = 120;

    @ApiModelProperty(value = "其他选项输入框是否必填 ( 1 是,  2 )")
    Integer otherEnterRequired = 2;

    @ApiModelProperty(value = "在所有的 选项后面 有输入框")
    Integer itemAfterHasEnter = 2;

    @ApiModelProperty(value = "选项输入框 后面得输入框是否为 必填")
    Integer itemAfterHasEnterRequired = 2;

    @ApiModelProperty(value = "选项输入框 后面得输入框 可以写多少字")
    Integer itemAfterHasEnterSize = 120;

    @ApiModelProperty(value = "多选 最多可选几个")
    Integer maxChooseSize;

    @ApiModelProperty(value = "时间类型，限定选择时间")
    Integer defineChooseDate = 2;


    @ApiModelProperty(value = "只能选当前及以前的时间 1  只能选当前 及 以后的时间 2")
    Integer defineChooseDateType;

    @ApiModelProperty(value = "数字类型,支持小数")
    Integer canDecimal;

    @ApiModelProperty(value = "保留小数，后几位")
    Integer precision;

    @ApiModelProperty(value = "文本描述，文本描述的标题对齐")
    String titleTextAlign;

    @ApiModelProperty(value = "文本描述 内容对齐方式")
    String contentTextAlign;

    @ApiModelProperty(value = "分割线样式")
    String dividerStyle;

    @ApiModelProperty(value = "分割线宽度")
    Integer dividerWidth;

    @ApiModelProperty(value = "是否展示分割线")
    Integer dividerShow;

    @ApiModelProperty(value = "地址组件是否有详情输入框")
    Integer hasAddressDetail;

    @ApiModelProperty(value = "地址组件 详情输入框是否必填")
    Integer hasAddressDetailRequired;

    @ApiModelProperty(value = "详情地址输入框最多多少字")
    Integer addressDetailSize;

    @ApiModelProperty(value = "分页按钮的 样式")
    String pageType;

    @ApiModelProperty(value = "是否可以返回上一页")
    Integer canBackPrevious;

    @ApiModelProperty(value = "是否显示 当前页码")
    Integer showPageNo;

    @ApiModelProperty(value = "上传多个图片 （是1， 否2）")
    Integer uploadMany;

    @ApiModelProperty(value = "上传数量")
    Integer uploadNumber;

    Integer codeType;

    boolean isRequired;

    String errorWhenEmpty;

    String minValue;

    String maxValue;

    @ApiModelProperty(value = "最大可输入长度。 限 单行 多行文本使用")
    Integer maxEnterNumber;

    String defaultValue;

    String value;

    @JSONField(jsonDirect=true)
    String values;

    @ApiModelProperty(value = "复选框values的id集合")
    String checkBoxValues;

    @ApiModelProperty(value = "趋势图，1 是， null 0 否")
    private Integer showTrend;

    @ApiModelProperty(value = "趋势图参考值是否有参考值 true 有， false 没有")
    private Boolean hasTrendReference;

    @ApiModelProperty(value = "趋势图参考值，exact_value 精确数值， range 区间范围")
    private String trendReference;

    @ApiModelProperty(value = "趋势图 精确数值")
    private String trendReferenceExactValue;

    @ApiModelProperty(value = "趋势图 区间范围参考最大值")
    private String trendReferenceMax;

    @ApiModelProperty(value = "趋势图 区间范围参考最小值")
    private String trendReferenceMin;

    @ApiModelProperty(value = "是否显示基准值和目标值")
    private Boolean showReferenceTargetValue;

    @ApiModelProperty(value = "第一次填写作为基准值")
    private Boolean firstWriteAsReferenceValue;

    @ApiModelProperty(value = "基准值")
    private Double referenceValue;

    @ApiModelProperty(value = "目标值")
    private Double targetValue;

    boolean enableSmsCheck;

    String verifyRule;

    @ApiModelProperty(value = "字段标识", dataType = "FormFieldExactType")
    String exactType;

    @ApiModelProperty(value = "字段类型", dataType = "FormWidgetType")
    String widgetType;

    Integer isUpdatable;

    Integer order;

    List<FormOptions> options;

    @ApiModelProperty(value = "修改标记")
    Boolean modifyTags = false;

    @ApiModelProperty(value = "显示数据反馈")
    Boolean showDataFeedback;

    @ApiModelProperty(value = "数据反馈内容")
    List<DataFeedBack> dataFeedBacks;

    /**
     * true 表示注册流程中，此组件不显示。
     * null 或 false 表单注册流程中正常使用
     */
    @ApiModelProperty(value = "取消注册流程中出现")
    Boolean cancelRegistrationProcessAppear;

    @ApiModelProperty(value = "疾病信息字段展示区域（疾病信息 disease， 诊断信息 diagnostic）")
    String healthFieldShowDisplayArea;

    @ApiModelProperty(value = "正常范围条件 大于 gt， 大于等于 gte, 小于 lt， 小于等于 lte， X至Y interval ")
    String scopeConditions;

    @ApiModelProperty("正常x数值")
    Double scopeXNumber;

    @ApiModelProperty("正常y数值")
    Double scopeYNumber;

    @ApiModelProperty("默认医生的患者填写")
    Boolean defaultDoctorPatientNeedWrite;

}
