<template>
    <section class="contentAll">
        <!-- <x-progress :percent="percent2" :show-cancel="false"></x-progress> -->
        <div class="group">
            <div :class="status===1?'one animationOne':'one'">
                <div>
                    <img :src="baseImg" alt="" width="30%">
                    <p>{{baseInfoName}}</p>
                </div>
            </div>
            <div class="dot">
                <div></div>
                <div></div>
                <div></div>
            </div>
            <div :class="status == 2?'two animationOne':'two'">
                <div>
                    <img :src="healthImg" alt="" width="30%">
                    <p>{{healthInfoName}}</p>
                </div>
            </div>
            <div class="dot" v-if="statusAll.hasFillDrugs===0">
                <div></div>
                <div></div>
                <div></div>
            </div>
            <div v-if="statusAll.hasFillDrugs===0" :class="status==3?'three animationOne':'three'">
                <div>
                    <img :src="medicineImg" alt="" width="30%">
                    <p>我的药箱</p>
                </div>
            </div>
        </div>
    </section>
</template>
<script>
import { XProgress, Box } from 'vux'
import Api from '@/api/Content.js'
export default {
  components: {
    XProgress,
    Box
  },
  data () {
    return {
      percent1: 30,
      percent2: 60,
      status:1,
      baseImg:require('@/assets/my/baseInfo.png'),
      healthImg:require('@/assets/my/health.png'),
      medicineImg:require('@/assets/my/medicine.png'),
      baseInfoName: '',
      healthInfoName: '',
      statusAll:{}
    }
  },
  mounted(){
      if(this.$route.query&&this.$route.query.status){
          this.status = this.$route.query.status
      }
      console.log(this.status === 2);
      if(localStorage.getItem('statusAll')){
          this.statusAll = JSON.parse(localStorage.getItem('statusAll'))
          this.baseInfoName = this.statusAll.baseInfoName
          this.healthInfoName = this.statusAll.healthInfoName
          this.goaround()
      }else{
          this.getStatus()
      }

        // if(this.statusAll.hasFillDrugs===0){
        //     this.$router.push('/four')
        // }else{
        //     this.$router.push('/five')
        // }
  },
  methods:{
    getStatus(){
        Api.regGuidegetGuide({}).then((res) => {
            if(res.data.code === 0){
                this.statusAll = res.data.data
                this.baseInfoName = this.statusAll.baseInfoName
                this.healthInfoName = this.statusAll.healthInfoName
                this.goaround()
          }
        })
    },
    goaround(){
      console.log(this.statusAll.hasFillDrugs)
      if(this.statusAll.hasFillDrugs===0){
        setTimeout(()=>{
            this.$router.push({path: '/questionnaire/editquestion',query:{status: this.status}})
        },3000)
      }else{
        if(this.status<3){
          setTimeout(()=>{
              this.$router.push({path: '/questionnaire/editquestion',query:{status: this.status}})
          },3000)
        }else{
          setTimeout(()=>{
            this.$router.replace({
              path: '/five',
              query:{type: 'drugSelect'}
            })
          },200)

        }
      }
    }
  }
}
</script>
<style scoped>
.contentAll{
    width: 100vw;
    height: 100vh;
    background: #f5f5f5;
    display:flex;/*Flex布局*/
    display: -webkit-flex; /* Safari */
    align-items:center;/*指定垂直居中*/
}
.group{
    width: 80px;
    margin: auto ;
}
.dot{
    width: 100%;
    height: 60px;
    padding:10px 0px;
}
.dot>div{
    width:8px;
    height: 8px;
    background: #fff;
    margin: 0px auto;
    margin-bottom: 14px;
    border-radius: 50%;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
}
.one,.two,.three{
    width: 80px;
    height: 80px;
    /* margin-bottom: 80px; */
    background: red;
    border-radius: 50%;
    text-align: center;
    background: #fff;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)

}

.one>div,.two>div,.three>div{
    width: 80px;
    height: 80px;
    overflow: hidden;
    display: table-cell;
	  vertical-align: middle;
}
.one>div>p,.two>div>p,.three>div>p{
    font-size: 12px;
}

/* 动画效果 */
.animationOne {
    animation-name:bounce;
    animation-duration:3000ms;
    animation-timing-function:linear;
    animation-delay:0;
    animation-iteration-count:1;
    animation-fill-mode:forwards;
}

@keyframes bounce {
  0% {
    transform: scale(1.4);
    opacity: 1;
  }
  25% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.4);
  }
  75% {
   -webkit-transform:perspective(400px) rotateX(180deg);
   opacity: 1;
  }
  99%{
    transform:perspective(400px) rotateX(0deg);
    opacity: 0;
  }
  100%{
      opacity: 1;
  }

}
/* @keyframes rotateback{
    0%{-webkit-transform:perspective(400px) rotateX(180deg);}
    100%{-webkit-transform:perspective(400px) rotateX(0deg);}
} */
</style>
