<template>
  <div class="myCenter">
    <div class="headerContent">
      <div class="header">
        <div class="headerImg">
          <div class="headerImglf">
            <div class="imgInner">
              <img :src="baseInfo.avatar ? baseInfo.avatar : 'https://caing-test.obs.cn-north-4.myhuaweicloud.com/23a176879c0c5675556687e5718cc82.png'" alt="" @click="showImage">
            </div>
            <div class="docsInner">
              <p class="name">{{ baseInfo.name }}</p>
              <p class="item">{{ baseInfo.hospitalName }}</p>
            </div>
          </div>
        </div>
        <div class="headerDocs">
          <span @click="() => $router.push({path: '/doctorInfoDetail'})">编辑资料</span>
        </div>
      </div>
      <div class="myItem">
        <div class="item">
          <p class="Number">{{ allData.subscribeNum || '0' }}</p>
          <p class="docs">{{ register }}{{ patient }}</p>
        </div>
        <div class="item">
          <p class="Number">{{ allData.total || '0' }}</p>
          <p class="docs">关注粉丝</p>
        </div>
      </div>
    </div>

    <div style="padding: 0.5rem 1rem 0rem 1rem;">
      <div class="sysBox" @click="() => $router.push('/sysMessage')">

        <van-image
          width="18px"
          height="18px"
          fit="contain"
          :src="require('@/assets/my/home_horn.png')"
        />

        <div class="sysContent">
          {{ sysMessage }}
        </div>

        <van-image
          width="25px"
          height="25px"
          fit="contain"
          :src="require('@/assets/my/home_arrow.png')"
        />
      </div>
    </div>

    <div class="content">
      <van-grid :column-num="3" :border="false" clickable :icon-size="43">
        <van-grid-item  v-for="(i,k) in routerData" :icon="i.iconUrl" :key="k" :text="i.name" style="height: 85.6px; font-size: 16px"
                        :badge="i.badge" @click="clickMenu(i)">
        </van-grid-item>
      </van-grid>
<!--      <grid :show-lr-borders="false" :show-vertical-dividers="false" v-for="(i,k) in list" :key="k+'1'">-->
<!--        <grid-item v-show="z.path"  v-for="(z,index) in i" :key="index" :label="z.name" :link="z.path">-->
<!--          <div slot="icon" style="position: relative;width: 43px">-->
<!--            <badge v-if="z.path==='/im/messageList'&& doctorCount>0|| z.path==='/reservation/index'&&approvalNumber>0"-->
<!--                   :text="z.path==='/im/messageList'?doctorCount:doctorCount>99?'99+':approvalNumber"-->
<!--                   style="position: absolute; right: -5px;top: -6px;min-width: 17px;height:17px;font-size: 12px;padding: 2px;border-radius: 50%"></badge>-->
<!--            <img style="width: 43px;height: 43px" v-if="z.iconUrl" :src="z.iconUrl">-->
<!--          </div>-->
<!--        </grid-item>-->
<!--      </grid>-->
    </div>


    <div class="tips" v-if="isTenantDay">
      <div style="display: flex; align-items: center; margin-left: 15px">
        <img :src="require('@/assets/my/home_tips_waring_icon.png')" width="20" height="20"/>
        <div style="color: #FFF; font-size: 13px; margin-left: 5px">{{content}}</div>
      </div>

      <van-icon name="cross" color="#FFF" style="margin-right: 15px" @click="() => { isTenantDay = false }"/>

    </div>

    <div class="guide-container" @click="goToGuide">
      <div class="guide-btn">
        操作指南 <van-icon name="arrow" size="11" />
      </div>

    </div>

  </div>
</template>

<script>
import Vue from 'vue'
import Api from '@/api/doctor.js'
import ApiT from '@/api/Content.js'
import {
  ImagePreview,
  Grid, GridItem
} from 'vant'
import {
  Badge
} from 'vux'
import {mapGetters} from 'vuex'
Vue.use(Grid)
Vue.use(GridItem)

