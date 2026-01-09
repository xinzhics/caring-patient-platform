<template>
  <div class="box1">
    <div class="title">
      <span class="username">{{field.label}}</span>
      <span class="mustchoose" v-if="field.required===true">*</span>
    </div>
    <div class="depict">{{field.labelDesc}}</div>
    <div>
      <!-- 必选 -->
      <div v-if="field.required===true">
        <van-cell v-if="field.values[0].attrValue" ref="color" :value='field.values[0].attrValue' :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}" />
        <van-cell v-if="!field.values[0].attrValue" :value="field.placeholder" ref="color" :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}" />
      </div>
      <!-- 非必选 -->
      <div v-if="field.required!==true">
        <van-cell v-if="field.values[0].attrValue" :value='field.values[0].attrValue':style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"  />
        <van-cell v-if="!field.values[0].attrValue" :value="field.placeholder" :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}" />
      </div>
    </div>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped  src="./index.less">
</style>
