<template>
  <div>
    <div v-for="(i, k) in list" :key="k">
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
      <div v-else style="background: white; marginTop: 15px">
        <van-cell :title="getTime(i.createTime)" clickable is-link @click="addData(i.id)" >
          <!-- 使用 right-icon 插槽来自定义右侧图标 -->
          <template #icon>
            <div slot="icon" style="background: #66728B; width: 5px; height: 18px; margin-right: 6px; " />
          </template>
        </van-cell>
        <div style="padding: 0px 15px 15px 15px" @click="addData(i.id)">
          <div style="border-radius: 10px; background: #F5F5F5; padding: 10px 15px; font-size: 14px;">
            <div v-for="(item, index) in getArray(i.jsonContent)" :key="index">
              <div style="display: flex" v-if="getFormValue(item).type === 'text'">
                <div style="color: #999999; min-width: 80px;">{{item.label}}：</div>
                <div style="color: #333333; flex: 1; margin-left: 5px; text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">
                  {{getFormValue(item).value}} {{getFormValue(item).unit}}
                </div>
              </div>
              <div v-if="getFormValue(item).type !== 'text'&&item.widgetType!=='Desc'" style="display: flex;">
                <div style="display: flex">
                  <div style="color: #999999; min-width: 80px;">{{item.label}}：</div>
                  <van-row style="margin-top: 5px;" type="flex"  justify="space-between">
                    <van-col span="6" v-for="(i, k) in getImage(item.values)" :key="k" v-if="k < 3">
                      <div class="flex-demo" v-if="i.attrValue">
                        <van-image v-if="k < 3" width="4rem" height="4rem" style="margin-right: 10px;" :src="i.attrValue" />
                      </div>
                    </van-col>
                  </van-row>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Icon, Cell, Image} from 'vant'
import {getValue} from '@/components/utils/index'
import moment from 'moment'

Vue.use(Icon)
Vue.use(Col)
Vue.use(Image)
Vue.use(Cell)
Vue.use(Row)
export default {
  props: {
    list: {
      type: Array
    }
  },
  created () {
    console.log(this.rightText)
  },
  methods: {
    getScore (item) {
      this.$emit('getScore', item.id)
    },
    addData (id) {
      console.log('addData')
      this.$emit('addData', id)
    },
    getTime (time) {
      return moment(time).format('YYYY/MM/DD HH:mm')
    },
    getFormValue (item) {
      return getValue(item)
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
    }
  }
}
</script>
