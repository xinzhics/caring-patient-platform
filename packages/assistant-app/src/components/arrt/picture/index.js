import Vue from 'vue'
import {
  Uploader,
  Dialog
} from 'vant'
import FormCheckFuncEvent from '../formVue'
import { fileUpload } from '@/api/fileUpload'
import '@vant/touch-emulator'
Vue.use(Uploader)
export default {
  components: {
    [Dialog.Component.name]: Dialog.Component
  },
  data () {
    return {
      deletable: false,
      fileList: [],
      showred: false,
      noClick: false
    }
  },
  mounted () {
    FormCheckFuncEvent.$on('formResultCheck', () => {
      this.checkFieldValues()
    })
    if (this.field.values.length > 1) {
      if (this.field.values[0].attrValue === undefined) {
        this.field.values.split(0, 1)
      }
    }
    if (this.field.values.length > 0 && this.field.values[0].attrValue === undefined) {
      this.field.values = []
    } else if (this.field.values.length > 0 && this.field.values[0].attrValue !== undefined) {
      for (let i = 0; i < this.field.values.length; i++) {
        this.fileList.push({
          url: this.field.values[i].attrValue
        })
      }
      if (this.isHealth === '1') {
        this.noClick = true
      }
    }
  },
  methods: {
    isCanClick () {
      if (this.noClick) {
        this.$emit('noClick')
      }
    },
    disabled () {
      // 判断医生是否可以编辑
      if (this.field.isUpdatable === undefined) {
        return true
      } else if (this.field.isUpdatable === 1) {
        return false
      } else if (this.field.isUpdatable === 0) {
        return true
      }
    },

    beforeDelete (val, index) {
      if (this.noClick) {
        this.$emit('noClick')
        return
      }
      if (this.userRole !== 'patient') {
        if (this.field.isUpdatable === undefined) {
          return false
        }
        if (this.field.isUpdatable === 0) {
          return false
        }
      }
      Dialog.confirm({
        message: '确认要删除该照片吗？'
      })
        .then(() => {
          this.fileList.splice(index.index, 1)
          this.field.values.splice(index.index, 1)
          return true
        })
        .catch(() => {
          return false
        })
    },

    afterRead (files) {
      this.showred = false
      if (files) {
        files.status = 'uploading'
        files.message = '上传中...'
        this.$compressionImg(files.file).then(f => {
          var formData = new FormData()
          formData.append('file', f)
          formData.append('folderId', 1308295372556206080)
          fileUpload(formData)
            .then(res => {
              this.field.values.push({
                attrValue: res.data.url
              })
              files.status = 'done '
              files.message = '上传完成'
            })
        })
      }
    },
    checkFieldValues () {
      if (this.field.required) {
        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
          this.showred = true
        } else {
          this.showred = false
        }
      } else {
        this.showred = false
      }
    }
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
    userRole: {
      type: String,
      default: ''
    },
    isHealth: {
      type: String,
      default: '0'
    }
  }
}
