<template>
  <div class="single-line">
    <div class="box1">
      <div class="title">

        <span class="username">{{field.label}}</span>
        <span class="mustchoose" v-if="field.required">*</span>
      </div>
      <div class="depict">{{field.labelDesc}}</div>
      <!-- 必填单行文本 -->
      <div class="userinfo">
        <van-field :ref="field.id" @click-input="isCanClick"  :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"
          :disabled="!field.isUpdatable || field.isUpdatable !== 1" :error="show"
          :class="[show===true?'cell2':'']" @input="change" v-model="field.values[0].attrValue" @blur="fieldInput"
          show-word-limit :maxlength="field.maxEnterNumber" :placeholder="field.placeholder"
          />

      </div>
    </div>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./field-Singleline.less">
</style>
