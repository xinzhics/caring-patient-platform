<template>
  <section class="content">
    <navBar pageTitle="系统消息" backUrl="/home"/>
    <div v-if="isloading">
      <div v-if="list.length > 0">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          style="background: #f7f7f7"
          @load="onLoad">
          <div v-for="(item, key) in list" :key="key" ref="itemRefs" @click="jumpUrl(item)">
            <div v-if="item.functionType === 'INTERACTIVE_MESSAGE'" style="margin: 5px 16px 20px 16px">
              <div style="background: white; border-radius: 8px; padding: 15px; ">
                <div style="display: flex; align-items: center; justify-content: space-between">
                  <div style="display: flex; align-items: center">
                    <van-image v-if="item.interactiveFunctionType === 'MEDICATION'" width="25px" height="25px" :src="require('@/assets/my/system_1.png')"/>
                    <van-image v-else-if="item.interactiveFunctionType === 'HEALTH_RECORD' || item.interactiveFunctionType === 'REVIEW_MANAGE'" width="25px" height="25px" :src="require('@/assets/my/system_2.png')"/>
                    <van-image v-else width="25px" height="25px" :src="require('@/assets/my/system_3.png')"/>
                    <div style="font-weight: 600; font-size: 16px; color: #1F1F1F; margin-left: 5px">
                      <div v-if="item.interactiveFunctionType === 'MEDICATION'">我的药箱</div>
                      <div v-else-if="item.interactiveFunctionType === 'HEALTH_RECORD' || item.interactiveFunctionType === 'REVIEW_MANAGE'">健康日志提醒</div>
                      <div v-else>健康监测提醒</div>
                    </div>
                  </div>
                  <div v-if="item.readStatus === 0"
                       style="width: 8px; height: 8px; background: red; border-radius: 10px; margin-bottom: 6px"></div>
                </div>

                <div style="margin-top: 12px; margin-bottom: 12px; font-weight: 400; font-size: 14px; color: #292929;">{{item.pushContent}}</div>

                <!--   评论    -->
                <div v-if="item.doctorReadStatus === 1 || item.doctorCommentStatus === 1"
                     style="padding: 14px; background: #F7F7F7; border-radius: 12px; margin-top: 12px">
                  <div v-if="item.doctorReadStatus === 1">
                    <div style="display: flex; align-items: center; justify-content: space-between">
                      <div style="display:flex; align-items: center">
                        <div style="display: flex; position: relative;">
                          <van-image
                            width="30px"
                            height="30px"
                            round
                            style="border-radius: 30px"
                            v-if="item.doctorAvatar"
                            :src="item.doctorAvatar"
                          />
                          <van-image
                            width="30px"
                            height="30px"
                            v-else
                            :src="require('@/assets/my/doctor_avatar.png')"
                          />

                          <div v-if="item.patientReadDoctorStatus === 0" style="background-color: red; width: 5px; height: 5px; border-radius: 5px; position: absolute; top: 1px; right: 1px;"></div>
                        </div>
                        <div style="font-size: 13px; color: #5E5E5E; font-weight: 600; margin-left: 5px">
                          {{ item.doctorName }}
                        </div>
                      </div>
                      <div style="display: flex; font-size: 13px; color: #5E5E5E; align-items: center">
                        <div>{{ getTime(item.doctorReadTime, "YYYY-MM-DD HH:mm") }}</div>
                      </div>
                    </div>
                    <div style="font-size: 13px; color: #1F1F1F; margin-left: 35px; margin-top: 3px">
                      {{ item.doctorName }}已查看您提交的信息
                    </div>
                  </div>

                  <div v-if="item.doctorCommentStatus === 1 && item.doctorReadStatus === 1"
                       style="border-bottom: 1px solid #EFEFEF; margin: 12px 0px"></div>

                  <div v-if="item.doctorCommentStatus === 1">
                    <div style="display: flex; align-items: center; justify-content: space-between">
                      <div style="display:flex; align-items: center">
                        <div style="display: flex; position: relative;">
                          <van-image
                            width="30px"
                            height="30px"
                            round
                            style="border-radius: 30px"
                            v-if="item.doctorAvatar"
                            :src="item.doctorAvatar"
                          />
                          <van-image
                            width="30px"
                            height="30px"
                            v-else
                            :src="require('@/assets/my/doctor_avatar.png')"
                          />

                          <div v-if="item.patientReadDoctorCommentStatus === 0" style="background-color: red; width: 5px; height: 5px; border-radius: 5px; position: absolute; top: 1px; right: 1px;"></div>
                        </div>
                        <div style="font-size: 13px; color: #5E5E5E; font-weight: 600; margin-left: 5px">
                          {{ item.doctorName }}
                        </div>
                      </div>
                      <div style="display: flex; font-size: 13px; color: #5E5E5E; align-items: center">
                        <div>{{ getTime(item.doctorCommentContent.createTime, "YYYY-MM-DD HH:mm") }}</div>
                      </div>
                    </div>
                    <div style="font-size: 13px; color: #1F1F1F; margin-left: 35px; margin-top: 3px">
                      {{ item.doctorCommentContent.commentContent }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else>
              <div style="display: flex; justify-content: center">
                <div v-if="item.time" class="time">{{ item.time }}</div>
              </div>

              <van-cell-group inset style="margin-top: 5px; margin-bottom: 20px">
                <van-cell size="large">
                  <template slot="title">
                    <div class="title_box">
                      <div class="title_style">{{ item.functionName }}</div>
                      <div class="red_drop" v-if="!item.readStatus"></div>
                    </div>
                  </template>
                  <template slot="label">
                    <div>
                      <div class="label_box">
                        <div class="label_title">
                          推送时间：
                        </div>
                        <div class="label_content">
                          {{ item.createTime }}
                        </div>
                      </div>

                      <div class="label_box">
                        <div class="label_title">
                          推送人：
                        </div>
                        <div class="label_content">
                          {{ item.pushPerson }}
                        </div>
                      </div>

                      <div class="label_box">
                        <div class="label_title">
                          推送内容：
                        </div>
                        <div class="label_content"
                             style="width: 200px; -webkit-line-clamp: 2; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-box-orient: vertical;">
                          {{ item.pushContent }}
                        </div>
                      </div>
                    </div>
                  </template>

                </van-cell>
              </van-cell-group>
            </div>

          </div>
        </van-list>
      </div>
      <div v-else class="no_data">
        <van-image
          width="250"
          height="120"
          fit="fill"
          :src="require('@/assets/my/system_no_data.png')"
        />
        <div style="font-size: 16px; color: #999999; margin-top: 30px">暂无系统消息</div>
      </div>
    </div>
  </section>
</template>

<script>

import Vue from 'vue';
import {Empty, List, Cell, CellGroup} from 'vant';
import ContentApi from "@/api/Content";

Vue.use(List);
Vue.use(Empty);
Vue.use(Cell);
Vue.use(CellGroup);

export default {
  name: "index",
  components: {
    navBar: () => import('@/components/headers/navBar')
  },
  data() {
    return {
      list: [],
      loading: false,
      finished: false,
      isloading: false,
      itemObservers: [], // 存储 Intersection Observer 实例
      params: {
        model: {
          patientId: localStorage.getItem('userId'),
        },
        order: "descending",
        size: 20,
        current: 1,
        sort: "id"
      },
      ids: [],
      intervalId: null,
    }
  },
  mounted() {
    if (this.$route.query && this.$route.query.messageType) {
      this.params.model.functionType = this.$route.query.messageType
    }
    // 接口请求后执行定时任务
    this.getMessage()
    this.startInterval()
  },
  updated() {
    this.initObservers();
  },
  beforeDestroy() {
    // 组件卸载时停止定时任务
    this.stopInterval();
    this.setSystemMsgRead()
  },
  methods: {
    jumpUrl(item) {
      if (item.jumpUrl) {
        window.location.href = item.jumpUrl;
      } else if (item.functionType === 'INTERACTIVE_MESSAGE') {
        // 互动消息中 医生评论消息，可以点击跳转
        if (item.interactiveFunctionType === 'MEDICATION') {
          // 用药
          this.$router.push("/medicine/history")
        } else if (item.interactiveFunctionType === 'HEALTH_RECORD') {
          // 疾病信息
          this.$router.push({path:'/health/list',query:{formId: item.businessId, onback: 2}})
        } else if (item.interactiveFunctionType === 'REVIEW_MANAGE') {
          // 检查报告
          this.$router.push({path:'/testNumber/detail',query:{content: item.businessId, onback: 2}})
        } else if (item.interactiveFunctionType === 'CUSTOM_FOLLOW_UP') {
          // 自定义随访
          this.$router.push({path:'/custom/follow/detail/result',query:{formId: item.businessId, onback: 2}})
        } else if (item.interactiveFunctionType === 'BLOOD_PRESSURE') {
          // 血压
          this.$router.push({path:'/monitor/pressureEditor',query:{content: item.businessId, onback: "/patient/systemMessage"}})
        } else if (item.interactiveFunctionType === 'BLOOD_SUGAR') {
          // 血糖
          this.$router.push({path:'/monitor/glucoseEditor',query:{id: item.businessId, onback: "/patient/systemMessage"}})
        }
      } else {
        this.$vux.toast.text('此功能不可跳转', 'center')
      }
    },
    // 每隔五秒查询一次是否有未读消息需要设置已读
    startInterval() {
      // 清除之前的定时任务（如果存在）
      if (this.intervalId) {
        clearInterval(this.intervalId);
      }
      // 设置新的定时任务
      this.intervalId = setInterval(() => {
        this.setSystemMsgRead()
        // 执行你的程序逻辑
      }, 5000); // 每5秒执行一次
    },
    // 设置系统消息为已读
    setSystemMsgRead() {
      if (this.ids.length === 0) {
        return
      }
      let arr = this.ids
      ContentApi.setSystemMessageStatus({ids: arr})
        .then(res => {
          // 是否包含
          let diff = this.ids.filter(x => !arr.includes(x));
          this.ids = diff
        })
    },
    stopInterval() {
      // 停止定时任务
      if (this.intervalId) {
        clearInterval(this.intervalId);
        this.intervalId = null;
      }
    },
    initObservers() {
      const itemElements = this.$refs.itemRefs; // 获取所有子元素的引用
      if (!itemElements) {
        return
      }
      // 创建 Intersection Observer 实例并监听进入视口事件
      for (let i = 0; i < itemElements.length; i++) {
        const observer = new IntersectionObserver(
          (entries) => {
            entries.forEach((entry) => {
              if (entry.isIntersecting) {
                if (!this.list[i].readStatus || this.list[i].patientReadDoctorStatus === 0 || this.list[i].patientReadDoctorCommentStatus === 0) {
                  this.ids.push(this.list[i].id)
                  this.list[i].readStatus = true
                  this.$emit('patCountMsg')
                  this.list[i].patientReadDoctorStatus = 1
                  this.list[i].patientReadDoctorCommentStatus = 1
                }
              }
            });
          },
          {root: null, rootMargin: '0px', threshold: 1} // 视口阈值
        );
        observer.observe(itemElements[i]);
      }
    },
    onLoad() {
      setTimeout(() => {
        if (this.refreshing) {
          this.contentList = [];
          this.params.current = 1;
          this.refreshing = false;
        }
        this.getMessage()
      }, 500);
    },
    getMessage() {
      ContentApi.getPatientSystemMessage(this.params)
        .then(res => {
          if (this.params.current === 1) {
            this.list = res.data.data.records
          } else {
            this.list.push(...res.data.data.records)
          }
          let time = ''
          for (let i = 0; i < this.list.length; i++) {
            if (i === 0) {
              // 第一条消息显示时间
              time = this.list[i].createTime
              let day = this.messageStatus(this.list[i].createTime)
              this.list[i].time = day === 0 ? this.getTime(this.list[i].createTime, 'HH:mm') : day === 1 ? '昨天' : day === 2 ? '前天' : this.getTime(this.list[i].createTime, 'YYYY-MM-DD HH:mm')
            } else if (this.getTime(time, 'YYYY-MM-DD') !== this.getTime(this.list[i].createTime, 'YYYY-MM-DD')) {
              time = this.list[i].createTime
              let day = this.messageStatus(this.list[i].createTime)
              this.list[i].time = day === 0 ? this.getTime(this.list[i].createTime, 'HH:mm') : day === 1 ? '昨天' : day === 2 ? '前天' : this.getTime(this.list[i].createTime, 'YYYY-MM-DD HH:mm')
            }
          }

          this.isloading = true;
          this.params.current++;
          this.loading = false;

          if (res.data.data.records && res.data.data.records.length < 20) {
            this.finished = true;
          }
        })
    },
    messageStatus(messageDate) {
      const today = moment().startOf('day');
      const messageDay = moment(messageDate).startOf('day');
      const diffDays = today.diff(messageDay, 'days');
      return diffDays
    },
    getTime(time, format) {
      // "YYYY-MM-DD HH:mm"
      return moment(time).format(format)
    }
  }
}
</script>

<style lang="less" scoped>

.content {
  height: 100vh;
  background: #F7f7f7;

  .time {
    margin: 15px 0px;
    font-size: 12px;
    color: #999999;
    text-align: center;
    width: 150px;
  }

  .label_box {
    display: flex;
    margin-bottom: 10px
  }

  .label_title {
    font-size: 14px;
    color: #999999;
    width: 90px;
    flex: none;
  }

  .label_content {
    font-size: 14px;
    color: #333333;
    min-width: 0;
  }

  .title_box {
    display: flex;
    justify-content: space-between;
    align-items: center
  }

  .title_style {
    font-size: 16px;
    font-weight: 600;
    color: #333333;
  }

  .red_drop {
    width: 8px;
    height: 8px;
    background: #F84C4C;
    border-radius: 8px
  }

  .no_data {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 150px;
    flex-direction: column;
  }
}

</style>
