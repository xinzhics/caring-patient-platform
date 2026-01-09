<template>
  <div class="content">
    <div class="partOne">
      <div class="all">
        <div class="all_l">
          <p class="Number">{{allData.total}}</p>
          <p class="docs">{{patient}}总数(人)</p>
        </div>
        <div class="all_r">
          <p class="Number">{{allData.conversionRate}}</p>
          <p class="docs">{{registerrate}}</p>
        </div>
      </div>
      <div class="item">
        <div class="all_l">
          <img :src="img1"  width="20px" alt="">
          <p class="docs">{{register}}{{patient}}</p>
        </div>
        <div class="all_r">
          <div class="inner_l">
            <p class="Number">{{allData.subscribeNum}}</p>
            <p class="docs">全部</p>
          </div>
          <div class="inner_r">
            <p class="Number">{{allData.thisMonthSubscribeNum}}</p>
            <p class="docs">本月新增</p>
          </div>

        </div>
      </div>
      <div class="item">
        <div class="all_l">
          <img :src="img2"  width="20px" alt="">
          <p class="docs">{{notregister}}</p>
        </div>
        <div class="all_r">
          <div class="inner_l">
            <p class="Number" style="color:#FF6F00">{{allData.unSubscribeNum}}</p>
            <p class="docs">全部</p>
          </div>
          <div class="inner_r">
            <p class="Number" style="color:#FF6F00">{{allData.thisMonthUnSubscribeNum}}</p>
            <p class="docs">本月新增</p>
          </div>
        </div>
      </div>
      <div class="item">
        <div class="all_l">
          <img :src="img3" width="20px" alt="">
          <p class="docs">取消关注</p>
        </div>
        <div class="all_r">
          <div class="inner_l">
            <p class="Number"  style="color:#FF8B8B">{{allData.cancelSubscribe}}</p>
            <p class="docs">全部</p>
          </div>
          <div class="inner_r">
            <p class="Number" style="color:#FF8B8B">{{allData.thisMonthCancelSubscribe}}</p>
            <p class="docs">本月新增</p>
          </div>
        </div>
      </div>
    </div>
    <div class="partTwo">
      <p>男女比例</p>
      <div class="inner">
        <div class="line">
          <span>{{allData.sexData.grendOne.grass||'0%'}}</span>
          <div class="grass">
            <div class="man" :style="{width:allData.sexData.grendOne.grass? allData.sexData.grendOne.grass:'0%'}"></div>
            <div class="men" :style="{width:allData.sexData.grendTwo.grass||'0%'}"></div>
          </div>
          <span>{{allData.sexData.grendTwo.grass||'0%'}}</span>
        </div>
        <div class="docs">
          <div class="item">
            <span class="color"></span>
            <span>男</span>
          </div>
          <div class="item">
            <span class="color1"></span>
            <span>女</span>
          </div>
        </div>
      </div>
    </div>
    <div class="partThree" v-if="allData.ageDistribution&&allData.ageDistribution.length>0" >
      <p>年龄分布</p>
      <div class="inner">
        <div id="demo" style="min-height: 280px"></div>
      </div>
    </div>
    <!--<div class="partThree" v-if="allData.diagnosisDistribution&&allData.diagnosisDistribution.length>0">
      <p>{{diagnostictype}}</p>
      <div class="inner">
        <div id="demoTwo"></div>
      </div>
    </div>-->
  </div>
</template>

<script>
import Api from '@/api/doctor.js'

