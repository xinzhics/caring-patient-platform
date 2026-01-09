<template>
  <van-form>
    <div
        class="entry-group-baseinfo"
        v-for="(info,index) in question " :key="index">
      <!--单行文本-->
      <div
          class="bot-border"
          v-if="info.widgetType === Constants.CustomFormWidgetType.SingleLineText || info.widgetType === Constants.CustomFormWidgetType.FullName">
        <van-field
            v-model="info.values[0].attrValue"
            :placeholder="'请输入'+info.placeholder"
            :name="info.label"
            :label="info.label"
            :required='info.required'
            style="padding: 13px 16px;"
            error-message-align="right"
            @change="() => infoValueChange(info)"
            :error-message="info.errorMessage"
            :maxlength="info.max ? info.max : 450">
          <div slot="button" v-if="info.rightUnit">{{ info.rightUnit }}</div>
        </van-field>
        <div class="desc" v-if="info.labelDesc">{{ info.labelDesc }}</div>
      </div>

      <!--单选框-->
      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio'>
        <div class='bot-border'>
          <van-field :label="info.label" readonly :required="info.required"></van-field>
          <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
          <van-field :error-message="info.errorMessage" error-message-align="right">
            <template slot="input">
              <van-radio-group v-model="info.values[0].attrValue" class="attr-radio-group" @change="(value) => radioClick(value, info)">
                <van-row v-for="(option,k) in info.options" :key="k">
                  <van-col span="24">
                    <van-radio class="radio-item-padding option-word-wrap" :name="option.id">
                      {{ option.attrValue }}
                    </van-radio>
                  </van-col>
                </van-row>
              </van-radio-group>
            </template>
          </van-field>
        </div>
      </div>
      <!-- 下拉框 -->
      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.DropdownSelect'>
        <div class='bot-border'>
          <van-row>
            <van-col span="24" style="min-height: 0 !important;">
              <van-cell
                      title-class="van-cell-width50"
                      label-class="van-cell-width50"
                      :required='info.required'
                      :clickable='true'>
                <template slot="title">
                  <div>{{ info.label }}</div>
                </template>
                <van-dropdown-menu class="van-dropdown-menu__bar--opened">
                  <van-dropdown-item v-model='info.values[0].attrValue'
                                     :options='resolveOptions(info.options)'
                                     :closed='itemChage(info.values, info.options, info)'/>
                </van-dropdown-menu>
              </van-cell>
            </van-col>
            <van-col span="24" style="min-height: 0 !important;" v-if="info.errorMessage">
              <div class="desc" style="background-color: white !important; color: red !important; text-align: right">请选择选项</div>
            </van-col>
            <van-col span="24" style="min-height: 0 !important;">
              <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
            </van-col>
          </van-row>
        </div>
      </div>

      <!--复选框-->
      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.CheckBox'>
        <div class='bot-border'>
          <van-field :label="info.label" readonly :required="info.required"></van-field>
          <div class='desc' v-if='info.labelDesc'>{{ info.labelDesc }}</div>
          <van-field :error-message="info.errorMessage"  error-message-align="right">
            <template slot="input">
              <div>
                <van-row v-for="(option,k) in info.options" :key="k">
                  <van-col span="24">
                    <van-checkbox :name="option.id" v-model="option.select" @click="(event) => checkBoxClick(event, option, info)"
                                  shape="square" class="radio-item-padding option-word-wrap" checked-color="#1890FF">{{ option.attrValue }}</van-checkbox>
                  </van-col>
                </van-row>
              </div>
