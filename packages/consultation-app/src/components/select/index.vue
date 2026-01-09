<template>
  <div>
    <van-field v-model="showText" :border="false" :placeholder="placeholder||'请选择'" readonly
               :error-message-align="errorMessageAlign" :error-message='errorMessage' @click="show = !show"/>
    <van-popup v-model="show" position="bottom">
      <van-picker
          type="address"
          :columns="columns"
          show-toolbar
          :title="$attrs.label"
          value-key="name"
          @confirm="onConfirm"
          @cancel="show = !show"
      />
    </van-popup>
  </div>
</template>

<script>
import {parseTime} from "@/components/utils/index.js";
import {Popup ,Picker ,Field } from "vant";
export default {
  components:{
      [Popup.name]:Popup,
      [Picker.name]:Picker,
      [Field.name]:Field,
  },
  props: {
    placeholder: {
      type: String
    },
    columns: {},
    label: {},
    defaultInd: {},
    selVal: {},
    value: {},
    minDate: {},
    maxDate: {},
    currentDate: {},
    handlerResult: {}, // 处理选中的结果，选中的值需要经过此方法处理后展示
    surecb: {}, // 成功回调
    type: {},
    noEmit: {},
    errorMessageAlign: {
      type: String
    },
    errorMessage: {
      type: String
    },
    name: ''
  },
  data() {
    return {
      show: false,
      sourceData: {},
      selInd: -1,
      randomNum: "a" + Math.ceil(Math.random() * 10000),
      selText: "",
      resultData:
          this.selVal && typeof this.selVal == "object"
              ? JSON.parse(JSON.stringify(this.selVal))
              : this.selVal
    };
  },
  methods: {
    onConfirm(value, ind) {
      
      this.selInd = ind;
      if (this.handlerResult) {
        this.resultData = this.handlerResult(value);
      } else {
        this.resultData = value 
      }
      if (this.type == "date") {
        this.resultData = Date.parse(this.resultData);
      }
      if (this.surecb) {
        this.nextTick(() => {
          this.surecb(this.resultData, this.sourceData, this.selInd);
        });
      }
      // this.errorMessage = '';
      this.show = !this.show;
      // console.log(this.resultData)
    },
    setInitResult() {
      // console.log(this.value)
      if (!this.type && this.columns && this.columns.length > 0) {
       
        if (!this.resultData) {
          // 处理父组件异步传入数据时result未被赋值的问题
         
          this.resultData =
              this.value && typeof this.value == "object"
                  ? JSON.parse(JSON.stringify(this.value))
                  : this.value;
        }
        
        if (this.resultData) {
          
          for (let i = 0, j = this.columns.length; i < j; i++) {
            if (typeof this.columns[i] == "string") {
              if (this.columns[i] == this.resultData) {
                this.selInd = i;
                break;
              }
            } else {
              if (this.columns[i].id === this.resultData.attrValue) {
                this.selInd = i;
                break;
              }
            }
          }
          
          if (this.selInd >= 0) {
            this.$set(
                this,
                "resultData",
                JSON.parse(JSON.stringify(this.columns[this.selInd]))
            );
          }
          this.$set(
              this,
              "showText",
              this.resultData.text || this.resultData.valueText || this.resultData
          );
        }
      }
    },
    initSourceData() {
      if (this.selVal && typeof this.selVal == "object")
        this.sourceData = this.selVal;
    }
  },
  mounted() {
    
    this.setInitResult();
    this.initSourceData();
  },
  computed: {
    showText: {
      set(value) {
        this.selText = value;
      },
      get() {
        this.selText =
            this.resultData && typeof this.resultData == "object" && !this.type
                ? this.resultData["text"] || this.resultData["valueText"]
                : !this.type
                ? this.resultData
                : this.type == "date"
                    ? this.resultData
                        ? parseTime(this.resultData, "{y}-{m}-{d}")
                        : ""
                    : this.resultData;
        return this.selText;
      }
    },
  },
  watch: {
    columns() {
      this.setInitResult();
    },
    resultData(newVal) {
      if (newVal && this.type == "date") {
        newVal = parseTime(newVal, "{y}-{m}-{d}");
      }
      if (this.noEmit !== "") {
        this.$emit("input", newVal);
      } else {
        
        this.surecb && this.surecb(newVal, this.sourceData, this.selInd);
      }
    }
  }
};
</script>
<style scoped lang="less">


.van-field {
  padding: 0;
  padding-right: 20px;
  text-align: right;
  font-size: 14px;
  color: #333333;
}

/deep/ .van-field__control {
  text-align: right !important;
}

/deep/ .error-msg {
  color: #f44;
  font-size: 12px;
  text-align: right;
  margin-right: 15px;
}


/deep/ .van-cell__right-icon {
  margin-left: 5px;
  color: #969799;
  margin-right: 10px;
  border-bottom: 0px solid #FFFFFF;
}

/deep/ .van-cell::after {
  border-bottom: 0px solid #ebedf0;
}

/deep/ .van-field {
  padding-right: 0px;
}

</style>
