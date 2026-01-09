<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" title="预约设置" @onBack="goback"></headNavigation>
      <van-search
        v-model="doctorName"
        show-action
        shape="round"
        background="#ffffff"
        placeholder="请输入"
        @search="onSearch"
        :clearable="false"
      >
        <template #action>
          <div @click="onSearch">搜索</div>
        </template>
      </van-search>
    </van-sticky>
    <!--    列表部分-->
    <van-pull-refresh v-model='refreshing' @refresh='onRefresh'>
      <van-list
        v-model='loading'
        :finished='finished'
        finished-text='没有更多了'
        @load='onLoad'>
        <div style="padding: 0">
          <div v-for="doctor in doctorList" :key="doctor.id" @click="goDocReservation(doctor)" class="list">
            <div class="list-left">
              <div style="margin-right: 14px">
                <van-image
                  round
                  width="64px"
                  height="64px"
                  :src="doctor.avatar"
                />
              </div>
              <div>
                <div style="color: #333333">{{ doctor.name }}</div>
                <div style="font-size: 13px;color:#999999;margin-top: 9px"></div>
              </div>
            </div>
            <div class="list-right">
              <div v-if="doctor.closeAppoint && doctor.closeAppoint === 1">
                <van-button round style="width: 65px;color: #999999" size="small" plain type="default">已关闭
                </van-button>
              </div>
              <div v-else>
                <van-button round style="width: 65px" size="small" plain type="info">已开启</van-button>
              </div>
              <div>
                <van-icon size="20px" color="#B8B8B8" name="arrow"/>
              </div>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>
<script>
import Vue from 'vue'
import {
  Popup,
  PullRefresh,
  List,
  Button,
  Image,
  DropdownMenu,
  DropdownItem,
  Sticky,
  Row,
  Col,
  Icon,
  Tab,
  Tabs,
  Dialog,
  Toast,
  Search
} from 'vant'
import {appointmentDoctorPage} from '@/api/appointment.js'

Vue.use(DropdownMenu)
Vue.use(Search)
Vue.use(DropdownItem)
Vue.use(PullRefresh)
Vue.use(List)
Vue.use(Popup)
Vue.use(Button)
Vue.use(Image)
Vue.use(Sticky)
Vue.use(Row)
Vue.use(Col)
Vue.use(Icon)
Vue.use(Tab)
Vue.use(Tabs)
Vue.use(Dialog)
Vue.use(Toast)
export default {
  name: 'reservation',
  data () {
    return {
      nursingId: localStorage.getItem('caringNursingId'),
      doctorList: [],
      doctorName: '',
      current: 1,
      loading: false,
      finished: false,
      refreshing: false
    }
  },
  methods: {
    // 根据医生名称搜索
    onSearch () {
      this.onRefresh()
    },
    /**
     * 跳转到上一页
     */
    goback () {
      this.$router.replace({
        path: '/reservation'
      })
    },
    /**
     * 跳转到医生预约设置
     */
    goDocReservation (item) {
      this.$router.push({
        path: '/reservation/docReservation',
        query: {
          doctorId: item.id,
          status: item.closeAppoint,
          appointmentReview: item.appointmentReview,
          doctorName: item.name
        }
      })
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.doctorList = []
      this.current = 1
      this.loading = true
      this.onLoad()
    },
    onLoad () {
      const params = {
        current: this.current,
        model: {
          nursingId: this.nursingId,
          doctorName: this.doctorName
        },
        order: 'descending',
        size: 10,
        sort: 'id'
      }
      appointmentDoctorPage(params).then(res => {
        console.log(res)
        if (res.data) {
          this.doctorList.push(...res.data.records)
          if (res.data.pages === 0 || res.data.pages === this.current) {
            this.finished = true
          } else {
            this.finished = false
            this.current++
          }
        }
        this.loading = false
        this.refreshing = false
      })
    }
  }
}
</script>

<style scoped lang="less">
.list {
  padding: 22px 13px;
  background: #ffffff;
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .list-left {
    display: flex;
    align-items: center;
  }

  .list-right {
    display: flex;
    align-items: center;
  }
}
</style>
