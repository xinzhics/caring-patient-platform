<template>
  <section class="allContent">
    <div>
      <x-header
        :left-options="{backText: '', preventGoBack: true}"
        @on-click-back="back()">常用语设置
        <img
          slot="right" :src="require('@/assets/my/add.png')"
          width="20px" height="20px"
          @click="Jump()">
      </x-header>

      <div style="padding-top: 60px">
        <div v-if="list.length > 0">
          <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
            <van-list
              v-model="loading"
              :finished="finished"
              offset="50"
              finished-text="没有更多了"
              @load="getData">
              <van-cell v-for="(item, i) in list" :key="i" :value="item.content" @click="JumpEdit(item)"/>
            </van-list>
          </van-pull-refresh>
        </div>
        <div v-else>
          <van-empty description="暂无常用语"/>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
  import {List, Cell, Empty} from 'vant';
  import Api from '@/api/doctor.js'

  export default {
    name: "commonSetting",
    components: {
      [List.name]: List,
      [Cell.name]: Cell,
      [Empty.name]: Empty,
    },
    data() {
      return {
        list: [],
        loading: false,
        finished: false,
        refreshing: false,
        count: 1,
      }
    },
    methods: {
      //后退
      back() {
        this.$router.replace({
          path: '/common/commonList',
          query: {
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      },
      //跳转到添加
      Jump() {
        this.$router.replace({
            path: '/common/commonAdd',
            query: {
              imAccount: this.$route.query.imAccount,
              imPatientId: this.$route.query.imPatientId,
              formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
            }
          }
        )
      },
      //跳转到编辑
      JumpEdit(item) {
        this.$router.replace({
          path: '/common/commonEdit',
          query: {
            id: item.id,
            content: item.content,
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      },
      //下拉刷新
      onRefresh() {
        // 清空列表数据
        this.finished = false;
        // 重新加载数据
        // 将 loading 设置为 true，表示处于加载状态
        this.loading = true;
        this.count = 1;
        this.getData()
      },
      getData() {
        let params = {
          model: {
            accountId: localStorage.getItem('caring_doctor_id'),
            userType: 'doctor',
          },
          current: this.count,
          size: 30,
        }

        Api.commonList(params)
          .then(res => {
            if (res.data.data.records) {
              setTimeout(() => {
                if (this.refreshing) {
                  this.list = [];
                  this.refreshing = false;
                }
                res.data.data.records.forEach(item => {
                  this.list.push(item);
                })
                this.loading = false;
                if (res.data.data.records.length != 30) {
                  this.finished = true;
                } else {
                  this.count++;
                }
              }, 100);
            }
          })
      }
    },

    created() {
      this.getData()
    }
  }
</script>

<style scoped lang='less'>

  .allContent {
    width: 100vw;
    height: 100vh;
    background-color: #fafafa;
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
