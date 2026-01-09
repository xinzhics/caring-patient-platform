<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left"  title="修改历史" @onBack="back"></headNavigation>
    </van-sticky>
    <van-row
      style="text-align: center;margin-top: 13px; background: #fff;font-size:16px;font-family: Source Han Sans CN;color: #333333;height: 51px">
      <van-col style="line-height:51px;" span="14">修改时间</van-col>
      <van-col style="line-height:51px;" span="5">修改人</van-col>
      <van-col style="line-height:51px;" span="3">角色</van-col>
    </van-row>
    <div class="table">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="margin-left:0;width:100%; min-height: 30vh">
        <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
          <van-row v-for="(info,index) in list"
                   :key="info.id"
                   :class="'tr-color-' + index % 2"
                   @click="gonextpage(info)"
                   style="text-align: center;color: #666;height: 51px"
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
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
/deep/.van-col{
  height: 51px;
  display: flex;
  align-items: center;
}
</style>
