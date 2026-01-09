<template>
  <div>
    <div class="list" v-for="(item,index) in listData" :key="index">
      <div class="contents">
        <div class="title">
          <div style="width: 3px;height: 13px;border-radius: 4px;background: #3F86FF;margin-right: 7px"/>
          <div>{{ item.learnPlanName }}（共{{ item.cmsNumber }}篇）</div>
        </div>
        <div class="contents-statistics">
          <div style="display:flex;align-items: center;justify-content: space-between;width: 60%">
            <div>
              <van-circle
                  v-if="item.readingRate == 0"
                  v-model="item.readingRate"
                  :rate="item.readingRate"
                  size="72"
                  layer-color="#F5F5F5"
                  :stroke-width="65"
                  :color="{'0%': '#F5F5F5', '100%': '#F5F5F5',}"
                  style="position: relative"
              >
                <div style="position: absolute;top: 50%;left: 50%;transform:translate(-50%,-50%)">
                  <div style='font-size: 11px;color: #999999'>阅读率</div>
                  <div style='font-size: 14px;color: #3F86FF'>{{ item.readingRate }}%</div>
                </div>
              </van-circle>
              <van-circle
                  v-else
                  v-model="item.readingRate"
                  :rate="item.readingRate"
                  size="72"
                  layer-color="#F5F5F5"
                  :stroke-width="65"
                  :color="gradientColor"
                  style="position: relative"
              >
                <div style="position: absolute;top: 50%;left: 50%;transform:translate(-50%,-50%)">
                  <div style='font-size: 11px;color: #999999'>阅读率</div>
                  <div style='font-size: 14px;color: #3F86FF'>{{ item.readingRate }}%</div>
                </div>
              </van-circle>
            </div>
            <div
                style="text-align: center;color:#333333;font-size: 23px;padding-right: 29px;border-right: 1px solid #EBEBEB">
              <div style="">{{ item.receiveNumber }}</div>
              <div style="color: #999999;font-size: 13px">已收到</div>
            </div>
          </div>
          <div style="display: flex;justify-content: space-around;flex:1">
            <div style="text-align: center">
              <div style="font-size: 23px;color: #333333">{{ item.readNumber }}</div>
              <div style="display: flex;align-items: center">
                <div style="width: 5px;height: 5px;border-radius: 50%;background: #3F86FF;margin-right: 5px"></div>
                <div style="color: #999999;font-size: 13px">已读</div>
              </div>
            </div>
            <div style="text-align: center">
              <div style="font-size: 23px;color: #333333">{{ item.noReadNumber }}</div>
              <div style="display: flex;align-items: center">
                <div style="width: 5px;height: 5px;border-radius: 50%;background: #B8B8B8;margin-right: 5px"></div>
                <div style="color: #999999;font-size: 13px">未读</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <loading-dialog ref="loading"></loading-dialog>
  </div>
</template>

<script>
import Vue from 'vue';
import {Circle} from 'vant';
import Api from '@/api/followUp.js'
import loadingDialog from "./loadingDialog";

Vue.use(Circle);

export default {
  name: "learningStatistics",
  data() {
    return {
      listData: [],
      gradientColor: {
        '0%': '#6EA8FF',
        '100%': '#3F86FF',
      },
    }
  },
  components: {
    loadingDialog: () => import('./loadingDialog'),
  },
  mounted() {
    this.patientLearnPlanStatistics()
  },
  methods: {
    // 跳转到cms
    goCMS(item) {
      //将文章设置成已读
      if (item.messageId) {
        Api.openArticle(item.messageId)
      }

      if (item.cmsLink) {
        //此文章为外链，直接跳转到外链
        window.location.href = item.cmsLink + '?time=' + ((new Date()).getTime());
      } else {
        //跳转到cms查看文章
        this.$router.push({path: '/cms/show', query: {id: item.cmsId, isComment: true}})
      }
    },
    /**
     * 请求数据
     */
    patientLearnPlanStatistics() {
      this.$refs.loading.openLoading()
      Api.patientLearnPlanStatistics().then(res => {
        if (res.data.code === 0) {
          this.listData = res.data.data
          console.log(res.data)
          this.$refs.loading.cancelLoading()
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.list {
  padding: 18px 13px 0;
  background: #FFFFFF;
  margin-top: 13px;

  .title {
    display: flex;
    align-items: center;
    border-bottom: 1px solid #EBEBEB;
    padding-bottom: 18px;
    color: #333333;
  }

  .contents {
    padding: 0px 0px 17px 0px;

    .contents-title {
      padding: 11px 15px 11px 13px;
      background: #F5F5F5;
      border-radius: 7px;
      color: #666666;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .contents-statistics {
      padding: 17px 15px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  }
}
</style>
