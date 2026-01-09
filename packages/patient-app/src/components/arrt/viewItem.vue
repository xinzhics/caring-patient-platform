<template>
    <van-row class="attr-view">
        <van-col span="24" v-for="(field, index) in currentFields" :key="index" class="attr-col">
            <!-- 描述 -->
            <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.SingleLineText
             || field.widgetType === Constants.CustomFormWidgetType.Number ||
              field.widgetType === Constants.CustomFormWidgetType.FullName" span="24">
                <van-cell
                        title-class="van-cell-width50"
                        label-class="van-cell-width50"
                        :value="field.values && field.values[0] ? field.values[0].attrValue : '-'">
                    <template slot="title">
                        <div>{{ field.label }}</div>
                    </template>
<!--                    <template #label>-->
<!--                        <div>{{ field.labelDesc }}</div>-->
<!--                    </template>-->
                    <template slot="right-icon" v-if="field.rightUnit">
                        <span>{{ field.rightUnit }}</span>
                    </template>
                </van-cell>
            </van-col>
            <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.MultiLineText" span="24">
                <van-cell
                        title-class="van-cell-width50"
                        label-class="van-cell-width50"
                        :value="field.values && field.values[0] ? field.values[0].attrValue : '-'">
                    <template slot="title">
                        <div class="van-cell-div">{{ field.label }}</div>
                    </template>
<!--                    <template #label>-->
<!--                        <div class="van-cell-label">{{ field.labelDesc }}</div>-->
<!--                    </template>-->
                </van-cell>
            </van-col>
            <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.Radio
             || field.widgetType === Constants.CustomFormWidgetType.CheckBox
             || field.widgetType === Constants.CustomFormWidgetType.DropdownSelect" span="24">
                <van-cell
                        title-class="van-cell-width50"
                        label-class="van-cell-width50"
                        :value="getFieldsResult(field.values)">
                    <template slot="title">
                        <div>{{ field.label }}</div>
                    </template>
<!--                    <template #label>-->
<!--                        <div>{{ field.labelDesc }}</div>-->
<!--                    </template>-->
                </van-cell>
            </van-col>

            <!-- 图片上传 -->
            <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.SingleImageUpload
                        && field.exactType !== Constants.FieldExactType.Avatar" span="24">
                <van-cell
                        title-class="van-cell-width50"
                        label-class="van-cell-width50">
                    <template #title>
                        <div>{{ field.label }}</div>
                    </template>
<!--                    <template #label>-->
<!--                        <div>{{ field.labelDesc }}</div>-->
<!--                    </template>-->
                    <van-image width="100" height="100" :src="v.attrValue" v-for="(v,k) in field.values"  :key="k" fit="contain"
                               @click="showImage(v.attrValue)"/>
                </van-cell>
            </van-col>

            <!--图片上传查看-->
            <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.MultiImageUpload" span="24">
                <van-cell
                        title-class="van-cell-width50"
                        label-class="van-cell-width50">
                    <template #title>
                        <div>{{ field.label }}</div>
                    </template>
<!--                    <template #label>-->
<!--                        <div>{{ field.labelDesc }}</div>-->
<!--                    </template>-->
                    <van-image width="70" height="70" :src="v.attrValue" v-for="(v,k) in field.values" :key="k" fit="contain"
                               @click="showImage(v.attrValue)"/>
                </van-cell>
            </van-col>

            <!-- 时间 -->
            <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.Time
                    || field.widgetType === Constants.CustomFormWidgetType.Date" span="24">
                <van-cell
                        title-class="van-cell-width50"
                        label-class="van-cell-width50"
                        :value="setdateValT(field.values)">
                    <template slot="title">
                        <div>{{ field.label }}</div>
                    </template>
                    <!-- <span>{{field.values && field.values[0] ? field.values[0].attrValue : '-'}}</span> -->
