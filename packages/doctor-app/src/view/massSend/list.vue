<template>
  <div>
    <van-sticky>
      <x-header :left-options="{backText: ''}">群发消息</x-header>
      <van-search
        v-model="searchKeyName"
        show-action
        placeholder="请输入搜索关键词"
        @search="onSearch"
      />
    </van-sticky>
    <van-pull-refresh v-model="isLoading" @refresh="onRefresh" :style="{height: listHeight + 'px'}" style="overflow-y: scroll">
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-for="(item, index) in dataList" :key="item.id" style="padding: 10px 5px; background: white; ">
          <div>群发时间： {{item.sendTime}}</div>
          <div>群发对象：
            <span v-for="sendObj in item.sendObjects">
              {{sendObj.objectAssociationName}}
            </span>
          </div>
          <div>操作人： 医生的名称 </div>
          <div>群发内容 </div>
          <div v-if="item.type === 'text'">
            {{item.content}}
          </div>
          <div v-if="item.type === 'image'">
            <van-image
              width="100"
              height="100"
              lazy-load
              :src="item.content"
            />
          </div>
          <div v-if="item.type === 'cms'">
            {{ JSON.parse(item.content).title }}
          </div>
          <div>
            <van-button type="primary">再发一次</van-button>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>

    <div style="position: relative; bottom: 0; width: 100%;" @click="createMessage">
      <van-button type="primary">新建群发消息</van-button>
    </div>
  </div>
</template>

<script>
  import Api from '@/api/Content.js'
  import Vue from 'vue';
  import { PullRefresh, Search, List, Sticky,  Image as VanImage, Lazyload, Button   } from 'vant';
  Vue.use(Search);
  Vue.use(PullRefresh);
  Vue.use(List);
  Vue.use(VanImage);
  Vue.use(Lazyload);
  Vue.use(Button);
  Vue.use(Sticky);
  export default {
    data() {
      return{
        searchKeyName: null,
        isLoading: false,
        loading: false,
        finished: false,
        dataList: [],
        listHeight: window.innerHeight - 54 - 60,
        current: 1,
      }
    },
    mounted() {
      this.onLoad()
    },

    methods: {

      /**
       * 去创建一个新的群发消息
       */
      createMessage() {
        this.$router.push({
          path: '/massSend/selectPerson'
        })
      },

      /**
       * 搜索数据
       */
      onSearch() {
        this.onRefresh()
      },

      /**
       * list 加载更多数据
       */
      onLoad() {
        if (this.loading) {
          return
        }
        this.loading = true
        const params = {
          "page": this.current,
          "userId": localStorage.getItem('caring_doctor_id'),
          "userRole": "doctor"
        }
        if (this.searchKeyName) {
          params.searchKeyName = this.searchKeyName
        }
        Api.moreMessageList(params).then(res => {
          if (res.data.code === 0) {
            if (this.current === 1) {
              this.dataList = res.data.data
            } else {
              this.dataList.push(...res.data.data)
            }
            this.current++
            this.isLoading = false
            this.loading = false
            if (res.data.data && res.data.data.length < 5) {
              this.finished = true
            }
          }
        })
      },

      /**
       * 下拉刷新
       */
      onRefresh() {
        // 清空列表数据
        this.finished = false
        // 重新加载数据
        this.list = []
        this.current = 1
        this.isLoading = true
        this.onLoad()
      }
    }

  }
</script>

<style lang="less" scoped>

</style>
