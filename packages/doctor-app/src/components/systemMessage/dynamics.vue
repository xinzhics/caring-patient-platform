<template>
  <section style="position: relative">
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa; min-height: 65vh">
      <van-list
        v-model="loading"
        :finished="finished"
        @load="onLoad">

        <div v-for="(item, key) in list" :key="key" ref="itemRefs" style="padding: 15px 10px 0px 15px; background: #FAFAFA;"
             :style="{width: isOpen ? '100vw' : ''}">
          <div style="display: flex; width: 100%">
            <div style="padding-top: 40px; padding-right: 15px; display: flex; justify-content: flex-end"
                 :style="{display: isOpen ? 'flex' : 'none'}" @click="itemSelect(key)">
              <van-image v-if="item.flag" height="20px" width="20px"
                         :src="require('@/assets/my/system-select.png')"/>
              <van-image v-else height="20px" width="20px"
                         :src="require('@/assets/my/system-unselect.png')"/>
            </div>
            <div style="background: white; border-radius: 8px; width: 100%">
                <div v-if="item.msgType === 'consultation'" style="padding: 10px 15px" >
                  <!--     头部       -->
                  <div style="display: flex; align-items: center; justify-content: space-between">
                    <div style="display: flex; align-items: center">
                      <van-image
                        width="35px"
                        height="35px"
                        fit="contain"
                        :src="require('@/assets/my/system-6.png')"
                      />
                      <div style="margin-left: 10px;">
                        <div style="font-weight: 600; font-size: 15px; color: #1F1F1F;">
                          邀请反馈
                        </div>
                        <div style="font-size: 12px; color: #A6A6A6;">{{ getTime(item.createTime) }}</div>
                      </div>
                    </div>

                    <div style="display: flex; flex-direction: column; align-items: flex-end">
                      <div v-if="item.readStatus === 0"
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

                  <div style="font-size: 14px; color: #292929; margin-top: 12px;">{{ JSON.parse(item.content).message }}</div>
                  <div>
                    <span style="font-size: 14px; color: #292929">拒绝理由：</span>
                    <span style="font-size: 14px; color: #292929; font-weight: 550">{{ JSON.parse(item.content).reason }}</span>
                  </div>
                </div>
                <div v-else>
                  <div style="padding: 10px 15px">
                    <!--     头部       -->
                    <div style="display: flex; align-items: center; justify-content: space-between">
                      <div style="display: flex; align-items: center">
                        <van-image
                          width="35px"
                          height="35px"
                          fit="contain"
                          v-if="item.msgType !== 'subscribe'"
                          :src="require('@/assets/my/system-1.png')"
                        />
                        <van-image
                          width="35px"
                          height="35px"
                          fit="contain"
                          v-else
                          :src="require('@/assets/my/system-2.png')"
                        />

                        <div style="margin-left: 10px;">
                          <div style="font-weight: 600; font-size: 15px; color: #1F1F1F;">
                            {{ item.msgType !== 'subscribe' ? '取关通知' : '关注通知' }}
                          </div>
                          <div style="font-size: 12px; color: #A6A6A6;">{{ getTime(item.createTime) }}</div>
                        </div>
                      </div>

                      <div style="display: flex; flex-direction: column; align-items: flex-end">
                        <div v-if="item.readStatus === 0"
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

                    <div style="font-size: 14px; color: #292929; margin-top: 12px">
                      {{ item.content }}
                    </div>
                  </div>
                </div>
            </div>
          </div>
        </div>

      </van-list>
    </van-pull-refresh>

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
  </section>
</template>

<script>
import Api from '@/api/Content.js'
import doctorApi from "@/api/doctor"
import moment from "moment/moment";

export default {
  name: "index",
  data() {
    return {
      list: [],
      isOpen: false,
      param: {
        current: 1,
        model: {
          userRole: 'doctor',
          userId: localStorage.getItem('caring_doctor_id'),
        },
        size: 20
      },
      loading: false,
      finished: false,
      refreshing: false,
      allSelect: false,
      ids: [],
      intervalId: null,
    }
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
      doctorApi.setSystemMsgRead({ids: arr})
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
                if (!this.list[i].readStatus) {
                  this.ids.push(this.list[i].id)
                  this.list[i].readStatus = true
                  this.$emit('patCountMsg')
                }
              }
            });
          },
          {root: null, rootMargin: '0px', threshold: 1} // 视口阈值
        );
        observer.observe(itemElements[i]);
      }
    },
    deleteAll() {
      let ids = [];
      this.list.forEach(item => {
        if (item.flag) {
          ids.push(item.id)
        }
      })
      doctorApi.systemDelMsg(ids)
        .then(res => {
          this.isOpen = false;
          this.allSelect = false;
          this.refreshing = true;
          this.$emit("patientRefresh")
          this.onLoad()
        })
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
    selectItem(key, event) {
      event.stopPropagation();
      this.isOpen = true
    },
    // 单选用户
    itemSelect(index) {
      if (this.list[index].flag) {
        this.list[index].flag = !this.list[index].flag;
      } else {
        this.list[index].flag = true
      }

      this.allSelect = false;
    },
    onRefresh() {
      this.finished = false;
      this.loading = true;
      if (this.list.length > 0) {
        this.$emit('patientRefresh')
      }
      this.onLoad();
    },
    onLoad() {
      if (this.refreshing) {
        this.list = [];
        this.param.current = 1;
        this.refreshing = false;
      }
      if (this.param.current === 1) {
        // 接口请求后执行定时任务
        this.stopInterval()
        this.startInterval()
      }
      this.getMessage()
    },
    getMessage() {
      Api.getSystemMsg(this.param)
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