export default {
  name: "BasicsStatistics",
  data(){
    return{
      allData:{
        sexData:{
          grendOne:{},
          grendTwo:{}
        },
        diagnosisDistribution:[{value:0,name:'初始化的值'}],
        ageDistribution:[{value:0,name:'初始化的值'}],
      },
      img1:require('@/assets/drawable-xhdpi/set.png'),
      img2:require('@/assets/drawable-xhdpi/unset.png'),
      img3:require('@/assets/drawable-xhdpi/cancel.png'),
      patient: this.$getDictItem('patient'),
      registerrate: this.$getDictItem('registerrate'),
      register: this.$getDictItem('register'),
      diagnostictype: this.$getDictItem('diagnostictype'),
      notregister: this.$getDictItem('notregister')
    }
  },
  mounted(){
    this.getInfo()
  },
  methods:{
    getInfo(){
      const params={
        id: localStorage.getItem('caring_doctor_id')
      }
      Api.statisticDashboard(params).then((res) => {
        if(res.data.code === 0){
          // console.log(res)
          this.allData = res.data.data
          this.allData.sexData = {
            grendOne:{grass:0,sex:'男'},
            grendTwo:{grass:0,sex:'女'},
          }
          let allSexTotal = 0
          const that = this
          if(this.allData.sexDistribution&&this.allData.sexDistribution.length>0){
            this.allData.sexDistribution.forEach(element => {
              allSexTotal += Number(element.total)
            });
            this.allData.sexDistribution.forEach(element => {
              if(element.sex==='男'){
                that.$set(that.allData.sexData.grendOne,'grass',((Number(element.total)/Number(allSexTotal))*100).toFixed(1) +'%')
              }
              if(element.sex==='女'){
                that.$set(that.allData.sexData.grendTwo,'grass',((Number(element.total)/Number(allSexTotal))*100).toFixed(1) +'%')
              }
            });

          } else {
            that.$set(that.allData.sexData.grendOne,'grass', '50%')
            that.$set(that.allData.sexData.grendTwo,'grass', '50%')
          }
          this.caringEcharts()

        }
      })
    },
    getName(key) {
      if (key === "less18") {
        return '18岁以下'
      }else if (key === "more18less25") {
        return '18-25岁'
      }else if (key === "more25less30") {
        return '25-30岁'
      }else if (key === "more30less50") {
        return '30-50岁'
      }else if (key === "more51") {
        return '51岁以上'
      }
    },
    caringEcharts() {
      let allName = []
      this.allData.ageDistribution.forEach(element => {
        element.value = element.total
        element.name = element.age
        allName.push(element.name)
      });
      let data = []
      this.allData.ageDistribution.forEach(item => {
        data.push({
          value: item.count,
          name: this.getName(item.key),
        })
      })
      const option={
        tooltip: {
          trigger: "item", //根据item提示信息
          formatter: "{b} : {c} ({d}%)", //提示内容
        },
        legend: {
          top: 'bottom',
          icon: "circle",
        },
        series: [
          //饼状图设置
          {
            type: "pie", //类型为饼状
            //center: ['50%', '40%'],
            bottom: 30,
            // hoverAnimation: false, //鼠标悬停效果，默认是true
            radius: ["40%", "60%"], //内圈半径，外圈半径
            hoverAnimation: true, //鼠标悬停效果，默认是true
            data: data,
            label: {
              normal: {
                show: true,
                // formatter: "{d}%",
                formatter: function (a, b, c) {
                  return a.percent+'%\n'+a.data.name;
                },

                textStyle: {
                  fontWeight: "normal",
                  fontSize: 12,
                  color:'#666'
                },
              },
            },
            itemStyle: {
              normal:{
                color: function(params) {
                  var colorList = ['#77AAFF','#21C7C7','#FFA902','#FF6F01','#FF94AA'];
                  return colorList[params.dataIndex]
                }
              }
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
          },
        ],
      }
      const myChart1 = echarts.init(document.getElementById("demo"));
      // 绘制饼图
      myChart1.setOption(option);
    },
  }
}
</script>

<style lang="less" scoped>
.content{
  .partOne{
    background: #fff;
    padding: 0vw 5vw 5vw 5vw;
    .all{
      width: 100%;
      display: flex;
      justify-content: space-between;
      background: #5292FF;
      padding: 15px 0px;
      border-radius: 10px;
      .all_l{
        border-right: 1px solid #fafafa;

      }
      .all_l,.all_r{
        width: 50%;
        .Number{
          font-size: 1.2rem;
          color: #fff;
          text-align: center;

        }
        .docs{
          font-size: 0.8rem;
          color: #fff;
          text-align: center;
        }
      }
    }
    .item{
      width: 100%;
      display: flex;
      justify-content: space-between;
      background: #fafafa;
      padding: 10px 0px;
      border-radius: 10px;
      margin: 10px 0px 0px;
      .all_l{
        width: 30%;
        border-right: 1px solid #EEEEEE;
        text-align: center;
      }
      .all_r{
        width: 70%;
        display: flex;
        justify-content: space-between;
        background: #fafafa;
        .inner_l,.inner_r{
          width: 50%;
          .Number{
            font-size: 1rem;
            color: #77AAFF;
            text-align: center;
          }
          .docs{
            font-size: 0.8rem;
            color: #B8B8B8;
            text-align: center;
          }
        }
      }
      .all_l,.all_r{
        .Number{
          font-size: 1.2rem;
          color: #B8B8B8;
          text-align: center;

        }
        .docs{
          font-size: 0.8rem;
          color: #B8B8B8;
          text-align: center;
        }
      }
    }
  }
  .partTwo{
    width: 90vw;
    background: #fff;
    padding: 0px 5vw 5vw;
    margin-top: 10px;
    p{
      line-height: 40px;
      color: #666;
      border-bottom: 1px solid #EEEEEE;
    }
    .inner{
      padding: 15px 0px 0px;
      color: #666;
      .line{
        margin-top: 10px;
        display: flex;
        justify-content: space-around;
        line-height: 3rem;
        .grass{
          width: 80%;
          height: 3rem;
          background: #77AAFF;
          margin: 0 5px;
          display: flex;
          justify-content: space-between;
          .man{
            width: 50%;
            height: 3rem;
            background: #77AAFF;

          }
          .men{
            width: 50%;
            height: 3rem;
            background: #FF94AA;

          }
        }
      }
      .docs{
        margin-top: 15px;
        display: flex;
        justify-content: space-between;
        .item{
          width: 50%;
          text-align: center;
          .color{
            width: 5px;
            height: 5px;
            display: inline-block;
            background: #77AAFF;
            padding: 5px;
            vertical-align: middle;
          }
          .color1{
            width: 5px;
            height: 5px;
            display: inline-block;
            background: #FF94AA;
            padding: 5px;
            vertical-align: middle;
          }
        }
      }
    }
  }
  .partThree{
    width: 90vw;
    background: #fff;
    padding: 0px 5vw 5vw;
    margin-top: 10px;
    p{
      line-height: 40px;
      color: #666;
      border-bottom: 1px solid #EEEEEE;
    }
    .inner{
      #demo {
        width: 100%;
        height: 300px;
      }
      #demoTwo{
        width: 100%;
        height: 300px;
      }
    }
  }
}
</style>
