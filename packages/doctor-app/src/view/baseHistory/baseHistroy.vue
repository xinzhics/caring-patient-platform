<template>
  <section style="background:#FAFAFA">
    <x-header :left-options="{backText: ''}">修改历史</x-header>
    <van-row
      style="text-align: center;background: #fff;line-height:51px;font-size:16px;font-family: Source Han Sans CN;color: #333333;">
      <van-col span="14">修改时间</van-col>
      <van-col span="5">修改人</van-col>
      <van-col span="3">角色</van-col>
    </van-row>
    <div class="table">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa;margin-left:0;width:100%">
        <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
          <van-row v-for="(info,index) in list"
                      :key="index" 
                      :class="'tr-color-' + index % 2"
                      @click="gonextpage(info)"
                      style="text-align: center;line-height:51px;color: #666;"
                      align="center">
        <van-col span="14">{{info.createTime}}</van-col>
        <van-col span="5">{{info.updateUserName}}</van-col>
        <van-col span="3">
          <van-tag plain
            :color="info.userType=='DOCTOR'?'#337EFF':info.userType=='PATIENT'?'#6F7D97':info.userType=='NURSING_STAFF'?'#FFA35C':''">
            {{ info.userType=="DOCTOR"?name:info.userType=="PATIENT"?patient:info.userType=="NURSING_STAFF"?assistant:'' }}
          </van-tag>
        </van-col>
        <van-col span="2">
          <van-icon name="arrow" style="color:#BDBDBD" />
        </van-col>
      </van-row>
        </van-list>
        
        </van-pull-refresh>
    </div>
  </section>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
</style>