<template>
  <section>
    <navBar :pageTitle="pageTitle !== undefined ? pageTitle : '历史推送'"></navBar>
    <div v-if="allData.length == 0" style="margin-top: 10px">
      <div class="nodata">
        <img :src="noData" alt="">
        <p>您暂未历史推送数据</p>
      </div>
    </div>
    <div v-else style="margin-top: 10px">
        <div class="item" v-for="(i,k) in allData" :key="k" @click="detailBtn(i)">
            <div class="left">
                <p class="title">{{i.cmsTitle}}</p>
                <p class="docs">{{i.sendTime}}</p>
            </div>
            <div class="right">
                <img :src="i.icon" alt="" width="60px" height="60px">
            </div>
        </div>
    </div>


  </section>
</template>
<script>
import {Group, Cell, CellBox} from 'vux'
import Api from '@/api/Content.js'

export default {
  components: {
    Group,
    Cell,
    CellBox,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      noData: require('@/assets/my/medicineImg.png'),
      addImg: require('@/assets/my/add.png'),
      value: '',
      pageTitle: '',
      allData: []
    }
  },
  mounted() {
    this.getInfo()
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    getInfo() {
      const params = {
            "current": 1,
            "map": {},
            "model": {
                "userId":localStorage.getItem('userId')
            },
            "order": "descending",
            "size": 10,
            "sort": "id"
        }
      Api.planCmsReminderLog(params).then((res) => {
        if (res.data.code === 0) {
          this.allData = res.data.data.records
        }
      })
    },
    detailBtn(i){
        location.href= i.cmsLink
    }
  }
}
</script>


<style lang="less" scoped>

.nodata {
  width: 100vw;
  height: 70vh;
  text-align: center;
  padding-top: 30vw;
  font-size: 14px;
  color: rgba(102, 102, 102, 0.85);
  background: #f5f5f5;
}
.item{
    display: flex;
    justify-content: space-between;
    justify-items: center;
    border-top: 1px solid #ccc;
    // border-bottom: 1px solid #ccc;
    background: #ffff;
    padding: 5px 15px;
    height: 66px;
    .left{
        width: 78%;
        word-break: break-all;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2; /* 这里是超出几行省略 */
        overflow: hidden;
        .title{
            font-size: 13px;
        }
        .docs{
            font-size: 12px;
            margin-top: 5px;
        }

    }
    .right{
        width: 20%;
        display: flex;
      justify-content: right;
      align-items: center;
    }
}

/deep/ .vux-header {
  height: 50px;
}
</style>

