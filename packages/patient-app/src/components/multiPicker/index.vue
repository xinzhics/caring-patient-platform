<template>
  <div style="padding-top: 15px; background: #FFFFFF; padding-bottom: 15px">
    <van-cell :required="!!required" :title="title" is-link>
        <van-field
            v-model="showText"
            :placeholder="placeholder||'请选择'"
            :error-message-align="errorMessageAlign"
            :border="false"
            :error-message="errorMessage"
            readonly
            @click="show = !show"
        />
    </van-cell>
    <van-popup v-model="show" position="bottom">
      <van-picker
          show-toolbar
          ref="multiPicker"
          :columns="initColumns"
          @change="onChange"
          @confirm="onConfirm"
          @cancel="show = !show"
      />
    </van-popup>
  </div>
</template>
<script>
import {isNotEmptyArr} from "@/components/utils/index.js";
import {Popup ,Cell,Picker ,Field } from "vant";
export default {
  name: "multi-picker",
  components:{
      [Popup.name]:Popup,
      [Picker.name]:Picker,
      [Field.name]:Field,
      [Cell.name]:Cell
  },
  props: {
    title: {
      type: String
    },
    placeholder: {},
    columns: {
      type: Array
    },
    value: {},
    sepSign: {}, // 如需展示多级内容，请传入分隔符
    surecb: {
      type: Function
    },
    textKey: {
      default: "text"
    },
    errorMessage: {
      type: String
    },
    infoValueChange: {
      type: Function
    },
    errorMessageAlign: {
      type: String
    },
    required: {},
    name: ''
  },
  data() {
    return {
      selArr: [],
      show: false,
      isInit: false,
      deep: 1,
      showColumns: [],
      refs: "multiPicker",
      selectId: '',
      flag: true
    };
  },
  methods: {
    onConfirm() {
      this.show = false;
      if (isNotEmptyArr(this.selArr)) {
        let tempArr = [this.columns[this.selArr[0]]];
        for (let i = 1; i < this.selArr.length; i++) {
          if (tempArr[i - 1] && tempArr[i - 1].children) {
            tempArr[i] = tempArr[i - 1].children[this.selArr[i]];
          } else {
            break;
          }
        }
        tempArr = tempArr.trim();
        this.$emit("input", tempArr);
      }

      this.show = false;
    },
    onChange(picker, values) {
      this.selArr = this.$refs.multiPicker.getIndexes();
      for (let i = 1; i < this.selArr.length; i++) {
        this.initSingleLineColumns(i, 1);
      }
      this.infoValueChange();
    },
    getArrayDeep(options, deep) {
      if (!Array.isArray(options)) {
        return deep;
      } else {
        let idDeep = false;
        let optionsChild = [];
        options.forEach(ele => {
          if (Array.isArray(ele.children) && ele.children.length > 0) {
            idDeep = true;
            optionsChild = optionsChild.concat(ele.children);
          }
        });
        if (idDeep) {
          deep++;
          if (deep > 3) {
            deep = 3;
          } else {
            deep = this.getArrayDeep(optionsChild, deep);
          }
        }
        return deep;
      }
    },
    initPickColumns(ind) {
      this.deep = this.getArrayDeep(this.columns, 1);
      this.showColumns = new Array(this.deep);
      for (let i = 0; i < this.deep; i++) {
        this.initSingleLineColumns(i, 1);
      };
      return this.showColumns;
    },
    initSingleLineColumns(ind, fromInit) {
      let data = JSON.parse(JSON.stringify(this.columns));
      for (let i = 1; i <= ind; i++) {
        if (data[this.selArr[i - 1]]) {
          data = data[this.selArr[i - 1]].children || [];
        } else {
          data = [];
          break;
        }
      }
      this.$set(this.showColumns, ind, {
        values: this.initValues(data),
        className: "column" + ind,
        defaultIndex: this.selArr[ind]
      });
    },
    initValues(arr) {
      if (isNotEmptyArr(arr)) {
        let tempArr = [];
        arr.forEach(ele => {
          tempArr.push(ele.attrValue);
        });
        return tempArr;
      } else {
        return [];
      }
    },
    initShowText(userSel) {
      if (this.sepSign) {
        let text = "";
        this.value.forEach((ele, i) => {
          if (i === 0) {
            text = ele.attrValue
          } else {
            text += this.sepSign + ele.attrValue
          }
        });
        return text;
      } else {
        let attrValue = this.value[this.value.length - 1]['attrValue'];
        let valueText = this.value[this.value.length - 1]['valueText'];
        return valueText ? valueText : attrValue;
      }
    },
    initSelArr: function () {
      let self = this;
      this.selArr = [0, 0, 0];
      if (this.selectId && this.selectId.length > 0) {

        this.dafultIndex(self.columns, self, 0);
      }
    },
    dafultIndex: function (array, self, deep) {
      if (self.flag && array && array.length > 0) {
        for (let i = 0; i < array.length && self.flag; i++) {
          const ele = array[i];
          if (ele.children && ele.children.length > 0) {
            self.selArr[deep] = i;
            self.dafultIndex(ele.children, self, deep + 1);
          } else {
            if (self.selectId == ele.id) {
              self.selArr[deep] = i;
              self.flag = false;
              break;
            } else {
              if (array.length - 1 == i) {
                self.selArr.length = deep;
              }
            }
          }
        }
      }
    }

  },
  computed: {
    showText() {
      if (isNotEmptyArr(this.value)) {
        return this.initShowText(); // 初始化选中文字
      } else {
        return '';
      }
    },
    initColumns() {
      if (isNotEmptyArr(this.columns)) {
        return this.initPickColumns();
      }
    }
  },
  mounted: function () {
    if (this.value && this.value.length > 0) {
      let element = this.value[this.value.length - 1]['attrValue'];
      if (element) {
        /** ID类型 */
        this.selectId = element;
      } else {
        /** 值类型 */
        this.selectId = this.value;
      }
    }
    this.initSelArr();
  }
};
</script>
<style lang="less" scoped>
/deep/ .van-field__control {
  text-align: right !important;
}

/deep/ .van-cell {
  padding-top: 0px;
  padding-bottom: 0px;
  padding-right: 0px;
  padding-left: 16px;
  border-bottom: 1px solid #FFFFFF;
  color: #333333;
  font-size: 14px;
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

</style>
