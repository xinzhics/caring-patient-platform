<template>
  <section class="main">
    <navBar @toHistoryPage="$router.push('/testNumber/index')"
            :pageTitle="'检验数据详情'"/>
    <div class="box">
      <div class="label">
        <div class="strip"></div>
        <span class="title">用户信息</span>
      </div>

      <div style="display: flex; align-items: center; justify-content: space-between; padding-top: 10px;">
        <div style="display: flex; align-items: center; ">
          <div class="headerImg">
            <img class="headerImg" v-if="patientInfo.avatar !== ''" :src="patientInfo.avatar">
            <img class="headerImg" v-else src="@/components/arrt/images/head-portrait.png" alt="">
          </div>
          <div
            style=" display: flex; flex-direction: column; justify-content: center; height: 50px">
            <div style="margin-left: 10px; font-size: 15px; color: #333; font-weight: bold">{{ patientInfo.name }}</div>
            <div style="font-size: 13px; color: #333; margin-top: 3px"
                 v-if="jsGetAge(patientInfo.birthday) || patientInfo.sex!==null">
              <span style="margin-left: 10px;">{{
                  jsGetAge(patientInfo.birthday) ? jsGetAge(patientInfo.birthday) + '岁' : ''
                }}</span>
              <span v-if="patientInfo.sex!==null" style="margin-left: 10px;">{{
                  patientInfo.sex === 0 ? '男' : '女'
                }}</span>
            </div>
          </div>
        </div>

        <div>
          <span v-if="formData.formErrorResult === 2" style="color: #FF5A5A">结果异常</span>
        </div>
      </div>
    </div>

    <!-- 数值标签  -->
    <div v-for="(item, key) in numberData" :key="key">
      <div v-if="item.list.length > 0" style="background: #FFF; padding-bottom: 15px;">
        <div class="box" style="margin-top: 20px">
          <div class="label">
            <div class="strip"></div>
            <span class="title">{{ item.title }}</span>
          </div>
        </div>
        <div style="height: 45px; background: #BAE9B9; display: flex; align-items: center;
          padding: 0px 15px; font-size: 14px; color: #FFF; justify-content: space-between">
          <span>检验项目</span>
          <span>检查值</span>
        </div>
        <div v-for="(lItem, index) in item.list" :key="index"
             :style="{background: index % 2 === 0? '#FFF' : '#F5F5F5'}"
             style="padding: 0px 15px; height: 45px; ;display: flex; align-items: center; justify-content: space-between">
          <span style="font-size: 13px; color: #999999">{{ lItem.label }}</span>
          <span style="font-size: 14px; color: #333333">{{
              lItem.values[0].attrValue ? lItem.values[0].attrValue : '-'
            }}</span>
        </div>
      </div>
    </div>


    <!-- 图片标签  -->
    <div v-if="imageData.list.length > 0">
      <div class="box" style="margin-top: 20px">
        <div class="label">
          <div class="strip"></div>
          <span class="title">{{ imageData.title }}</span>
        </div>
      </div>
      <div style="padding: 0px 15px; background: #FFF">
        <van-grid :column-num="2" :border="false">
          <van-grid-item v-for="(item, key) in imageData.list" :key="key" @click="previewImage(item.attrValue)">
            <van-image :src="item.attrValue" fit="fit"/>
          </van-grid-item>
        </van-grid>
      </div>
    </div>

    <!-- 图片标签  -->
    <div v-if="otherData.list.length > 0" style="background: #FFF; padding-bottom: 15px;">
      <div class="box" style="margin-top: 20px">
        <div class="label">
          <div class="strip"></div>
          <span class="title">其他信息</span>
        </div>
      </div>

      <div v-for="(item, key) in otherData.list" :key="key"
           style="padding: 0px 15px; min-height: 45px; ;display: flex; align-items: center; justify-content: space-between">
        <span style="font-size: 13px; color: #999999">{{ item.label }}</span>
        <van-image
          width="80"
          height="80"
          fit="cover"
          style="padding-bottom: 10px"
          v-if="item.widgetType && item.widgetType === 'MultiImageUpload' && getContent(item.values[0]) !== '-'"
          :src="getContent(item.values[0])"
        />
        <span v-else style="font-size: 14px; color: #333333">{{ getContent(item.values[0])}}</span>
      </div>
    </div>

    <div style="display: flex; justify-content: center; padding: 25px 0px; width: auto; width: 100%">
      <div v-if="messageId" style="width: 100%">
        <van-row style=" width: 100%; display: flex; align-items: center; margin: 19px 0 13px 0; justify-content: center">
          <div class="caring-form-button" style="height: 47px; background: #fff; color: #66E0A7; width: 47%; margin-right: 12px; margin-left: 20px; border-radius: 30px"  @click="edit">
            <span>编辑</span>
          </div>

          <div class="caring-form-button" style="height: 47px; background: #66E0A7; color: #FFFFFF; width: 47%; margin-left: 12px; margin-right: 20px; border-radius: 30px" @click="toDo">
            <span>评论</span>
          </div>
        </van-row>
      </div>

      <van-button v-else type="primary"
                  style="width: 90%;  margin-bottom: 15px; background: #67E0A7; border: 1px solid #67E0A7"
                  round @click="edit()">编辑数据</van-button>
    </div>
    <dialog-comment ref="commentRef" @submit="submitComment"></dialog-comment>
  </section>
</template>

<script>
import Vue from 'vue';
import Api from '@/api/doctor.js'
import ContentApi from '@/api/Content.js'
import {Toast, Grid, GridItem, ImagePreview, Button} from "vant";
import dialogComment from "@/components/systemMessage/systemCommentDialog"

