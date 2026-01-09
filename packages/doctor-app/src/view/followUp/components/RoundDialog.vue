<template>
  <div>
    <van-action-sheet v-model="show" :title="title" :round="false">

      <!--多选-->
      <div class="content" v-if="isMultiple">
        <div v-for="(item, index) in dataList" :key="index" @click="selectItem(index)"
             :class="isSelect(item.id) ? 'select' : 'item'">
          <div>{{ item.name }}</div>
          <img v-if="isSelect(item.id)" :src="require('@/assets/my/follow_task_flow_select_icon.png')"
               style="width: 11px; height: 11px; position: absolute; right: 0.3px; top: 0.3px"/>
        </div>
      </div>

      <!--单选-->
      <div class="content" v-else>
        <div v-for="(item, index) in dataList" :key="index" @click="selectItem(index)"
             :class="pos == index ? 'select' : 'item'">
          <div>{{ item.showName }}</div>
          <img v-if="pos == index" :src="require('@/assets/my/follow_task_flow_select_icon.png')"
               style="width: 11px; height: 11px; position: absolute; right:0px; top: 0px"/>
        </div>
      </div>

      <div style="display: flex; justify-content: center">
        <van-button round type="info" style="height: 40px; width: 180px; margin-bottom: 25px" @click="selectConfirm()">
          确定
        </van-button>
      </div>
    </van-action-sheet>
  </div>
</template>

<script>

import Vue from 'vue';
import {ActionSheet} from 'vant';

Vue.use(ActionSheet);
export default {
  name: "RoundDialog",
  props: {
    dataList: {
      type: Array,
      default() {
        return []
      }
    },
    title: {
      type: String,
      default() {
        return "请选择";
      }
    },
    //是否多选， 默认单选
    isMultiple: {
      type: Boolean,
      default() {
        return false;
      }
    },
  },
  data() {
    return {
      show: false,
      pos: -1,
      ids: '',
    }
  },
  methods: {
    //多选时候判断是否选中
    isSelect(id) {
      if (!id && !this.ids) {
        //id 为空时候 并且 tagIds也为空，说明为全部患者
        return true
      }
      if (this.ids.includes(id + ',')) {
        return true
      }else {
        return false
      }
    },
    //选择item
    selectItem(index) {
      if (this.isMultiple) {
        //多选
        if (index == 0) {
          this.$set(this, 'ids', '')
        }else{
          if (this.ids.includes(this.dataList[index].id + ',')) {
            //包含id,则去掉选中
            this.$set(this, 'ids', this.ids.replace(this.dataList[index].id + ',', ''))
          }else{
            //不包含id， 则选中
            this.$set(this, 'ids', this.ids + this.dataList[index].id + ',')
          }
        }
      } else {
        this.pos = index
      }
    },
    //单选, 选择后按确定, 返回位置
    selectConfirm() {
      this.show = false
      this.$emit('selectConfirm', this.isMultiple ? this.ids : this.pos)
    },
    //通过 refs 打开弹窗
    openDialog() {
      this.show = true
    },
    //多选设置id
    setTagIds(tagIds) {
      this.ids = tagIds
    },
    //单选位置
    setPos(pos) {
      this.pos = pos
    },
  }
}
</script>

<style lang="less" scoped>

.content {
  padding: 16px 16px 16px;
  max-height: 250px;
  display: flex;
  flex-flow: row wrap;
  //justify-content: space-between;
  background: #FFFFFF;
  overflow-y: scroll;
  scrollbar-width: none;
  border-top: 1px solid #E4E4E4;

  .item {
    max-width: 100%;
    background: #F5F5F5;
    margin: 0 15px 17px 0;
    border-radius: 6px;
    border: 1px solid #F5F5F5;
    min-width: 80px;
    text-align: center;
    height: 35px;
    line-height: 35px;
    padding: 0 10px;
    font-size: 14px;
    color: #666666;
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
    padding: 0 10px;
    font-size: 14px;
    background: #F0F5FF;
    color: #3F86FF;
    border: 1px solid #B7D1FF;
    display: flex;
    position: relative;
    justify-content: center;
  }
}
</style>
