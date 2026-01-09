<template>
  <div class="box1">
    <div class="title">
      <span class="username">{{field.label}}</span>
      <span class="mustchoose" v-if="field.required">*</span>
    </div>
    <div class="depict">{{field.labelDesc}}</div>
    <!-- 只有一张图 -->
    <div class="pic" v-if="(field.uploadMany === 1&& field.uploadNumber === 1)||field.uploadMany !== 1">
      <van-uploader :class="[showred?'picc3':'picc1']" v-model="fileList" :disabled="disabled()" :before-delete="beforeDelete" :max-count="1" upload-text="点此上传" :after-read="afterRead" />
    </div>

    <!-- 可以上传两张图 -->
    <div class="pic2" v-if="field.uploadMany === 1 && field.uploadNumber === 2">
      <!-- 第一张图 -->
      <div class="picc">
        <van-uploader :class="[showred?'picc2':'']" v-model="fileList"
                      :disabled="disabled()" @click-upload="isCanClick"
                      :before-delete="beforeDelete"
                      :max-count="field.uploadNumber"
                      upload-text="点此上传"
                      :after-read="afterRead" />
      </div>
    </div>

    <!-- 俩图以上 -->
    <div class="pic3" v-if="field.uploadMany === 1 && field.uploadNumber > 2">
      <!-- 第一张图 -->
      <div class="picc">
        <van-uploader :class="[showred?'picc2':'']" v-model="fileList" :disabled="disabled()" @click-upload="isCanClick"
                      :after-read="afterRead" :before-delete="beforeDelete" :max-count="field.uploadNumber" upload-text="点此上传"></van-uploader>
      </div>

    </div>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped  src="./index.less">
</style>
