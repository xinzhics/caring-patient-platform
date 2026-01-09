<template>
  <div>
    <div class="list" v-for="(item,index) in listData" :key="index">
      <div class="title">
        <div style="width: 3px;height: 13px;border-radius: 4px;background: #3F86FF;margin-right: 7px"/>
        <div>{{ item.planName }}（共{{ item.cmsPushReadDetails.length }}篇）</div>
      </div>
      <div v-for="(cItem, key) in item.cmsPushReadDetails" :key="key" class="contents">
        <div class="contents-title" @click="goCMS(cItem, index)">
          <div style="font-size: 15px;">{{ cItem.cmsTitle }}</div>
          <van-icon color="#C6C6C6" name="arrow"/>
        </div>
        <div class="contents-statistics">
          <div style="display:flex;align-items: center;justify-content: space-between;width: 60%">
            <div>
              <van-circle
                v-if="cItem.readingRate == 0"
                v-model="cItem.readingRate"
                :rate="cItem.readingRate"
                size="72"
                layer-color="#F5F5F5"
                :stroke-width="65"
                :color="{'0%': '#F5F5F5', '100%': '#F5F5F5',}"
                style="position: relative"
              >
                <div style="position: absolute;top: 50%;left: 50%;transform:translate(-50%,-50%)">
                  <div style='font-size: 11px;color: #999999'>阅读率</div>
                  <div style='font-size: 14px;color: #3F86FF'>{{ cItem.readingRate }}%</div>
                </div>
              </van-circle>
              <van-circle
                v-else
                v-model="cItem.readingRate"
                :rate="cItem.readingRate"
                size="72"
                layer-color="#F5F5F5"
                :stroke-width="65"
                :color="gradientColor"
                style="position: relative"
              >
                <div style="position: absolute;top: 50%;left: 50%;transform:translate(-50%,-50%)">
                  <div style='font-size: 11px;color: #999999'>阅读率</div>
                  <div style='font-size: 14px;color: #3F86FF'>{{ cItem.readingRate }}%</div>
                </div>
              </van-circle>
            </div>
            <div
              style="text-align: center;color:#333333;font-size: 23px;padding-right: 29px;border-right: 1px solid #EBEBEB">
              <div style="">{{ cItem.pushUserNumber }}</div>
              <div style="color: #999999;font-size: 13px">送达人数</div>
            </div>
          </div>
          <div style="display: flex;justify-content: space-around;flex:1">
            <div style="text-align: center">
              <div style="font-size: 23px;color: #333333">{{ cItem.readUserNumber }}</div>
              <div style="display: flex;align-items: center">
                <div style="width: 5px;height: 5px;border-radius: 50%;background: #3F86FF;margin-right: 5px"></div>
                <div style="color: #999999;font-size: 13px">已读</div>
              </div>
            </div>
            <div style="text-align: center">
              <div style="font-size: 23px;color: #333333">{{ cItem.noReadUserNumber }}</div>
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

Vue.use(Circle);
import Api from '@/api/followUp.js'
import loadingDialog from "./loadingDialog";

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
    loadingDialog
  },
  mounted() {
    this.$refs.loading.openLoading()
    this.getDoctorQueryCountLearnPlanPush()
  },
  methods: {
    // 跳转到cms
    //跳转到cms
    goCMS(item) {
      if (item.cmsLink) {
        //此文章为外链，直接跳转到外链
        if (item.hrefUrl.startsWith("http")) {
          //头部有http协议，不需要拼接
          window.location.href = item.hrefUrl + '?time=' + ((new Date()).getTime());
        }else {
          window.location.href = 'http://' + item.hrefUrl + '?time=' + ((new Date()).getTime());
        }
      } else {
        //跳转到cms查看文章
        this.$router.push({path: '/cms/show', query: {id: item.cmsId, isComment: true}})
      }
    },
    getDoctorQueryCountLearnPlanPush() {
      Api.doctorQueryCountLearnPlanPush().then(res => {
        if (res.data.code === 0) {
          this.$refs.loading.cancelLoading()
          this.listData = res.data.data
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
    padding: 17px 0;

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
      border-bottom: 1px solid #EBEBEB;
    }
  }
}
</style>
