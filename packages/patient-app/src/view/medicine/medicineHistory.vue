<template>
  <section style="height: 100vh; background: #F5F5F5">
    <navBar pageTitle="用药记录"></navBar>
    <div style="padding-top: 10px; width: 100%">

      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
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
                      <div style="font-size: 12px; color: #999999">改前：{{ (putI.beforeCurrentDose === -1 ? '外用' : '一次' + putI.beforeCurrentDose)}}{{putI.beforeCurrentUnit ? putI.beforeCurrentUnit : ''}} {{putI.beforeCurrentNumberOfDay}}
                        {{putI.beforeTimePeriod=='day'?'天':putI.beforeTimePeriod=='hour'?'小时':putI.beforeTimePeriod=='week'?'周':'月'}}
                        {{putI.beforeCurrentNumberOfDay}}次</div>
                      <div style="font-size: 12px; color: #999999">改后：{{(putI.currentDose === -1 ? '外用' : '一次' + putI.currentDose)}}{{putI.currentUnit ? putI.currentUnit : ''}}  {{putI.currentCycleDuration}}
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
                      <div style="font-size: 12px; color: #999999">{{(currentI.currentDose === -1 ? '外用' : '一次' + currentI.currentDose)}}{{currentI.currentUnit ? currentI.currentUnit : ''}}  {{currentI.currentCycleDuration}}
                        {{currentI.currentTimePeriod=='day'?'天':currentI.currentTimePeriod=='hour'?'小时':currentI.currentTimePeriod=='week'?'周':'月'}}
                        {{currentI.currentNumberOfDay}}次</div>
                    </div>
                  </div>
                </div>

              </div>
            </div>

          </div>
        </van-list>
      </van-pull-refresh>
    </div>

  </section>
</template>

<script>
import Api from '@/api/Content.js'

export default {
  name: "medicineHistory",
  data() {
    return {
      current: 0,
      loading: false,
      finished: false,
      refreshing: false,
      list: [],
    }
  },
  components:{
    navBar: () => import('@/components/headers/navBar'),
  },
  mounted() {
    this.getData()
  },
  methods: {
    getArray(item, type) {
      let data = []
      for(let i = 0; i < item.data.length; i++) {
        if (type === item.data[i].operateType) {
          data.push(item.data[i])
        }
      }
      return data
    },
    getData() {
      Api.getMedicineHistor(this.current)
          .then(res => {
            if (res.data && res.data.code === 0 && res.data.data && res.data.data.length > 0) {
              let flag = false

              if (this.current === 0) {
                for (let i = 0; i < res.data.data.length; i++) {
                  if (this.list.length === 0) {
                    this.list.push({
                      historyDate: res.data.data[0].historyDate,
                      data: [res.data.data[0]]
                    })
                  }else {
                    for (let a = 0; a < this.list.length; a++) {
                      flag = true
                      if (this.list[a].historyDate === res.data.data[i].historyDate) {
                        flag = false
                        this.list[a].data.push(res.data.data[i])
                      }
                    }
                    if (flag) {
                      flag = false
                      this.list.push({
                        historyDate: res.data.data[i].historyDate,
                        data: [res.data.data[i]]
                      })
                    }
                  }
                }
              } else {
                for (let i = 0; i < res.data.data.length; i++) {
                  for (let a = 0; a < this.list.length; a++) {
                    flag = true
                    if (this.list[a].historyDate === res.data.data[i].historyDate) {
                      flag = false
                      this.list[a].data.push(res.data.data[i])
                    }
                  }
                  if (flag) {
                    flag = false
                    this.list.push({
                      historyDate: res.data.data[i].historyDate,
                      data: [res.data.data[i]]
                    })
                  }
                }
              }
              this.current++;
              this.loading = false;
              if (res.data.data.records && res.data.data.records.length === 0) {
                this.finished = true;
              }
            }else {
              this.finished = true;
            }
          })
    },
    onRefresh() {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      this.onLoad();
    },
    onLoad() {
      setTimeout(() => {
        if (this.refreshing) {
          this.list = [];
          this.current = 0;
          this.refreshing = false;
        }
        this.getData()
      }, 500);
    },
  }
}
</script>

<style less="scss" scoped>

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  left: 0;
}

</style>
