<template>
  <div style="width: 100vw">
    <navBar :isGroup="true"  :pageTitle=" pageTitle !== undefined ? pageTitle : '疾病信息'"/>
    <div v-if="showPage" :style="{height: pageHeight + 'px'}" style="overflow-y: auto">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model="loading"
          v-if="contentList.length>0"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div id="list" style="margin: 19px 0;background: #F7F7F7; padding: 0 15px">
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
        </van-list>
        <div v-if="showPage&&contentList.length===0" class="no_data">
          <van-image
            width="250"
            height="120"
            fit="fill"
            :src="require('@/assets/my/system_no_data.png')"
          />
          <div style="font-size: 16px; color: #999999; margin-top: 30px">暂无数据</div>
        </div>
      </van-pull-refresh>
    </div>
    <div
      style="height: 47px;text-align: center;background: #67E0A7;color: #fff; left: 4%; right: 4%;
      margin: 19px 0 13px 0;line-height: 47px;border-radius: 24px;position: fixed;bottom: 13px"
      @click="goEditor">
      新增{{ pageTitle }}
    </div>
  </div>
</template>

<script>
import Vue from 'vue';
import {Icon, SwipeCell, Button, List, PullRefresh, Image as VanImage,Dialog} from 'vant';
import Api from '@/api/Content.js'

Vue.use(Icon);
Vue.use(Dialog);
Vue.use(VanImage);
Vue.use(SwipeCell);
Vue.use(Button);
Vue.use(List);
Vue.use(PullRefresh);
export default {
  name: "healthList",
  components: {
    navBar: () => import('@/components/headers/navBar')
  },
  data() {
    return {
      pageWidth: window.innerWidth,
      pageHeight: window.innerHeight - 46 - 33 - 10 - 20,
      pageTitle: '',
      boxOpen: false,
      params: {
        "current": 1,
        "map": {},
        "model": {
          "userId": localStorage.getItem('userId')
        },
        "order": "descending",
        "size": 10,
        "sort": "id"
      },
      contentList: [],
      loading: false,
      finished: false,
      refreshing: false,
      showPage: false,
      listIndex:undefined
    }
  },
  created() {
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 100)
  },
  mounted() {
    this.healthFormResultList()
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    beforeClose({ position, instance }) {
      switch (position) {
        case 'right':
          Api.updateForDeleteFormResult(this.contentList[this.listIndex].id).then(res=>{
            console.log(res)
            if (res.data.code === 0) {
              this.$vux.toast.text('删除成功!', 'center')
              this.contentList.splice(this.listIndex,1)
              instance.close();
            }
          })
      }
    },
    goEditor() {
      this.$router.push({
        path: '/health/editor',
      })
    },
    goHealthForm(item) {
      if (item.showList.length === 1 &&(item.showList[0].widgetType === 'Avatar'||item.showList[0].widgetType === 'MultiImageUpload')) {
        this.$router.push({
          path: '/health/imageList',
          query: {
            formId: item.id,
          }
        })
      } else {
        this.$router.push({
          path: '/health/list',
          query: {
            formId: item.id,
          }
        })
      }

    },
    getValues(item) {
      const value = item.values
      if (value && value.length > 0) {
        if (item.widgetType === 'SingleLineText' && item.exactType !== 'hospital' || item.widgetType === 'MultiLineText' || item.widgetType === 'Number' || item.widgetType === 'Time'
          || item.widgetType === 'Date' || item.widgetType === 'FullName') { // 单行 多行 数字 日期 姓名
          const attrValue = value[0].attrValue ? value[0].attrValue : '请输入内容'
          if (attrValue !== '请输入内容' && item.rightUnit) {
            return attrValue + item.rightUnit
          } else if (!item.rightUnit) {
            return attrValue
          }
        }
        if (item.widgetType === 'Radio' || item.widgetType === 'DropdownSelect' || item.widgetType === 'SingleLineText' && item.exactType === 'hospital') { // 单选 下拉
          if (value[0].valueText) {
            return value[0].valueText ? value[0].valueText === '其他' && item.otherLabelRemark ? item.otherLabelRemark : value[0].valueText : '请输入内容'
          } else {
            return '请输入内容'
          }

        }
        if (item.widgetType === 'CheckBox') { // 多选
          let str = value[0].valueText === '其他' && item.otherLabelRemark ? item.otherLabelRemark : value[0].valueText
          return str
        }
        if (item.widgetType === 'MultiImageUpload'||item.widgetType==='Avatar') {
          return '图片详情'

        }
        if (item.widgetType === '')
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
    getDay(day) {
      return moment(day).format("YYYY-MM-DD")
    },
    healthFormResultList() {
      Api.healthFormResultList(this.params).then(res => {
        if (res.data.code === 0) {
          this.showPage = true
          if (this.params.current === 1) {
            this.contentList = res.data.data.records
          } else {
            this.contentList = [...this.contentList, ...res.data.data.records]
          }
          this.contentList.forEach(item => {
            const jsonContent = JSON.parse(item.jsonContent).slice(0, 3)
            item.showList = jsonContent
          })
          this.params.current++
          this.loading = false
          if (this.params.current >= res.data.data.pages) {
            this.finished = true;
          }
        }
      })
    },
    onLoad() {
      if (this.refreshing) {
        this.contentList = [];
        this.params.current = 1
        this.refreshing = false;
      }
      this.healthFormResultList()
    },
    onRefresh() {
      this.params.current = 1
      this.loading = true;
      this.finished = false;
      this.onLoad();
    },
  }
}
</script>

<style scoped lang="less">
.no_data {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 150px;
  flex-direction: column;
}

/deep/ .van-swipe-cell {
  margin-bottom: 19px;
  .van-swipe-cell__wrapper {
    background: #F7F7F7 !important;
  }

  //background: #337eff;
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
