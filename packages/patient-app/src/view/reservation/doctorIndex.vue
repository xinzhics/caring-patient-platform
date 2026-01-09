<template>
  <section>
    <navBar :pageTitle="name+'详情'"></navBar>
    <div class="headerContent">
      <img :src="allInfo.avatar?allInfo.avatar:headerImgT" alt="">
      <div class="content">
        <p class="name">{{ allInfo.name }}</p>
        <p class="docs">{{
            allInfo.deptartmentName
          }}&nbsp;{{ allInfo.title && allInfo.deptartmentName ? '|' : '' }}&nbsp;{{
            allInfo.title
          }}</p>
        <p class="docs">{{ allInfo.hospitalName }}</p>
      </div>
    </div>
    <div class="doctor-introduce">
      <div :class="packUp?'':'packUpBox'"><span
          style="color: #333333">专业特长：</span>{{ allInfo.extraInfo.Specialties === '' ? '-' : allInfo.extraInfo.Specialties }}
      </div>
      <div :class="packUp?'packDownBox':'packUpBox'"><span
          style="color: #333333">详细介绍：</span>{{ allInfo.extraInfo.Introduction === '' ? '-' : allInfo.extraInfo.Introduction}}
      </div>
      <div class="packUpImg" v-if="allInfo.extraInfo.Specialties!==''&&allInfo.extraInfo.Introduction!==''">
        <img width="17px" @click="packUp=!packUp" height="17px" :src="packUp?packUpImgUp:packUpImgDown" alt="">
      </div>
    </div>
    <div class="listContent">
      <div class="dataContent">
        <div class="statusList">
          <div v-for="(i,k) in weekList" :key="k" :style="{background:k===selectIndex?'#66728C':''}"
               class="item"
               @click="selectBtn(i,k)">
            <p class="title" :style="{color:k===selectIndex?'#fff':''}">{{ i.title }}</p>
            <p class="date" :style="{color:k===selectIndex?'#fff':''}">{{ transDate(i.date) }}</p>
            <p class="status" :style="{color:k===selectIndex?'#fff':(i.status===0?'#FF6F00':'#66728C')}">
              {{ i.status === 1 ? '有号' : (i.status === 0 ? '约满' : '-') }}</p>
          </div>
        </div>
        <div class="listDeatil">
          <p style="padding: 5px 0">时间：上午</p>
          <div class="content">
            <div class="content-text">当前余号:
              <span :style="{color:weekList[selectIndex].morning>0?'#66728C':'#999999','margin-left':'5px'}"> {{
                  weekList[selectIndex].morning
                }}</span>
            </div>
            <div>
              <p :style="{width: '45px','text-align':'center',padding:'5px 12px',fontSize:'13px',lineHeight: '20px',display:'inline-block',verticalAlign:'middle',
              background:getColor(weekList[selectIndex].myMorningAppoint, 1),color:'#fff',borderRadius:'20px'}"
                 @click="setmyappoint(weekList[selectIndex].myMorningAppoint,weekList[selectIndex].morning,1)">
                {{ getAppoint(weekList[selectIndex].myMorningAppoint, 1) }}
              </p>
            </div>
          </div>
        </div>
        <div class="listDeatil">
          <p style="padding: 5px 0">时间：下午</p>
          <div class="content">
            <div class="content-text">
              当前余号:
              <span :style="{color:weekList[selectIndex].afternoon>0?'#66728C':'#999999','margin-left':'5px'}">{{
                  weekList[selectIndex].afternoon
                }}</span>
            </div>
            <div>
              <p :style="{width: '45px','text-align':'center',padding:'5px 12px',fontSize:'13px',lineHeight: '20px',display:'inline-block',verticalAlign:'middle'
              ,background:getColor(weekList[selectIndex].myAfterAppoint, 2)
              ,color:'#fff',borderRadius:'20px'}"
                 @click="setmyappoint(weekList[selectIndex].myAfterAppoint,weekList[selectIndex].afternoon,2)">
                {{ getAppoint(weekList[selectIndex].myAfterAppoint, 2) }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <x-dialog v-model="showOne" class="dialog-demo" hide-on-blur
              :dialog-style="{height:'363px',borderRadius:'10px',width:'80%',background:'url('+sureBtn+')',backgroundSize:'100% 100%'}">
      <div style="position:absolute;top:15px;right:15px;" @click="showOne=false">
        <!--        <img :src="gaunbi" alt="" width="20px">-->
      </div>
      <!-- <x-icon style="position:absolute;top:10px;right:10px;color:#fff" type="ios-close-empty" class="closeBtn" size="30" @click="showOne=false" ></x-icon> -->
      <div style="margin:96px 0 0 43px;font-size:14px;color:#999999;text-align:left">
        <p style="margin-bottom: 6px">姓名：<span style="color:#66728C">{{ allInfo.name }}</span></p>
        <p>时间：<span
            style="color:#66728C">{{ transDateT(selectedDate) }}&nbsp;{{
            timeDate === 1 ? '(上午)' : '(下午)'
          }}</span></p>
      </div>
      <div style="position:absolute;bottom:0px;left:0;width:100%">
        <x-button style="color:#fff;background:#66728C;width:40vw;margin:20px auto" @click.native="postitem()">
          确定
        </x-button>
      </div>
    </x-dialog>
  </section>
</template>
<script>
import Api from '@/api/Content.js'

export default {
  data() {
    return {
      headerImgT: require('@/assets/my/touxiang.png'),
      headerimg: require('@/assets/my/resertion.png'),
      sureBtn: require('@/assets/my/sureBtn.png'),
      gaunbi: require('@/assets/my/guanbi.png'),
      packUpImgUp: require('@/assets/my/pack-up.png'),
      packUpImgDown: require('@/assets/my/pack-up-down.png'),
      packUp: false,
      selectedCan: true,
      allInfo: {
        extraInfo: {
          Specialties: '',
          Introduction: ''
        }
      },
      selectIndex: 0,
      weekList: [
        {morning: '', afternoon: ''}
      ],
      selectedContent: {},
      Canset: true,
      showOne: false,
      timeDate: 1,
      selectedDate: '',
      name: "",
      isFlag: false,
    }
  },
  components:{
    navBar: () => import('@/components/headers/navBar'),
  },
  mounted() {
    // this.name = window.dictionaryItem.get('doctor')
    this.name = this.$getDictItem('doctor')
    if (this.$route.query && this.$route.query.id) {
      this.getInfo(this.$route.query.id)
      this.getDoctorStatus()
    }

  },
  methods: {
    getColor(myMorningAppoint, isMorning) {
      if (this.weekList[this.selectIndex].morning === 0 && isMorning === 1) {
        return '#DDDDDD'
      } else if (this.weekList[this.selectIndex].afternoon === 0 && isMorning === 2) {
        return '#DDDDDD'
      } else if (myMorningAppoint === -1) {
        return '#66728C'
      } else if (myMorningAppoint === 1) {
        return '#DDDDDD'
      } else if (myMorningAppoint === 0) {
        return '#DDDDDD'
      } else if (myMorningAppoint === -2) {
        return '#FFBE8B'
      }
    },
    getAppoint(myMorningAppoint, isMorning) {
      if (this.weekList[this.selectIndex].morning === 0 && isMorning === 1) {
        return '约满'
      } else if (this.weekList[this.selectIndex].afternoon === 0 && isMorning === 2) {
        return '约满'
      } else if (myMorningAppoint === -1) {
        return '预约'
      } else if (myMorningAppoint === 1) {
        return '已就诊'
      } else if (myMorningAppoint === 0) {
        return '已预约'
      } else if (myMorningAppoint === -2) {
        return '审核中'
      }
    },
    /**
     * 点击预约
     * @param i 状态
     * @param number 上午或下午剩余号源数
     * @param tiem 上午或者下午
     */
    setmyappoint(i, number, time) {
      if (number === 0) {
        this.$vux.toast.text('预约已满，请选择其他时间段预约', 'cneter');
        return
      }
      if (i === 0) {
        this.$vux.toast.text('您已预约，请勿重复预约', 'cneter');
        return
      }
      if (i === -2) {
        return;
      }
      this.isFlag = false
      this.showOne = true;
      this.timeDate = time
    },
    getInfo(k) {
      const params = {
        id: k
      }
      Api.searchDoctor(params).then((res) => {
        if (res.data.code === 0) {
          if (res.data.data) {
            if (res.data.data.extraInfo) {
              res.data.data.extraInfo = JSON.parse(res.data.data.extraInfo)
            } else {
              res.data.data.extraInfo = {Specialties: '', Introduction: ''}
            }
            this.allInfo = res.data.data
            console.log(this.allInfo)
          }
        }
      })
    },
    getDoctorStatus() {
      const params = {
        id: this.$route.query.id,
        patientId: localStorage.getItem('userId')
      }
      Api.getDoctorStatus(params).then((res) => {
        this.weekList = res.data.data
        console.log(this.weekList,'查看数据=======================')

        if (this.Canset) {
          this.selectedContent = this.weekList[0]
          this.selectedDate = /\d{4}-\d{1,2}-\d{1,2}/g.exec(this.selectedContent.date)[0]
          // this.appointmentpage()
        } else {
          return
        }

      })
    },
    transDate(n) {
      if (n) {
        const now = new Date(n.replace(/-/g, '/'))
        let mon = Number(now.getMonth() + 1)
        let day = Number(now.getDate())
        if (mon < 10) {
          mon = '0' + mon
        }
        if (day < 10) {
          day = '0' + day
        }

        return mon + "·" + day;
      }

    },
    transDateT(data) {
      var date = ''
      var reg = /(\d{4})\-(\d{2})\-(\d{2})/;
      var str = data;
      if (data) {
        date = str.replace(reg, "$1年$2月$3日");
      } else {
        date = ''
      }
      return date
    },
    selectBtn(i, k) {
      this.selectIndex = k
      this.selectedContent = i
      this.selectedDate = /\d{4}-\d{1,2}-\d{1,2}/g.exec(this.selectedContent.date)[0]
    },
    postitem() {
      if (this.isFlag) {
        return
      }
      this.isFlag = true
      let thisD = /\d{4}-\d{1,2}-\d{1,2}/g.exec(this.selectedContent.date)
      this.selectedDate = /\d{4}-\d{1,2}-\d{1,2}/g.exec(this.selectedContent.date)[0]
      const params = {
        "appointDate": thisD[0],
        "doctorId": this.$route.query.id,
        "patientId": localStorage.getItem('userId'),
        "time": this.timeDate
      }
      Api.postappointment(params).then((res) => {
        if (res.data.code === 0) {
          this.showOne = false
          this.Canset = false
          this.$vux.toast.text('预约成功', 'cneter');
          this.getDoctorStatus()
        } else {
          this.showOne = false
        }
      }).catch(res => {
        this.showOne = false
        this.getDoctorStatus()
      })
    }
  }
}
</script>
<style lang="less" scoped>
.headerContent {
  display: flex;
  justify-content: start;
  padding: 17px 13px;
  background: #fff;

  img {
    width: 70px;
    height: 70px;
    margin-right: 10px;
    border-radius: 4px;
  }

  .content {
    text-align: left;

    .content-text {
      display: flex;
      align-items: center;
    }

    // padding: 5px 0px;
    .name {
      font-weight: 600;
      font-size: 16px;
      line-height: 2;
      color: #333;
    }

    .docs {
      font-size: 12px;
      color: #666;
    }
  }
}

.listContent {
  margin-top: 10px;

  .listheader {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    border-bottom: 1px solid #EBEBEB;
    background: #fff;

    div {
      width: 50%;
      text-align: center;

      span {
        padding-bottom: 5px;
        border-bottom: 2px solid #fff;
        font-size: 13px;
      }

      .selected {
        font-size: 15px;
        color: #66728C;
        border-bottom: 2px solid #66728C;
      }
    }
  }

  .dataContent {
    .contentOne, .contentTwo {
      padding: 5px 20px;
      background: #fff;

      .groupTitle {
        padding: 5px 0px;
        font-size: 15px;
        border-bottom: 1px solid #EBEBEB;
        color: #333;
        line-height: 1.6;
      }

      .content {
        font-size: 14px;
        color: #666;
        padding: 10px 0px 10px;
        line-height: 1.6;
      }
    }

    .contentTwo {
      margin-top: 10px;
    }

    .statusList {
      display: flex;
      justify-content: space-between;
      padding: 20px 10px;
      background: #fff;

      .item {
        width: 13.5%;
        font-size: 14px;
        text-align: center;
        padding: 3px 0px 5px;
        border-radius: 4px;

        .title {
          line-height: 28px;
          color: #666666;
          font-size: 16px;
        }

        .date {
          font-size: 13px;
          color: #999999;
        }

        .status {
          font-size: 13px;
        }
      }
    }

    .listDeatil {
      margin-top: 10px;
      padding: 0px 10px;
      background: #fff;

      p {
        font-size: 16px;
        line-height: 40px;
        border-bottom: 1px solid #f5f5f5;
        color: #333;
      }

      .content {
        display: flex;
        justify-content: space-between;
        padding: 30px 0px;
        color: #666666;
        // font-size: 15px;
      }
    }
  }
}

.doctor-introduce {
  background: #FFFFFF;
  padding: 0 13px 9px 13px;
  font-size: 14px;
  color: #666;

  .packUpBox {
    display: inline-block;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    width: 100%;
  }

  .packDownBox {
    margin-top: 26px;
    white-space: pre-wrap;
  }

  .packUpImg {
    display: flex;
    justify-content: center;
    margin-top: 10px;
  }
}
</style>
