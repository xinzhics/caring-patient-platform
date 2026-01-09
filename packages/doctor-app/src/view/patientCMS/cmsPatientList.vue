<template>
  <section class="allContent">
    <x-header
      :left-options="{backText: '', preventGoBack: true}"
      @on-click-back="back()">患教文章
    </x-header>
    <div style="padding-top: 50px">
      <van-tabs :ellipsis="false" v-model="active" swipeable @click="tabsChange()">
        <van-tab v-for="(item, i) in list" :title="item.label" :key="i">
          <div v-if="articleList.length > 0">
            <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
              <van-list
                v-model="loading"
                :finished="finished"
                offset="50"
                finished-text="没有更多了"
                @load="getArticleList">
                <div class="itemContent" v-for="(x,y) in articleList" :key="y" @click="goItem(x)">
                  <div class="contentInner">
                    <p class="title">{{ x.title }}</p>
                    <div class="decs">
                      <p class="time">{{ (/\d{4}-\d{1,2}-\d{1,2}/g.exec(x.createTime)[0]).replace(/-/g, '.') }}</p>
                      <div class="watch" v-if="false">
                        <span class="watchOne"><img :src="watch"/>{{ x.hitCount ? x.hitCount : 0 }}</span>
                        <span class="watchTwo"><img :src="say"/>{{ x.messageNum ? messageNum : 0 }}</span>
                      </div>
                    </div>
                  </div>
                  <img class="itemImg" :src="x.icon"/>
                </div>
              </van-list>
            </van-pull-refresh>
          </div>
          <div v-else>
            <van-empty description="暂无患教文章"/>
          </div>
        </van-tab>
      </van-tabs>
    </div>

    <van-overlay :show="show" @click="show = false">
      <div class="wrapper">
        <van-loading type="spinner" color="#FFF" size="60px"/>
      </div>
    </van-overlay>

  </section>
</template>

<script>
import {Tab, Tabs, Empty, List, Overlay, Loading} from 'vant';
import Api from '@/api/doctor.js'

export default {
  name: "cmsPatientList",
  components: {
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Empty.name]: Empty,
    [List.name]: List,
    [Overlay.name]: Overlay,
    [Loading.name]: Loading,
  },
  data() {
    return {
      list: [],
      articleList: [],
      active: 0,
      current: 1,
      loading: false,
      finished: false,
      refreshing: false,
      show: false
    }
  },
  methods: {
    //后退
    back() {
      this.$router.replace({
        path: '/im/index',
        query: {
          imAccount: this.$route.query.imAccount,
          imPatientId: this.$route.query.imPatientId,
        }
      })
    },
    tabsChange() {
      if (this.list.length > 0) {
        this.show = true
        this.current = 1
        this.getArticleList()
      }
    },
    //下拉刷新
    onRefresh() {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      this.count = 1;
      this.getArticleList()
    },
    getArticleList() {
      let params = {
        size: 30,
        current: this.current,
        model: {
          doctorOwner: false,
          channelType: "Article",
          channelId: this.list[this.active].label === "全部" ? "" : this.list[this.active].id,
        },
      }
      Api.getArticleList(params)
        .then(res => {
          if (res.data.data.records) {

            setTimeout(() => {
              this.show = false
              if (this.refreshing || this.current === 1) {
                this.articleList = [];
                this.refreshing = false;
              }
              res.data.data.records.forEach(item => {
                this.articleList.push(item);
              })
              this.loading = false;
              if (res.data.data.records.length != 30) {
                this.finished = true;
              } else {
                this.current++;
              }
            }, 100);
          }
        })
    },
    getChannelList() {
      let params = {
        current: 1,
        size: 200,
        model: {
          ownerType: "TENANT",
          doctorOwner: false
        }
      }

      Api.getChannelList(params)
        .then(res => {
          if (res.data.data.records) {
            this.list.push({label: '全部', id: '1'})
            this.getArticleList()
            res.data.data.records.forEach(item => {
              this.list.push({label: item.channelName, id: item.id})
            })
          }
        })
    },
    goItem(i) {
      if (i.link) {
        let params = {
          icon: i.icon,
          title: i.title,
          summary: i.summary,
          link: i.link,
          id: i.id
        }
        this.$router.replace({
          path: '/im/index',
          query: {
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            cms: JSON.stringify(params),
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      } else {
        this.$router.replace({
          path: '/cms/cmsPatientDetail', query: {
            id: i.id,
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      }
    }
  },
  created() {
    this.show = true
    this.getChannelList()
  }
}
</script>

<style scoped lang="less">
.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;

  .itemContent {
    display: flex;
    justify-content: space-between;
    line-height: 30px;
    padding: 15px 10px;
    border-bottom: 1px solid rgba(102, 102, 102, 0.1);

    .contentInner {
      width: 62%;
      margin-right: 3%;

      .title {
        font-size: 15px;
        font-weight: 600;
        line-height: 25px;
        margin-bottom: 16px;
        height: 50px;
        text-overflow: -o-ellipsis-lastline;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .decs {
        line-height: 14px;
        font-size: 14px;
        display: flex;
        justify-content: space-between;
        color: #999;

        .watch {
          .watchOne {
            margin-right: 10px;

            img {
              width: 15px;
              vertical-align: middle;
              margin-right: 5px
            }
          }

          .watchTwo {
            img {
              width: 13px;
              vertical-align: middle;
              margin-right: 5px
            }
          }
        }
      }
    }

    .lastContentPages {
      width: 100%;
      text-align: center;
    }

    .itemImg {
      width: 35%;
      height: 80px;
    }
  }
}

.wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
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