export default {
  components: {
    Badge
  },
  name: 'Home',
  data() {
    return {
      headerImg: require('@/assets/my/nursingstaff_avatar.png'),
      list: [],
      baseInfo: {},
      allData: {},
      routerData: [],
      reservationMenu: undefined,
      imMenu: undefined,
      isbind: false,
      name: this.$getDictItem('doctor'),
      patient: this.$getDictItem('patient'),
      register: this.$getDictItem('register'),
      sysMessage: '无',
      approvalNumber: undefined,
      content: '系统还有x天到期，请联系管理员及时续费',
      isTenantDay: false,
      doctorId: undefined
    }
  },
  computed: {
    ...mapGetters({
        doctorCount: 'onGetDoctorImCount',
    }),
  },
  watch: {
    doctorCount:function (val, old) {
      // 旧数据如果大于 新数据，则重新加载数据
      if (old > val) {
        this.getDoctorMsgCount()
      }
      this.updateBadge('doctorCount')
    }
  },
  created() {
    this.routerData = []
    if (localStorage.getItem('caring_doctor_id') && localStorage.getItem('caring_doctor_id') !== 'undefined') {
      this.doctorId = localStorage.getItem('caring_doctor_id')
    }
    if (localStorage.getItem('doctorRouterData') && this.routerData.length === 0) {
      const routers = JSON.parse(localStorage.getItem('doctorRouterData'))
      for (let i = 0; i < routers.length; i++) {
        if (routers[i].status) {
          this.routerData.push(routers[i])
        }
      }
      this.updateBadge('doctorCount')
    }
    this.getInfo()
    if (this.doctorId) {
      this.getDoctorMsgCount()
      this.getApprovalNumber()
      this.getDoctorQueryTenantDay()
    }
  },
  methods: {
    getDoctorQueryTenantDay() {
      if (moment().format('yyyy-MM-DD') === localStorage.getItem('queryTime')) {
        return
      }
      localStorage.setItem('queryTime', moment().format('yyyy-MM-DD'))
      Api.getDoctorQueryTenantDay(this.doctorId)
        .then(res => {
          if (res.data.data === 0) {
            this.isTenantDay = true
            this.content = "系统已到期，请联系管理员及时续费"
          }else if (res.data.data > 0) {
            this.isTenantDay = true
            this.content = "系统还有" + res.data.data + "天到期，请联系管理员及时续费"
          }
        })
    },
    updateBadge(type) {
      if (type === 'doctorCount') {
        if (this.imMenu === undefined) {
          for (let i = 0; i < this.routerData.length; i++) {
            if (this.routerData[i].path === '/im/messageList') {
              this.imMenu = this.routerData[i]
              break
            }
          }
        }
        console.log('imMenu', this.imMenu)
        if (this.imMenu !== undefined && this.doctorCount !== undefined) {
          if (this.doctorCount === 0 || this.doctorCount === '0') {
            this.imMenu.badge = undefined
          } else {
            if (this.doctorCount > 99) {
              this.imMenu.badge = '99+'
            } else {
              this.imMenu.badge = this.doctorCount
            }
            this.$forceUpdate()
          }
        }
      }
      console.log('imMenu', this.imMenu)
      if (type === 'approvalNumber') {
        if ( this.reservationMenu === undefined) {
          for (let i = 0; i < this.routerData.length; i++) {
            if (this.routerData[i].path === '/reservation/index') {
              this.reservationMenu = this.routerData[i]
              break
            }
          }
        }
        console.log('approvalNumber', this.reservationMenu)
        if (this.reservationMenu !== undefined && this.approvalNumber !== undefined) {
          if (this.approvalNumber === 0 || this.approvalNumber === '0') {
            this.reservationMenu.badge = undefined
          } else {
            if (this.approvalNumber > 99) {
              this.reservationMenu.badge = '99+'
            } else {
              this.reservationMenu.badge = this.approvalNumber
            }
            this.$forceUpdate()
          }
        }
        console.log('approvalNumber', this.reservationMenu)
      }
    },
    // 点击菜单跳转
    clickMenu(item) {
      console.log('item.path', item.path)
      if (item.path === '/scanImg') {
        this.$router.push({
          path: item.path,
          query: {
            doctorId: this.doctorId
          }
        })
      } else if (item.dictItemId == '25') {
        if (item.path.startsWith('http') || item.path.startsWith('HTTP')) {
          window.location.href  = item.path
        } else {
          this.$router.push({
            path: item.path
          })
        }
      } else {
        this.$router.push({
          path: item.path
        })
      }
    },
    getApprovalNumber(){
      ApiT.approvalNumber(this.doctorId).then(res=>{
        if (res.data.code === 0) {
          this.approvalNumber = res.data.data
          this.updateBadge('approvalNumber')
        }
      })
    },
    getSysMessage() {
      let param = {
        "current": 1,
        "model": {
          userRole: 'doctor',
          userId: this.doctorId
        },
        "size": 1
      }
      ApiT.getSystemMsg(param)
        .then(res => {
          if (res.data && res.data.code == 0) {
            if (res.data.data.records && res.data.data.records.length > 0) {
              if (res.data.data.records[0].msgType == "consultation") {
                let message = JSON.parse(res.data.data.records[0].content)
                this.sysMessage = message.message
              } else {
                this.sysMessage = res.data.data.records[0].content
              }
            }
          }
        })
    },
    loadingData() {
      this.getNumber()
      this.getDoctorMsgCount()
    },
    getDoctorMsgCount() {
      let doctorId = this.doctorId
      if (!doctorId) {
        return
      }
      Api.getCountDoctorMessage(doctorId).then(res => {
        if (res) {
          this.$store.commit('updateDoctorImCount', {
            count: res.data.data ? res.data.data : 0
          })
        }
      })
    },
    getInfo() {
      if (this.doctorId) {
        Api.getContent({id: this.doctorId}).then(res => {
          if (res.data.code === 0) {
            if (res.data.data) {
              localStorage.setItem('userImAccount', res.data.data.imAccount)
              localStorage.setItem('imLoginTime', moment().format("yyyy-MM-DD HH:mm:ss.SSS"))
              //登录环信
              let options = {
                apiUrl: WebIM.config.apiURL,
                user: res.data.data.imAccount,
                pwd: '123456',
                appKey: WebIM.config.appkey
              }
              WebIM.conn.open(options)
              this.baseInfo = res.data.data
              this.loadingData()
              this.getSysMessage()
            } else {
              localStorage.removeItem('caring_doctor_id')
              localStorage.removeItem('doctortoken')
              this.$router.replace('/')
            }
          }
        })
      }
    },
    getNumber() {
      if (!this.doctorId) {
        return
      }
      Api.statisticDashboard({id: this.doctorId}).then(res => {
        if (res.data.code === 0) {
          this.allData = res.data.data
        }
      })
    },
    showImage() {
      if (this.baseInfo.avatar) {
        ImagePreview({
          images: [this.baseInfo.avatar],
          closeable: true
        })
      }
    },
    goToGuide() {
      // TODO: 跳转到操作指南链接
      window.location.href = 'https://jn5owtfchd.feishu.cn/wiki/IqHVwnEnti1S6bkKZFScRfSDnpe?from=from_copylink'
    },

    getQueryString(name) {
      let reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')
      let r = window.location.search.substr(1).match(reg)
      if (r != null) return unescape(r[2])
      return null
    }
  }
}

