<template>
  <section class="allContent" style="overflow-y: hidden">
    <navBar pageTitle="详情"></navBar>
    <div style="overflow-y: scroll; height: 100%">
      <group>
        <van-cell is-link :value="data.groupName" @click="() => {updateData = data.groupName, type = 1, isShow = true}">
          <span slot="title">名称<span style="color: red">*</span></span>
        </van-cell>

        <van-cell title="描述" is-link :value="data.groupDesc" @click="() => {updateData = data.groupDesc, type = 2, isShow = true}">
        </van-cell>
        <cell title="邀请二维码" is-link :link="'/consultation/qrCode?groupId=' + this.groupId">
          <div>
            <span style="color: #3F86FF">查看</span>
          </div>
        </cell>
      </group>


      <group>
        <div style="display: flex; justify-content: space-between; padding: 10px 15px; align-items: center">
          <span>参与人员</span>
          <van-icon name="add-o" size="25px" style="padding: 5px" v-if="data.doctorId && userId === data.doctorId"
                    @click="jumpDoctorList()"/>
        </div>
        <div class="listBackground">
          <div
            class="itenLine"
            v-for="(item,i) in data.consultationGroupMembers" :key="i">
            <div style="padding: 15px 12px 12px 15px; display: flex; align-items: center">
              <img :src="!item.memberAvatar ? headerImgT :item.memberAvatar" alt=""
                   class="memberImg">
              <div style="display:inline-block;vertical-align: middle; flex: 1">
                <div style="display: flex; font-size: 10px;">
                  <span class="contentTitle">{{
                      item.memberRole === 'patient' && item.doctorRemark ?item.memberName+'(' +item.doctorRemark +')': item.memberRole === 'patient' && patientName ? patientName : item.memberName
                    }}</span>
                  <div class="contentTag">
                    <div style="text-align: center; color: #999999; font-size: 11px">{{
                        getRoleName(item)
                      }}
                    </div>
                  </div>
                </div>
                <p style="color: #666666; font-size: 12px">
                  {{ getData(item.updateTime) }}
                </p>
              </div>
              <van-button
                v-if="item.memberRole !== 'patient' && item.memberUserId !== userId && data.doctorId && data.doctorId === userId"
                plain type="warning"
                style="width: 50px;" round size="mini"
                @click="removeDoctor(item)">移除
              </van-button>
            </div>
          </div>


        </div>
      </group>

      <div style="display: grid; justify-content: center; margin-top: 40px; margin-bottom: 80px ">
        <x-button mini type="primary" action-type="button"
                  style="height:44px; background: #ffffff; width: 200px; color: red; margin-bottom: 8px;"
                  @click.native="endConsultation()">{{ data.doctorId === userId ? '结束' : '退出' }}
        </x-button>
        <span style="font-size: 12px; color: #999999; text-align: center">{{ data.createTime }}</span>
      </div>
    </div>
    <van-dialog v-model="isShow" style="border-radius: 20px;" :hide-on-blur="true" :show-confirm-button="false">
      <div style="padding: 15px 0px;">
        <div
          style="position: relative; width: 100%; display: flex; align-items: center; margin-bottom: 10px;">
          <div style="display: flex; width: 100%; justify-content: center;">
            <div
              style=" max-width: 120px; text-overflow:ellipsis;overflow:hidden;white-space:nowrap; color: #333333; font-size: 16px;">
              请输入
            </div>
          </div>
          <van-icon name="cross" style="position: absolute; right: 0; margin-right: 15px;" size="15px;" color="#B8B8B8"
                    @click="isShow = !isShow"/>
        </div>
        <div style="display: flex; justify-content: center;">
          <div style="width: 80px; height: 5px;background: #FFBE8B; opacity: 1;border-radius: 8px;"></div>
        </div>

        <div style="margin: 0px 15px; background: #F5F5F5; border-radius: 5px; border: 1px solid #EBEBEB;
        margin-top: 20px; margin-bottom: 20px;">
          <van-field
            v-model="updateData"
            rows="4"
            autosize
            style="background: #F5F5F5;"
            type="textarea"
            maxlength="120"
            placeholder="请输入内容"
            show-word-limit
          />
        </div>
        <div style="text-align: center">
          <van-button mini type="primary" round
                      style="height:44px; background: #66728B; width: 200px; margin-top: 10px;"
                      @click="()=> updateNameOrDesc()">确定
          </van-button>
        </div>
      </div>
    </van-dialog>
  </section>
</template>

<script>

