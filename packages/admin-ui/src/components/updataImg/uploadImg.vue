<template>
  <el-upload
    class="avatar-uploader"
    action=" "
    :show-file-list="false"
    :on-success="handleAvatarSuccess"
    :before-upload="beforeAvatarUpload"
  >
    <img v-if="imgUrl&&type==='img'" :src="imgUrl" class="avatar">
    <i v-else-if="!imgUrl&&type==='img'" class="el-icon-plus avatar-uploader-icon avatar" />
    <el-button v-else-if="type==='btn'" size="mini" icon="el-icon-upload2">点击上传</el-button>
    <i v-if="imgUrl&&type==='btn'" class="el-icon-circle-check uploadCheck" />
  </el-upload>
</template>

<script>
import Api from "@/api/ChannelContent.js";
export default {
  props: {
    imgUrl: {
      type: String,
      default: () => {
        return ''
      }
    },
    type: {
      type: String,
      default: () => {
        return 'img'
      }
    }
  },
  methods: {
    handleAvatarSuccess(res, file) {
      this.imgUrl = URL.createObjectURL(file.raw)
    },
    beforeAvatarUpload(file) {
      var formData = new FormData()
      formData.append('file', file)
      formData.append('folderId', 1308295372556206080)

      if (this.type === 'img') {
        const isJPG = (file.type === 'image/jpeg' || file.type === 'image/png')
        if (!isJPG) {
          this.$message.error('上传图片只能为jpg或者png!')
          return
        }
      }
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
        return
      }
      Api.updateImg(formData).then((res) => {
        if (res.data.code === 0) {
          this.$message({
            message: '文件上传成功！',
            type: 'success'
          })
          // console.log(res)
          this.$emit('uploadBtn', res.data.data.url)
        } else {
          this.$message({
            message: '文件上传失败！',
            type: 'error'
          })
        }
      })

      // const isJPG = file.type === 'image/jpeg'
      // const isLt2M = file.size / 1024 / 1024 < 2

      // // if (!isJPG) {
      // //   this.$message.error('上传头像图片只能是 JPG 格式!');
      // // }
      // if (!isLt2M) {
      //   this.$message.error('上传头像图片大小不能超过 2MB!')
      // }
      // return isLt2M
    }
  }
}
</script>
<style>

  .avatar-uploader .el-upload {

    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center;
  }
  .avatar {
    width: 120px;
    /* height: 120px; */
    display: block;
    border: 1px dashed #d9d9d9;
  }
  .uploadCheck{
    margin-left:30px;
    color: #67C23A;
  }
</style>
