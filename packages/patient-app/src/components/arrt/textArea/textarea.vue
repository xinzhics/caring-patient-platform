<template>
  <div class="box1" v-if="field">
    <div class="title">
      <span class="username">{{field.label}}</span>
      <span class="mustchoose" v-if="field.required">*</span>
    </div>
    <div class="depict">{{field.labelDesc}}</div>
    <div class="userinfo">
      <van-field v-model="field.values[0].attrValue"
                 @click-input="isCanClikc" :readonly="noClick"
                 :ref="field.id"
                 @input="change"
                 @blur="fieldInput"
                 show-word-limit
                 :class="[show===true?'cell2':'',field.values[0].attrValue&&field.values[0].attrValue.length>=field.maxEnterNumber?'maxTextNumber':'']"
                 :error="show"
                 :maxlength="field.maxEnterNumber"
                  :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"
                 :placeholder="field.placeholder" type="textarea" />
    </div>
  </div>
</template>
<style lang='less' scoped  src="./textarea.less">
</style>
<script src="./index.js">
</script>
