<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :rightIcon="groupId ? 'delete' : ''" @showpop="deleteGroupFunc" :title="(groupId ? '编辑' : '添加') + '小组'" @onBack="goback"></headNavigation>
    </van-sticky>
    <div style="margin-top: 13px">
      <div class="box " @click="showSetName()">
        <div class="list-left box2 text-ellipsis">小组名称</div>
        <div style="display: flex;align-items: center">
          <div style="color: #333" class="text-ellipsis">{{ group.name }}</div>
          <van-icon name="arrow" />
        </div>
      </div>
      <div class="box" @click="showSetPerson()" style="margin-bottom: 11px">
        <div class="list-left ">联系人</div>
        <div style="display: flex;align-items: center; text-align: left;">
          <span style="color: #333" class="text-ellipsis">{{ group.contactName }}</span>
          <van-icon name="arrow" />
        </div>
      </div>
      <div class="box" style="margin-top: 10px" @click="showSetPhone()">
        <div class="list-left">电话</div>
        <div style="display: flex;align-items: center; text-align: left;">
          <span style="color: #333" class="text-ellipsis">{{ group.contactMobile }}</span>
          <van-icon name="arrow" />
        </div>
      </div>
      <div class="box" @click="showSetAddress()">
        <div class="list-left">地址</div>
        <div style="display: flex;align-items: center; text-align: left;">
          <span style="color: #333" class="text-ellipsis">{{ group.address }}</span>
          <van-icon name="arrow" />
        </div>
      </div>
      <div class="box" style="margin-top: 10px" @click="showSetRemarks()">
        <div class="list-left">备注</div>
        <div style="display: flex;align-items: center; text-align: left">
          <span class="text-ellipsis" style="color: #333">{{ group.remarks }}</span>
          <van-icon name="arrow" />
        </div>
      </div>
    </div>
    <!--      弹窗部分-->
    <!--      小组名称弹窗-->
    <editDoctorDialog ref="setName" maxTextNumber="30" title="小组名称" @value="setName" :propsValue="group.name" ></editDoctorDialog>
    <!--      电话号弹窗-->
    <editDoctorDialog ref="setPhone" maxTextNumber="11" title="电话号" @value="setPhone" :propsValue="group.contactMobile" ></editDoctorDialog>
    <!--      联系人弹窗-->
    <editDoctorDialog ref="setPerson" maxTextNumber="30" title="联系人" @value="setPerson" :propsValue="group.contactName" ></editDoctorDialog>
    <!--      地址弹窗-->
    <editDoctorDialog ref="setAddress" maxTextNumber="30" title="地址" @value="setAddress" :propsValue="group.address" ></editDoctorDialog>
    <!--      备注弹窗-->
    <editDoctorDialog ref="setRemarks" maxTextNumber="30" title="备注" @value="setRemarks" :propsValue="group.remarks" ></editDoctorDialog>
    <div style="width: 217px;margin: 48px auto ">
      <van-button
        :class="disabledStatus() ? 'disable' : ''"
        :disabled="disabledStatus()"
        :loading="submitGroupStatus"
        @click="submitGroup"
        style="width: 217px;"
        round
        type="info">完成</van-button>
    </div>

  </div>
</template>

<script>
import editDoctorDialog from '@/components/editDoctorDilog/editDoctorDialog'
import {groupDetail, updateGroup, createGroup, deleteGroup} from '@/api/group.js'
import Vue from 'vue'
import {Col, Row, Icon, Popup, Button, Toast, Dialog, Sticky} from 'vant'
Vue.use(Icon)
Vue.use(Popup)
Vue.use(Sticky)
Vue.use(Col)
Vue.use(Row)
Vue.use(Button)
export default {
  components: {
    editDoctorDialog
  },
  data () {
    return {
      groupId: this.$route.query.groupId,
      nursingId: localStorage.getItem('caringNursingId'),
      submitGroupStatus: false,
      group: {},
      value: '', // 小组名称
      phoneNumber: '', // 电话
      person: '', // 联系人
      address: '', // 地址
      remarks: '' // 备注
    }
  },
  props: {
    title: {
      type: String
    }
  },
  created () {
    console.log(this.groupId)
    this.getGroupDetail()
  },
  methods: {
    showSetName () {
      this.$refs.setName.setValue(this.group.name)
      this.$refs.setName.openDialog()
    },
    showSetPerson () {
      this.$refs.setPerson.setValue(this.group.contactName)
      this.$refs.setPerson.openDialog()
    },
    showSetAddress () {
      this.$refs.setAddress.setValue(this.group.address)
      this.$refs.setAddress.openDialog()
    },
    showSetRemarks () {
      this.$refs.setRemarks.setValue(this.group.remarks)
      this.$refs.setRemarks.openDialog()
    },
    showSetPhone () {
      this.$refs.setPhone.setValue(this.group.contactMobile)
      this.$refs.setPhone.openDialog()
    },
    getGroupDetail () {
      if (this.groupId) {
        groupDetail(this.groupId).then(res => {
          console.log(res)
          this.group = res.data
        })
      }
    },
    /**
     * 提交小组
     */
    submitGroup () {
      this.submitGroupStatus = true
      if (this.group.id) {
        this.updateGroup()
      } else {
        this.createGroup()
      }
    },
    /**
     * 删除小组
     */
    deleteGroupFunc () {
      if (this.groupId) {
        if (this.group.doctorCount && this.group.doctorCount > 0) {
          Toast({message: '该小组下已有' + this.$getDictItem('doctor') + '，不可删除', closeOnClick: true})
        } else {
          Dialog.confirm({
            message: '是否删除该小组？',
            confirmButtonColor: '#337EFF'
          })
            .then(() => {
              deleteGroup(this.groupId).then(res => {
                if (res.code === 0) {
                  Toast({message: '删除成功', closeOnClick: true, duration: 1500})
                  this.$router.replace({
                    path: '/mydoctor'
                  })
                }
              })
            })
            .catch(() => {
              // on cancel
            })
        }
      }
    },
    /**
     * 添加小组
     */
    createGroup () {
      this.group.nurseId = this.nursingId
      createGroup(this.group).then(res => {
        this.submitGroupStatus = false
        if (res.code === 0) {
          this.$router.replace({
            path: '/mydoctor'
          })
        }
      })
    },
    /**
     * 修改小组
     */
    updateGroup () {
      this.group.nurseId = this.nursingId
      updateGroup(this.group).then(res => {
        this.submitGroupStatus = false
        if (res.code === 0) {
          this.$router.replace({
            path: '/mydoctor/groupDetails',
            query: {
              groupId: this.groupId
            }
          })
        }
      })
    },
    /**
     * 返回方法
     */
    goback () {
      this.$router.go(-1)
    },
    setName (val) {
      this.$set(this.group, 'name', val)
    },
    setPhone (val) {
      this.$set(this.group, 'contactMobile', val)
    },
    setPerson (val) {
      this.$set(this.group, 'contactName', val)
    },
    setAddress (val) {
      this.$set(this.group, 'address', val)
    },
    setRemarks (val) {
      this.$set(this.group, 'remarks', val)
    },
    /**
     * 提交按钮的禁用
     */
    disabledStatus () {
      if (!this.group.name) {
        return true
      }
      return false
    }
  }

}
</script>

<style scoped>
.box{
  background: #fff;
  height: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 17px 13px;
  border-bottom: 1px solid #EEEEEE;
}
.list-left{
  color: #666666;
}
.box2::after {
  color: #ee0a24;
  font-size: 14px;
  content: '*';
}
</style>
