import Vue from 'vue';
import Api from '@/api/Content.js'
import { VueCropper } from 'vue-cropper'
import {
  Overlay,Popup,NavBar,Toast
} from 'vant';
Vue.use(Overlay);
Vue.use(Toast);
Vue.use(Popup);
Vue.use(NavBar);
export default {
  components: {
    VueCropper
  },
  data() {
    return {
      fileList: [],
      show: true,
      url: '',
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
        fixed: true, // 是否开启截图框宽高固定比例
        //fixedNumber: [2.35, 1], // 截图框的宽高比例
        full: true, // 是否输出原图比例的截图
        canMoveBox: true, // 截图框能否拖动
        original: false, // 上传图片按照原始比例渲染
        centerBox: false, // 截图框是否被限制在图片里面
        infoTrue: true // true 为展示真实输出图片宽高 false 展示看到的截图框宽高
      },
      loadingShow: false, // 是否展示loading
      noClick:false
    };
  },
  mounted() {
    if (this.field.values && this.field.values.length > 0 && this.field.values[0].attrValue) {
      this.fileList = []
      this.fileList.push({
        url: this.field.values[0].attrValue
      })
      if (this.isHealth === '1') {
        this.noClick = true
      }
    } else if(this.field.values && this.field.values.length > 0 &&!this.field.values[0].attrValue) {
      console.log('数据处理了么')
      this.field.values = [{
        attrValue:''
      }]
    }
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
    isHealth:{
      type:String,
      default:'0'
    }
  },
  methods: {
    // 实时预览函数
    realTime(data) {
      this.$refs.cropper.getCropData(data => {
        this.imageBase64 = 'data:image/jpeg;base64' + data
      })
    },
    beforeRead (file) {
      console.log(file.length)
      if (file.length>1) {
        Toast.fail('头像只能上传一张,请重新上传');

      }else {
        this.imageFileName = file.name
        this.imageToBase64(file)
        this.showCropper = true
      }

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
    getCropBlob () {
      // let formData = new FormData()
      this.loadingShow = true
      this.$refs.cropper.getCropBlob((data) => {
        console.log(data)
        var formData = new FormData()
        formData.append('file',data,this.imageFileName)
        formData.append('folderId',1308295372556206080)
        console.log(formData)
        Api.updateImg(formData)
            .then(res => {
              this.field.values[0].attrValue = res.data.data.url
              this.fileList = []
              this.fileList.push({
                url: this.field.values[0].attrValue
              })
              this.showCropper=false
            })
        // uploadAva(formData).then((res) => {
        // this.showCropper = false
        this.$emit('update:showAta', false)
        // this.$emit('update:avaUrl', res.data)
        // })

      })
    },
    onClickLeft () {
      this.loadingShow = false
      this.showCropper = false
      this.$parent.showPhotoUploader = false
    },


    click() {
      if (this.noClick) {
        this.$emit('noClikc')
        console.log(  132)
      }
    },
    afterRead(files) {
      // 此时可以自行将文件上传至服务器
      if (this.fileList.length > 1) {
        this.fileList.shift()
      }
      if (files) {
        files.status = 'uploading';
        files.message = '上传中...';
        this.$compressionImg.compressImg(files.file).then(f => {
          var formData = new FormData()
          formData.append('file', f)
          formData.append('folderId', 1308295372556206080)
          Api.updateImg(formData)
            .then(res => {
              this.field.values[0].attrValue = res.data.data.url
              this.fileList = []
              this.fileList.push({
                url: this.field.values[0].attrValue
              })
              files.status = 'done ';
              files.message = '上传完成';
              this.showCropper=false
            })
        });
      }
    },
    checkFieldValues() {
      if (this.field.required) {
        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {

        }
      }
    }
  },
};
