<template>
  <section style="width:100vw;height:100vh;background:#F5F5F5;-webkit-touch-callout: none;user-select: none;">
    <navBar pageTitle="我的预约" backUrl="/reservation/index"></navBar>
    <div class="nodata" v-if="list.length===0&&showNodataImg">
      <img :src="noData" alt="">
      <p>您暂未预约{{ name }}</p>
    </div>
    <group style="margin-top:0px!important;margin-top:10px" class="reservationClass" v-for="(i,k) in list" :key="k">
      <div
          style="margin:0px 13px;padding: 17px 0;display:flex;justify-content: space-between;border-bottom:1px solid #EEEEEE;color:#666666">
        <p style="line-height:20px">预约时间：{{ i.appointDate }} {{ i.time === 1 ? '上午 ' : '下午' }}</p>
        <span
            :style="{color:i.status===0?'#3F86FF':i.status===-2?'#FF6F00':i.status===3?'#FF5555':'#999999','font-weight':'600'}">{{
            getStatus(i.status)
          }}</span>
      </div>
      <div>
        <div style="display: flex;align-items: center;padding: 13px !important;justify-content: space-between">
          <div
              @touchstart="delHistory(i,k)" @click="goitem(i)" @touchmove="onTouchMove" @touchend="touchend()"
              slot="title" style="display: flex;align-items: center; width: 60%">
            <img :src="i.avatar" alt=""
                 style="width:43px;border-radius:50%;height:43px;vertical-align: middle;margin-right:10px">
            <div style="display:inline-block;vertical-align: middle; width: 100%">
              <span style="color:#333;font-size:15px;line-height:30px;font-weight:600">{{ i.doctorName }}</span>
              <p style="font-size:12px;color: #999;overflow: hidden;text-overflow:ellipsis;white-space: nowrap; width: 100%">
                {{ i.deptartmentName }}{{ i.title && i.deptartmentName ? '&nbsp;|&nbsp;' : '' }}{{ i.title }}</p>
            </div>
          </div>
          <div v-if="i.status === 0||i.status === -2" @click.stop="cancel(i,k)"
               style="font-size:13px;padding:0px 10px;border:1px solid rgba(102,114,140,0.6);line-height:20px;border-radius:15px;color:#66728C">
            {{ i.status === 0 || i.status === -2 ? '取消预约' : '' }}
          </div>
        </div>
      </div>


    </group>
    <p v-if="canMore" style="text-align:center;font-size:12px;color:#999;padding:5px 25px" @click="getInfo()">
      加载更多...</p>
    <x-dialog v-model="showOne" class="dialog-demo" hide-on-blur :dialog-style="{borderRadius:'10px',width:'70%'}">
      <div style="margin:0px;line-height:30px;text-align:center;padding-top:20px">
        <img :src="warn" alt="" width="60px">
      </div>
      <div>
        <div style="margin:8px 0px;line-height:40px;text-align:center;color:#666">您确定要取消本次预约吗？</div>
      </div>
      <div style="border-top:1px solid #EBEBEB;display: flex;justify-content: space-between;">
        <p style="padding:8px 0px;line-height:30px;text-align:center;color:#333;width:50%;border-right:1px solid #EBEBEB"
           @click="showOne=false">暂不取消</p>
        <p style="padding:8px 0px;line-height:30px;text-align:center;color:#333;width:50%"
           @click.stop="putSubmit(myContent)">取消预约</p>
      </div>
      <!-- <x-button style="color:#fff;background:#3f86ff;width:40vw;margin:20px auto" @click.native="showOne=false">我知道了</x-button> -->
    </x-dialog>
    <!--    删除弹窗-->
    <x-dialog v-model="showTouchDilog" class="dialog-demo" hide-on-blur
              :dialog-style="{borderRadius:'10px',width:'70%'}">
      <div style="margin:0px;line-height:30px;text-align:center;padding-top:20px">
        <img :src="warn" alt="" width="60px">
      </div>
      <div style="margin: 0 0 18px 0">
        <div style="text-align:center;color:#666">确定删除吗？</div>
        <div style="text-align:center;color:#666"> 删除后内容不可恢复！</div>
      </div>
      <div style="border-top:1px solid #EBEBEB;display: flex;justify-content: space-between;">
        <p style="padding:8px 0px;line-height:30px;text-align:center;color:#333;width:50%;border-right:1px solid #EBEBEB"
           @click="showTouchDilog=false">取消</p>
        <p style="padding:8px 0px;line-height:30px;text-align:center;color:#333;width:50%"
           @click="deleteAppoint">确定</p>
      </div>
      <!-- <x-button style="color:#fff;background:#3f86ff;width:40vw;margin:20px auto" @click.native="showOne=false">我知道了</x-button> -->
    </x-dialog>
  </section>
