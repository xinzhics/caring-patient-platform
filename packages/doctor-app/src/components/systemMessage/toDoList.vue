<template>
  <div style="position: relative">
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa; min-height: 70vh">
      <van-list
        v-model="loading"
        :finished="finished"
        @load="onLoad">

        <div v-for="(item, key) in list" :key="key">
          <div style="display: flex;">
            <div style="width: 15%; padding-top: 40px; padding-left: 20px; display: flex; justify-content: flex-end"
                 :style="{display: isOpen ? 'flex' : 'none'}" @click="itemSelect(key)">
              <van-image v-if="item.flag" height="20px" width="20px"
                         :src="require('@/assets/my/system-select.png')"/>
              <van-image v-else height="20px" width="20px"
                         :src="require('@/assets/my/system-unselect.png')"/>
            </div>
            <div style="width: 100vw; display: flex;"
                 :style="{paddingBottom: key === list.length - 1 ? '100px' : '0px'}">
              <div style="padding: 15px 10px 0px 15px; background: #FAFAFA;"
                   :style="{width: isOpen ? '100vw' : 'none'}">
                <div style="background: white; padding: 10px 15px; border-radius: 10px;">
                  <!--     头部       -->
                  <div style="display: flex; align-items: center; justify-content: space-between"
                       @click="jumpItem(item)">
                    <div style="display: flex; align-items: center">
                      <van-image
                        width="40px"
                        height="40px"
                        fit="contain"
                        v-if="item.interactiveFunctionType === 'MEDICATION'"
                        :src="require('@/assets/my/system-4.png')"
                      />
                      <van-image
                        width="40px"
                        height="40px"
                        fit="contain"
                        v-else-if="item.interactiveFunctionType === 'INDICATOR_MONITORING'"
                        :src="require('@/assets/my/system-5.png')"
                      />
                      <van-image
                        width="40px"
                        height="40px"
                        fit="contain"
                        v-else-if="item.interactiveFunctionType === 'BLOOD_PRESSURE'"
                        :src="require('@/assets/my/system-5.png')"
                      />
                      <van-image
                        width="40px"
                        height="40px"
                        fit="contain"
                        v-else
                        :src="require('@/assets/my/system-3.png')"
                      />

                      <div style="margin-left: 10px;">
                        <div style="font-weight: 600; font-size: 15px; color: #1F1F1F;">
                          {{ item.interactiveFunctionType === 'MEDICATION' ? '我的药箱' : item.planName }}
                        </div>
                        <div style="font-size: 12px; color: #A6A6A6;">{{ getTime(item.createTime) }}</div>
                      </div>
                    </div>

                    <div style="display: flex; flex-direction: column; align-items: flex-end">
                      <div v-if="item.doctorReadStatus === 0"
                           style="width: 8px; height: 8px; background: red; border-radius: 10px; margin-bottom: 6px"></div>
                      <van-image
                        width="30px"
                        height="20px"
                        fit="contain"
                        @click.native="selectItem(key, $event)"
                        :src="require('@/assets/my/system-go.png')"
                      />
                    </div>
                  </div>

                  <div style="font-size: 14px; color: #292929; margin-top: 12px" @click="jumpItem(item)">
                    {{ item.pushContent }}
                  </div>

                  <!--   评论    -->
                  <div v-if="item.doctorReadStatus === 1 || item.doctorCommentStatus === 1"
                       style="padding: 14px; background: #F7F7F7; border-radius: 12px; margin-top: 12px">
                    <div v-if="item.doctorReadStatus === 1">
                      <div style="display: flex; align-items: center; justify-content: space-between">
                        <div style="display:flex; align-items: center">
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
                          <div style="font-size: 13px; color: #5E5E5E; font-weight: 600; margin-left: 5px">
                            {{ item.doctorName }}
                          </div>
                        </div>
                        <div style="display: flex; font-size: 13px; color: #5E5E5E; align-items: center">
                          <div>{{ getTime(item.doctorReadTime) }}</div>
                        </div>
                      </div>
                      <div style="font-size: 13px; color: #1F1F1F; margin-left: 35px; margin-top: 3px">
                        您已查看{{ item.pushPerson }}提交的信息
                      </div>
                    </div>

                    <div v-if="item.doctorCommentStatus === 1 && item.doctorReadStatus === 1"
                         style="border-bottom: 1px solid #EFEFEF; margin: 12px 0px"></div>

                    <div v-if="item.doctorCommentStatus === 1">
                      <div style="display: flex; align-items: center; justify-content: space-between">
                        <div style="display:flex; align-items: center">
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
                          <div style="font-size: 13px; color: #5E5E5E; font-weight: 600; margin-left: 5px">
                            {{ item.doctorName }}
                          </div>
                        </div>
                        <div style="display: flex; font-size: 13px; color: #5E5E5E; align-items: center">
                          <div>{{ getTime(item.doctorCommentContent.createTime) }}</div>
                          <van-image
                            width="13px"
                            height="13px"
                            style="margin-left: 5px;"
                            @click="deleteComment(item, key)"
                            :src="require('@/assets/my/delete.png')"
                          />
                        </div>
                      </div>
                      <div style="font-size: 13px; color: #1F1F1F; margin-left: 35px; margin-top: 3px">
                        {{ item.doctorCommentContent.commentContent }}
                      </div>
                    </div>
                  </div>

                  <!--   底部    -->
                  <div style="display: flex; justify-content: space-between; margin-top: 15px">
                    <div></div>
                    <div
                      style="display: flex; align-items: center; font-size: 13px; color: #5B5B5B; font-weight: 600;">
                      <div v-if="item.doctorReadStatus === 1" style="display: flex; align-items: center;">
                        <van-image
                          width="20px"
                          height="20px"
                          :src="require('@/assets/my/system-look-no.png')"
                        />
                        <div style="margin-left: 5px; color: #D2D2D2">看过</div>
                      </div>
                      <div v-else style="display: flex; align-items: center;"
                           @click="lookMessage(item.id, item.doctorName, key)">
                        <van-image
                          width="20px"
                          height="20px"
                          :src="require('@/assets/my/system-look.png')"
                        />
                        <div style="margin-left: 5px">看过</div>
                      </div>
                      <div v-if="item.doctorCommentStatus === 1"
                           style="display: flex; align-items: center; margin-left: 20px">
                        <van-image
                          width="20px"
                          height="20px"
                          :src="require('@/assets/my/system-comment-no.png')"
                        />
                        <div style="margin-left: 5px; color: #D2D2D2">评论</div>
                      </div>
                      <div v-else style="display: flex; align-items: center; margin-left: 20px"
                           @click="openComment(key)">
                        <van-image
                          width="20px"
                          height="20px"
                          :src="require('@/assets/my/system-comment.png')"
                        />
                        <div style="margin-left: 5px">评论</div>
                      </div>

                    </div>
                  </div>

                </div>
              </div>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>

    <dialog-comment ref="commentRef" @submit="submitComment"></dialog-comment>
    <system-del-tips-dialog ref="systemDelTipsDialog"/>

    <div class="div-bottom" v-if="isOpen">
      <div style="font-size: 16px; color: #B2B2B2; display: flex; align-items: center" @click="selectAll">
        <van-image v-if="allSelect" height="20px" width="20px" :src="require('@/assets/my/system-select.png')"/>
        <van-image v-else height="20px" width="20px" :src="require('@/assets/my/system-unselect.png')"/>
        <span style="margin-left: 5px">全选</span>
      </div>

      <div>
        <van-button round plain style="margin-right: 10px; width: 80px; border: 1px solid #24C789; color: #24C789"
                    @click="isOpen = false">取消
        </van-button>
        <van-button round style="width: 80px; background: #24C789; color: white" @click="deleteAll">删除</van-button>
      </div>
    </div>
  </div>
