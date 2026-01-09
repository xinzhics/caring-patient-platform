<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :rightIcon="allData.length > 0 ? addImg : ''" :title="pageTitle"
                      @onBack="back" @showpop="toAdd" ></headNavigation>
    </van-sticky>
    <div v-if="allData.length > 0 || dropDownLoading">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa;">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <van-row v-for="(i) in allData" :key="i.id">
            <div v-if="i.scoreQuestionnaire === 1" @click="getScore(i)"
                 style="width: auto; margin: 15px 10px; background-color: #fff; border-radius: 12px;">
              <van-cell :title="getTime(i.createTime, 'YYYY-MM-DD')" style="border-radius: 12px">
                <div style="color: #68E1A8">
                  查看详情
                </div>
              </van-cell>
              <van-cell title="总得分" :border="false" v-if="i.showFormResultSumScore === 1"
                        style="border-radius: 12px">
                <div style="color: #333">
                  {{i.formResultSumScore}}
                </div>
              </van-cell>
              <van-cell title="平均分" v-if="i.showFormResultAverageScore === 1"
                        style="border-radius: 12px;" :style="{marginTop: i.showFormResultSumScore === 1 ? '-5px' : '0px'}">
                <div style="color: #333">
                  {{i.formResultAverageScore}}
                </div>
              </van-cell>
            </div>
            <div v-else style="background: white; marginTop: 15px" @click="editItem(i.id)">
              <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
                <div style="display: flex; align-items: center">
                  <van-icon name="todo-list-o" style="margin-right: 5px;" size="20px"/>
                  {{getTime(i.createTime)}}
                </div>
                <div style="position: absolute; right: 15px">
                  <van-icon name="arrow" style="float: right"/>
                </div>
              </div>
              <div style="padding: 10px 15px 15px 15px">
                <div style="border-radius: 10px; background: #F5F5F5; padding: 10px 15px;">
                  <div v-for="(item, index) in getArray(i.jsonContent)" :key="index">
                    <div style="display: flex" v-if="getFormValue(item).type === 'text'">
                      <div style="color: #999999; min-width: 80px;">{{item.label}}：</div>
                      <div style="color: #333333; flex: 1; margin-left: 5px; text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">
                        {{getFormValue(item).value}}
                      </div>
                    </div>
                    <div v-else style="display: flex;">
                      <van-row type="flex"  justify="space-between">
                        <van-col span="6" v-for="(i, k) in getImage(item.values)" :key="k" v-if="k < 4">
                          <div class="flex-demo" style="margin-top:10px" v-if="i.attrValue">
                            <van-image
                              v-if="k < 3"
                              width="4rem"
                              height="4rem"
                              style="margin-right: 10px;"
                              :src="i.attrValue"
                            />
                            <van-image
                              v-if="k === 3"
                              width="4rem"
                              height="4rem"
                              style="margin-right: 10px;"
                              :src="require('@/assets/patient/threePoints.png')"
                            />
                          </div>
                          <!--如图片不够4个，则需要空白位置填充-->
                          <div v-else>
                          </div>
                        </van-col>
                      </van-row>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </van-row>
        </van-list>
      </van-pull-refresh>
    </div>
    <div v-if="allData.length === 0 && !dropDownLoading" >
      <div class="nodata">
        <img :src="noData" style="padding-top: 0; width: 50%; max-width: 200px">
        <p>暂未添加{{pageTitle ? pageTitle : '数据'}}</p>
        <p>请点击下方添加按钮进行添加</p>
        <van-button
          @click="toAdd"
          style="width: 217px;background: #337EFF;height: 48px;border-radius: 43px;color: #fff;text-align: center;line-height: 48px;margin: 48px auto">
        添加
        </van-button>
      </div>
    </div>
  </div>
</template>
<script>
import {pageFormResultByType, pageCustomForm} from '@/api/formApi.js'
import {getValue} from '@/components/utils/index'
import moment from 'moment'
import Vue from 'vue'
import {Button, List, PullRefresh, Row, Col, Toast, Icon, Sticky, Image, Cell} from 'vant'

