<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">{{pageTitle ? pageTitle : '健康日志'}}
      <img slot="right" @click="$router.push('/healthCalendar/editor')" style="width:20px" :src="addImg" alt="" srcset="">
    </x-header>
    <!-- <x-header :left-options="{backText: ''}">健康日志</x-header> -->
    <div v-if="allData.length == 0">
      <div class="nodata">
        <img :src="noData" alt="" style="padding-top: 150px;">
        <p>暂未添加{{pageTitle ? pageTitle : '健康日志'}}</p>
      </div>
    </div>

    <div v-else style="padding-top: 50px;">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa;">
        <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
          <group v-for="(i,k) in allData" :key="k">
            <div style="background: white; marginTop: 15px" @click="Goitem(i)">
              <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
                <div style="display: flex; align-items: center">
                  <van-icon name="todo-list-o" style="margin-right: 5px;" size="20px" />
                  {{getTime(i.createTime)}}
                </div>
                <div style="position: absolute; right: 15px">
                  <van-icon name="arrow" style="float: right" />
                </div>

              </div>
              <div style="padding: 10px 15px 15px 15px">
                <div style="border-radius: 10px; background: #F5F5F5; padding: 10px 15px;">
                  <div v-for="(item, index) in getArray(i.jsonContent)" :key="index">
                    <div style="display: flex" v-if="getFormValue(item).type === 'text'">
                      <div style="color: #999999">{{item.label}}：</div>
                      <div style="color: #333333">{{getFormValue(item).value}}
                      </div>
                    </div>
                    <div v-if="getFormValue(item).type !== 'text'&&item.widgetType!=='Desc'" style="display: flex; margin-top: 10px;">
                      <flexbox>
                        <flexbox-item v-for="(i, k) in getImage(item.values)" :key="k" v-if="k < 4">
                          <div class="flex-demo" v-if="i.attrValue">
                            <van-image v-if="k < 3" width="4rem" height="4rem" style="margin-right: 10px;" :src="i.attrValue" />
                            <van-image v-if="k === 3" width="4rem" height="4rem" style="margin-right: 10px;" :src="require('@/assets/my/threePoints.png')" />
                          </div>
                          <!--如图片不够4个，则需要空白位置填充-->
                          <div v-else>

                          </div>
                        </flexbox-item>
                      </flexbox>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </group>
        </van-list>
      </van-pull-refresh>
    </div>
  </section>
</template>
<script>
import { Group, Cell, CellBox } from 'vux'
import Api from '@/api/Content.js'
import { getValue } from '@/components/utils/index'
import { Constants } from '@/api/constants.js'

export default {
  components: {
    Group,
    Cell,
    CellBox
  },
  data() {
    return {
      noData: require('@/assets/my/medicineImg.png'),
      addImg: require('@/assets/my/add.png'),
      value: '',
      current: 1,
      loading: false,
      finished: false,
      refreshing: false,
      allData: [],
      pageTitle: localStorage.getItem('pageTitle')
    }
  },
  mounted() {
    this.getInfo()
  },
  beforeMount() {
    // window.document.title=Base64.decode(localStorage.getItem('headerTenant'))
    let a = JSON.parse(localStorage.getItem('projectInfo'))
    console.log(a.name)
    window.document.title = a.name
  },
  methods: {
    getImage(values) {
      if (values) {
        if (values.length === 1) {
          values.push({ attrValue: '' })
          values.push({ attrValue: '' })
          values.push({ attrValue: '' })
        } else if (values.length === 2) {
          values.push({ attrValue: '' })
          values.push({ attrValue: '' })
        } else if (values.length === 3) {
          values.push({ attrValue: '' })
        }
      }
      return values
    },
    getFormValue(item) {
      return getValue(item)
    },
    getTime(time) {
      return moment(time).format('YYYY年MM月DD日')
    },
    getArray(json) {
      let list = []
      let flag = false
      let jsonList = JSON.parse(json)
      for (let i = 0; i < jsonList.length; i++) {
        if (i < 4) {
          list.push(jsonList[i])
        } else if (
          !flag &&
          (jsonList[i].widgetType === Constants.CustomFormWidgetType.SingleImageUpload ||
            jsonList[i].widgetType === Constants.CustomFormWidgetType.MultiImageUpload)
        ) {
          flag = true
          list.push(jsonList[i])
        }
      }

      return list
    },
    onRefresh() {
      // 清空列表数据
      this.finished = false
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true
      this.onLoad()
    },
    onLoad() {
      setTimeout(() => {
        if (this.refreshing) {
          this.list = []
          this.current = 1
          this.refreshing = false
        }
        this.getInfo()
      }, 500)
    },
    getInfo() {
      const params = {
        type: 5,
        current: this.current,
        patientId: localStorage.getItem('patientId')
      }
      Api.getCheckData(params).then(res => {
        if (res.data.code === 0) {
          console.log(res, '=====================<')
          if (this.current === 1) {
            this.allData = res.data.data.records
          } else {
            this.allData.push(...res.data.data.records)
          }
          this.current++
          this.loading = false
          if (res.data.data.records && res.data.data.records.length < 30) {
            this.finished = true
          }
        }
      })
    },
    Goitem(k) {
      this.$router.push({
        path: '/healthCalendar/editor',
        query: {
          content: k.id
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