import Api from '@/api/Content.js'
import doctorApi from "@/api/doctor";
import {Dialog} from 'vant';
import navBar from "@/components/headers/navBar";
export default {
  name: "consultationDetail",
  components: {
    [Dialog.name]: Dialog,
    navBar
  },
  data() {
    return {
      data: {},
      type: 0,
      isShow: false,
      updateData: '',
      submitStatus: false,
      groupId: localStorage.getItem('groupId'),
      userId: localStorage.getItem('caring_doctor_id'),
      headerImgT: require('@/assets/my/default_avatar.png'),
      patientName: '',
    }
  },
  methods: {
    getRoleName(item) {
      if (item.memberRole === 'patient') {
        return this.$getDictItem('patient')
      } else if (item.memberRole === "NursingStaff") {
        return this.$getDictItem('assistant')
      } else {
        return item.memberRole === 'roleDoctor' && item.memberUserId === this.userId ? "我" : item.memberRoleRemarks
      }
    },
    /**
     * 更新名字或者描述
     */
    updateNameOrDesc() {
      console.log('更新的内容', this.updateData)
      if (this.submitStatus) {
        return
      }
      const params = {
        id: this.data.id
      }
      if (this.updateData) {
        this.updateData = this.updateData.trim()
      }
      if (this.type === 1) {
        if (!this.updateData) {
          this.$vux.toast.text("请输入名称!", 'center');
          return;
        }
        params.groupName = this.updateData
      }
      if (this.type === 2) {
        params.groupDesc = this.updateData
      }
      this.submitStatus = true;
      doctorApi.updateConsultationNameOrDesc(params).then(res => {
          if (res.data.code === 0) {
            if (this.type === 1) {
              this.data.groupName = this.updateData
            }
            if (this.type === 2) {
              this.data.groupDesc = this.updateData
            }
            this.updateData = ''
            this.submitStatus = false
            this.isShow = false
          } else {
            this.updateData = ''
            this.submitStatus = false
            this.isShow = false
          }
      })
      .catch(() => {
        this.updateData = ''
        this.submitStatus = false
        this.isShow = false
      })
    },
    endConsultation() {
      Dialog.confirm({
        title: '提示',
        message: this.data.doctorId === this.userId ? '您确定要结束此次会诊吗？' : '您确定要退出此次会诊吗？',
      })
        .then(() => {
          if (this.data.doctorId === this.userId) {
            doctorApi.endConsultation()
              .then(res => {
                if (res.data && res.data.code === 0) {
                  this.$vux.toast.text("结束成功!", 'center');
                  this.$router.go(-2)
                }
              })
          } else {
            let arr = this.data.consultationGroupMembers.filter(item => item.memberUserId === this.userId)
            if (arr && arr.length > 0) {
              doctorApi.removeDoctor(arr[0].id)
                .then(res => {
                  if (res.data && res.data.code === 0) {
                    this.$vux.toast.text("退出成功!", 'center');
                    this.$router.go(-2)
                  }
                })
            }
          }
        })
        .catch(() => {
          // on cancel
        });
    },
    removeDoctor(item) {
      const message = '您确定要移除' + item.memberName + '(' + item.memberRoleRemarks + ') 吗？'
      Dialog.confirm({
        title: '提示',
        message: message,
      })
        .then(() => {
          doctorApi.removeDoctor(item.id)
            .then(res => {
              if (res.data && res.data.code === 0) {
                this.getConsultationDetail()
              }
            })
        })
        .catch(() => {
          // on cancel
        });
    },
    getConsultationDetail() {
      Api.getConsultationGroupDetail(localStorage.getItem('groupId'))
        .then((res) => {
          if (res.data.code === 0) {
            this.data = res.data.data
            console.log(this.data.doctorId)
          }
        })
    },
    getData(data) {
      return moment(data).format("YYYY.MM.DD HH:mm");
    },
    jumpDoctorList() {
      this.$router.push('/consultation/doctorList')
    },
  },

  created() {
    if (this.$route.query && this.$route.query.remark) {
      this.patientName = this.$route.query.remark
      console.log(this.patientName)
    }

    this.getConsultationDetail()
  }
}
</script>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;

  .listBackground {
    background: #FFFFFF;
    border-top: 1px solid #F5F5F5
  }

  .itenLine {
    border-bottom: 1px solid #F5F5F5
  }

  .memberImg {
    width: 35px;
    border-radius: 50%;
    height: 35px;
    vertical-align: middle;
    margin-right: 10px
  }

  .contentTitle {
    font-weight: 600;
    font-size: 15px;
    color: #333333
  }

  .contentTag {
    background: #F5F5F5;
    display: flex;
    align-items: center;
    justify-items: center;
    border-radius: 10px;
    margin-left: 5px;
    padding-left: 6px;
    padding-right: 6px;
    padding-top: 2px;
    padding-bottom: 2px;
  }
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




