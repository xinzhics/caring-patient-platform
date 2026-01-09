<template>
  <section style="line-height: 1.1875rem /* 19/16 */">
    <van-sticky>
      <headNavigation leftIcon="arrow-left"
                      :title="'评分结果'" @onBack="back"/>
    </van-sticky>
    <div style="background: white; margin-top: 1px; padding: 15px">
      <div class="content">
        <div class="box" v-if="scoreRule.showResultSum === 1">
          <div class="label" style="font-size: 16px; font-weight: 500"><span>总分</span></div>
          <div class="value" v-if="scoreRule.formResultCountWay === 'sum_score'">{{ data.formResultSumScore }}分</div>
          <div class="value" v-if="scoreRule.formResultCountWay === 'average_score'">{{ data.formResultAverageScore }}分
          </div>
          <div class="value" v-if="scoreRule.formResultCountWay === 'sum_average_score'">
            {{ data.formResultSumAverageScore }}分
          </div>
        </div>
        <div class="box" v-if="scoreRule.showAverage === 1">
          <div class="label"><span>平均分</span></div>
          <div class="value">{{ data.formResultAverageScore }}分</div>
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
    <attrPage v-if="currentFields && currentFields.length > 0" :current-fields="currentFields"></attrPage>
  </section>
</template>

<script>
import Vue from 'vue'
import {Icon, Tag, Col, Row} from 'vant'
import {formResultDetail} from '@/api/formApi.js'
import {getFormResultScore} from '@/api/patient.js'
import attrPage from '@/components/arrt/editorCopy'

Vue.use(Col)
Vue.use(Row)
Vue.use(Tag)
Vue.use(Icon)
export default {
  components: {
    attrPage
  },
  data () {
    return {
      planId: '', // 计划id
      dataId: '', // 数据id
      currentFields: [],
      list: [],
      data: {},
      scoreRule: {}
    }
  },
  created () {
    if (this.$route.query && this.$route.query.dataId) {
      this.dataId = this.$route.query.dataId
      this.getData()
      this.getFormResultScore()
    }
  },
  methods: {
    back () {
      this.$router.go(-1)
    },
    getData () {
      formResultDetail(this.dataId).then((res) => {
        this.currentFields = JSON.parse(res.data.jsonContent)
        this.$forceUpdate()
      })
    },
    getFormResultScore () {
      getFormResultScore(this.dataId)
        .then(res => {
          if (res.data) {
            this.scoreRule = res.data.scoreRule
            this.data = res.data
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
                  let index = this.list.findIndex(lItem => lItem.fieldGroupUUId === item.uuid + '')
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
.content {
  width: auto;
  background: #FFFFFF;
  border-radius: 9px;
  position: relative;
  padding: 12px;
  box-shadow: 2px 1px 5px grey;

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
    font-size: 14px;
    color: #666;
    margin-top: 20px;
  }

  .list_label {
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

</style>
<style lang="less">
  .scoreQuestionnaireAnalysis img {
    max-width: 100%;
  }
</style>