Vue.use(Toast)
Vue.use(Sticky)
Vue.use(Row)
Vue.use(Col)
Vue.use(Icon)
Vue.use(Button)
Vue.use(List)
Vue.use(PullRefresh)
Vue.use(Image)
Vue.use(Cell)
export default {
  data () {
    return {
      pageTitle: localStorage.getItem('pageTitle'),
      addImg: require('@/assets/patient/add.png'),
      noData: require('@/assets/patient/noData.png'),
      value: '',
      planId: this.$route.params.planId,
      planType: this.$route.query.planType,
      current: 1,
      loading: true,
      finished: false,
      refreshing: false,
      dropDownLoading: false,
      allData: []
    }
  },
  created () {
    this.onRefresh()
  },
  methods: {
    getScore (i) {
      this.$router.push({
        path: '/score/show',
        query: {
          dataId: i.id,
          id: this.planId,
          title: this.pageTitle,
          backUrl: '/patient/custom/follow/' + this.planId
        }
      })
    },
    /**
     * 去添加
     */
    toAdd () {
      this.$router.push({
        path: '/patient/form/editor',
        query: {
          planType: this.planType,
          planId: this.planId,
          title: this.pageTitle,
          backUrl: '/patient/custom/follow/' + this.planId
        }
      })
    },
    getImage (values) {
      if (values) {
        if (values.length === 1) {
          values.push({attrValue: ''})
          values.push({attrValue: ''})
          values.push({attrValue: ''})
        } else if (values.length === 2) {
          values.push({attrValue: ''})
          values.push({attrValue: ''})
        } else if (values.length === 3) {
          values.push({attrValue: ''})
        }
      }
      return values
    },
    getFormValue (item) {
      return getValue(item)
    },
    getTime (time) {
      return moment(time).format('YYYY年MM月DD日')
    },
    getArray (json) {
      let list = []
      let flag = false
      let jsonList = JSON.parse(json)
      for (let i = 0; i < jsonList.length; i++) {
        if (i < 4) {
          list.push(jsonList[i])
        } else if (!flag && (jsonList[i].widgetType === this.Constants.CustomFormWidgetType.SingleImageUpload ||
          jsonList[i].widgetType === this.Constants.CustomFormWidgetType.MultiImageUpload)) {
          flag = true
          list.push(jsonList[i])
        }
      }
      return list
    },
    onRefresh () {
      this.loading = true
      this.finished = false
      this.current = 1
      this.dropDownLoading = true
      this.allData = []
      this.onLoad()
    },
    onLoad () {
      const patientId = localStorage.getItem('patientId')
      if (this.planId) {
        // 自定义随访表单使用planId
        pageCustomForm(patientId, this.planId, this.current).then((res) => {
          this.loading = false
          this.dropDownLoading = false
          this.refreshing = false
          this.finished = true
          if (res.code === 0) {
            this.allData.push(...res.data.records)
            if (res.data.pages === 0 || res.data.pages === this.current) {
              this.finished = true
            } else {
              this.finished = false
              this.current++
            }
          }
        })
      } else {
        // 健康日志。 复查提醒 使用planType
        pageFormResultByType(patientId, this.current, this.planType).then((res) => {
          this.loading = false
          this.dropDownLoading = false
          this.refreshing = false
          this.finished = true
          if (res.code === 0) {
            this.allData.push(...res.data.records)
            if (res.data.pages === 0 || res.data.pages === this.current) {
              this.finished = true
            } else {
              this.finished = false
              this.current++
            }
          }
        })
      }
    },
    back () {
      this.$router.replace({
        path: '/patient/center'
      })
    },

    /**
     * 去编辑
     * @param k
     */
    editItem (id) {
      this.$router.push({
        path: '/patient/form/editor',
        query: {
          planType: this.planType,
          planId: this.planId,
          formResultId: id
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
  .allContent {
    width: 100vw;
    height: 100vh;
    background-color: #fafafa;
  }

  .nodata {
    width: 100vw;
    height: 100vh;
    text-align: center;
    font-size: 14px;
    color: rgba(102, 102, 102, 0.85);
    background: #f5f5f5;
  }
</style>
