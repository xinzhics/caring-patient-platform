<template>
  <div style="width: 100%;margin-top: 9px">
    <div>
      <div style="width: 100%;background: #ffffff">
        <div v-show="!showUpdata" style="width:120px;margin: 0 auto;">
          <van-uploader :deletable=false :after-read="afterRead" style="width: 100%">
            <div style="height: 65px;background: #ffff;line-height: 65px;text-align: center;width: 100%;margin: 0 auto">
              <van-icon name="add-o"/>
              <span>从相册选取</span>
            </div>
          </van-uploader>
        </div>
      </div>
      <div v-show="showUpdata" style="width: 100%;height: 209px;background: #fff;padding-top: 29px;padding-bottom: 15px;display: flex;flex-direction:column;align-items: center">
        <van-image
          @click="imagePreview()"
          show-loading
          width="10rem"
          height="10rem"
          fit="contain"
          :src="fileUrl"
        >
          <template v-slot:loading>
            <van-loading type="spinner" size="20" />
          </template>
          <template v-slot:error>
            <van-loading v-if="uploadLoading" type="spinner" size="20" />
            <span v-else>加载失败</span>
          </template>
        </van-image>
        <div style="width: 70px; margin-top: 20px">
          <van-uploader class="upload-2" :preview-image="false" :deletable=false :after-read="afterRead" style="width: 100%">
            <van-button round size="small" type="default">重新上传</van-button>
          </van-uploader>
        </div>

      </div>
    </div>
    <div :class="!showUpdata?'noPic':'hasPic'" style="margin-top: 75px;display: flex;justify-content: center">
      <van-button :class="fileUrl===''?'disable':''" :disabled="fileUrl===''?true:false" @click="submit" style="width: 217px" round type="info">发送</van-button>
    </div>
    <Dialog @showPop="showPop" :localMessageId="localMessageId" :submit-message="submitMessage" :show="dialogShow" :people-number="peopleNumber"></Dialog>
  </div>
</template>

<script>
import Vue from 'vue'
import {Uploader, Icon, ImagePreview, Loading, Image as VanImage} from 'vant'
import { fileUpload } from '@/api/fileUpload.js'
import Dialog from './Dialog'
import '@vant/touch-emulator'
Vue.use(VanImage)
Vue.use(Loading)
Vue.use(ImagePreview)
Vue.use(Uploader)
Vue.use(Icon)
export default {
  components: {
    Dialog
  },
  data () {
    return {
      uploadLoading: false,
      fileUrl: '',
      submitMessage: {},
      peopleNumber: 0,
      dialogShow: false,
      nursingId: localStorage.getItem('caringNursingId'),
      localMessageId: this.$route.query.localMessageId, // 本地设置的群发信息的缓存 key
      showUpdata: false
    }
  },
  methods: {
    /**
     * 预览图片
     */
    imagePreview () {
      ImagePreview({images: [this.fileUrl], closeable: true})
    },
    afterRead (files) {
      console.log(files)
      if (files) {
        files.status = 'uploading'
        files.message = '上传中...'
        let formData = new FormData()
        formData.append('file', files.file)
        formData.append('folderId', 1308295372556206080)
        this.uploadLoading = true
        this.showUpdata = true
        fileUpload(formData)
          .then(res => {
            this.fileUrl = res.data.url
            files.status = 'done '
            files.message = '上传完成'
            this.uploadLoading = false
          })
      }
    },
    showPop () {
      this.dialogShow = false
    },
    submit () {
      if (this.fileUrl) {
        const localMessageJSONString = localStorage.getItem(this.localMessageId)
        const localMessage = JSON.parse(localMessageJSONString)
        this.peopleNumber = localMessage.peopleNumber
        const params = {}
        params.receiverId = localMessage.receiverId
        params.senderId = this.nursingId
        params.type = 'image'
        params.content = this.fileUrl
        this.submitMessage = params
        this.dialogShow = true
        console.log('提交啦', this.submitMessage)
      }
    }
  }
}
</script>

<style scoped>
/deep/ .van-uploader__wrapper {
  width: 100%;
}

/deep/ .van-uploader__input-wrapper {
  width: 100%;
}
.noPic{
  margin-top: 317px !important;
}
.upload-2{
  display: flex;
  justify-content: center;
}
.disable{
  background: #989898 !important;
  border-color: #989898 !important;
}
</style>
