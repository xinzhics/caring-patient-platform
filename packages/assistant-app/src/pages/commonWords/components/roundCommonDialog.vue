<template>
  <div>
    <van-dialog v-model="show" :show-confirm-button="false">
      <div style="position: relative">
        <div class="title">请选择</div>
        <div style="position: absolute; top: 12px; right: 10px" @click="closeDialog"><van-icon name="cross" size="18" color="#707070" /></div>
      </div>
      <!--单选-->
      <div class="content">
        <div v-for="(item, index) in dataList" :key="index" @click="selectItem(index)"
             :class="pos == index ? 'select' : 'item'">
          <div>{{ item.typeName }}</div>
          <div v-if="pos == index"
               style="display: flex; align-items: center; justify-content: center; width: 14px; height: 14px; position: absolute; right:0; background-color: var(--caring-common-type-select-color); bottom: 0; border-radius: 3px 0 3px 0">
            <van-icon name="success" color="#fff" size="9" />
          </div>
        </div>
      </div>
      <div style="display: flex; justify-content: center; margin: 15px 0 20px 0">
        <van-button round type="info"
                    style="width: 160px; height: 35px; color: white; border-color: var(--caring-common-button-default-bg); background-color: var(--caring-common-button-default-bg)"
                    :style="{backgroundColor: pos > -1 ? 'var(--caring-common-button-default-bg)' : 'var(--caring-common-button-disable-bg)'}"
                    @click="selectConfirm()">
          确定
        </van-button>
      </div>
    </van-dialog>
  </div>
</template>

<script>

import Vue from 'vue'
import {Dialog} from 'vant'

Vue.use(Dialog)
export default {
  name: 'RoundDialog',
  props: {
    dataList: {
      type: Array,
      default () {
        return []
      }
    },
    title: {
      type: String,
      default () {
        return '请选择'
      }
    },
    // 是否多选， 默认单选
    isMultiple: {
      type: Boolean,
      default () {
        return false
      }
    }
  },
  data () {
    return {
      show: false,
      pos: -1,
      ids: ''
    }
  },
  methods: {
    // 多选时候判断是否选中
    isSelect (id) {
      if (!id && !this.ids) {
        // id 为空时候 并且 tagIds也为空，说明为全部患者
        return true
      }
      if (this.ids.includes(id + ',')) {
        return true
      } else {
        return false
      }
    },
    // 关闭弹窗
    closeDialog () {
      this.show = false
    },
    // 选择item
    selectItem (index) {
      this.pos = index
    },
    // 单选, 选择后按确定, 返回位置
    selectConfirm () {
      this.show = false
      this.$emit('selectConfirm', this.pos)
    },
    // 通过 refs 打开弹窗
    openDialog () {
      this.show = true
    },
    // 多选设置id
    setTagIds (tagIds) {
      this.ids = tagIds
    },
    // 单选位置
    setPos (pos) {
      this.pos = pos
    }
  }
}
</script>

<style lang="less" scoped>
.title{
  height: 50px;
  line-height: 50px;
  text-align: center;
  font-size: 18px;
  font-family: PingFang SC, PingFang SC;
  font-weight: 600;
  color: #333333;
}
.content {
  padding: 16px 16px 16px;
  max-height: 250px;
  display: flex;
  flex-flow: row wrap;
  background: #FFFFFF;
  overflow-y: scroll;
  scrollbar-width: none;
  border-top: 1px solid #E4E4E4;

  .item {
    max-width: 100%;
    margin: 0 15px 17px 0;
    border-radius: 6px;
    border: 1px solid var(--caring-common-no-select);
    min-width: 80px;
    text-align: center;
    height: 35px;
    line-height: 35px;
    padding: 0 12px;
    font-size: 14px;
    color: var(--caring-common-no-select-text);
    div {
      overflow: hidden; //超出的文本隐藏
      text-overflow: ellipsis; //溢出用省略号显示
      white-space: nowrap; // 默认不换行；
    }
  }

  .select {
    max-width: 100%;
    margin: 0 15px 17px 0;
    border-radius: 6px;
    min-width: 80px;
    text-align: center;
    height: 35px;
    line-height: 35px;
    padding: 0 12px;
    font-size: 14px;
    color: var(--caring-common-type-select-color);
    border: 1px solid var(--caring-common-type-select-color);
    display: flex;
    position: relative;
    justify-content: center;
  }
}
</style>