</script>

<style lang="less" scoped>
.myCenter {
  width: 100vw;
  height: 100vh;
  background: #fafafa;
  position: relative;

  .headerContent {
    // width:100%;
    // height:calc(40vh);
    background: #fff;
    overflow: hidden;
    padding: 0.5rem 1rem;

    .header {
      display: flex;
      justify-content: space-between;
      padding: 20px 0px 25px;
      border-bottom: 1px dashed #e4e7ed;

      .headerImg {
        .headerImglf {
          .imgInner {
            width: 4rem;
            height: 4rem;
            border-radius: 50%;
            overflow: hidden;
            border: 1px solid rgba(0, 0, 0, 0.2);
            float: left;

            img {
              width: 94%;
              height: 94%;
              border-radius: 50%;
              background: #fff;
              margin: 3%;
            }
          }

          .docsInner {
            float: left;
            margin-left: 0.5rem;
            margin-top: 8px;

            .name {
              font-size: 18px;
              color: #333;
            }

            .item {
              font-size: 12px;
              color: #999;
            }
          }
        }
      }

      .headerDocs {
        // background: #f00;
        padding-top: 18px;
        width: 130px;
        display: flex;
        justify-content: right;
        height: fit-content;
        span {
          color: #999;
          font-size: 12px;
          padding: 2px 10px;
          border-radius: 20px;
          border: 1px solid #b8b8b8;
        }

      }
    }

    .myItem {
      display: flex;
      justify-content: space-between;
      padding: 1rem 0px 0.5rem;

      .item {
        width: 50%;
        text-align: center;

        .Number {
          font-size: 18px;
          color: #666666;
          line-height: 1.2;
        }

        .docs {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }

  /deep/.content {
    background: #fff;
    width: 82vw;
    // border-radius: 12px;
    padding: 1rem 4vw;
    margin: 10px 5vw 0px;

    .weui-grids {
      &::before {
        border: none !important;
      }
      .weui-grid__icon{
        width: 43px !important;
        height: 43px !important;
      }
      .weui-grid {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;

        &::after {
          border: none !important;
        }
      }
    }
    .van-grid-item__text{
      font-size: 14px !important;
    }
  }

  .sysBox {
    background: white;
    border-radius: 5px;
    padding: 10px 5px 5px 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .sysContent {
      flex: 1;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      margin-left: 5px;
      margin-right: 10px;
      color: #666666;
      font-size: 12px
    }
  }

  .tips {
    height: 40px;
    display: flex;
    position: absolute;
    justify-content: space-between;
    align-items: center;
    background: rgba(0, 0, 0, 0.68);
    top: 0;
    width: 100%;
  }

  .guide-container {
    position: absolute;
    top: 0;
    right: 0;
    .guide-btn {
      height: 40px;
      width: 120px;
      background-image: url('~@/assets/my/home-caozuo.png');
      background-size: 100% 100%;
      display: flex;
      align-items: center;
      justify-content: right;
      color: #fff;
      font-size: 14px;
      padding-bottom: 13px;
      padding-right: 20px;
      gap: 5px;
    }
  }
}
</style>