<!--              <van-checkbox-group v-model="info.checkBoxValues" @change="(value) => checkBoxChange(info)">-->
<!--                <van-row v-for="(option,k) in info.options" :key="k">-->
<!--                  <van-col span="24">-->
<!--                    <van-checkbox :name="option.id" v-model="option.select" @click="(event) => checkBoxClick(event, option, info)"-->
<!--                                  shape="square" class="radio-item-padding option-word-wrap" checked-color="#1890FF">{{ option.attrValue }}</van-checkbox>-->
<!--                  </van-col>-->
<!--                </van-row>-->
<!--              </van-checkbox-group>-->
            </template>
          </van-field>
        </div>
      </div>

      <!--数字   是否為必填-->
      <div
          class="bot-border"
          v-if="info.widgetType == Constants.CustomFormWidgetType.Number">
        <van-field
            v-model="info.values[0].attrValue"
            :placeholder="'请输入'+info.placeholder"
            :name="info.label"
            :label="info.label"
            type="number"
            style="padding: 13px 16px;"
            :required='info.required'
            @change="() => infoValueChange(info)"
            :error-message="info.errorMessage"
            error-message-align="right"
            :maxlength="info.max ? info.max : 450">
          <div slot="button" v-if="info.rightUnit">{{ info.rightUnit }}</div>
        </van-field>
        <div class="desc" v-if="info.labelDesc">{{ info.labelDesc }}</div>
      </div>

      <!--图片上传-->
      <div
          class="bot-border"
          v-else-if="info.widgetType === Constants.CustomFormWidgetType.SingleImageUpload || info.widgetType === Constants.CustomFormWidgetType.MultiImageUpload || info.exactType === Constants.FieldExactType.Avatar"
      >
        <div>
          <lano-uploader
              :ref="info.label+Date.parse(new Date())"
              v-model="info.values"
              :label="info.label"
              accept="image/*"
              keyName="attrValue"
              :max="info.max"
              :min="info.min"
              error-message-align="right"
              :required="info.required"
              :error-message='info.required && info.values && info.values.length > 0 && info.values[0].attrValue.length > 0 ? "" : info.errorMessage'
          />
          <div class="desc" v-if="info.labelDesc">{{ info.labelDesc }}</div>
        </div>
      </div>

      <!--日期选择器-->
      <div class="bot-border"
           v-else-if="info.widgetType === Constants.CustomFormWidgetType.Date || info.exactType === Constants.FieldExactType.Birthday">
        <van-field
                readonly
                clickable
                name="datetimePicker"
                :value="info.values[0].attrValue"
                :required="info.required"
                :label="info.label"
                :placeholder="info.placeholder"
                right-icon="arrow"
                @change="() => infoValueChange(info)"
                @click="openPicker(info)"
                error-message-align="right"
                :error-message='info.errorMessage'
        />
        <van-popup v-model="info.showPicker" position="bottom">
          <van-datetime-picker
                  type="date"
                  :min-date="getMinDate(info)"
                  :max-date="getMaxDate(info)"
                  :value="getDatePickerValue(info)"
                  :columns-order="['year', 'month', 'day']"
                  :formatter="formatterDate"
                  @confirm="(time) => onDateConfirm(time, info)"
                  @cancel="cancelPicker(info)"
          />
        </van-popup>
        <div v-if='info.labelDesc' class='desc'>{{ info.labelDesc }}</div>
      </div>
      <!--多行文本-->
      <div
          class="bot-border"
          v-else-if="info.widgetType === Constants.CustomFormWidgetType.MultiLineText">
        <van-field
            style="padding: 13px 16px;"
            v-model="info.values[0].attrValue"
            rows="1"
            :label="info.label"
            :name="info.label"
            autosize
            type="textarea"
            :required='info.required'
            @change="() => infoValueChange(info)"
            error-message-align="right"
            :placeholder="'请输入'+info.label"
            :maxlength="info.max ? info.max : 450"
            :error-message="info.errorMessage"
        />
        <div v-if="info.labelDesc" class="desc">{{ info.labelDesc }}</div>
      </div>
    </div>
  </van-form>
</template>

<script>
  import {parseTime} from "@/components/utils/index";
