<template>
  <div class="box1">
    <div class="title">
      <span class="username">{{field.label}}</span>
      <span class="mustchoose" v-if="field.required">*</span>
    </div>
    <div class="depict">{{field.labelDesc}}</div>
    <!-- 只有一张图 -->
    <div class="pic" v-if="(field.uploadMany === 1&& field.uploadNumber === 1)||field.uploadMany !== 1">
      <van-uploader :readonly="noClick" :class="[showred?'picc3':'picc1']" v-model="fileList"
        :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6','z-index':'1'}"
                    :before-delete="beforeDelete" :max-count="1" upload-text="点此上传" :after-read="afterRead" />
    </div>

    <!-- 可以上传两张图 -->
    <div class="pic2" v-if="field.uploadMany === 1 && field.uploadNumber === 2">
      <!-- 第一张图 -->
      <div class="picc">
        <van-uploader @click-upload="isCanClikc" :readonly="noClick" style="z-index: 1" :class="[showred?'picc2':'',field.modifyTags?'picborder':'']" v-model="fileList"
                      :before-delete="beforeDelete" :max-count="field.uploadNumber" upload-text="点此上传" :after-read="afterRead" />
      </div>
    </div>

    <!-- 俩图以上 -->
    <div class="pic3" v-if="field.uploadMany === 1 && field.uploadNumber > 2">
      <!-- 第一张图 -->
      <div class="picc">
        <van-uploader :class="[showred?'picc2':'',field.modifyTags?'picborder':'']" v-model="fileList"
                      style="z-index: 1"
                      :after-read="afterRead" :before-delete="beforeDelete"
                      ref="border"
                      @click-upload="isCanClikc" :readonly="noClick"
                      :max-count="field.uploadNumber" upload-text="点此上传"></van-uploader>
      </div>

    </div>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped  src="./index.less">
</style>
