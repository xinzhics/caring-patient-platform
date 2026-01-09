<template>
  <section>

    <x-header :left-options="{backText: ''}" @on-click-back="$router.push('/home')">{{pageTitle ? pageTitle : '随访'}}
      <img v-if="allData.length != 0" slot="right" @click="$router.push(`/custom/follow/${planId}/editor`)" style="width:20px" :src="addImg" alt="" srcset="">
    </x-header>
    <div v-if="allData.length == 0" style="height:100vh;background:#f5f5f5">
       <div  style="padding-top:100px">
      <div class="nodata">
        <img :src="noData" alt="">
        <p>您暂未添加{{pageTitle ? pageTitle : '随访'}}</p>
        <p>请点击下方添加按钮进行添加</p>

        <x-button style="background:#66728C;width:40%;color:#fff;margin:15vw auto" @click.native="$router.push(`/custom/follow/${planId}/editor`)">添加
        </x-button>
      </div>
    </div>
    </div>

    <div v-if="allData.length != 0" style="padding-top: 50px;">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa;">
        <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
          <div v-for="(i, k) in allData" :key="k" @click="Goitem(i)">
            <div v-if="i.scoreQuestionnaire === 1" style="width: auto; margin: 15px 10px; background-color: #fff; border-radius: 12px;">
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
            <div v-else style="background: white; marginTop: 15px">
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
                        <div v-else style="display: flex; ">
                          <flexbox>
                            <flexbox-item v-for="(i, k) in getImage(item.values)" :key="k" v-if="k < 4&&i.attrValue">
                              <div class="flex-demo" v-if="i.attrValue" style="margin-top: 10px;">
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
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>

  </section>
</template>
<script>
import { Group,  } from 'vux'
import Api from '@/api/Content.js'
import { getValue } from '@/components/utils/index'
import { Constants } from '@/api/constants.js'
import Vue from 'vue'
import { Cell} from 'vant'
Vue.use(Cell)
export default {
  components: {
    Group
  },
  data() {
    return {
      planId: this.$route.params.planId,
      pageTitle: localStorage.getItem('pageTitle'),
      noData: require('@/assets/my/medicineImg.png'),
      addImg: require('@/assets/my/add.png'),
      value: '',
      current: 1,
      loading: false,
      finished: false,
      refreshing: false,
      allData: []
    }
  },
  created() {
    const routerData = localStorage.getItem('routerData')
    const path = this.$route.path
    if (routerData && routerData.length > 0) {
      const routerDataArray = JSON.parse(routerData)
      for (let i = 0; i < routerDataArray.length; i++) {
        if (path.indexOf(routerDataArray[i].path) > -1) {
          localStorage.setItem('pageTitle', routerDataArray[i].name)
        }
      }
    }
  },
  mounted() {
    this.getInfo()
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
      // console.log(item,"======================>");
      return getValue(item)
    },
    getTime(time) {
      return moment(time).format('YYYY年MM月DD日')
    },
    getArray(json) {
      let list = []
      let flag = false
      let jsonList = JSON.parse(json)
      // console.log(jsonList,"=====================================?>");
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
      // console.log(list);
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
        planId: this.planId,
        current: 1,
        patientId: localStorage.getItem('patientId')
      }
      Api.getCustomPlanForm(params).then(res => {
        if (res.data.code === 0) {
          console.log(res)
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
      if (k.scoreQuestionnaire === 1) {
        // 评分表单
        this.$router.push({
          path: '/score/show',
          query: {
            dataId: k.id,
            title: this.pageTitle,
            id: this.planId,
          }
        })
      } else {
        this.$router.push({
          path: '/healthCalendar/editor',
          query: {
            content: k.id
          }
        })
      }
    }
  }
}

</script>


<style lang="less" scoped>
#app{
  height: 100vh !important;
  background-color: #fafafa !important;

}
.allContent {
  width: 100vw;
  height: 100vh;
}

.nodata {
  width: 100vw;
  // height: calc(100vh -150px);


// height: calc(100vh);
  text-align: center;
  font-size: 14px;
  color: rgba(102, 102, 102, 0.85);
  background: #f5f5f5 !important;
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

