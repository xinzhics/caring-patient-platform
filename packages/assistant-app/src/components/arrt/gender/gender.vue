<template>
  <div class="main">
    <div class="title">
      <span class="name">{{field.label}}</span>
      <span class="mustchoose" v-if="field.required">*</span>
      <div class="describe">{{field.labelDesc}}</div>
    </div>
    <div class="gender" :class="showred === true ? 'showred':''">
      <div class="man"
        :class="[selectOption.id === info.id?'man2 ':'',field.modifyTags &&selectOption.id === info.id?'man3':'']"
        v-for="(info,index) in field.options" :key="index" @click="choosesex(info, index)">
        <div>{{info.attrValue}}</div>
        <div class="icon" v-if="selectOption.id === info.id">
          <van-icon name="success" color="#6F7D97" />
        </div>
      </div>
    </div>
  </div>
</template>

<script src="./index.js">
</script>
<style lang="less" scoped src="./index.less">
</style>
