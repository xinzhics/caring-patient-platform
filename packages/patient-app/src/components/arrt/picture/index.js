import Vue from 'vue';
import {
  Uploader,
  Toast
} from 'vant';
import {
  Dialog
} from 'vant';
import Api from '@/api/Content.js'
import FormCheckFuncEvent from "../formVue";


Vue.use(Uploader);
export default {
  components: {
    [Dialog.Component.name]: Dialog.Component
  },
  data() {
    return {
      deletable: false,
      fileList: [],
      showred: false,
      noClick:false
    };
  },
  created() {
    this.cleanFileValues()

  },
  mounted() {
    FormCheckFuncEvent.$on('formResultCheck', () => {
      this.checkFieldValues()
    })
    if (this.field.values.length > 0 && this.field.values[0].attrValue === undefined) {
      this.field.values = []
    } else if (this.field.values.length > 0 && this.field.values[0].attrValue !== undefined) {

      for (let i = 0; i < this.field.values.length; i++) {
        this.fileList.push({
          url: this.field.values[i].attrValue,
        })
      }
      if (this.isHealth==='1') {
        this.noClick = true
      }
    }
    // console.log(this.field);

  },
  methods: {
    isCanClikc() {
      if (this.noClick) {
        this.$emit('noClikc')
      }
    },
    beforeDelete(val, index) {
      if (this.noClick) {
        this.$emit('noClikc')
        return
      }
      Dialog.confirm({
          message: '确认要删除该照片吗？',
        })
        .then(() => {
          this.fileList.splice(index.index, 1)
          this.field.values.splice(index.index, 1)
          return true
        })
        .catch(() => {
          return false
        });
    },

    afterRead(files) {
      this.showred = false
      if (files) {
        files.status = 'uploading';
        files.message = '上传中...';
        this.$compressionImg.compressImg(files.file).then(f => {
          var formData = new FormData()
          formData.append('file', f)
          formData.append('folderId', 1308295372556206080)
          Api.updateImg(formData)
            .then(res => {
              this.field.values.push({
                attrValue: res.data.data.url
              })
              files.status = 'done ';
              files.message = '上传完成';
            })
        });
      }
    },
    cleanFileValues () {
      if (this.field.values.length >= 1) {
        for (let i = this.field.values.length - 1; i > 0; i--) {
          if (this.field.values[i].attrValue === undefined) {
            this.field.values.split(i, 1)
          }
        }
      }
    },
    checkFieldValues() {
      this.cleanFileValues()
      if (this.field.required) {
        // 先 清理一下 垃圾数据
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
    isHealth:{
      type:String,
      default:'0'
    }
  },
};
