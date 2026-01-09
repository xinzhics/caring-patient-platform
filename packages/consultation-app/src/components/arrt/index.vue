<template>
  <van-row class="attr-view">
    <van-col span="24" v-for="(field, index) in currentFields" :key="index" class="attr-col">
      <!-- 描述 -->
      <attrDesc v-if="field.widgetType === Constants.CustomFormWidgetType.Desc" :field="field"></attrDesc>
      <splitLine v-if="field.widgetType === Constants.CustomFormWidgetType.SplitLine" :field="field"></splitLine>
      <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.SingleLineText
             || field.widgetType === Constants.CustomFormWidgetType.Number ||
              field.widgetType === Constants.CustomFormWidgetType.FullName" span="24">
        <van-cell
            title-class="van-cell-width50"
            label-class="van-cell-width50"
            :value="field.values && field.values[0]&& field.values[0].attrValue? field.values[0].attrValue : '-'">
          <template slot="title">
            <div>{{ field.label }}</div>
          </template>
          <template slot="right-icon"  v-if="field.rightUnit">
            <span style="color: #999999; margin-left: 3px">{{ field.rightUnit }}</span>
          </template>
        </van-cell>
      </van-col>

      <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.MultiLineText" span="24">
        <van-cell
            title-class="van-cell-width50"
            label-class="van-cell-width50"
            :value="field.values && field.values[0]&&field.values[0].attrValue ? field.values[0].attrValue : '-'">
          <!-- <template #title> -->
            <div slot="title" class="van-cell-div">{{ field.label }}</div>
          <!-- </template> -->
         <!-- <template #label>
            <div class="van-cell-label">{{ field.labelDesc }}</div>
          </template>-->
        </van-cell>
      </van-col>
      <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.Radio
             || field.widgetType === Constants.CustomFormWidgetType.CheckBox
             || field.widgetType === Constants.CustomFormWidgetType.DropdownSelect" span="24">
        <van-cell
            title-class="van-cell-width50"
            label-class="van-cell-width50">
            <span slot="title">{{ field.label }}</span>
            <!-- <span>{{field.values}}</span> -->
            <span>{{getFieldsResult(field.values, field.otherValue)||'-'}}</span>
          <!--<template #label>
            <div>{{ field.labelDesc }}</div>
          </template>-->
        </van-cell>
        <!-- 当 题目设置了级联题目时， 需要展示选项上绑定的 问题 -->
        <div v-for="(value, index) in field.values" :key="index">
          <viewItem :all-fields="value.questions"></viewItem>
        </div>
      </van-col>

      <!--头像-->
      <van-col v-if="field.exactType === Constants.FieldExactType.Avatar" span="24"
               style="text-align: center; background-color: #fff; height: 140px; padding-top: 30px">
        <img v-for="(v,k) in field.values" :src="v.attrValue" :key="k" alt class="med-img" @click="showImage(v.attrValue)"/>
        <div style="padding-top: 8px;">
          <span style="color: #333333; font-size: 14px; text-align: center;">头像</span>
        </div>
      </van-col>

      <!-- 图片上传 -->
      <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.SingleImageUpload" span="24">
        <van-cell
            title-class="van-cell-width50"
            label-class="van-cell-width50">
          <!-- <template #title> -->
            <div slot="title">{{ field.label }}</div>
          <!-- </template> -->
          <!--<template #label>
            <div>{{ field.labelDesc }}</div>
          </template>-->
        </van-cell>
        <van-image v-for="(v,k) in field.values" :key="k" :src="v.attrValue"  fit="contain" @click="showImage(v.attrValue)"/>
      </van-col>

      <!--图片上传查看-->
      <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.MultiImageUpload" span="24">
        <van-cell
            title-class="van-cell-width50"
            label-class="van-cell-width50">
          <!-- <template #title> -->
            <div slot="title">{{ field.label }}</div>
          <!-- </template> -->
          <!--<template #label>
            <div>{{ field.labelDesc }}</div>
          </template>-->
          <van-image width="70" height="70"  v-for="(v,k) in field.values" :src="v.attrValue" :key="k" fit="contain" @click="showImage(v.attrValue)"/>
        </van-cell>
      </van-col>

      <!-- 多级下拉 -->
      <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.MultiLevelDropdownSelect" span="24">
        <van-cell
            title-class="van-cell-width50"
            label-class="van-cell-width50">
          <!-- <template #title> -->
            <div slot="title">{{ field.label }}</div>
          <!-- </template> -->
          <span>{{getDropdownSelectResult(field.values)||'-'}}</span>
          <!--<template #label>
            <div>{{ field.labelDesc }}</div>
          </template>-->
        </van-cell>
      </van-col>
      <!-- 时间 -->
      <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.Time
                    || field.widgetType === Constants.CustomFormWidgetType.Date" span="24">
        <van-cell
            title-class="van-cell-width50"
            label-class="van-cell-width50">
          <!-- <template #title> -->
            <div slot="title">{{ field.label }}</div>
          <!-- </template> -->
          <span>{{field.values && field.values[0]&&field.values[0].attrValue ? field.values[0].attrValue : '-'}}</span>
         <!-- <template #label>
            <div>{{ field.labelDesc }}</div>
          </template>-->
        </van-cell>
      </van-col>
      <!-- 性别暂时没有特殊处理 -->
      <!-- 行政区划地址 -->
      <van-col v-if="field.widgetType === Constants.CustomFormWidgetType.Address" span="24">
        <van-cell
            title-class="van-cell-width50"
            label-class="van-cell-width50"
            :title="field.label">
          <!-- <template #title> -->
            <div slot="title">{{ field.label }}</div>
          <!-- </template> -->
          <span>{{getAddressResult(field.values, field)||'-'}}</span>
        </van-cell>
      </van-col>
    </van-col>
  </van-row>
