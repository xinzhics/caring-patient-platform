<template>
  <section>
    <van-sticky :offset-top="0">
      <headNavigation leftIcon="arrow-left" title="历史用药记录"
                      @onBack="$router.go(-1)" ></headNavigation>
    </van-sticky>
    <div style="width: 100%">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" v-if="show"
                        style="background-color: #fafafa; min-height: 90vh">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <div v-for="(i,k) in list" :key="k">
            <div style="margin: 15px; background: white; padding: 0px 15px; border-radius: 8px;">
              <div style="padding: 10px 0px; border-bottom: #E8E8E8 solid 1px; font-size: 15px; color: #333333">
                时间： {{i.historyDate}}
              </div>
              <div style="padding: 10px 0px;">
                <div style="display: flex">
                  <!--左侧-->
                  <div>
                    <div style="display: flex; align-items: center; max-width: 150px;">
                      <div style="border-radius: 4px; width: 8px; height: 8px; background: #34C943"></div>
                      <span style="color: #666666; font-size: 14px; margin-left: 5px;">新增用药：</span>
                    </div>
                  </div>
                  <!--右侧-->
                  <div v-if="getArray(i, 'CREATED').length > 0">
                    <div style="font-size: 14px; color: #333333"
                         v-for="(newI, newK) in getArray(i, 'CREATED')"
                         :key="newK">{{newI.medicineName}}</div>
                  </div>
                  <div v-else>
                    <div style="font-size: 14px; color: #333333">-</div>
                  </div>
                </div>

                <div style="display: flex; margin-top: 5px;">
                  <!--左侧-->
                  <div>
                    <div style="display: flex; align-items: center; max-width: 150px;">
                      <div style="border-radius: 4px; width: 8px; height: 8px; background: #FEA900"></div>
                      <span style="color: #666666; font-size: 14px; margin-left: 5px;">修改用药：</span>
                    </div>
                  </div>
                  <!--右侧-->
                  <div v-if="getArray(i, 'UPDATED').length > 0">
                    <div style="font-size: 14px; color: #333333; margin-bottom: 5px;" v-for="(putI, putK) in getArray(i, 'UPDATED')" :key="putK">
                      <div>{{putI.medicineName}}</div>
                      <div style="font-size: 12px; color: #999999">改前：一次 {{(putI.beforeCurrentDose === -1 ? '外用' : putI.beforeCurrentDose)}}{{putI.beforeCurrentUnit ? putI.beforeCurrentUnit : ''}} {{putI.beforeCurrentNumberOfDay}}
                        {{putI.beforeTimePeriod=='day'?'天':putI.beforeTimePeriod=='hour'?'小时':putI.beforeTimePeriod=='week'?'周':'月'}}
                        {{putI.beforeCurrentNumberOfDay}}次</div>
                      <div style="font-size: 12px; color: #999999">改后：一次 {{(putI.currentDose === -1 ? '外用' : putI.currentDose)}}{{putI.currentUnit ? putI.currentUnit : ''}}  {{putI.currentCycleDuration}}
                        {{putI.currentTimePeriod=='day'?'天':putI.currentTimePeriod=='hour'?'小时':putI.currentTimePeriod=='week'?'周':'月'}}
                        {{putI.currentNumberOfDay}}次</div>
                    </div>
                  </div>
                  <div v-else>
                    <div style="font-size: 14px; color: #333333; margin-bottom: 5px;">
                      <div>-</div>
                    </div>
                  </div>
                </div>

                <div style="display: flex; margin-top: 5px;">
                  <!--左侧-->
                  <div>
                    <div style="display: flex; align-items: center; max-width: 150px;">
                      <div style="border-radius: 4px; width: 8px; height: 8px; background: #3F86FF"></div>
                      <span style="color: #666666; font-size: 14px; margin-left: 5px;">当前用药：</span>
                    </div>
                  </div>
                  <!--右侧-->
                  <div>
                    <div style="font-size: 14px; color: #333333; margin-bottom: 5px;" v-for="(currentI, currentK) in getArray(i, 'CURRENT')" :key="currentK">
                      <div>{{currentI.medicineName}}</div>
                      <div style="font-size: 12px; color: #999999">一次  {{(currentI.currentDose === -1 ? '外用' : currentI.currentDose)}}{{currentI.currentUnit ? currentI.currentUnit : ''}}  {{currentI.currentCycleDuration}}
                        {{currentI.currentTimePeriod=='day'?'天':currentI.currentTimePeriod=='hour'?'小时':currentI.currentTimePeriod=='week'?'周':'月'}}
                        {{currentI.currentNumberOfDay}}次</div>
                    </div>
                  </div>
                </div>
                <div style="display: flex; margin-top: 5px;">
                  <!--左侧-->
                  <div>
                    <div style="display: flex; align-items: center; max-width: 150px;">
                      <div style="border-radius: 4px; width: 8px; height: 8px; background: red"></div>
                      <span style="color: #666666; font-size: 14px; margin-left: 5px;">停止用药：</span>
                    </div>
                  </div>
                  <!--右侧-->
                  <div v-if="getArray(i, 'STOP').length > 0">
                    <div style="font-size: 14px; color: #333333"
                         v-for="(newI, newK) in getArray(i, 'STOP')"
                         :key="newK">{{newI.medicineName}}</div>
                  </div>
                  <div v-else>
                    <div style="font-size: 14px; color: #333333">-</div>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </van-list>
      </van-pull-refresh>
      <div v-if="!show && !loading">
        <div style="width:143px ;margin:132px auto">
          <img src="../../../assets/patient/noData.png" alt="" style="width:100%">
          <div style="color: #999999;font-size: 15px;text-align: center;margin-top: 21px">
            暂无数据
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
<script>
import { getMedicineHistory } from '@/api/drugsApi.js'
import Vue from 'vue'
import {Cell, Col, Icon, List, PullRefresh, Row, Search, Sticky} from 'vant'
Vue.use(List)
Vue.use(PullRefresh)
Vue.use(Search)
Vue.use(Sticky)
Vue.use(Icon)
Vue.use(Row)
Vue.use(Col)
Vue.use(Cell)
export default {
  data () {
    return {
      show: false,
      current: 0,
      loading: false,
      finished: true,
      refreshing: false,
      list: []
    }
  },
  mounted () {
    this.onRefresh()
  },
  methods: {
    getArray (item, type) {
      let data = []
      for (let i = 0; i < item.data.length; i++) {
        if (type === item.data[i].operateType) {
          data.push(item.data[i])
        }
      }
      return data
    },
    onRefresh () {
      this.finished = false
      this.current = 0
      this.list = []
      this.onLoad()
      console.log('onRefresh')
    },
    onLoad () {
      this.loading = true
      if (this.loading) {
        getMedicineHistory(this.current).then(res => {
          this.loading = false
          this.refreshing = false
          console.log('getMedicineHistory', res)
          if (res.code === 0 && res.data) {
            this.show = true
            let flag = false
            if (this.current === 0) {
              for (let i = 0; i < res.data.length; i++) {
                if (this.list.length === 0) {
                  this.list.push({
                    historyDate: res.data[0].historyDate,
                    data: [res.data[0]]
                  })
                } else {
                  for (let a = 0; a < this.list.length; a++) {
                    flag = true
                    if (this.list[a].historyDate === res.data[i].historyDate) {
                      flag = false
                      this.list[a].data.push(res.data[i])
                    }
                  }
                  if (flag) {
                    flag = false
                    this.list.push({
                      historyDate: res.data[i].historyDate,
                      data: [res.data[i]]
                    })
                  }
                }
              }
            } else {
              for (let i = 0; i < res.data.length; i++) {
                for (let a = 0; a < this.list.length; a++) {
                  flag = true
                  if (this.list[a].historyDate === res.data[i].historyDate) {
                    flag = false
                    this.list[a].data.push(res.data[i])
                  }
                }
                if (flag) {
                  flag = false
                  this.list.push({
                    historyDate: res.data[i].historyDate,
                    data: [res.data[i]]
                  })
                }
              }
            }
          }
          if (!res.data) {
            this.finished = true
          } else {
            this.finished = false
            this.current++
          }
        })
      }
    }
  }
}
</script>
