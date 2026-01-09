<template>
  <section class="allContent" style="background: #fff">
    <x-header :left-options="{ backText: '', preventGoBack:true}" @on-click-back="windowClose"
      >{{ this.$getDictItem("doctor") }}申请</x-header
    >
    <van-form @submit="onSubmit1">
      <div class="lano-upload-wrap">
        <div class="lano-upload-preview">
          <van-image :src="doctorApply.avatar" @click="showImage()"></van-image>
        </div>
      </div>
      <div>
        <div class="userimage">
          <van-uploader :after-read="onUpload">
            <div class="change">点击更换头像</div>
          </van-uploader>
        </div>
      </div>
      <div class="main">
        <div class="title" style="font-size: 14px; color: #666">
          <span>姓名</span>
          <span style="color: red">*</span>
        </div>
        <van-field
          type="text"
          v-model="doctorApply.name"
          :placeholder="'请填写' + this.$getDictItem('doctor') + '姓名'"
          maxlength="10"
          show-word-limit
          :rules="[{ required: true }]"
          class="cell3"
        />
      </div>

      <div class="main">
        <div class="title" style="font-size: 14px; color: #666">
          <span>电话</span>
          <span style="color: red">*</span>
        </div>
        <van-field
          class="cell3"
          v-model="doctorApply.mobile"
          @input="inputMobile"
          type="tel"
          :placeholder="
            '请输入手机号，将用于' + this.$getDictItem('doctor') + '登录'
          "
          maxlength="11"
          show-word-limit
          :rules="[
            { required: true },
          ]"
        />
        <div class="tishi" v-if="showTish">手机号已经被占用</div>
      </div>

      <van-cell-group class="father">
        <!-- 医院 -->
        <!-- <van-field v-model="doctorApply.hospitalName" type="text" name="医院" placeholder="请输入" label="医院" maxlength="10" show-word-limit /> -->
        <div class="main">
          <div class="title">
            <span class="name" style="font-size: 14px; color: #666">医院</span>
          </div>
          <div>
            <van-cell
              :class="[doctorApply.hospitalName !== '' ? 'cell2' : '']"
              class="cell"
              is-link
              :value="
                doctorApply.hospitalName !== ''
                  ? doctorApply.hospitalName
                  : '请选择医院'
              "
              @click="showPopup"
            />
            <div>
              <van-popup
                v-model="show"
                position="bottom"
                :style="{ height: '100%' }"
              >
                <hospital
                  class="main1"
                  v-if="show"
                  @hospitalName="hName"
                  @hideList="hide"
                />
              </van-popup>
            </div>
          </div>
        </div>

        <div class="main">
          <div class="title" style="font-size: 14px; color: #666">
            <span>科室</span>
          </div>
          <van-field
            class="cell3"
            v-model="doctorApply.deptartmentName"
            type="text"
            placeholder="请输入"
            maxlength="10"
            show-word-limit
          />
        </div>

        <div class="main">
          <div class="title" style="font-size: 14px; color: #666">
            <span>职称</span>
          </div>
          <van-field
            class="cell3"
            v-model="doctorApply.title"
            type="text"
            placeholder="请输入"
            maxlength="10"
            show-word-limit
          />
        </div>

        <div class="zhuanyetechang">
          <div class="label">专业特长</div>
          <textarea rows="3" cols="20" v-model="doctorApply.specialties" placeholder="请输入"></textarea>
        </div>
        <div class="zhuanyetechang">
          <div class="label">详情介绍</div>
          <textarea rows="3" cols="20" v-model="doctorApply.introduction" placeholder="请输入"></textarea>
        </div>

        <!-- <van-field v-model="doctorApply.introduction" rows="3" autosize placeholder="请输入" label="详细介绍" type="textarea" show-word-limit /> -->
      </van-cell-group>
      <div style="margin: 16px; margin-top: 4rem; height: 6rem">
        <van-button round block type="info" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </section>
