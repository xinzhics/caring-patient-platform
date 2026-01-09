<template>
  <section style="background:#FAFAFA" >
    <van-row align="center" style="height:50px" class="headTitle">
      <van-col offset="1" span="9">
        <van-icon name="arrow-left" @click="hide"/>
      </van-col>
      <van-col span="12">选择医院</van-col>
    </van-row>

    <van-search
  v-model="hospitalName"
  placeholder="搜索"
  @input="onInput"
  @blur="onBlur"
  @focus="onFocus"
   @search="onSearch"
  show-action
>
  <div slot="action" @click="onSearch">
    <van-button type="info" @click="onSearch">搜索</van-button>
  </div>
</van-search>

    <!-- 三个选项 -->

    <van-row type="flex" justify="space-between" align="center" style="height:48px"  v-show="type !== 4">
      <van-col offset="2" @click="chooseOptions(1)" span="8" :style="{'color': (type >= 1  ?'#337EFF':'')}">省份
        <van-icon name="arrow-down" :style="{'color': (type >= 1 ? '#337EFF':'')}"/>
      </van-col>
      <van-col span="8" @click="chooseOptions(2)" :style="{'color': (type >= 2 ? '#337EFF':'')}">城市
        <van-icon name="arrow-down" :style="{'color': (type >= 2 ? '#337EFF':'')}"/>
      </van-col>
      <van-col span="6" @click="chooseOptions(3)" :style="{'color': (type === 3 ? '#337EFF':'')}">医院
        <van-icon name="arrow-down" :style="{'color': (type === 3 ? '#337EFF':'')}"/>
      </van-col>
    </van-row>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
       v-if="focus"
        v-model="loading"
        :finished="finished"
        :finished-text="focus?'没有更多了':''"
        @load="onLoad">
          <div class="showData" v-for="(i,index) in list " :key="index">
            <div class="options" @click="chooseOption(i)">{{ getItem(i) }}</div>
          </div>
      </van-list>
    </van-pull-refresh>


  </section>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
</style>