</template>

<script>

import moment from "moment";
import doctorApi from "@/api/doctor"
import dialogComment from "@/components/systemMessage/systemCommentDialog"
import systemDelTipsDialog from "@/components/systemMessage/systemDelTipsDialog"
import Vue from 'vue';
import {Button} from 'vant';

Vue.use(Button);

export default {
  name: "index",
  components: {
    dialogComment,
    systemDelTipsDialog
  },
  data() {
    return {
      loading: false,
      finished: false,
      refreshing: false,
      isLoading: false,
      isOpen: false,
      list: [],
      pos: -1,
      allSelect: false,
      param: {
        current: 1,
        model: {},
        size: 20
      },
    }
  },
  methods: {
    jumpItem(data) {
      localStorage.setItem('patientId', data.patientId)
      if (data.doctorReadStatus === 0) {
        doctorApi.doctorSeeMessage({messageId: data.id, doctorName: data.doctorName})
          .then(res => {})
      }
      if ('MEDICATION' === data.interactiveFunctionType) {
        // 跳转药品详情
        if (data.doctorCommentContent) {
          this.$router.push({path: '/medicine/index'})
        } else {
          this.$router.push({
            path: '/medicine/index',
            query: {
              doctorId: data.doctorId,
              doctorName: data.doctorName,
              messageId: data.id,
            }
          })
        }
      } else if ('BLOOD_SUGAR' === data.interactiveFunctionType) {
        localStorage.setItem('pageTitle', data.planName)
        if (data.doctorCommentContent) {
          this.$router.push({path: '/monitor/glucoseEditor', query: {id: data.businessId}})
        } else {
          this.$router.push({
            path: '/monitor/glucoseEditor',
            query: {
              id: data.businessId,
              doctorId: data.doctorId,
              doctorName: data.doctorName,
              systemMsgId: data.id,
            }
          })
        }
      } else {
        localStorage.setItem('pageTitle', data.planName)
        doctorApi.checkFormResultExist({formResultId: data.businessId})
          .then(res => {
            if (res.data.data) {
              if ('REVIEW_MANAGE' === data.interactiveFunctionType) {
                // 检验数据详情页面
                if (data.doctorCommentContent) {
                  this.$router.push({
                    path: '/testNumber/showdata',
                    query: {
                      content: data.businessId
                    }
                  })
                } else {
                  this.$router.push({
                    path: '/testNumber/showdata',
                    query: {
                      content: data.businessId,
                      doctorId: data.doctorId,
                      doctorName: data.doctorName,
                      messageId: data.id,
                    }
                  })
                }
              } else if ('HEALTH_RECORD' === data.interactiveFunctionType) {
                // 跳转健康档案详情
                if (data.doctorCommentContent) {
                  this.$router.push({
                    path: '/health/detail',
                    query: {
                      formId: data.businessId
                    }
                  })
                } else {
                  this.$router.push({
                    path: '/health/detail',
                    query: {
                      formId: data.businessId,
                      doctorId: data.doctorId,
                      doctorName: data.doctorName,
                      messageId: data.id,
                    }
                  })
                }
              } else {
                if (data.doctorCommentContent) {
                  this.$router.push({path: '/healthCalendar/form/result', query: {formResultId: data.businessId}})
                } else {
                  this.$router.push({
                    path: '/healthCalendar/form/result',
                    query: {
                      formResultId: data.businessId,
                      doctorId: data.doctorId,
                      doctorName: data.doctorName,
                      messageId: data.id,
                    }
                  })
                }
              }
            } else {
              this.$refs.systemDelTipsDialog.open("该条信息已删除")
            }
          })
      }
    },
    deleteAll() {
      let ids = [];
      this.list.forEach(item => {
        if (item.flag) {
          ids.push(item.id)
        }
      })
      doctorApi.doctorDelToDoMessage({ids})
        .then(res => {
          this.isOpen = false;
          this.allSelect = false;
          this.refreshing = true;
          this.$emit("doctorRefresh");
          this.onLoad()
        })
    },
    // 单选用户
    itemSelect(index) {
      if (this.list[index].flag) {
        this.list[index].flag = false;
      } else {
        this.list[index].flag = true
      }

      this.allSelect = false;
    },
    // 全选
    selectAll() {
      if (this.allSelect) {
        this.list.forEach(item => {
          item.flag = false;
        })
        this.allSelect = false;
      } else {
        this.list.forEach(item => {
          item.flag = true;
        })
        this.allSelect = true;
      }
    },
    // 删除评论
    deleteComment(item, index) {
      if (!item.id || !item.doctorCommentContent || !item.doctorCommentContent.id) {
        return;
      }

      doctorApi.doctorDelCommentMessage({messageId: item.id, commentId: item.doctorCommentContent.id})
        .then(res => {
          this.list[index].doctorCommentStatus = 0;
          this.list[index].doctorCommentContent = undefined
        })
    },
    // 提交评论
    submitComment(val) {
      if (this.isLoading) {
        return
      }
      this.isLoading = true;
      doctorApi.doctorCommentMessage({
        commentContent: val,
        doctorId: this.list[this.pos].doctorId,
        doctorName: this.list[this.pos].doctorName,
        messageId: this.list[this.pos].id,
        patientId: this.list[this.pos].patientId,
      })
        .then(res => {
          this.list[this.pos].doctorCommentStatus = 1;
          this.isLoading = false;
          this.list[this.pos].doctorCommentContent = res.data.data
        })
    },
    // 查看消息
    lookMessage(messageId, doctorName, index) {
      doctorApi.doctorSeeMessage({messageId: messageId, doctorName: doctorName})
        .then(res => {
          this.list[index].doctorReadStatus = 1;
          this.list[index].doctorReadTime = moment().format('YYYY-MM-DD HH:mm');
          this.$emit('docCountMessage')
          this.$refs.systemDelTipsDialog.open('您已查看“' + this.list[index].pushPerson + '”提交的信息')
        })
    },
    selectItem(key, event) {
      event.stopPropagation();
      this.isOpen = true
    },
    onRefresh() {
      this.finished = false;
      this.loading = true;
      if (this.list.length > 0) {
        this.$emit('doctorRefresh')
      }
      this.onLoad();
    },
    onLoad() {
      if (this.refreshing) {
        this.list = [];
        this.param.current = 1;
        this.refreshing = false;
      }
      this.getMessage()
    },
    getMessage() {
      doctorApi.doctorToDoMessage(this.param)
        .then(res => {
          if (res.data && res.data.code == 0) {
            if (this.param.current === 1) {
              this.list = []
              res.data.data.records.forEach(item => {
                this.list.push({
                  ...item,
                  flag: false
                })
              })
            } else {
              res.data.data.records.forEach(item => {
                this.list.push({
                  ...item,
                  flag: false
                })
              })
            }
            this.loading = false;
            if (this.param.current >= res.data.data.pages) {
              this.finished = true;
            }
            this.param.current++;
          }
        })
    },
    getTime(time) {
      return moment(time).format('YYYY-MM-DD HH:mm')
    },
    openComment(index) {
      this.pos = index;
      this.$refs.commentRef.open()
    }
  }
}
</script>

<style lang="less" scoped>

.div-bottom {
  z-index: 9999;
  background: #FFF;
  position: fixed;
  bottom: 0px;
  left: 0;
  right: 0;
  padding: 0px 15px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid #F5F5F5;
}

</style>
