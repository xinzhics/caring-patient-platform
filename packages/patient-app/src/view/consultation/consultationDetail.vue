<template>
  <section>
    <x-header :left-options="{backText: ''}">详情<a slot="right" class="fa fa-exist"/>
    </x-header>
    <div>
      <van-cell-group>
        <van-cell title="名称" :value="this.data.groupName"></van-cell>
        <van-cell title="描述" :value="this.data.groupDesc"></van-cell>
      </van-cell-group>

      <van-cell-group>
        <van-cell title="姓名" :value="this.patientName"></van-cell>
        <van-cell title="角色" :value="name"></van-cell>
        <van-cell title="加入时间" :value="this.patientAddTime"></van-cell>
      </van-cell-group>

      <div style="padding-left: 15px; padding-right: 15px; padding-top: 10px; padding-bottom: 10px;
            font-size: 12px; color: #999999">参与人员
      </div>

      <div style="background: #FFFFFF">
        <div
          style="border-bottom:1px solid #F5F5F5"
          v-for="(item,i) in this.data.consultationGroupMembers" :key="i">
          <div style="padding-left: 15px; padding-right: 15px; padding-top: 12px; padding-bottom: 12px">
            <img v-if="item.memberAvatar" :src="item.memberAvatar" alt=""
                 style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px">
            <img v-if="!item.memberAvatar" :src="headerImgT" alt=""
                 style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px">
            <div style="display:inline-block;vertical-align: middle;">
              <div style="display: flex; font-size: 10px;">
                <span style="font-weight:600; font-size: 15px; color: #333333">{{ item.memberName }}</span>
                <div style="background: #F5F5F5; display: flex; align-items: center; justify-items: center; border-radius: 10px;
                margin-left: 5px; padding-left: 6px; padding-right: 6px; padding-top: 2px; padding-bottom: 2px">
                  <div style="text-align: center; color: #999999; font-size: 11px">{{ item.memberRole === 'patient' ? "我" : item.memberRoleRemarks }}</div>
                </div>

              </div>
              <p style="color: #666666; font-size: 12px">
                {{ getData(item.updateTime) }}
              </p>
            </div>
          </div>
        </div>

      </div>
    </div>


  </section>
</template>

<script>

import Api from '@/api/Content.js'
import Vue from 'vue';
import { Cell, CellGroup } from 'vant';

Vue.use(Cell);
Vue.use(CellGroup);

export default {
  name: "consultationDetail",
  data() {
    return {
      data: {},
      headerImgT: require('@/assets/my/default_avatar.png'),
      memeberList: [1, 2, 3],
      patientName: "",
      patientAddTime: "",
      name:""
    }
  },
  mounted(){
     // this.name = window.dictionaryItem.get('doctor')
     this.name = this.$getDictItem('patient')
  },
  methods: {
    getConsultationDetail() {
      Api.getConsultationGroupDetail(this.$route.query.id).then((res) => {
        if (res.data.code === 0) {
          this.data = res.data.data
          this.data.consultationGroupMembers.some((item, i) => {
            if (item.memberRole === 'patient') {
              this.patientName = item.memberName;
              this.patientAddTime = moment(item.updateTime).format("YYYY.MM.DD HH:mm");
              return true;
            }
          })
        }
      })
    },
    getData(data) {
      return moment(data).format("YYYY.MM.DD HH:mm");
    },
  },

  created() {
    this.getConsultationDetail()
  }
}
</script>

<style scoped>

</style>



