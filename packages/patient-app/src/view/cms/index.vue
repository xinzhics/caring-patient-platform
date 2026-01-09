<template>
  <section>
    <navBar @toHistoryPage="$router.push('/cms/save')" :pageTitle="pageTitle ? pageTitle : '健康教育'" :rightIcon="save" :isCmsIndex="true" :showRightIcon="getIsLogin()" />
    <div>
      <swiper v-if="baseList.length > 0" :list="baseList" auto show-desc-mask v-model="indexNum"></swiper>
      <tab :line-width=2 active-color='#119CFF' v-model="indexTab">
        <tab-item class="vux-center" :selected="demo2 === item.id" v-for="(item, index) in list2" @click.native="click(item.id)" :key="index">{{item.channelName}}</tab-item>
      </tab>
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa;">
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="已经没有文章啦"
        @load="onLoad"
      >
        <div class="itemContent" v-for="(x,y) in contentList" :key="y" @click="goItem(x)">
          <div class="contentInner">
            <p class="title">{{x.title}}</p>
            <div class="decs">
              <p class="time">{{(/\d{4}-\d{1,2}-\d{1,2}/g.exec(x.createTime)[0]).replace(/-/g, '.')}}</p>
              <div class="watch" v-if="false">
                <span class="watchOne"><img :src="watch"/>{{x.hitCount?x.hitCount:0}}</span>
                <span class="watchTwo"><img :src="say"/>{{x.messageNum?messageNum:0}}</span>
              </div>
            </div>
          </div>
          <img class="itemImg"  :src="x.icon"  width="70px" height="70px"/>
        </div>
      </van-list>
    </van-pull-refresh>
  </section>
</template>
<script>
  import {Swiper, SwiperItem, Tab, TabItem} from "vux";
  import Api from '@/api/Content.js'

  export default {
    components: {
      [Swiper.name]: Swiper,
      [Tab.name]: Tab,
      [TabItem.name]: TabItem,
      [SwiperItem.name]: SwiperItem,
      navBar: () => import('@/components/headers/navBar'),
    },
    data() {
      return {
        pageTitle: '',
        noData: require('@/assets/my/medicineImg.png'),
        save: require('@/assets/my/save.png'),
        show: false,
        baseList: [],
        indexNum: 0,
        indexTab: 0,
        noContentList: false,
        loadLastPage: false,
        demo2: '',
        list2: [],
        watch: require('@/assets/my/watch.png'),
        say: require('@/assets/my/say.png'),
        contentList: [],
        baseInfo: {},
        loading: false,
        finished: false,
        refreshing: false,
        params: {
          model: {
            ownerType: 'TENANT',
            channelType: "Article",
            doctorOwner: Number(this.$route.query.doctorOwner),
            channelGroupId: this.$route.query.channelGroupId
          },
          order: "descending",
          size: 20,
          current: 1,
          sort: "id"
        }
      }
    },
    mounted() {
      if (!this.params.model.doctorOwner && !this.params.model.channelGroupId) {
        this.params.model.doctorOwner = 0
      }
      this.getChannelGroup()
      this.getChannelList()
      this.getBanner()

    },
    methods: {
      getIsLogin() {
        if (localStorage.getItem('userId')) {
          return true
        }else {
          return false
        }

      },
      getChannelGroup() {
        if (this.params.model.channelGroupId) {
          Api.getChannelGroup(this.params.model.channelGroupId).then(res => {
            if (res.data.code === 0) {
              this.pageTitle = res.data.data.groupName
            }
          })
        }
      },
      getChannelList() {
        Api.channelpage(this.params).then((res) => {
          if (res.data.code === 0) {
            const thisObj = {
              channelName: '全部'
            }
            res.data.data.records.unshift(thisObj)
            this.list2 = res.data.data.records
            // this.setTab()
          }
        })
      },
      onLoad() {
        setTimeout(() => {
          if (this.refreshing) {
            this.contentList = [];
            this.params.current = 1;
            this.refreshing = false;
          }
          this.getChannelContentList(this.params)
        }, 500);
      },
      onRefresh() {
        // 清空列表数据
        this.finished = false;
        // 重新加载数据
        // 将 loading 设置为 true，表示处于加载状态
        this.loading = true;
        this.onLoad();
      },
      getChannelContentList(params) {
        Api.channelContentpage(params).then((res) => {
          if (res.data.code === 0) {
            if (this.params.current === 1) {
              this.contentList = res.data.data.records
            } else {
              this.contentList.push(...res.data.data.records)
            }
            this.params.current++;
            this.loading = false;
            if (res.data.data.records && res.data.data.records.length < 20) {
              this.finished = true;
            }
          }
        })
      },
      click(channelId) {
        // 清空列表数据
        this.finished = false;
        // 重新加载数据
        // 将 loading 设置为 true，表示处于加载状态
        this.loading = true;
        if (channelId) {
          this.params.model.channelId = channelId
        }else {
          this.params.model.channelId = 0
        }
        this.refreshing = true
        this.onLoad();
      },
      getBanner() {
        const params = {
          channelType: "Banner",
          doctorOwner: this.params.model.doctorOwner,
          channelGroupId: this.params.model.channelGroupId
        }
        Api.channelContentquery(params).then((res) => {
          if (res.data.code === 0) {
            res.data.data.forEach(element => {
              element.img = element.icon
              if (element.link) {
                element.url = element.link
              } else {
                element.url = '/cms/show?id=' + element.id
              }
            });
            this.baseList = res.data.data
          }
        })
      },
      goItem(i) {
        if (i.link) {
          window.location.href = i.link;
        } else {
          this.$router.push({path: '/cms/show', query: {id: i.id}})
        }

      }
    }
  }
</script>
<style lang="less" scoped>

  /deep/ .vux-header {
    margin-bottom: 0px;
    height: 50px;
    position: fixed;
    width: 100%;
    z-index: 999;
    top: 0;
    left: 0;
  }

  .itemContent {
    display: flex;
    line-height: 30px;
    padding: 15px 0px;
    display: flex;
    padding: 15px;
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

  .noContentInner {
    text-align: center;
    min-height: 300px;
    max-height: 500px;
  }
</style>

