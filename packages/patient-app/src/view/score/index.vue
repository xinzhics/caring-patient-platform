<template>
  <section>
    <navBar @toHistoryPage="$router.push('/healthCalendar/editor')"
            backUrl="/home"
            :pageTitle="pageTitle ? pageTitle : '监测列表'"
            :rightIcon="addImg"
            :showRightIcon="list.length !== 0"/>
    <div v-if="list.length == 0 && !loading">
      <div class="nodata">
        <img :src="noData" alt="" style="padding-top: 150px;">
        <p>暂未添加{{ pageTitle ? pageTitle : '监测列表' }}</p>
        <p>请点击下方添加按钮进行添加</p>
        <x-button style="background:#66728C;width:40%;color:#fff;margin:15vw auto"
                  @click.native="$router.push('/healthCalendar/editor')">添加
        </x-button>
      </div>
    </div>

    <div v-else>
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #f5f5f5;">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <div v-for="(i,k) in list" :key="k" @click="jumpDetail()">
            <div style="width: auto; margin: 20px; background-color: #fff; border-radius: 12px;">
              <van-cell title="2023-10-09">
                <div style="color: #68E1A8">
                  查看详情
                </div>
              </van-cell>
              <van-cell title="总得分" is-link :border="false">
                <div style="color: #333">
                  79
                </div>
              </van-cell>
              <van-cell title="平均分" is-link>
                <div style="color: #333">
                  13.3
                </div>
              </van-cell>

            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </section>
</template>

<script>

import Vue from 'vue';
import { Cell } from 'vant';

Vue.use(Cell);
export default {
  name: "index",
  components: {
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      pageTitle: '监测列表',
      noData: require('@/assets/my/medicineImg.png'),
      addImg: require('@/assets/my/add.png'),
      value: '',
      current: 1,
      loading: false,
      finished: true,
      refreshing: false,
      list: [1,2,3,4,5]
    }
  },
  methods: {
    onRefresh() {
// 清空列表数据
      this.finished = false;
      this.current = 1;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.list = []
      this.loading = true;
      this.onLoad();
    },
    onLoad() {

    },
    jumpDetail() {
      this.$router.push({
        path: '/score/result',
      })
    }
  }
}
</script>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
}

.nodata {
  width: 100vw;
  height: 100vh;
  text-align: center;
  font-size: 14px;
  color: rgba(102, 102, 102, 0.85);
  background: #f5f5f5;
}

/deep/ .van-cell {
  border-radius: 12px;
}

</style>
