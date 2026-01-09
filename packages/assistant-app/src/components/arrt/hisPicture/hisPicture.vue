<template>
  <div class="box1">
    <div class="title">
      <span class="username">{{field.label}}</span>
      <span class="mustchoose" v-if="field.required">*</span>
    </div>
    <div class="depict">{{field.labelDesc}}</div>
    <!-- 只有一张图 -->
    <div class="pic" v-if="field.uploadMany === 2">
      <van-uploader v-if="show" :class="[showred?'picc3':'picc1']" v-model="fileList"
        :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"
        :before-delete="beforeDelete" :max-count="1" upload-text="点此上传" :after-read="afterRead" />
      <div class="nodata" v-if="!show">
        <img src="../images/del.png" alt="">
      </div>
    </div>

    <!-- 可以上传两张图 -->
    <div class="pic2" v-if="field.uploadMany === 1 && field.uploadNumber === 2">
      <!-- 第一张图 -->
      <div class="picc">
        <van-uploader v-if="show" :class="[showred?'picc2':'',field.modifyTags?'picborder':'']" v-model="fileList"
          :before-delete="beforeDelete" :max-count="field.uploadNumber" upload-text="点此上传" :after-read="afterRead" />
      </div>
      <div class="pic2-nodata" v-if="!show">
        <img src="../images/del.png" alt="">
      </div>
    </div>

    <!-- 俩图以上 -->
    <div class="pic3" v-if="field.uploadMany === 1 && field.uploadNumber > 2">
      <!-- 第一张图 -->
      <div class="picc">
        <van-uploader v-if="show" :class="[showred?'picc2':'',field.modifyTags?'picborder':'']" v-model="fileList"
          :after-read="afterRead" :before-delete="beforeDelete" ref="border" :max-count="field.uploadNumber"
          upload-text="点此上传"></van-uploader>

        <div class="pic3-nodata" v-if="!show">
          <img src="../images/del.png" alt="">
        </div>
      </div>

    </div>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
</style>
