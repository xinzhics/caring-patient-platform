<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">请选择</x-header>
    <div style="padding-top: 50px;">
      <van-search
        ref="searchDiv"
        v-model="param.model.name"
        show-action
        shape="round"
        placeholder="搜索"
        @search="onSearch"
        @clear="onCancel"
      >
        <div slot="action" @click="onSearch">搜索</div>
      </van-search>
      <div ref="mainDiv" style="height: 500px;overflow-y:scroll; overflow-x:hidden;">
        <div v-if="nursingList.length > 0">
          <div style="font-size: 12px;color: #B8B8B8; padding: 10px;">
            我的医助
          </div>
          <van-cell-group>
            <van-cell v-for="(item, key) in nursingList" :key="key" :style="{opacity: isNursing ? 0.5 : 1}"
                      @click="onClickNursing(key)">
              <!-- 使用 right-icon 插槽来自定义右侧图标 -->
              <template #title>
                <div style="display: flex; align-items: center; justify-content: space-between">
                  <van-image
                    round
                    width="40px"
                    height="40px"
                    :src="item.avatar ? item.avatar : nursingDefaultImage"
                  />
                  <span style="margin-left: 5px; font-size: 14px; flex: 1; color: #666666">{{ item.name }}</span>
                  <van-icon v-if="item.isSelect" name="checked" size="25px" color="#3F86FF"/>
                  <van-icon v-else name="circle" size="25px"/>
                </div>
              </template>
            </van-cell>
          </van-cell-group>
        </div>

        <div style="font-size: 12px;color: #B8B8B8; padding: 10px;">
          所有医生
        </div>


        <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa;">
          <van-list
            v-model="loading"
            :finished="finished"
            @load="onLoad">
            <van-cell-group>
              <van-cell v-for="(item, key) in doctorList" :key="key" @click="onClickDoctor(key)"
                        :style="{opacity: item.isFlag ? 0.5 : 1}">
                <template #title>
                  <div style="display: flex; align-items: center; justify-content: space-between">
                    <van-image
                      round
                      width="40px"
                      height="40px"
                      :src="item.avatar"
                    />
                    <span style="margin-left: 5px; font-size: 14px; flex: 1; color: #666666">{{ item.name }}</span>
                    <van-icon v-if="item.isSelect" name="checked" size="25px" color="#3F86FF"/>
                    <van-icon v-else name="circle" size="25px"/>
                  </div>
                </template>
              </van-cell>
            </van-cell-group>
          </van-list>
        </van-pull-refresh>

      </div>
      <div style="height: 50px; padding-right: 20px;">

        <van-button type="info" size="small" round style="min-width: 80px; margin-top: 10px; float: right" :loading="saveLoading" loading-text="添加中.."
                    @click="addDoctorToGroup()">确定
        </van-button>
      </div>
    </div>


  </section>
</template>

<script>
import DoctorApi from "@/api/doctor.js";
import ContentApi from '@/api/Content.js'

