<template>
  <div >
    <div style="padding: 0 15px">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
          style="margin-bottom: 70px"
        >
          <div v-if="contentList.length > 0" id="list" style="margin-top: 19px;background: #F7F7F7">
            <van-swipe-cell :before-close="beforeClose"  v-for="(item,index) in contentList" :key="index">
              <div class="card" @click="goHealthForm(item)">
                <div class="title">
                  <div class="time">{{ getDay(item.createTime) }}</div>
                  <div class="goDetails">查看详情</div>
                </div>
                <div class="questions" v-for="(i,k) in item.showList" :key="k">
                  <div style="width: 30%">{{ i.label }}：</div>
                  <div class="answer text">
                    <div class="text" :style="{color:getValues(i)==='请输入内容'?'#999':''}">{{ getValues(i) }}</div>
                    <van-icon name="arrow"/>
                  </div>
                </div>
              </div>
              <template slot="right">
                <van-button @click="listIndex = index" square text="删除" type="danger" class="delete-button"/>
              </template>
            </van-swipe-cell>
          </div>
          <div v-if="!loading && contentList.length === 0" class="noData" >
            <van-image :src="require('@/assets/my/noData.png')" width="70%"/>
            <div>{{patient}}未添加数据</div>
            <div style="margin-top: 5px;">点击<span style="background: #67E0A7; color: white; border-radius: 15px; padding: 3px 8px; margin: 0px 3px;" @click="goRecommend">推荐功能</span>则可将该功能推送至{{patient}}填写
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
    <div style="padding: 0 15px">
      <div
        style="height: 45px;text-align: center;background: #67E0A7;color: #fff;
      margin: 19px 0 13px 0;line-height: 47px;border-radius: 24px;position: fixed;bottom: 13px;width: 92%"
        @click="goEditor">
        新增数据
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import {SwipeCell, Toast, Button, PullRefresh, List} from 'vant'
import {healthFormResultList, updateForDeleteFormResult} from '@/api/formResult.js'
import moment from 'moment'

Vue.use(SwipeCell)
Vue.use(Toast)
Vue.use(Button)
Vue.use(PullRefresh)
Vue.use(List)
export default {
  props: {
    pageTitle: {
      type: String,
      default: '疾病信息'
    }
  },
  data () {
    return {
      boxOpen: false,
      params: {
        'current': 1,
        'map': {},
        'model': {
          'userId': localStorage.getItem('patientId')
        },
        'order': 'descending',
        'size': 10,
        'sort': 'id'
      },
      contentList: [],
      loading: false,
      finished: false,
      refreshing: false,
      listIndex: undefined,
      patient: this.$getDictItem('patient')
    }
  },
  mounted () {
    this.healthFormResultList()
    localStorage.setItem('pageTitle', this.pageTitle)
  },
  methods: {
    goRecommend () {
      this.$emit('recommendation', 'HEALTH')
    },
    beforeClose ({ position, instance }) {
      switch (position) {
        case 'right':
          updateForDeleteFormResult(this.contentList[this.listIndex].id).then(res => {
            console.log(res)
            if (res.code === 0) {
              Toast('删除成功!')
              this.contentList.splice(this.listIndex, 1)
              instance.close()
            }
          })
      }
    },
    goEditor () {
      this.$router.push({
        path: '/health/editor'
      })
    },
    goHealthForm (item) {
      if (item.showList.length === 1 && (item.showList[0].widgetType === 'Avatar' || item.showList[0].widgetType === 'MultiImageUpload')) {
        this.$router.push({
          path: '/health/imageList',
          query: {
            formId: item.id
          }
        })
      } else {
        this.$router.push({
          path: '/health/detail',
          query: {
            formId: item.id
          }
        })
      }
    },
    getValues (item) {
      const value = item.values
      if (value && value.length > 0) {
        // 单行 多行 数字 日期 姓名
        if (item.exactType !== 'hospital' &&
          (item.widgetType === 'SingleLineText' || item.widgetType === 'MultiLineText' || item.widgetType === 'Number' || item.widgetType === 'Time' || item.widgetType === 'Date' || item.widgetType === 'FullName')) {
          const attrValue = value[0].attrValue ? value[0].attrValue : '请输入内容'
          if (attrValue !== '请输入内容' && item.rightUnit) {
            return attrValue + item.rightUnit
          } else if (!item.rightUnit) {
            return attrValue
          }
        }
        // 单选 下拉
        if (item.widgetType === 'Radio' || item.widgetType === 'DropdownSelect' || item.widgetType === 'SingleLineText' || item.exactType === 'hospital') {
          if (value[0].valueText) {
            return value[0].valueText ? value[0].valueText : '请输入内容'
          } else {
            return '请输入内容'
          }
        }
        if (item.widgetType === 'CheckBox') { // 多选
          return value[0].valueText
        }
        if (item.widgetType === 'MultiImageUpload' || item.widgetType === 'Avatar') {
          return '图片详情'
        }
        if (item.widgetType === 'Address') {
          let str = ''
          value[0].attrValue.forEach(item => {
            str += item
          })
          // 如果有备注
          if (item.value) {
            str += item.value
          }
          return str
        }
      } else {
        return '请输入内容'
      }
    },
    getDay (day) {
      return moment(day).format('YYYY-MM-DD')
    },
    healthFormResultList () {
      if (this.loading) {
        return
      }
      this.loading = true
      healthFormResultList(this.params).then(res => {
        if (res.code === 0) {
          this.loading = false
          if (this.params.current === 1) {
            this.contentList = res.data.records
          } else {
            this.contentList = [...this.contentList, ...res.data.records]
          }
          this.contentList.forEach(item => {
            const jsonContent = JSON.parse(item.jsonContent).slice(0, 3)
            item.showList = jsonContent
          })
          console.log(this.contentList)
          this.params.current++
          this.loading = false
          if (this.params.current >= res.data.pages) {
            this.finished = true
          }
        }
      })
    },
    onLoad () {
      if (this.refreshing) {
        this.contentList = []
        this.params.current = 1
        this.refreshing = false
      }
      this.healthFormResultList()
    },
    onRefresh () {
      this.params.current = 1
      this.finished = false
      this.onLoad()
    }
  }
}

</script>

<style scoped lang="less">
/deep/ .van-swipe-cell {
  margin-bottom: 19px;
  .van-swipe-cell__wrapper {
    background: #F7F7F7 !important;
  }

  //background: #337eff;
}

.noData {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-size: 13px;
  color: #999;
  padding-top: 80px;
}

.card {
  background: #FFFFFF;
  border-radius: 9px;
  padding: 13px 15px;
  z-index: 99;

  .title {
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid #F5F5F5;
    font-size: 13px;
    padding-bottom: 13px;

    .goDetails {
      color: #67E0A7;
    }
  }

  .questions {
    margin-top: 13px;
    font-size: 13px;
    color: #999999;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .answer {
      color: #333333;
      width: 50%;
      text-align: right;
      display: flex;
      align-items: center;
      justify-content: flex-end;
    }

    .text {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap
    }
  }
}

.delete-button {
  height: 100%;
  border-radius: 9px;
  background: #FD4E4E;
  border: none;
  margin-left: 2px;
}

.delete-button-open {
  background: #FD4E4E !important;
  border-radius: 0 9px 9px 0;
}

.delete-button-close {
  background: #fff !important;
  border-radius: 9px
}
</style>