import {ImagePreview, Popup, DatetimePicker, Row,Col,Cell,Form,Button,Field,DropdownMenu, DropdownItem,RadioGroup, Radio,Checkbox, CheckboxGroup} from "vant";
export default {
  name: "index-item",
  props: {
    question: {
      type: Array
    }
  },
   components:{
      [Row.name]:Row,
      [Col.name]:Col,
      [Cell.name]:Cell,
      [Form.name]:Form,
      [Button.name]:Button,
      [Field.name]:Field,
      [DropdownMenu.name]:DropdownMenu,
      [RadioGroup.name]:RadioGroup,
      [Radio.name]:Radio,
      [DropdownItem.name]:DropdownItem,
      [Checkbox.name]:Checkbox,
      [CheckboxGroup.name]:CheckboxGroup,
      [DatetimePicker.name]:DatetimePicker,
      [Popup.name]:Popup,
      selectPicker: () => import("@/components/select/index"),
      lanoUploader: () => import("@/components/upload/index")
   },
  created() {
      console.log('created', this.question)
      if (this.question) {
          for (let i = 0; i < this.question.length > 0; i++) {
              if (this.question[i].widgetType === this.Constants.CustomFormWidgetType.Date) {
                this.question[i].showPicker = false
              }
          }
      }
  },
  methods: {
    radioClick(id, info) {
      let option = undefined;
      for (let i = 0; i < info.options.length; i++) {
        if (id === info.options[i].id) {
          info.options[i].desc = '';
          option = info.options[i];
        }
      }
      info.values[0].valueText = option.attrValue;
      info.values[0].questions = option.questions;
      info.errorMessage = '';
      this.$forceUpdate();
    },
    checkBoxChange(info) {
      if (info.required && info.checkBoxValues && info.checkBoxValues.length === 0) {
        info.errorMessage = info.placeholder;
      } else {
        info.errorMessage = '';
      }
    },
    checkBoxClick(event, option, info) {
      if (option.select) {
        const val = { valueText: option.attrValue, attrValue: option.id };
        if (info.values && info.values.length === 1) {
          if (!info.values[0].attrValue || info.values[0].attrValue.length === 0) {
            info.values = [];
          }
        }
        info.values.push(val);
      } else {
        for (let i = 0; i < info.values.length; i++) {
          if (info.values[i].attrValue === option.id) {
            info.values.splice(i, 1);
          }
        }
      }
      this.$forceUpdate();
    },
    infoValueChange(info) {
      if (!info.required) {
        info.errorMessage = '';
        return;
      }
      if (info.values && info.values[0] && info.values[0].attrValue && info.values[0].attrValue.length > 0) {
        info.errorMessage = '';
      } else {
        if (info.placeholder) {
          info.errorMessage = info.placeholder;
        }
      }
    },
    /**
     * 单选
     * */
    itemChage(values, options, info) {
      options.forEach(ele => {
        if (null != values[0].attrValue && values[0].attrValue === ele.id) {
          values[0].valueText = ele.attrValue;
          values[0].attrValue = ele.id;
        }
      });
      if (values[0] && values[0].attrValue) {
        info.errorMessage = '';
      }
    },

    // 设置日期选项器的打开的最小时间
    getMinDate(info) {
      if (info.defineChooseDate === 1 && info.defineChooseDateType !== 1) {
        return new Date();
      } else {
        let year = new Date().getFullYear();
        year = year - 100;
        return new Date(year + '/' + '01/' + '01');
      }
    },
    // 设置日期选择器的打开的最大时间
    getMaxDate(info) {
      if (info.defineChooseDate === 1 && info.defineChooseDateType !== 2) {
        return new Date();
      } else {
        // 获取一个 100年后的时间
        let year = new Date().getFullYear();
        year = year + 100;
        return new Date(year + '/' + '12/' + '31');
      }
    },
    // 设置日期选择器打开时选择的的时间
    getDatePickerValue(info) {
      if (info.values && info.values[0] && info.values[0].attrValue) {
        const dateStr = info.values[0].attrValue.replace(/-/g, '/');
        return new Date(dateStr);
      } else {
        if (info.defaultValue) {
          return new Date(info.defaultValue);
        } else {
          return new Date();
        }
      }
    },
    formatterDate(type, val) {
      if (type === 'year') {
        return val;
      }
      if (type === 'month') {
        return val;
      }
      if (type === 'day') {
        return val;
      }
      return val;
    },
    // 关闭 时间选择器
    cancelPicker(info) {
      info.showPicker = false;
      this.$forceUpdate();
    },
    // 打开时间选择器
    openPicker(info) {
      info.showPicker = true;
      this.$forceUpdate();
    },
    onDateConfirm(date, info) {
      const val = parseTime(date, "{y}-{m}-{d}");
      console.log('onDateConfirm', val);
      info.values[0].valueText = val;
      info.values[0].attrValue = val;
      info.showPicker = false;
      this.$forceUpdate();
    },
    // 确定选择时间
    onConfirm(time, info) {
      info.values[0].valueText = time;
      info.values[0].attrValue = time;
      info.showPicker = false;
      this.$forceUpdate();
    },
    resolveOptions(options) {
      if (!options) {
        return [];
      }
      options.forEach(ele => {
        ele.text = ele.attrValue;
        ele.value = ele.id;
        ele.key = ele.key;
      });
      return options;
    }
  },
};
</script>
<style lang="less" scoped>

.single {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
  display: flex;
  position: absolute;
  padding-left: 16px;
  height: 100%;
  width: 28%;
  font-size: 14px;
  padding-top: 15px;
  color: #5f5f5f;
}

.desc {
  color: #6f6f6f;
  font-size: 12px;
  background: #FFFFFF;
  padding: 0px 15px 10px 15px;
}

::v-deep .van-dropdown-menu {
  display: flex;
  font-size: 14px
}

::v-deep .van-dropdown-menu__bar {
  width: 100%;
  background: #FFFFFF;
  box-shadow: 0 0px rgba(100, 101, 102, .08);
}

::v-deep .van-dropdown-menu__item {
  justify-content: flex-end;
  padding-right: 20px;
  display: flex;
}

::v-deep .van-dropdown-menu van-hairline--top-bottom {
  border-width: 0px 0;
}

::v-deep .van-dropdown-menu__title {
  font-size: 14px;
  max-width: 60%;
}


::v-deep .van-field__control {
  text-align: right;
  color: #0e0e0e;
}

.bot-border {
  position: relative;
  border-bottom: 0.5px solid #f5f5f5;

  &:after {
    position: absolute;
    box-sizing: border-box;
    content: " ";
    pointer-events: none;
    top: -50%;
    right: -50%;
    bottom: -50%;
    left: -50%;;
    // border-bottom: 1px solid #ebedf0;
    -webkit-transform: scale(0.5);
    transform: scale(0.5);
  }
}

.submit-btn {
  width: 60%;
  border-radius: 6px;
  margin: 20px auto;
  display: block;
}

.entry-group-baseinfo {
  ::v-deep .van-cell--clickable {
    background-color: #ffffff;
  }
}

::v-deep .van-cell::after {
  border-bottom: 0px solid #ebedf0;
}

</style>