</template>
<script>
import Vue from "vue";
import { ImagePreview, Row, Col, Cell, Uploader, Loading, Search } from "vant";
import { Image as VanImage } from "vant";
import wx from "weixin-js-sdk";
import Api from "@/api/doctor.js";
import { Icon, XDialog, XButton } from "vux";
import hospital from "../../components/arrt/hospitalApply/hospitalList/hospitalList.vue";
import doctorApi from "../../api/doctor";
Vue.use(Search);
export default {
  name: "doctorApply",
  components: {
    [Row.name]: Row,
    [Col.name]: Col,
    [Cell.name]: Cell,
    [Uploader.name]: Uploader,
    [VanImage.name]: VanImage,
    [ImagePreview.name]: ImagePreview,
    [Loading.name]: Loading,
    Icon,
    XDialog,
    XButton,
    hospital,
  },
  data() {
    return {
      accept: {
        default: "",
      },
      fileList: [],
      setTip: true,
      showDialog: false,
      selectInd: "",
      name: "lano-uploader",
      mobileExist: false,
      doctorApply: {
        avatar: "",
        nickName: "",
        wxAppId: "",
        mobile: "",
        hospitalName: "",
        deptartmentName: "",
        title: "",
        specialties: "",
        introduction: "",
        name: "",
      },
      show: false,
      showTish:false
    };
  },
  created() {
    if ( this.$route.query.headImgUrl) {
      this.doctorApply.avatar = this.$route.query.headImgUrl;
      this.fileList = [{ url: this.$route.query.headImgUrl }];
    } else {
      const defaultAvtar = "https://caing-test.obs.cn-north-4.myhuaweicloud.com/23a176879c0c5675556687e5718cc82.png"
      this.doctorApply.avatar = defaultAvtar;
      this.fileList = [{ url: defaultAvtar }];
    }
    this.doctorApply.openId = this.$route.query.openId;
  },
  mounted() {
    this.doctorApply.wxAppId = localStorage.getItem("wxAppId");
  },
  methods: {
    // 医院组件
    showPopup() {
      this.show = true;
    },
    hide(showlist) {
      this.show = showlist;
    },
    hName(hospname) {
      this.doctorApply.hospitalName = hospname;
    },

    onUpload(files) {
      if (files) {
        var formData = new FormData();
        formData.append("file", files.file);
        formData.append("folderId", 1308295372556206080);

        if (this.type === "img") {
          const isJPG =
            files.file.type === "image/jpeg" || files.file.type === "image/png";
          if (!isJPG) {
            this.$vux.toast.text("上传图片只能为jpg或者png", "center");
            return;
          }
        }
        const isLt2M = files.file.size / 1024 / 1024 < 4;
        if (!isLt2M) {
          this.$vux.toast.text("上传图片大小不能超过 4MB", "center");
          return;
        }
        this.$vux.toast.text("图片上传中", "center");
        Api.uploadDoctorImage(formData).then((res) => {
          if (res.data.code === 0) {
            this.$vux.toast.text("上传成功", "center");
            this.doctorApply.avatar = res.data.data.url;
          } else {
            this.$vux.toast.text("文件上传失败", "center");
          }
        });
      }
    },
    showImage() {
      ImagePreview({ images: [this.doctorApply.avatar], closeable: true });
    },
    // 请求服务器删除文件，和 属性值
    deleteFile(fileId) {
      Service.get("/pds/wx/patient/deleteAttrValue/" + fileId);
    },
    delFile(ind, file) {
      this.showDialog = true;
      this.selectInd = ind;
    },
    isImg(url) {
      if (url) {
        let imgType = "jpg/jpeg/png/gif/svg/ico/webp/bmp";
        let ind = url.lastIndexOf(".");
        let suf = url.substring(ind + 1, url.length);
        if (imgType.findIndex(suf) > -1) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    },
    detelBtn() {
      this.fileList.splice(this.selectInd, 1);
      this.showDialog = false;
    },
    cancelBtn() {
      this.showDialog = false;
    },
    afterRead(file) {
      // 此时可以自行将文件上传至服务器
    },
    inputMobile() {
      this.mobileExist = false;
      if (this.doctorApply.mobile.length >= 11) {
        Api.existByMobile({ mobile: this.doctorApply.mobile }).then((res) => {
          if (res.data.code === 0) {
            console.log(res.data)
            if (res.data.data) {
              this.mobileExist = true;
              this.showTish=true
            }
            if(res.data.data===false){
              this.showTish=false
            }
          }
        });
      }
    },
    validator() {
      if (this.mobileExist) {
        return false;
      } else {
        return true;
      }
    },
    onSubmit1() {
      if(this.show){
        return
      }
      if (this.mobileExist) {
        this.showTish=true
        return;
      }
      this.doctorApply.mobile = this.doctorApply.mobile.trim();
      let mobile = this.doctorApply.mobile.replace(/\s+/g, "");
      this.doctorApply.mobile = mobile;
      if (mobile.length < 11) {
        this.$vux.toast.text("请输入11位手机号码", "center");
        return;
      }
      Api.phoneAuditExist(mobile).then(res => {
        if (res.data.data === true) {
          this.$vux.toast.text('信息已经在审核，请勿重复提交', 'center')
        } else {
          Api.submitDoctorAudit(this.doctorApply)
            .then((res) => {
              if (res.data.code === 0) {
                this.$vux.toast.text("提交信息成功", "center");
                this.submitted();
              }
            })
            .catch((res) => {
              this.showTish=true
              this.$vux.toast.text("提交信息失败", "center");
            });
        }
      })

    },
    windowClose() {
      wx.closeWindow();
    },
    submitted() {
      this.$router.replace("/submitted/index");
    },
  },
};
</script>
<style lang="less" scoped>
.tishi{
  width: 92%;
  margin-left: 4%;
  margin-right: 4%;
  font-size: 12px;
  color: red;
  margin-top: 5px;
}
// 医院样式
.main {
  background-color: #fff;
  padding-top: 18px;
  /deep/.cell2 {
    .van-cell__value {
      span {
        color: #333333;
      }
    }
  }
  .van-cell__value {
    text-align: left;
    span {
      margin-left: 1rem;
      color: #d6d6d6;
    }
  }
  .title {
    margin-left: 4%;
    margin-bottom: 10px;

    .name {
      font-size: 16px;
      font-family: Source Han Sans CN;
      font-weight: 500;
      color: #333333;
    }

    .mustchoose {
      font-size: 16px;
      font-weight: 500;
      color: #ff5555;
      opacity: 1;
    }

    .describe {
      font-size: 13px;
      font-family: Source Han Sans CN;
      font-weight: 400;
      color: #999999;
      opacity: 1;
      margin-top: 0.3rem;
      margin-bottom: 17px;
    }
  }
}
.mian1 {
  /deep/.van-search {
    background: #f0f0f0;

    .van-search__content {
      background: #fff;
      height: 46px;
      border-radius: 4px;
      padding: 0;

      .van-cell {
        padding: 0 !important;
        line-height: 46px;
        margin: 0 !important;
        .van-field__left-icon {
          margin-left: 13px;
          margin-right: 6px;

          i {
            font-size: 15px;
            color: #000;
            margin-top: 0 !important;
          }
        }

        .van-cell__value {
          padding: 0 !important;
          .van-field__body {
            // line-height: 35px;  //搜索字体
            ::-webkit-input-placeholder {
              /*Webkit browsers*/
              color: rgba(0, 0, 0, 0.25);
              font-size: 15px;
            }
          }
        }
      }
    }
  }

  .select {
    width: 100%;
    background-color: #fff;
    height: 48px;
  }

  .showData {
    width: 100%;
    background-color: #fff;
    .options {
      padding-left: 13px;
      border-bottom: 1px solid #ececec;
      min-height: 48px;
      line-height: 48px;
      color: #666666;
      font-size: 16px;
      font-family: Source Han Sans CN;
    }
  }


  /deep/.headTitle {
    .van-col {
      height: 50px;
      line-height: 50px;
    }
  }
  .searchList {
    background-color: #fff;
    padding-left: 13px;
    color: #666;
    min-height: 48px;
    border-bottom: 1px solid #ececec;
    line-height: 48px;
  }

  /deep/.van-icon-clear {
    margin-top: 0 !important;
  }
}

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
  /deep/.cell {
    padding: 0 !important;
    // width: 92%;
    // margin-left: 13px;
    // margin-right: 13px;
    height: 2.875rem;
    background: rgba(245, 245, 245, 0.39);
    border: 1px solid #d6d6d6;
    opacity: 1;
    border-radius: 4px;
    position: relative;
  }

  /deep/.van-cell__value {
    line-height: 2.875rem;
    // padding-left: .8125rem
  }

  /deep/.van-icon {
    margin-top: 0.7rem !important

  ;
    margin-right: 0.5rem;
  }
  .hosp {
    position: relative;
    top: 0;
  }
  /deep/.cellColor {
    .van-cell__value {
      color: #333333;
    }
  }

  /deep/.noData {
    border: 1px solid red;
    .van-cell__value {
      color: red;
    }
  }
  .lano-upload-wrap {
    width: 100%;
    .lano-upload-preview {
      background-color: #fff;
      width: 100%;
      display: flex;
      justify-content: center;
      .van-image {
        margin: 0 auto;
        width: 5rem;
        height: 5rem;
        padding: 0;
        margin-top: 5rem;
        border-radius: 50%;
        /deep/.van-image__img {
          width: 100%;
          height: 100%;
          border-radius: 50%;
        }
      }
    }
  }
  textarea {
    resize: none;
  }
  .useimg {
    padding-top: 4rem;
    width: 100%;
    .user-img {
      width: 5rem;
      height: 5rem;
      margin: 0 auto;
      margin-bottom: 0.5rem;
      border-radius: 50%;
      img {
        width: 5rem;
        height: 5rem;
        border-radius: 50%;
      }
    }
  }
  /deep/.userimage {
    background-color: #fff;
    width: 100%;
    display: flex;
    justify-content: center;
    text-align: center;
    .van-uploader {
      width: 10rem;
      // padding-left: 10.8rem;
      .van-uploader__wrapper {
        .van-uploader__input-wrapper {
          width: 100%;
        }
      }
    }
  }
  /deep/.van-button {
    width: 50%;
    margin-bottom: 2rem;
    margin: 0 auto;
  }

  .van-image__img {
    border-radius: 50%;
  }
  .van-cell__title {
    text-align: center;
  }

  /deep/.van-image__loading {
    background-color: #fff !important;
  }
  /deep/.van-image__loading-icon {
    width: 4rem;
    height: 4rem;
    border-radius: 50%;
  }

  .change {
    width: 100%;
    height: 3rem;
    text-align: center;
    padding: 0;
    color: #ccc;
    line-height: 3rem;
    font-size: 15px;
  }

  /deep/.van-field__word-limit {
    position: absolute;
    right: 0.5rem;
    top: 0.6rem;
  }
  /deep/.van-image {
    padding-top: 3rem;
  }
  .zhuanyetechang {
    width: 92%;

    margin: 1.5rem auto;
    margin-left: 4%;
    margin-right: 4%;
    textarea {
      width: 95%;
      border: none;

      background: rgba(245, 245, 245, 0.39);
      padding-left: 1rem;
      font-size: 0.9rem;
      border: 1px solid #d6d6d6;
      border-radius: 4px;
    }
  }

  .label {
    font-size: 0.9rem;
    color: #666;
    margin-bottom: 0.5rem;
  }
  textarea::-webkit-input-placeholder {
    color: #d1cbcc;
  }
  .van-cell::after {
    border: none;
  }
  [class*="van-hairline"]::after {
    border: none;
  }
  /deep/.van-cell {
    width: 92%;
    // height: 6rem;
    padding-left: 0;
    padding-right: 0;
    margin-left: 4%;
    margin-right: 4%;
  }
  .van-cell__value {
    width: 100%;
  }

  .lano-uplodaer-container {
    padding: 10px 10px 10px 15px;
  }

  .lano-uploader-label {
    color: #323233;
  }

  .lano-upload-wrap {
    display: flex;
    flex-wrap: wrap;
    position: relative;
  }

  .child {
    margin: auto;
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
  }

  .lano-upload-preview {
    display: inline-block;
    position: relative;
    margin: 0 8px 8px 0;

    /deep/ .van-image__img {
      width: 50px;
      height: 50px;
    }

    /deep/ .van-image {
      width: 50px;
      height: 50px;
    }

    /deep/ .van-image__error {
      .iconfont {
        font-size: 50px;
      }
    }

    .upload-img {
      width: 50px;
      height: 50px;
      line-height: 50px;
      text-align: center;
      display: block;
    }

    .iconRemarks {
      font-size: 50px;
    }

    .lano-uploader-del {
      position: absolute;
      bottom: 0;
      right: -5px;
      padding: 2px;

      .iconfont {
        color: #eee;
        font-size: 16px;
      }
    }
  }

  /deep/ .van-uploader {
    width: auto;
  }

  /deep/ .van-uploader__upload {
    width: 50px;
    height: 50px;
  }

  .error-msg {
    color: #f44;
    font-size: 12px;
    text-align: right;
    margin-right: 15px;
  }
  // 不知道什么时候能用上
  .lano-uplodaer-container {
    padding: 10px 10px 10px 15px;
  }

  .lano-uploader-label {
    color: #323233;
    // padding: 10px 0;
  }

  .lano-upload-wrap {
    display: flex;
    flex-wrap: wrap;
    position: relative;
  }

  .child {
    margin: auto;
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
  }

  .lano-upload-preview {
    display: inline-block;
    position: relative;
    margin: 0 8px 8px 0;

    /deep/ .van-image__img {
      width: 50px;
      height: 50px;
    }

    /deep/ .van-image {
      width: 50px;
      height: 50px;
    }

    /deep/ .van-image__error {
      .iconfont {
        font-size: 50px;
      }
    }

    .upload-img {
      width: 50px;
      height: 50px;
      line-height: 50px;
      text-align: center;
      display: block;
    }

    .iconRemarks {
      font-size: 50px;
    }

    .lano-uploader-del {
      position: absolute;
      bottom: 0;
      right: -5px;
      // background: #999;
      padding: 2px;

      .iconfont {
        color: #eee;
        font-size: 16px;
      }
    }
  }

  /deep/ .van-uploader {
    width: auto;
  }

  /deep/ .van-uploader__upload {
    width: 50px;
    height: 50px;
  }

  .error-msg {
    color: #f44;
    font-size: 12px;
    text-align: right;
    margin-right: 15px;
  }
}
/deep/.cell3 {
  padding: 0 !important;
  border: 1px solid #d6d6d6;
  padding-bottom: 16px;
  border-radius: 4px;
  background: rgba(245, 245, 245, 0.39);
  .van-field__body {
    padding-left: 16px !important;
  }
}

/deep/.van-button {
  width: 100% !important;
}
</style>