</template>
<script>
import Api from '@/api/Content.js'
import {Group } from 'vux'

export default {
  components: {
    Group,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      list: [],
      moreIndex: 1,
      canMore: true,
      showOne: false,
      warn: require('@/assets/my/warn.png'),
      myContent: {},
      noData: require('@/assets/my/medicineImg.png'),
      name: "",
      longTouch: false, // 判断是否是长按；
      loop: null,
      appointmentId: '',
      listIndex: undefined,
      showTouchDilog: false,
      showNodataImg: false,
      touchMove:false
    }
  },
  mounted() {
    this.getInfo()
    // this.name = window.dictionaryItem.get('doctor')
    this.name = this.$getDictItem('doctor')
  },
  methods: {
    /**
     * 取消预约点击
     * @param i
     * @param index
     */
    cancel(i,index){
      this.listIndex = index
      this.showOne=true
      this.myContent=i
    },
    /**
     * 长按并拖动
     */
    onTouchMove(){
      this.touchMove=true
    },
    // 删除
    deleteAppoint() {
      Api.deleteAppoint(this.appointmentId).then(res => {
        if (res.data.code === 0) {
          this.list.splice(this.listIndex, 1)
          this.longTouch = false
          this.showTouchDilog = false
          this.$vux.toast.text('删除成功', 'cneter');
        }
      })
    },
    /**
     * 返回就诊状态
     * @param status 就诊状态
     */
    getStatus(status) {
      if (status === 0) {
        return '待就诊'
      } else if (status === 1) {
        return '已就诊'
      } else if (status === 2) {
        return '已取消'
      } else if (status === -2) {
        return '审核中'
      } else if (status === 3) {
        return '预约失败'
      } else if (status === 4) {
        return '已过期'
      }
    },
    delHistory(item, index) {
      // 模拟长按
      if (this.touchMove) {
        console.log(item)
        this.touchMove = false
        return
      } else {
        clearTimeout(this.loop)// 再次清空定时器，防止重复注册定时器（会把点击事件也阻止掉）
        this.longTouch = false // 关键
        this.loop = setTimeout(() => {
          this.longTouch = true  // 关键
          if (item.status !== 0 && item.status !== -2&&!this.touchMove) { // 除了待就诊与审核中 其他都可以删除
            this.showTouchDilog = true
            this.appointmentId = item.id
            this.listIndex = index
          }
        }, 500)
      }

    },
    /**
     * 长按结束
     */
    touchend() {
      clearTimeout(this.loop) // 清空定时器，防止重复注册定时器
      this.touchMove = false
    },
    /**
     * 跳转医生详情页
     * @param k
     */
    goitem(k) {
      //debugger
      console.log('goitem', k)
      if (this.touchMove){
        return
      } else {
        if (k.closeAppoint && k.closeAppoint === 1) {
          this.$vux.toast.text(k.doctorName + '已关闭预约功能', 'cneter');
        } else {
          this.$router.push({
            path: '/reservation/doctorIndex',
            query: {id: k.doctorId}
          })
        }
      }
    },
    /**
     * 获取预约列表数据
     */
    getInfo() {
      const params = {
        "current": this.moreIndex,
        "map": {},
        "model": {
          "patientId": localStorage.getItem('userId'),
        },
        "order": "descending",
        "size": 10,
        "sort": "id"
      }
      Api.postappoint(params).then((res) => {
        //  console.log(res)
        if (res.data.code === 0) {
          // console.log(res)
          this.showNodataImg = true
          if (res.data.data.records.length < 10) {
            this.canMore = false
          } else {
            this.moreIndex = this.moreIndex + 1
          }
          this.list = this.list.concat(res.data.data.records)
        }
      })
    },
    putSubmit(i) {
      Api.putappoint(i.id).then((res) => {
        //  console.log(res)
        if (res.data.code === 0) {
          this.showOne = false
          this.list.splice(this.listIndex, 1)
          this.$vux.toast.text('取消预约成功', 'cneter');
          this.moreIndex = 1
        }
      })
    }
  }
}
</script>
<style lang="less" scoped>
.nodata {
  background: #fff;
  width: 100vw;
  height: 30vh;
  text-align: center;
  padding-top: 20vw;
  font-size: 14px;
  color: rgba(102, 102, 102, 0.85);
  background: #f5f5f5;
}
/deep/.reservationClass{
  .vux-label{
    width: 100%
  }
}
</style>
