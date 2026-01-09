<template>
  <section class="myCenter">
    <x-header :left-options="{backText: '',preventGoBack:true}" @on-click-back="jump">{{pageTitle ? pageTitle : '评分结果'}}</x-header>
    <div class="headerContent" :style="{backgroundImage:'url(https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/form_result_score_bg_img.png)'}">
      <div style="position: absolute; top: 40px; left: 20px;">
        <div style="font-size: 24px; color: white; font-weight: 500">本次测试结果</div>
        <div style="font-size: 14px; color: white; font-weight: 500">此测试结果仅供参考，不能代替{{doctorName}}诊断</div>
      </div>

      <div class="content">
        <div class="box" v-if="scoreRule.showResultSum === 1">
          <div class="label" style="font-size: 16px; font-weight: 500"><span>总分</span></div>
          <div class="value" v-if="scoreRule.formResultCountWay === 'sum_score'">{{data.formResultSumScore}}分</div>
          <div class="value" v-if="scoreRule.formResultCountWay === 'average_score'">{{data.formResultAverageScore}}分</div>
          <div class="value" v-if="scoreRule.formResultCountWay === 'sum_average_score'">{{data.formResultSumAverageScore}}分</div>
        </div>
        <div class="box" v-if="scoreRule.showAverage === 1">
          <div class="label"><span>平均分</span></div>
          <div class="value">{{data.formResultAverageScore}}分</div>
        </div>
        <div v-if="scoreRule.showGroupSum === 1">
          <div class="list_title">
            <div style="color: #909399;">项目评分</div>
            <div style="color: #909399;">得分</div>
          </div>
          <div>
            <div v-for="(item, key) in list" :key="key" class="list_title" style="margin-top: 5px;">
              <div class="list_label" style="width: 250px">
                {{ item.groupName }}
              </div>
              <div v-if="item.count !== undefined && item.count != null">{{ item.count }}分</div>
              <div v-else>0分</div>
            </div>
          </div>
        </div>
        <div style="margin-top: 30px;" v-if="data.showScoreQuestionnaireAnalysis">
          <div class="scoreQuestionnaireAnalysis" v-html="data.scoreQuestionnaireAnalysis"></div>
        </div>
      </div>
    </div>
    <div style="position: fixed;
    left: 0;
    text-align: center;
    bottom: 20px;
    margin: 0 auto;
    right: 0;">
      <van-button type="primary" round style="width: 260px; font-size: 18px; height: 50px; background: #3F86FF; border: 2px solid #3F86FF"
                  @click="jump()">确认</van-button>
    </div>

  </section>
</template>

<script>

import Content from "@/api/Content";
import Vue from "vue";
import {Button} from "vant";

Vue.use(Button);
export default {
  name: "result",
  data() {
    return {
      pageTitle: this.$route.query.title,
      h5UiImage_attribute1: localStorage.getItem('h5UiImage_attribute1'),
      list: [],
      data: {},
      backQuery: {id: this.$route.query.id, title: this.$route.query.title},
      backUrl: this.$route.query.backUrl,
      scoreRule: {},
      doctorName: this.$getDictItem('doctor'),
    }
  },
  mounted() {
    this.getFormResultScore()
  },
  methods: {
    jump() {
      this.$router.go(-1)
    },
    getFormResultScore() {
      Content.getFormResultScore(this.$route.query.dataId)
        .then(res => {
          if (res.data.data) {
            this.scoreRule = res.data.data.scoreRule
            this.data = res.data.data
            this.data.fieldsGroups.forEach(item => {
              this.list.push({
                fieldGroupUUId: item.fieldGroupUUId,
                count: undefined,
                groupName: item.groupName
              })
            })
            if (this.data.formFieldGroupSumInfo) {
              let arr = JSON.parse(this.data.formFieldGroupSumInfo)
              if (arr && arr.length > 0) {
                arr.forEach(item => {
                  let index = this.list.findIndex(lItem => lItem.fieldGroupUUId === item.uuid + '');
                  if (index > -1) {
                    this.list[index].count = item.score
                  }
                })
              }
            }
          }
        })
    }
  }
}
</script>

<style lang="less" scoped>
.myCenter {
  width: 100vw;
  height: 100vh;
  background: #FAFAFA;

  .headerContent {
    background-size: 100% 200px;
    background-repeat: no-repeat;
    padding: 26px 17px;
    overflow: hidden;
    position: relative;

    .content {
      margin-top: 100px;
      padding: 19px;
      background: #FFFFFF;
      border-radius: 9px;
      position: relative;
      box-shadow: 2px 1px 5px grey;
    }

    .box {
      display: flex;
      align-items: center;
      margin-top: 10px;
      justify-content: space-between;

      .label {
        font-size: 14px;
        color: #FFF;
        background: #3F86FF;
        width: 70px;
        text-align: center;
        height: 35px;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 40px;
      }

      .value {
        font-size: 24px;
        color: #333;
      }
    }

    .list_title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 16px;
      font-weight: 500;
      color: #666;
      margin-top: 20px;
    }

    .list_label {
      color: #303133;
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}
</style>
<style lang="less">
  .scoreQuestionnaireAnalysis img {
    max-width: 100%;
  }
</style>