<!--                    <template #label>-->
<!--                        <div>{{ field.labelDesc }}</div>-->
<!--                    </template>-->
                </van-cell>
            </van-col>
            <!-- 性别暂时没有特殊处理 -->
            <!-- 行政区划地址 -->
            <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.Address" span="24">
                <van-cell
                        :title="field.label"
                        :label="field.labelDesc"
                        :value="getAddressResult(field.values)">
                    <template #title>
                        <div>{{ field.label }}</div>
                    </template>
<!--                    <template #label>-->
<!--                        <div>{{ field.labelDesc }}</div>-->
<!--                    </template>-->
                </van-cell>
                <van-cell
                        v-if="field.hasAddressDetail === 1"
                        title="详情地址"
                        :value="field.value">
                </van-cell>
            </van-col>
        </van-col>
    </van-row>
</template>
<script>
import {ImagePreview,Row,Col,Cell} from "vant";
    export default {
        components:{
            [Row.name]:Row,
            [Col.name]:Col,
            [Cell.name]:Cell,
        },
        props: {
            allFields: {
                type: Array
            }
        },
        data: function () {
            return {
                currentFields: [] // 当前展示的元素
            };
        },
        created() {
            this.initFields();
        },
        methods: {
            setdateValT(i){
                var val = '-'
                if(i&&i[0]){
                    val = i[0].attrValue
                }
                return String(val)

            },
            // 多级下拉
            getDropdownSelectResult(values) {
                if (values && values.length > 0) {
                    return values[values.length - 1].attrValue;
                } else {
                    return '-';
                }
            },
            // 地址的选择
            getAddressResult(values) {
                if (values && values.length > 0) {
                    const address = values[0].attrValue;
                    if (address && address.length > 0) {
                        return address.join('-');
                    } else {
                        return '-';
                    }
                } else {
                    return '-';
                }
            },
            // 普通的选择
            getFieldsResult(values) {
                if (values && values.length > 0) {
                    const result = [];
                    for (let i = 0; i < values.length; i++) {
                        let value;
                        if (values[i].desc && values[i].desc.length > 0) {
                            value = values[i].valueText + '(' + values[i].desc + ')';
                        } else {
                            value = values[i].valueText;
                        }
                        result.push(value);
                    }
                    if (result && result.length > 0) {
                        return result.join('、');
                    } else {
                        return '-';
                    }
                } else {
                    return '-';
                }
            },
            initFields() {
                this.currentFields = [];
                if (this.allFields && this.allFields.length > 0) {
                    const tempArray = [];
                    for (let i = 0; i < this.allFields.length; i++) {
                        if (this.allFields[i].widgetType !== this.Constants.CustomFormWidgetType.Page) {
                            tempArray.push(this.allFields[i]);
                        }
                    }
                    this.currentFields = tempArray;
                    // console.log('currentFields', this.currentFields);
                }
            },
            showImage(attrValue) {
                if (attrValue) {
                    ImagePreview({images: [attrValue], closeable: true});
                }
            }
        }
    };
</script>
<style lang="less" scoped>
    .attr-view {
        .attr-col {
            position: relative;
            display: -webkit-box;
            display: -webkit-flex;
            display: flex;
            box-sizing: border-box;
            width: 100%;
            overflow: hidden;
        }

        .attr-col:not(:last-child)::after {
            position: absolute;
            box-sizing: border-box;
            content: ' ';
            pointer-events: none;
            right: 0;
            bottom: 0;
            left: 16px;
            border-bottom: 1px solid #ebedf0;
            -webkit-transform: scaleY(.5);
            transform: scaleY(.5);
        }

        .attr-button {
            width: 40% !important;
            height: 35px !important;
        }

        .van-cell-width50 {
            width: 45%;
            height: auto;
        }

        .van-cell-label {
            width: 100%;
            height: auto;
        }

        .van-cell-div {
            word-wrap: break-word;
            word-break: normal;
        }

        .med-img {
            width: 60px;
            height: 60px;
            border-radius: 50%;
        }
    }
</style>
