<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">{{ "参与人员" }}
    </x-header>

    <div v-for="(item,i) in list" :key="i">
      <div class="box">
        <img v-if="item.memberAvatar" :src="!item.memberAvatar ? headerImgT :item.memberAvatar" alt=""
             class="memberImg">
        <div style="display:inline-block;vertical-align: middle; flex: 1">
          <div style="display: flex; font-size: 10px;">
            <span class="contentTitle">{{ item.memberName }}</span>
            <div class="contentTag">
              <div style="text-align: center; color: #999999; font-size: 11px">{{
                  item.memberRole === 'roleDoctor' && item.memberUserId === userId ? "我" : item.memberRoleRemarks
                }}
              </div>
            </div>
          </div>
          <p style="color: #666666; font-size: 12px">
            {{ getData(item.updateTime) }}
          </p>
        </div>
      </div>
    </div>
  </section>
</template>

<script>

import Api from '@/api/Content.js'

export default {
  name: "consultationMember",
  data() {
    return {
      list: [],
    }
  },
  methods: {
    getData(data) {
      return moment(data).format("YYYY.MM.DD HH:mm");
    },
  },
  created() {
    Api.getConsultationGroupDetail(localStorage.getItem("groupId"))
      .then(res => {
        if (res.data && res.data.code === 0) {
          this.list = res.data.data.consultationGroupMembers
          console.log(this.list)
        }
      })
  }
}
</script>

<style lang="less" scoped>
.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;

  .box {
    padding: 15px 12px 12px 15px;
    display: flex;
    align-items: center;
    background: white;
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
</style>
