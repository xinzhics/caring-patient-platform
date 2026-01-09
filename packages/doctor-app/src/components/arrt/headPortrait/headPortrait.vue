<template>
  <div class="box1">

    <div class="title">
      <!-- <van-image round width="10rem" height="10rem" :src=" url" /> -->
      <div class="userimg">
        <van-uploader :class="fileList.length>0?'img2':'img1'" ref="img"
                      :after-read="afterRead"
                      v-model="fileList"
                      :before-read="beforeRead"
                      :disabled="!field.isUpdatable || field.isUpdatable !== 1"
                      :style="{'border':field.modifyTags ? '1px dashed #337EFF':''}"
                      style="border-radius: 50%;" @click-upload="click" />
      </div>
      <van-popup
          :lock-scroll="false"
          duration="0"
          v-model="showCropper"
          position="top"
          :style="{ height: '100%' }"
      >
        <div class="cropper-container">
          <!--          <van-loading v-show="loadingShow" size='50' color="#fff" vertical text-color='#fff'>上传中...</van-loading>-->
          <vueCropper
              style="background-image:none;background: #000"
              ref="cropper"
              :img="option.img"
              :outputSize="option.size"
              :outputType="option.outputType"
              :info="true"
              :full="option.full"
              :canMove="option.canMove"
              :canMoveBox="option.canMoveBox"
              :original="option.original"
              :autoCrop="option.autoCrop"
              :fixed="option.fixed"
              :centerBox="option.centerBox"
              :infoTrue="option.infoTrue"
              :fixedBox="option.fixedBox"
              :auto-crop-width="option.autoCropWidth"
              :auto-crop-height="option.autoCropHeight"
              @realTime="realTime"
          ></vueCropper>
          <van-nav-bar
              left-text="取消"
              right-text="完成"
              @click-left="onClickLeft"
              @click-right="getCropBlob"
          />
        </div>
      </van-popup>
    </div>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
</style>