</template>
<script>
import {ImagePreview,Row,Col,Cell,Image} from "vant";
export default {
    components:{
        [Row.name]:Row,
        [Col.name]:Col,
        [Cell.name]:Cell,
        [Image.name]:Image,
        attrDesc: () => import('@/components/arrt/descindex'),
        splitLine: () => import('@/components/arrt/splitLineindex'),
        viewItem: () => import('@/components/arrt/viewItem')
    },
   
  props: {
    allFields: {
      type: Array,
      default() {
        return []
      }
    }
  },
  data: function () {
    return {
      totalPage: 1, // 总页数
      currentIndex: 1, // 当前下标
      lastPage: false, // 是否最后一页
      currentFields: [], // 当前展示的元素
      fieldsMap: {}, // 根据分页组件 对元素表单进行分组
      pageField: [], // 分页组件
      pageNo: 0,
      currentPage: undefined // 当前分页
    };
  },
  watch: {
    allFields(newValue, oldValue) {
        // console.log(newValue)
        //  console.log(oldValue)
      this.initFields();
    }
  },
   mounted(){
    //    console.log(this.allFields)
        this.initFields();
    },
  methods: {
    // 多级下拉
    getDropdownSelectResult(values) {
      if (values && values.length > 0) {
        return values[values.length - 1].attrValue;
      } else {
        return '-';
      }
    },
    // 地址的选择
    getAddressResult(values, field) {
      if (values && values.length > 0) {
        const address = values[0].attrValue;
        if (address && address.length > 0) {
          const addressString = address.join('-');
          if (field.hasAddressDetail === 1) {
            return addressString + field.value;
          }
          return addressString;
        } else {
          return '-';
        }
      } else {
        return '-';
      }
    },
    // 普通的选择
    getFieldsResult(values, otherValue) {
        let backVal = ''
        // console.log(typeof values == 'object')
        // values = JSON.parse(values)
        if(typeof values !== 'object'){
            values = JSON.parse(values)
        }

      if (values && values.length > 0) {
        //   console.log(values)
        const result = [];
        
        for (let i = 0; i < values.length; i++) {
          let value;
          
          if (values[i].desc && values[i].desc.length > 0) {
            value = values[i].valueText + '(' + values[i].desc + ')';
          } else {
            value = values[i].valueText;
          }
          
          if (values[i].valueText === '其他') {
            if (otherValue && otherValue.length > 0) {
              value = values[i].valueText + '(' + otherValue + ')';
            } else {
              value = values[i].valueText;
            }
          } 
          result.push(value);
        }
        // console.log(result)
        if (result && result.length > 0) {
            backVal =  result.join('、')
        } else {
          backVal =  '-';
        }
      } else {
         backVal =  '-';
      }
        return backVal
    },
    initFields() {
      this.currentFields = [];
      if (this.allFields && this.allFields.length > 0) {
        const tempArray = [];
        for (let i = 0; i < this.allFields.length; i++) {
          if (this.allFields[i].widgetType !== this.Constants.CustomFormWidgetType.Page) {
            tempArray.push(this.allFields[i]);
          }
          if(typeof this.allFields[i].values !== 'object'){
              this.allFields[i].values = eval("(" + this.allFields[i].values + ")")
          }
        }
        this.currentFields = tempArray;
      }
    },
    hasOther(values) {
      let has = false;
      if (values && values.length > 0) {
        for (let i = 0; i < values.length; i++) {
          if (values[i].valueText === '其他') {
            has = true;
            break;
          }
        }
      }
      return has;
    },
    showImage(attrValue) {
      if (attrValue) {
        ImagePreview({images: [attrValue], closeable: true});
      }
    }
  },
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