export default {
  name: "doctorList",
  data() {
    return {
      nursingDefaultImage: require('@/assets/my/default_avatar.png'),
      doctorId: [],
      nursingList: [],
      doctorList: [],
      nursingIMAccount: '',
      isNursing: false,
      param: {
        current: 1,
        size: 30,
        model: {
          wxStatus: 1,
          name: '',
        }
      },
      loading: false,
      finished: false,
      refreshing: false,
      saveLoading: false,
    }
  },
  methods: {
    onRefresh() {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      this.onLoad();
    },
    onLoad() {
      setTimeout(() => {
        if (this.refreshing) {
          this.doctorList = [];
          this.param.current = 1;
          this.refreshing = false;
        }
        this.getDoctorList()
      }, 500);
    },
    getNursingStaffInfo() {
      //先获取医生信息中的医助Id
      DoctorApi.getContent({id: localStorage.getItem('caring_doctor_id')})
        .then(res => {
          if (res.data.code === 0) {
            ContentApi.getNursingStaffInfo(res.data.data.nursingId)
              .then(res => {
                if (res.data && res.data.code === 0) {
                  if (this.isNursing) {
                    res.data.data.isSelect = true
                  } else {
                    res.data.data.isSelect = false
                  }
                  this.nursingList.push(res.data.data)
                }
              })
          }
        })
    },
    addDoctorToGroup() {
      let arr = this.doctorList.filter(item => item.isSelect && !item.isFlag)
      if ((arr && arr.length > 0) || this.nursingIMAccount) {
        let arrId = arr.map(item => item.openId)
        let param = {
          doctorOpenId: arrId,
          invitePeople: localStorage.getItem('caring_doctor_id'),
          invitePeopleRole: 'doctor',
          groupId: localStorage.getItem('groupId'),
          nursingIMAccount: this.nursingIMAccount,
        }
        this.saveLoading = true
        DoctorApi.addDoctorToGroup(param)
          .then(res => {
            this.saveLoading = false
            if (res.data && res.data.code === 0) {
              if (this.nursingIMAccount && (arr && arr.length > 0)) {
                this.$vux.toast.text("邀请成功，等待医生同意邀请！", 'center');
              } else if (arr && arr.length > 0) {
                this.$vux.toast.text("邀请成功，等待医生同意邀请！", 'center');
              } else {
                this.$vux.toast.text("邀请成功", 'center');
              }
              this.$router.back()
            }
          })
      } else {
        this.$vux.toast.text("请选择要添加的医生或医助", 'center');
      }

    },
    onClickNursing(key) {
      if (this.isNursing) {
        return
      }
      this.nursingList[key].isSelect = !this.nursingList[key].isSelect
      if (this.nursingList[key].isSelect) {
        this.nursingIMAccount = this.nursingList[key].imAccount
      } else {
        this.nursingIMAccount = ''
      }
    },
    onClickDoctor(key) {
      if (this.doctorList[key].isFlag) {
        return
      }
      this.doctorList[key].isSelect = !this.doctorList[key].isSelect
    },
    getDivHeight() {
      const screenheight = window.innerHeight
      this.$refs.mainDiv.style.height = (screenheight - 100 - this.$refs.searchDiv.offsetHeight) + 'px'
    },
    onSearch() {
      this.doctorSelect = []
      this.doctorList = []
      this.getDoctorList()
    },
    onCancel() {
      this.doctorSelect = []
      this.doctorList = []
      this.getDoctorList()
    },
    getConsultationDetail() {
      ContentApi.getConsultationGroupDetail(localStorage.getItem('groupId')).then((res) => {
        if (res.data.code === 0) {
          let arr = res.data.data.consultationGroupMembers
            .filter(item => {
              if (item.memberRole === "roleDoctor") {
                return item
              } else if (item.memberRole === "NursingStaff") {
                this.isNursing = true
              }
            })
          this.doctorId = arr.map(item => item.memberOpenId)
          this.getNursingStaffInfo()
          this.getDoctorList()
        }
      })
    },
    getDoctorList() {
      DoctorApi.getDoctorList(this.param)
        .then(res => {
          if (res.data && res.data.code === 0) {
            if (this.current === 1) {
              this.doctorList = []
            }
            res.data.data.records.forEach(item => {
              if (this.doctorId.findIndex(id => id === item.openId) == -1) {
                //不在讨论组中
                item.isSelect = false
              } else {
                item.isSelect = true
                item.isFlag = true
              }
              //在讨论组中的医生
              this.doctorList.push(item)
            })
            this.param.current++;
            this.loading = false;
            if (this.doctorList.length == res.data.data.total) {
              this.finished = true;
            }
          }
        })
    }
  },
  created() {
    this.getConsultationDetail()
  },
  // 在 mounted 生命周期监听窗口变化并触发上文处理函数，修改高度
  mounted() {
    this.getDivHeight()
    window.addEventListener('resize', this.getDivHeight)
  },
  destroyed() {
    window.removeEventListener('resize', this.getDivHeight, false)
  },
}
</script>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background: #fafafa;
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