Vue.use(Grid);
Vue.use(GridItem);
Vue.use(Button);
export default {
  components: {
    dialogComment,
    [ImagePreview.Component.name]: ImagePreview.Component,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      patientInfo: {},
      dataId: '',
      formData: {},
      numberData: [],
      imageData: {
        title: '',
        list: []
      },
      otherData: {
        list: [],
        title: '',
      },
      messageId: '',
      isLoading: false,
    }
  },
  mounted() {
    if (this.$route.query && this.$route.query.messageId) {
      this.messageId = this.$route.query.messageId
    }
    if (this.$route.query.content) {
      this.dataId = this.$route.query.content
      this.getData()
    }
  },
  methods: {
    // 提交评论
    submitComment(val) {
      if (this.isLoading) {
        return
      }
      this.isLoading = true;
      let patientId = localStorage.getItem('patientId')
      Api.doctorCommentMessage({
        commentContent: val,
        doctorId: this.$route.query.doctorId,
        doctorName: this.$route.query.doctorName,
        messageId: this.$route.query.messageId,
        patientId: patientId,
      })
        .then(res => {
          this.$refs.commentRef.close()
          this.$toast('评论成功')
          this.$router.go(-1)
        })
    },
    toDo() {
      this.$refs.commentRef.open()
    },
    edit() {
      this.$router.push({
        path: '/testNumber/editor',
        query: {
          content: this.dataId
        }
      })
    },
    getContent(value) {
      if (value && value.valueText) {
        return value.valueText
      } else if (value && value.attrValue) {
        return value.attrValue
      } else {
        return '-'
      }
    },
    // 预览图片
    previewImage(image) {
      ImagePreview({
        images: [image],
        closeable: true,
      });
    },
    getData() {
      const params = {
        id: this.dataId
      }
      Toast.loading('加载中');
      ContentApi.getCheckDataformResult(params).then(res => {
        Toast.clear()
        if (res.data.code === 0) {
          this.formData = res.data.data
          this.getPatientInfo(this.formData.userId)
          let list = JSON.parse(res.data.data.jsonContent)
          list.forEach(item => {
            // 多图上传
            if (item.widgetType === 'MultiImageUpload') {
              if (!this.imageData.title) {
                this.imageData.title = item.label
              }
              item.values.forEach(vItem => {
                this.imageData.list.push(vItem)
              })
            } else if (item.widgetType === 'Radio') {
              // 如果是单选的时候，判断是否有数值组件
              item.values.forEach(vTtem => {
                this.numberData.push({
                  list: [],
                  title: vTtem.valueText,
                  flag: true,
                })
                if (vTtem.questions) {
                  vTtem.questions.forEach(qItem => {
                    if (qItem.widgetType === 'Number') {
                      this.numberData[this.numberData.length - 1].list.push(qItem)
                    } else {
                      this.otherData.list.push(qItem)
                    }
                  })
                }
              })
            } else {
              this.otherData.list.push(item)
              if (item.values && item.values.length > 0) {
                item.values.forEach(vTtem => {
                  if (vTtem.questions && vTtem.questions.length > 0) {
                    vTtem.questions.forEach(qItem => {
                      this.otherData.list.push(qItem)
                    })
                  }
                })
              }
            }
          })
        }
      })
    },
    getPatientInfo(userId) {
      const params = {
        id: userId
      }
      Api.getContentPatient(params).then((res) => {
        if (res.data.code === 0) {
          if (res.data.data) {
            this.patientInfo = res.data.data
            localStorage.setItem('patientBaseInfo', JSON.stringify(res.data.data))
          }
        }
      })
    },
    /*根据出生日期算出年龄*/
    jsGetAge(strBirthday) {
      var returnAge = '';
      var strBirthdayArr = '';
      if (strBirthday) {
        if (strBirthday.indexOf("-") != -1) {
          strBirthdayArr = strBirthday.split("-");
        } else if (strBirthday.indexOf("/") != -1) {
          strBirthdayArr = strBirthday.split("/");
        }
        var birthYear = strBirthdayArr[0];
        var birthMonth = strBirthdayArr[1];
        var birthDay = strBirthdayArr[2];

        var d = new Date();
        var nowYear = d.getFullYear();
        var nowMonth = d.getMonth() + 1;
        var nowDay = d.getDate();

        if (nowYear == birthYear) {
          returnAge = 0;//同年 则为0岁
        } else {
          var ageDiff = nowYear - birthYear; //年之差
          if (ageDiff > 0) {
            if (nowMonth == birthMonth) {
              var dayDiff = nowDay - birthDay;//日之差
              if (dayDiff < 0) {
                returnAge = ageDiff - 1;
              } else {
                returnAge = ageDiff;
              }
            } else {
              var monthDiff = nowMonth - birthMonth;//月之差
              if (monthDiff < 0) {
                returnAge = ageDiff - 1;
              } else {
                returnAge = ageDiff;
              }
            }
          } else {
            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
          }
        }
      } else {
        returnAge = ''
      }
      //如果结果为 -1 或者为 0， 则不显示年龄
      if (returnAge === -1 || returnAge === 0) {
        returnAge = ''
      }
      return returnAge;//返回周岁年龄
    },
  }
}

</script>

<style scoped lang="less">
.box {
  background: #FFF;
  padding: 15px;

  .label {
    display: flex;
    align-items: center;

    .strip {
      width: 3px;
      height: 21px;
      margin-right: 10px;
      border-radius: 10px;
      background: #66E0A7;
    }

    .title {
      font-size: 16px;
      font-weight: bold;
      color: #333
    }
  }
}

.headerImg {
  width: 60px;
  height: 60px;
  border-radius: 50%;
}


</style>
