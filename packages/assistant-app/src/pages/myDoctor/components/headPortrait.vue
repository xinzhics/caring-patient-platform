<template>
    <div>
      <van-uploader :before-read="beforeRead" :after-read="afterRead" >
        <div style="display: flex;align-items: center">
          <img :src="avatar" style="width: 52px;height: 52px;border-radius: 50%" alt="">
        </div>
      </van-uploader>
      <van-popup
        :lock-scroll="false"
        duration="0"
        v-model="showCropper"
        position="top"
        :style="{ height: '100%',background:'black' }"
      >
        <div class="cropper-container">
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
</template>

<script>
import Vue from 'vue'
import { VueCropper } from 'vue-cropper'
import { fileUpload } from '@/api/fileUpload.js'
import {Uploader, Popup, NavBar, Toast} from 'vant'
import '@vant/touch-emulator'
Vue.use(Uploader)
Vue.use(Popup)
Vue.use(NavBar)
Vue.use(Toast)
export default {
  data () {
    return {
      imageFileName: '',
      showCropper: false, // 裁剪的弹窗
      option: {
        img: '', // 裁剪图片的地址
        info: true, // 裁剪框的大小信息
        outputSize: 0.8, // 裁剪生成图片的质量
        outputType: 'jpeg', // 裁剪生成图片的格式
        canScale: true, // 图片是否允许滚轮缩放
        autoCrop: true, // 是否默认生成截图框
        autoCropWidth: 200, // 默认生成截图框宽度
        autoCropHeight: 200, // 默认生成截图框高度
        fixedBox: false, // 固定截图框大小 不允许改变
        fixed: false, // 是否开启截图框宽高固定比例
        full: true, // 是否输出原图比例的截图
        canMoveBox: true, // 截图框能否拖动
        original: false, // 上传图片按照原始比例渲染
        centerBox: false, // 截图框是否被限制在图片里面
        infoTrue: true // true 为展示真实输出图片宽高 false 展示看到的截图框宽高
      },
      baseInfo: {
        extraInfo: {
          Specialties: '',
          Introduction: ''
        }
      }
    }
  },
  components: {
    VueCropper
  },
  props: {
    avatar: {
      type: String
    },
    uploadCompleted: {
      type: Function
    }
  },
  methods: {
    // 裁剪图片
    realTime (data) {
      this.$refs.cropper.getCropData(data => {
        this.imageBase64 = 'data:image/jpeg;base64' + data
      })
    },
    beforeRead (file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      if (!isJPG) {
        Toast('上传图片只能为jpg或者png')
        return
      }
      this.imageFileName = file.name
      this.imageToBase64(file)
      this.showCropper = true
    },
    imageToBase64 (file) {
      let reader = new FileReader()
      let that = this
      reader.readAsDataURL(file)
      reader.onload = () => {
        that.option.img = reader.result
        console.log(that.option.img)
      }
    },
    /**
     * 点裁剪弹窗确定
     */
    getCropBlob () {
      // let formData = new FormData()
      this.loadingShow = true
      this.$refs.cropper.getCropBlob((data) => {
        console.log(data)
        var formData = new FormData()
        formData.append('file', data, this.imageFileName)
        formData.append('folderId', 1308295372556206080)
        this.Upload(formData)
      })
    },
    /**
     * 上传图片方法
     * @param data
     * @constructor
     */
    Upload (data) {
      fileUpload(data).then(res => {
        if (res.code === 0) {
          this.avatar = res.data.url
          Toast('上传成功')
          this.uploadSuccess(this.avatar)
          this.showCropper = false
        } else {
          Toast('文件上传失败')
        }
      })
    },
    onClickLeft () {
      this.loadingShow = false
      this.showCropper = false
      this.$parent.showPhotoUploader = false
    },
    afterRead (file) {
      // 此时可以自行将文件上传至服务器
      console.log(file)
      this.uploadSuccess()
    },
    uploadSuccess (avatar) {
      // 吧图片的url 放在这个方法中
      this.uploadCompleted(avatar)
    }
  }
}
</script>

<style scoped>
.cropper-container {
    width: auto;
    height: 100vh;
}
/deep/.van-nav-bar{
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0px;
  background-color: rgba(0, 0, 0, 1);
}

/deep/.van-hairline--bottom:after {
  border-bottom-width: 0 !important;
}

/deep/ .van-nav-bar__left {
  font-size: 16px;
}

/deep/ .van-nav-bar__right {
  font-size: 16px;
}
</style>
